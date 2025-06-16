package fr.sparna.rdf.shacl.jsonschema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.jsonschema.jsonld.LocalNameUriToJsonMapper;
import fr.sparna.rdf.shacl.jsonschema.jsonld.UriToJsonMapper;
import fr.sparna.rdf.shacl.jsonschema.model.ArraySchema;
import fr.sparna.rdf.shacl.jsonschema.model.BooleanSchema;
import fr.sparna.rdf.shacl.jsonschema.model.CombinedSchema;
import fr.sparna.rdf.shacl.jsonschema.model.ConstSchema;
import fr.sparna.rdf.shacl.jsonschema.model.EmptySchema;
import fr.sparna.rdf.shacl.jsonschema.model.EnumSchema;
import fr.sparna.rdf.shacl.jsonschema.model.NumberSchema;
import fr.sparna.rdf.shacl.jsonschema.model.ObjectSchema;
import fr.sparna.rdf.shacl.jsonschema.model.ReferenceSchema;
import fr.sparna.rdf.shacl.jsonschema.model.Schema;
import fr.sparna.rdf.shacl.jsonschema.model.StringSchema;
import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.OwlOntology;
import fr.sparna.rdf.jena.shacl.PropertyShape;
import fr.sparna.rdf.jena.shacl.ShapesGraph;

public class JsonSchemaGenerator {
    
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	public static final String JSON_SCHEMA_VERSION = "https://json-schema.org/draft/2020-12/schema";
    public static final String CONTEXT = "@context";
    public static final String CONTAINER_LANGUAGE = "container_language";

    // indicates the URI of which NodeShapes should be considered roots
    private List<String> rootShapes;
    // maps URI to JSON terms
    private UriToJsonMapper uriMapper;
    // the string value to put in final @context
    private String targetContextUrl;
	// the language in which to read the labels and description
	private String lang;


    public JsonSchemaGenerator(String lang, String targetContextUrl, List<String> rootShapes) {
		this.lang = lang;
        this.targetContextUrl = targetContextUrl;
        this.rootShapes = rootShapes;
        this.uriMapper = new LocalNameUriToJsonMapper();
    }

    public JsonSchemaGenerator(String lang, List<String> rootShape) {
        //this(lang, null, Collections.singletonList(rootShape));
    	this(lang, null, rootShape);
    }

