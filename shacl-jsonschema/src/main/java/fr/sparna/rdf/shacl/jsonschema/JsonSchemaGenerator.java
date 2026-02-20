package fr.sparna.rdf.shacl.jsonschema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.OwlOntology;
import fr.sparna.rdf.jena.shacl.PropertyShape;
import fr.sparna.rdf.jena.shacl.ShOrReadingUtils;
import fr.sparna.rdf.jena.shacl.Shape;
import fr.sparna.rdf.jena.shacl.ShapesGraph;
import fr.sparna.rdf.shacl.jsonschema.jsonld.ContextUriMapper;
import fr.sparna.rdf.shacl.jsonschema.jsonld.LocalNameUriToJsonMapper;
import fr.sparna.rdf.shacl.jsonschema.jsonld.ProbingJsonLdContextWrapper;
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
import jakarta.json.JsonValue;


public class JsonSchemaGenerator {
    
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	public static final String JSON_SCHEMA_VERSION = "https://json-schema.org/draft/2020-12/schema";
    public static final String CONTEXT = "@context";
    public static final String CONTAINER_LANGUAGE = "container_language";
    
    // maps URI to JSON terms
    private UriToJsonMapper uriMapper;
	// the language in which to read the labels and description
	private String lang;

	private JsonValue context;
	private ProbingJsonLdContextWrapper contextWrapper;


    public JsonSchemaGenerator(String lang) {
		this(lang,null);
    }

	public JsonSchemaGenerator(String lang, JsonValue context) {
		this.context = context;
		if(context != null) {
			ProbingJsonLdContextWrapper test = new ProbingJsonLdContextWrapper(context);
			this.contextWrapper = test; //new ProbingJsonLdContextWrapper(context);
			this.uriMapper = new ContextUriMapper(context);
		} else {
			this.contextWrapper = null;
			this.uriMapper = new LocalNameUriToJsonMapper();
		}

		// set the language for labels and descriptions
		this.lang = lang;
    }

	public Schema convertToJsonSchema(Model shaclGraph,boolean ignoreProperties, boolean addProperties) throws Exception {
		return convertToJsonSchema(shaclGraph, Arrays.asList(),ignoreProperties,addProperties);
	}

	public Schema convertToJsonSchema(
		Model shaclGraph,
		List<String> rootShapes
	) throws Exception {
		return this.convertToJsonSchema(shaclGraph, rootShapes, false, false);
	}

	public Schema convertToJsonSchema(
		Model shaclGraph,
		List<String> rootShapes,
		boolean doNotIncludeValues,
		boolean addProperties
	) throws Exception {
		// make a deep copy of the Model because we will be pruning the unused node shapes in it
		Model shaclGraphCopy = ModelFactory.createDefaultModel().add(shaclGraph);
		ShapesGraph shapesGraph = new ShapesGraph(shaclGraphCopy, null);
		shapesGraph.pruneEmptyAndUnusedNodeShapes();
		return convertToJsonSchema(shapesGraph, rootShapes, doNotIncludeValues, addProperties);
	}