    public Schema convertToJsonSchema(Model shaclGraph) throws Exception {
        log.info("Generating JSON schema...");
		ShapesGraph shapesGraph = new ShapesGraph(shaclGraph, null);
		    
		// Create JSON Schema 
        // root schema
		ObjectSchema.Builder rootSchema = ObjectSchema.builder();		
		// always set the schema version to the latest one
        rootSchema.schemaVersion(JSON_SCHEMA_VERSION);
		// always set a @context $def
        rootSchema.embeddedSchema(CONTEXT, getContextSchema());
		// always set a "container_language" $def
		rootSchema.embeddedSchema(CONTAINER_LANGUAGE, getContainerLanguage());

		// always set a @context pointing to the $def
		rootSchema.addPropertySchema(CONTEXT, 
		ReferenceSchema
			.builder()
			.refValue("#/$defs/"+CONTEXT)
			.build());
		
		// @context is required
		rootSchema.addRequiredProperty(CONTEXT);
		// always set additionalProperties to false
		rootSchema.additionalProperties(false);

        // Read Ontology
        populateMetadataFromOntology(shapesGraph,rootSchema);        
        
		// Read nodeshapes and generate corresponding object schemas
		Predicate<NodeShape> hasNoActivePropertyShape = this.buildHasNoActivePropertyShapePredicate();
		for (NodeShape ns : shapesGraph.getAllNodeShapes()) {
			
			rootSchema.embeddedSchema(
					ns.getNodeShape().getLocalName(),
					// create the object schema from the node shape
					hasNoActivePropertyShape.test(ns)
					?convertNodeShapeToStringSchema(ns)
					:convertNodeShapeToObjectSchema(ns, shaclGraph)
			);
		}
		
		// find the root node shapes
		List<NodeShape> rootNodeShapes = findRootNodeShapes(shapesGraph.getAllNodeShapes());

		// now create the "data" schema

		Schema dataSchema;
        if(rootNodeShapes.size() > 1) {
            // if there are more than 1, use an OneOf schema
            List<Schema> oneOfList = new ArrayList<>();
            for (NodeShape nsroot : rootNodeShapes) {
                oneOfList.add(ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(nsroot.getNodeShape())).build());
            }
			dataSchema = ArraySchema.builder().minItems(1).allItemSchema(CombinedSchema.anyOf(oneOfList).build()).build();
        } else if(rootNodeShapes.size() == 1) {
            // if there is only one, use only this one
            Schema singleRootSchema = ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(rootNodeShapes.get(0).getNodeShape())).build();
            dataSchema = ArraySchema.builder().minItems(1).allItemSchema(singleRootSchema).build();
        } else {
			throw new Exception("Could not determine a root schema");
		}

		rootSchema.addPropertySchema("data", dataSchema);
		rootSchema.addRequiredProperty("data");
		
		log.info("Done generating JSON schema...");
		return rootSchema.build();
    }

    /**
     * Constructs a reference to the corresponding schema builf from the provided resource
     * @param r
     * @return
     */
    private static String buildSchemaReference(Resource r) {
        return ("#/$defs/"+r.getLocalName());
    }

    private void populateMetadataFromOntology(ShapesGraph shapesGraph,ObjectSchema.Builder rootSchema) {
    	
    	// Generate OWL
    	OwlOntology owl = shapesGraph.getOntology();
    	
		if(owl != null) {
			// URI = id
			rootSchema.id(owl.getResource().getURI()); 

			// title
			Optional.ofNullable(owl.getTitleOrLabel(this.lang)).ifPresent(title -> rootSchema.title(title));
			
			// version
			Optional.ofNullable(owl.getOwlVersionInfo()).ifPresent(version -> rootSchema.version(version));

			// description
			Optional.ofNullable(owl.getDescription(this.lang)).ifPresent(desc -> rootSchema.description(desc));			
		}

		
	}
	
	private Schema getContextSchema() {
		// if we know the target context URL, use it, otherwise use a simple string schema
		if(this.targetContextUrl != null) {
			return ConstSchema
	    		.builder()
	    		.permittedValue(this.targetContextUrl)
	    		.comment("The fixed @context JSON-LD URL")
	    		.build();
		} else {
			return StringSchema
	    		.builder()
	    		.format("iri-reference")
	    		.comment("The URL of the JSON-LD @context")
	    		.build();
		}
		
	}
	
	private Schema getContainerLanguage() {
		
       Schema containerLanguage = ObjectSchema
				.builder()
				.patternProperty("^[A-Za-z]{2,3}$", 
						StringSchema
						.builder() 
						.comment("Overly simplified version of the regex. See https://github.com/w3c/wot-thing-description/blob/main/validation/td-json-schema-validation.json for a complete regex")
						.build())
				.additionalProperties(false)
				.build();
		
	    return containerLanguage;
	}
	
	/**
	 * This is the case where the ndoeShape has no active property shape : we want it to be a simple iri-reference
	 */
	private Schema convertNodeShapeToStringSchema(
		NodeShape nodeShape
	) throws Exception {
		StringSchema.Builder stringSchema = StringSchema.builder();
		
		String title = null;
		if (nodeShape.getRdfsLabel(lang).size() > 0) {
			title = nodeShape.getRdfsLabel(lang).stream().map(label -> label.getString()).collect(Collectors.joining(" "));
		}
		
		String description = null;
		if (nodeShape.getRdfsComment(lang).size() > 0) {
			description = nodeShape.getRdfsComment(lang).stream().map(l -> l.getString()).collect(Collectors.joining(" "));
		}

		stringSchema
			.title(title)
			.description(description)
			.format("iri-reference");

		return stringSchema.build();
	}

	private Predicate<NodeShape> buildHasNoActivePropertyShapePredicate() {
		return new Predicate<NodeShape>() {

			@Override
			public boolean test(NodeShape ns) {
				// as soon as we find one non-deactivated property shape, we return false
				for (PropertyShape ps : ns.getProperties()) {
					if (!ps.isDeactivated()) {
						return false;
					}
				}
				// no property shape active at all, return true
				return true;
			}
		};
	}

	private Schema convertNodeShapeToObjectSchema(
		NodeShape nodeShape,
		Model model
	) throws Exception {
		
		log.debug("Converting NodeShape "+nodeShape.getNodeShape().getURI()+" to JSON Schema, with "+nodeShape.getProperties().size()+" properties");

		ObjectSchema.Builder objectSchema = ObjectSchema.builder();	
		
		if (nodeShape.getRdfsLabel(lang).size() > 0) {
			objectSchema.title(nodeShape.getRdfsLabel(lang).stream().map(s -> s.getString()).collect(Collectors.joining(" ")));
		}
		
		if (nodeShape.getRdfsComment(lang).size() > 0) {
			objectSchema.description(nodeShape.getRdfsComment(lang).stream().map(l -> l.getString()).collect(Collectors.joining(" ")));
		}
		
		List<String> examplesId = nodeShape.getExamples().stream().map(e -> e.asLiteral().getString()).collect(Collectors.toList());
		
		StringSchema.Builder stringSchemaBuilder = StringSchema.builder();
		if (nodeShape.getPattern().isPresent()) {
			stringSchemaBuilder.pattern(nodeShape.getPattern().get().getString());
		}
		if( !examplesId.isEmpty()) {
			stringSchemaBuilder.examples(examplesId);
		}
		stringSchemaBuilder.format("iri-reference");

		// always set an id property, always required
		objectSchema.addPropertySchema("id", stringSchemaBuilder.build());		
		objectSchema.addRequiredProperty("id");

		for (PropertyShape ps : nodeShape.getProperties()) {

			// skip the property shape if it is deactivated
			if (ps.isDeactivated()) {
				continue;
			}

			// Name of Property
			Resource path = ps.getShPath().get().asResource();
			Set<String> shortnames = ShaclReadingUtils.findShortNamesOfPath(path,model);
			if(shortnames.isEmpty()) {
				shortnames.add(uriMapper.mapPropertyURI(path));
			} 

			// Get name of property 
			String term = shortnames.iterator().next();
			if(shortnames.size() > 1) {
				log.warn("Found multiple shortnames for path "+path+", will use only one : '"+term+"'");
			}

			objectSchema.addPropertySchema(term, this.convertPropertyShapeSchema(nodeShape, ps, model));

			// add to required properties if necessary
			if (ps.getShMinCount().isPresent()) {
				if (ps.getShMinCount().get().getInt() > 0) {
					objectSchema.addRequiredProperty(term);
				}
			}			
		}		
		
		// set additionnal properties to false if the NodeShape is sh:closed
		if (nodeShape.isClosed()) {
			objectSchema.additionalProperties(false);
		}
		
        return objectSchema.build();
		
	}

	private Schema convertPropertyShapeSchema(
		NodeShape nodeShape,
		PropertyShape ps,
		Model model
	) throws Exception {

		log.trace("Converting PropertyShape "+ps.getPropertyShape().getURI()+" to JSON Schema");

		// precedence order
		// 1. sh:hasValue ==> ConstSchema
		// 2. sh:in ==> EnumSchema
		// 3. sh:node ==> iri-reference if no embed, or ReferenceSchema if embed, wrapped into ArraySchema if maxCount > 1
		// 4. sh:pattern ==> StringSchema with pattern
		// 5. sh:datatype ==> StringSchema or ReferenceSchema to ContainerLanguage or BooleanSchema or NumberSchema
		// 6. sh:nodeKind == IRI ==> StringSchema with format = iri-reference
		// otherwise return EmptySchema

		Schema.Builder singleValueBuilder = null;

		// sh:hasValue	
		if (!ps.getShHasValue().isEmpty()) {
			// TODO : handle constant literal values			
			singleValueBuilder = ConstSchema
				.builder()
				.permittedValue(this.uriMapper.mapValueURI(ps.getShHasValue().get().asResource()));
		}

		// sh:in
		if (singleValueBuilder == null && ps.getShIn() != null) {
			
			List<Object> values = new ArrayList<>();
			for (RDFNode i : ps.getShIn()) {				
				if (i.isURIResource()) {
					values.add(this.uriMapper.mapValueURI(i.asResource()));
				} else if (i.isLiteral()) {
					values.add(i.asLiteral().getValue());
				} else {
					log.warn("Found a sh:in value that is neither a URI nor a Literal, ignoring it: "+i);
				}
			}
			
			if(values.size() > 0) {
				singleValueBuilder = EnumSchema
						.builder()
						// TODO : we are always requiring string values for enums, but they could be e.g. numbers or even of different types
						.requiresString(true)
						.possibleValues(values);	
			}
		}

		//sh:node
		if (singleValueBuilder == null && !ps.getShNode().isEmpty()) {				

			ShapesGraph shapesGraph = new ShapesGraph(model, null);
			if(ps.isEmbedNever() || shapesGraph.findNodeShapeByResource(ps.getShNode().get()) == null) {
				// no embedding, or reference to sh:node not found, this is a URI reference
				singleValueBuilder = StringSchema
				.builder()
				.format("iri-reference");
			} else {
				// this is a reference to another Schema coming from the NodeShape
				// TODO : make sure the NodeShape exists, otherwise the schema is inconsistent
				singleValueBuilder = ReferenceSchema
					.builder()
					// note : the builder-specific method needs to be called **before** the generic method
					.refValue(JsonSchemaGenerator
								.buildSchemaReference(ps.getShNode().get().asResource()));
			}
		}

		// sh:pattern
		if (singleValueBuilder == null && !ps.getShPattern().isEmpty()) {
			singleValueBuilder = StringSchema
					.builder()
					// note : the builder-specific method needs to be called **before** the generic method
					.pattern(ps.getShPattern().get().getString());			
		}

		// Datatype
		if (singleValueBuilder == null && ps.getShDatatype().isPresent()) {				

			Set<Resource> datatypes = ShaclReadingUtils.findDatatypesOfPath(ps.getShPath().get().asResource(), model);
			if(datatypes.size() > 1) {
				log.warn("Found different datatypes declared for path "+ps.getShPath().get().asResource()+", will declare only one");
			}
			
			if(!datatypes.isEmpty()) {
				Resource theDatatype = datatypes.iterator().next();
				String datatype = theDatatype.getURI();
				if (datatype.equals(RDF.langString.getURI())) {						
					singleValueBuilder = ReferenceSchema
							.builder()	
							// note : the builder-specific method needs to be called **before** the generic method
							.refValue("#/$defs/"+CONTAINER_LANGUAGE);
					
				} else {
					Optional<DatatypeToJsonSchemaMapping> typefound = DatatypeToJsonSchemaMapping.findByDatatypeUri(datatype);
					
					if (typefound.isPresent()) {							
						if (typefound.get().getJsonSchemaType().equals(JsonSchemaType.STRING)) {								
							singleValueBuilder = StringSchema
								.builder()
								.format(typefound.get().getJsonSchemaFormat());
						} else if (typefound.get().getJsonSchemaType().equals(JsonSchemaType.BOOLEAN)) {
							singleValueBuilder = BooleanSchema
										.builder();
						} else if (typefound.get().getJsonSchemaType().equals(JsonSchemaType.NUMBER) || typefound.get().getJsonSchemaType().equals(JsonSchemaType.INTEGER)) {
							singleValueBuilder = NumberSchema
								.builder();
						}
					} else {
						// default to StringSchema
						singleValueBuilder = StringSchema
							.builder();
					}
				}
			}		
		}

		// NodeKind IRI
		if (singleValueBuilder == null && ps.getShNodeKind().filter(nodeKind -> nodeKind.getURI().equals(SH.IRI.getURI())).isPresent()) {	
			singleValueBuilder = StringSchema
			.builder()
			.format("iri-reference");
		}

		// NodeKind Literal
		if (singleValueBuilder == null && ps.getShNodeKind().filter(nodeKind -> nodeKind.getURI().equals(SH.Literal.getURI())).isPresent()) {	
			singleValueBuilder = StringSchema
			.builder();
		}

		// default
		if(singleValueBuilder == null) {
			singleValueBuilder = EmptySchema
			.builder();
		}

		Schema.Builder builder = singleValueBuilder;

		// if the property shape has a maxCount > 1 or no maxCount specified, then we need to wrap the single value schema into an ArraySchema
		if (singleValueBuilder != null && !ps.getShMaxCount().isPresent() || ps.getShMaxCount().get().asLiteral().getInt() > 1 ) {
			// this is an ArraySchema that will contain the inner Schema built previously
			Schema innerSchema = singleValueBuilder.build();

			builder = ArraySchema
			.builder()
			.minItems(1)
			.allItemSchema(innerSchema);
		} 

		// add title and description 
		// schema title
		String titleProperty = null;		
		if (ps.getShName().isPresent()) {
			titleProperty = ps.getShName().get().asLiteral().getString();
		}
			
		// schema description
		String descriptionProperty = null;
		if (ps.getShDescription().isPresent()) {
			descriptionProperty = ps.getShDescription().get().asLiteral().getString();
		}

		// set title and description on the final outer builder, ArraySchema or the single value schema
		builder.title(titleProperty).description(descriptionProperty);

		// nothing, return an EmptySchema
		return builder.build();

	}
	
	public String getTargetContextUrl() {
		return targetContextUrl;
	}

	public void setTargetContextUrl(String targetContextUrl) {
		this.targetContextUrl = targetContextUrl;
	}

    private List<NodeShape> findRootNodeShapes(List<NodeShape> nodeShapes) {

        Predicate<NodeShape> predicate = new Predicate<NodeShape>() {

            @Override
            public boolean test(NodeShape ns) {
                // to be a root, a NodeShape must either:

                // 1. be referenced as sh:node or indirectly via sh:class from a property shape
                // that does not use embedding
                boolean isEmbedded = true;
                for (NodeShape nodeShape : nodeShapes) {
                    for (PropertyShape propertyShape : nodeShape.getProperties()) {
                        if(
                            propertyShape.getShNode().filter(r -> r.getURI().equals(ns.getNodeShape().getURI())).isPresent()
                            ||
                            propertyShape.getShClass().filter(r -> {
                                return 
								// just in case to prevent NullPointer
								(r.getURI() != null)
								&&
                                r.getURI().equals(ns.getNodeShape().getURI())
                                ||
                                (ns.getTargetClass() != null && r.getURI().equals(ns.getTargetClass().getURI()))
                                ;
                            }).isPresent()
                        ) {
                            if(propertyShape.isEmbedNever()) {
								log.debug("Found that "+ns.getNodeShape().getURI() + " is not embedded in property "+propertyShape.getPropertyShape().getURI());
                                isEmbedded = false;
                                break;
                            }
                        }                        
                    }
                    if(!isEmbedded) {
                        break;
                    }
                }

                // 2. be explicitely specified as an input
                boolean isExplicitelyRootShape = rootShapes.contains(ns.getNodeShape().getURI());

                return  isExplicitelyRootShape || !isEmbedded;
            }
            
        };

        return nodeShapes.stream().filter(predicate).collect(Collectors.toList());
    }
}