    public Schema convertToJsonSchema(
		ShapesGraph shapesGraph,
		List<String> rootShapes,
		boolean doNotIncludeValues,
		boolean addProperties
	) throws Exception {
        log.info("Generating JSON schema...");
		    
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
		for (NodeShape ns : shapesGraph.getAllNodeShapes()) {
			if(!ns.isPureValueShape()) {
				rootSchema.embeddedSchema(
					ns.getNodeShape().getLocalName(),
					convertNodeShapeToObjectSchema(ns, shapesGraph.getShaclGraph(),!doNotIncludeValues,addProperties)
				);
			}

			/*
			rootSchema.embeddedSchema(
					ns.getNodeShape().getLocalName(),
					// create the object schema from the node shape
					ns.isPureValueShape()
					?convertNodeShapeToStringSchema(ns, shapesGraph.getShaclGraph())
					:convertNodeShapeToObjectSchema(ns, shapesGraph.getShaclGraph(),!doNotIncludeValues,addProperties)
			);
			 */
		}
		
		// find the root node shapes
		List<NodeShape> rootNodeShapes = findRootNodeShapes(shapesGraph.getAllNodeShapes(), rootShapes);

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
    	
    	// Get the ontology object
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
		return EmptySchema.builder().comment("The JSON-LD @context").build();
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
	 * Handles :
	 * - sh:pattern with shortening
	 * - sh:datatype, including in qualifiedValueShape
	 * - sh:nodeKind IRI
	 * - sh:nodeKind Literal
	 * - skos:example
	 * 
	 * @param nodeShapeOrPropertyShape Either the property shape itself of a NodeShape referenced through sh:node
	 * @param inContextOfPropertyShape The property shape in which the call is done, just to know the sh:path for shortening
	 * @param model
	 * @return
	 * @throws Exception
	 */
	private Schema.Builder convertShapeToSchemaForProperty(
		Shape nodeShapeOrPropertyShape,
		PropertyShape inContextOfPropertyShape,
		Model model
	) throws Exception {
		Schema.Builder singleValueBuilder = null;

		// sh:pattern
		if (singleValueBuilder == null && !nodeShapeOrPropertyShape.getShPattern().isEmpty()) {
			String pattern = nodeShapeOrPropertyShape.getShPattern().get().getString();
			String patternAsInJson = pattern;
			if(!nodeShapeOrPropertyShape.couldBeLiteral()) {
				if(inContextOfPropertyShape.getShPath().isURIResource()) {
					patternAsInJson = this.uriMapper.mapUriPatternToJsonPattern(pattern, inContextOfPropertyShape.getShPath().getURI());
				} else {
					patternAsInJson = this.uriMapper.mapUriPatternToJsonPattern(pattern, null);
				}
			}
			
			singleValueBuilder = StringSchema
					.builder()
					// note : the builder-specific method needs to be called **before** the generic method
					.pattern(patternAsInJson);			
		}

		// sh:datatype, including in qualifiedValueShape
		Optional<Resource> qualifiedValueShapeDatatype = Optional.ofNullable(
			nodeShapeOrPropertyShape.getShape().getProperty(SH.qualifiedValueShape)
		)
		.map(st -> st.getResource())
		.map(r -> new NodeShape(r)).flatMap(ns -> ns.getShDatatype());

		if (
			singleValueBuilder == null
			&&
			(
				nodeShapeOrPropertyShape.getShDatatype().isPresent()
				||
				qualifiedValueShapeDatatype.isPresent()
			)
		) {

			Resource theDatatype = nodeShapeOrPropertyShape.getShDatatype().orElseGet(() -> qualifiedValueShapeDatatype.orElse(null));

			if(theDatatype != null) {
				String datatype = theDatatype.getURI();
				// TODO : theoretically we should check the schema to make sure the property is really a @container: @language
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
		if (singleValueBuilder == null && nodeShapeOrPropertyShape.getShNodeKind().filter(nodeKind -> nodeKind.getURI().equals(SH.IRI.getURI())).isPresent()) {	
			singleValueBuilder = StringSchema
			.builder()
			.format("iri-reference");
		}

		// NodeKind Literal
		if (singleValueBuilder == null && nodeShapeOrPropertyShape.getShNodeKind().filter(nodeKind -> nodeKind.getURI().equals(SH.Literal.getURI())).isPresent()) {	
			singleValueBuilder = StringSchema
			.builder();
		}

		// shorten all examples according to context
		List<String> examplesRaw = nodeShapeOrPropertyShape.getExamples().stream().map(e -> e.asLiteral().getString()).collect(Collectors.toList());
		
		List<String> examples = examplesRaw.stream()
			.map(ex -> model.createResource(ex))
			.map(res -> {
				if(inContextOfPropertyShape.getShPath().isURIResource()) {
					return this.uriMapper.mapValueURI(res, inContextOfPropertyShape.getShPath().getURI());
				} else {
					return this.uriMapper.mapValueURI(res, null);
				}
			 })
			.collect(Collectors.toList());
		
		if( singleValueBuilder != null && !examples.isEmpty() ) {
			singleValueBuilder.examples(examples);
		}

		return singleValueBuilder;
	}


	private Schema convertNodeShapeToObjectSchema(
		NodeShape nodeShape,
		Model model,
		boolean includeValues,
		boolean addProperties
	) throws Exception {
		
		log.debug("Converting NodeShape "+nodeShape.getNodeShape().getURI()+" to JSON Schema, with "+nodeShape.getProperties().size()+" properties");

		ObjectSchema.Builder objectSchema = ObjectSchema.builder();	
		
		if (nodeShape.getRdfsLabel(lang).size() > 0) {
			objectSchema.title(nodeShape.getRdfsLabel(lang).stream().map(s -> s.getString()).collect(Collectors.joining(" ")));
		}
		
		if (nodeShape.getRdfsComment(lang).size() > 0) {
			objectSchema.description(nodeShape.getRdfsComment(lang).stream().map(l -> l.getString()).collect(Collectors.joining(" ")));
		}
		
		List<String> examplesRaw = nodeShape.getExamples().stream().map(e -> e.asLiteral().getString()).collect(Collectors.toList());
		
		// shorten all examples according to context
		List<String> examplesId = examplesRaw.stream()
			.map(ex -> model.createResource(ex))
			.map(res -> this.uriMapper.mapValueURI(res, null))
			.collect(Collectors.toList());

		StringSchema.Builder idSchemaBuilder = StringSchema.builder();
		if (nodeShape.getShPattern().isPresent()) {
			// no property URI in this case
			idSchemaBuilder.pattern(this.uriMapper.mapUriPatternToJsonPattern(nodeShape.getShPattern().get().getString(), null));
		}
		if( !examplesId.isEmpty()) {
			idSchemaBuilder.examples(examplesId);
		}
		idSchemaBuilder.format("iri-reference");

		// always set an id property, always required
		objectSchema.addPropertySchema("id", idSchemaBuilder.build());		
		objectSchema.addRequiredProperty("id");

		for (PropertyShape ps : nodeShape.getProperties()) {

			// skip the property shape if it is deactivated
			if (ps.isDeactivated()) {
				continue;
			}

			// Name of Property
			Optional<Resource> qualifiedValueShapeDatatype = ps.getShQualifiedValueShape().map(r -> new NodeShape(r)).flatMap(ns -> ns.getShDatatype());
			Resource theDatatype = ps.getShDatatype().orElseGet(() -> qualifiedValueShapeDatatype.orElse(null));

			Triple<String,Boolean,Boolean> contextTest = uriMapper.mapPath(
				ps.getShPath(),
				ps.couldBeIriProperty(),
				theDatatype,
				// TODO : we don't handle language for now
				null
			);
			

			// we are preventing cases where the path is a blank node and was not mapped
			// in the context
			if(contextTest != null) {
				String shortname = contextTest.getLeft();
				objectSchema.addPropertySchema(shortname, this.convertPropertyShapeSchema(
					ps, 
					// requires array
					contextTest.getMiddle(),
					// requires container language
					contextTest.getRight(),
					model,
					includeValues)
				);

				Optional<Integer> qualifiedMinCount = ps.getShQualifiedMinCount();
				Integer theMinCount = ps.getShMinCount().orElseGet(() -> qualifiedMinCount.orElse(null));

				// add to required properties if necessary
				if (theMinCount != null && theMinCount > 0) {
					objectSchema.addRequiredProperty(shortname);
				}	
			}		
		}		
		
		// set additionnal properties to false if the NodeShape is sh:closed
		if (nodeShape.isClosed()) {
			if (!addProperties) {
				objectSchema.additionalProperties(false);
			}
		}
		
        return objectSchema.build();
		
	}

	private Schema convertPropertyShapeSchema(
		PropertyShape ps,
		boolean requiresArray,
		boolean requiresContainerLanguage,
		Model model,
		boolean includeValues
	) throws Exception {

		log.trace("Converting PropertyShape "+ps.getPropertyShape().getURI()+" to JSON Schema");
		ShapesGraph shapesGraph = new ShapesGraph(model, null);

		// precedence order
		// 1. sh:hasValue ==> const
		// 2. sh:in ==> enum
		// 3. sh:node or sh:node in qualifiedValueShape ==> iri-reference if no embed, or ref if embed, or copy the node shape schema if it is a pure value node
		// 4. sh:or with sh:node ==> anyOf of either ref, or the inner node shape schema if it is a pure value node
		// 5. sh:pattern ==> StringSchema with pattern
		// 6. sh:datatype ==> string or ref to ContainerLanguage or boolean or number
		// 7. sh:nodeKind == IRI ==> StringSchema with format = iri-reference
		// otherwise an empty schema
		// 
		// do the wrapping of the value schema in Array if maxCount > 1

		Schema.Builder singleValueBuilder = null;
		
		if (includeValues) {
			// sh:hasValue	
			if (!ps.getShHasValue().isEmpty()) {
				// TODO : handle constant literal values			
				singleValueBuilder = ConstSchema
					.builder()
					.permittedValue(this.uriMapper.mapValueURI(ps.getShHasValue().get().asResource(), (ps.getShPath().isURIResource())?ps.getShPath().getURI():null));
			}
	
			// sh:in
			if (singleValueBuilder == null && ps.getShIn() != null) {
				
				List<Object> values = new ArrayList<>();
				for (RDFNode i : ps.getShIn()) {				
					if (i.isURIResource()) {
						values.add(this.uriMapper.mapValueURI(i.asResource(), (ps.getShPath().isURIResource())?ps.getShPath().getURI():null));
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
		} // end checkbox for ignore if is a sh:hasValue or sh:in

		// sh:node, either a ref or a pure value node
		Resource shNodeForRef = this.findShNode(ps);
		if (
			singleValueBuilder == null
			&& 
			shNodeForRef != null
		) {
			if(shapesGraph.findNodeShapeByResource(shNodeForRef).isPureValueShape()) {
				singleValueBuilder = this.convertShapeToSchemaForProperty(new NodeShape(shNodeForRef), ps, model);
			} else if(ps.isEmbedNever() || shapesGraph.findNodeShapeByResource(shNodeForRef) == null) {
				// no embedding, or reference to sh:node not found, this is a URI reference
				singleValueBuilder = StringSchema
				.builder()
				.format("iri-reference");
			} else {
				// this is a reference to another Schema coming from the NodeShape
				singleValueBuilder = ReferenceSchema
					.builder()
					// note : the builder-specific method needs to be called **before** the generic method
					.refValue(JsonSchemaGenerator
								.buildSchemaReference(shNodeForRef));
			}
		}

		// sh:or with sh:node, either a ref or a value node
		List<Resource> shNodesInShOr = this.findShNodeInShOr(ps);
		if (singleValueBuilder == null && shNodesInShOr.size() > 0) {
            // Read sh:or and get all result possibles
			List<Schema> anyOfList = new ArrayList<>();

			// TODO : handle embed
			// if none of the references is converted here, it will default to others after this step
			// after the sh:or conversion
			for (Resource n : shNodesInShOr) {
				if(shapesGraph.findNodeShapeByResource(n).isPureValueShape()) {
					Schema.Builder innerSchemaBuilder = this.convertShapeToSchemaForProperty(new NodeShape(n), ps, model);
					if(innerSchemaBuilder != null) {
						anyOfList.add(innerSchemaBuilder.build());
					}
				} else {
					anyOfList.add(ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(n)).build());
				}
			}
			singleValueBuilder = CombinedSchema.anyOf(anyOfList);                     
		}

		if(singleValueBuilder == null) {
			singleValueBuilder = this.convertShapeToSchemaForProperty(ps, ps, model);
		}

		// default
		if(singleValueBuilder == null) {
			singleValueBuilder = EmptySchema
			.builder();
		}

		Schema.Builder finalBuilder = null;

		// if the property shape has a maxCount > 1 or no maxCount specified, then we need to wrap the single value schema into an ArraySchema
		Optional<Integer> qualifiedMaxCount = ps.getShQualifiedMaxCount();
		Integer theMaxCount = ps.getShMaxCount().orElseGet(() -> qualifiedMaxCount.orElse(null));
		if (
			singleValueBuilder != null
			&&
			(theMaxCount == null || theMaxCount > 1)
			&&
			// prevent array wrapping if the context requires a language container
			!requiresContainerLanguage
		) {
			// this is an ArraySchema that will contain the inner Schema built previously
			Schema innerSchema = singleValueBuilder.build();

			Integer maxItems = null;
			if(theMaxCount != null && theMaxCount > 1) {
				maxItems = theMaxCount;
			}

			finalBuilder = ArraySchema
			.builder()
			.minItems(1)
			.maxItems(maxItems)
			.allItemSchema(innerSchema);

		} 

		// no wrapping in array due to cardinality in SHACL, let's check if the context requires an array for this property
		if(
			finalBuilder == null
			&&
			requiresArray
			&&
			// prevent array wrapping if the context requires a language container
			!requiresContainerLanguage
		) {
			finalBuilder = ArraySchema
			.builder()
			.minItems(1)
			.allItemSchema(singleValueBuilder.build());
  		}	

		if(finalBuilder == null) {
   			// this is a single value schema, not wrapped into an ArraySchema
   			finalBuilder = singleValueBuilder;
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
		finalBuilder.title(titleProperty).description(descriptionProperty);

		// nothing, return an EmptySchema
		return finalBuilder.build();

	}

	private Resource findShNode(PropertyShape ps) {
		Optional<Resource> qualifiedValueShapeNode = ps.getShQualifiedValueShape().map(r -> new NodeShape(r)).flatMap(ns -> ns.getShNode());
		if (
				ps.getShNode().isPresent()
				||
				qualifiedValueShapeNode.isPresent()
		) {
			Resource theShNode = ps.getShNode().orElseGet(() -> qualifiedValueShapeNode.orElse(null));
			return theShNode;
		}
		return null;
	}

	private List<Resource> findShNodeInShOr(PropertyShape ps) {
		if(ps.getShOr() != null) {
			List<Resource> shNode = ShOrReadingUtils.readShNodeInShOr(ps.getShOr()).stream().map(r -> r.asResource()).collect(Collectors.toList());
			// filter to keep only references that are found in the current file
			ShapesGraph shapesGraph = new ShapesGraph(ps.getPropertyShape().getModel(), null);
			shNode = shNode.stream()
				.filter(n -> shapesGraph.findNodeShapeByResource(n) != null)
				.collect(Collectors.toList());
			return shNode;
		}
		return new ArrayList<>();
	}


    private List<NodeShape> findRootNodeShapes(List<NodeShape> nodeShapes, List<String> rootShapes) {

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

                // 2. or be explicitely specified as an input
                boolean isExplicitelyRootShape = rootShapes.contains(ns.getNodeShape().getURI());

                return  isExplicitelyRootShape || !isEmbedded;
            }
            
        };

        return nodeShapes.stream().filter(predicate).collect(Collectors.toList());
    }
}
