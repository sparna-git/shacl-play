package fr.sparna.jsonschema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.jsonschema.model.ArraySchema;
import fr.sparna.jsonschema.model.BooleanSchema;
import fr.sparna.jsonschema.model.CombinedSchema;
import fr.sparna.jsonschema.model.ConstSchema;
import fr.sparna.jsonschema.model.EmptySchema;
import fr.sparna.jsonschema.model.NumberSchema;
import fr.sparna.jsonschema.model.ObjectSchema;
import fr.sparna.jsonschema.model.ReferenceSchema;
import fr.sparna.jsonschema.model.Schema;
import fr.sparna.jsonschema.model.StringSchema;
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


    public JsonSchemaGenerator(String targetContextUrl, List<String> rootShapes) {
        this.targetContextUrl = targetContextUrl;
        this.rootShapes = rootShapes;
        this.uriMapper = new LocalNameUriToJsonMapper();
    }

    public JsonSchemaGenerator(String rootShape) {
        this(null, Collections.singletonList(rootShape));
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
		for (NodeShape ns : shapesGraph.getAllNodeShapes()) {			
			rootSchema.embeddedSchema(
					ns.getNodeShape().getLocalName(),
					// create the object schema from the node shape
					convertNodeShapeToObjectSchema(ns, shaclGraph)
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
			// Title
			if (owl.getTitleOrLabel("en") != null) {
				rootSchema.title(owl.getTitleOrLabel("en"));
			}
			
			// Version
			if (owl.getOwlVersionInfo() != null) {
				rootSchema.version(owl.getOwlVersionInfo());			
			}

			// Description
			if (owl.getDescription("en") != null) {
				rootSchema.description(owl.getDescription("en"));			
			}
			
			// URI of the ontology
			if (owl.getOWLUri() != null) {
				rootSchema.id(owl.getOWLUri().toString()); 
			} 
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
	
	private Schema convertNodeShapeToObjectSchema(
		NodeShape nodeShape,
		Model model
	) throws Exception {
	
		ObjectSchema.Builder objectSchema = ObjectSchema.builder();
		
		// always set an id property, always required
		objectSchema.addPropertySchema("id", StringSchema.builder().format("iri-reference").build() );
		objectSchema.addRequiredProperty("id");
		
		for (PropertyShape ps : nodeShape.getProperties()) {
				
			// Name of Property
            Resource path = ps.getShPath().get().asResource();
			Set<String> shortnames = ShaclReadingUtils.findShortNamesOfPath(path,model);
			if(shortnames.isEmpty()) {
				shortnames.add(uriMapper.mapToJson(path));
			} 
			String term = shortnames.iterator().next();
			if(shortnames.size() > 1) {
                log.warn("Found multiple shortnames for path "+path+", will use only one : '"+term+"'");
			}
		
			objectSchema.addPropertySchema(term, EmptySchema.builder().build());
		
			// NodeKind
            ps.getShNodeKind().filter(nodeKind -> nodeKind.getURI().equals(SH.IRI.getURI())).ifPresent(nodeKind -> {
                if (!ps.getShClass().isPresent() && !ps.getShNode().isPresent()) {
                    objectSchema.addPropertySchema(term, 
                        StringSchema.builder()
                        .format("iri-reference")
                        .build()
                    );
                }
            });		
				
			if (ps.getShDatatype().isPresent()) {				
			
				Set<Resource> datatypes = ShaclReadingUtils.findDatatypesOfPath(ps.getShPath().get().asResource(), model);
				if(datatypes.size() > 1) {
					log.warn("Found different datatypes declared for path "+path+", will declare only one");
				} else if(!datatypes.isEmpty()) {
					Resource theDatatype = datatypes.iterator().next();
					String datatype = theDatatype.getURI();
					if (datatype.equals(RDF.langString.getURI())) {						
						Schema refSchema = ReferenceSchema
								.builder()
								.refValue("#/$defs/"+CONTAINER_LANGUAGE)
								.build();
						
						objectSchema.addPropertySchema(term, refSchema);
						
					} else {
						Optional<JSONSchemaType> typefound = JSONSchemaType.findByDatatypeUri(datatype);
						
						if (typefound.isPresent()) {
							if (typefound.get().getJsonSchemaType().toString().equals("string")) {
								
								objectSchema.addPropertySchema(term, StringSchema
										.builder()
										.format(typefound.get().getJsonSchemaFormat())
										.build()
										);
							} else if (typefound.get().getJsonSchemaType().equals("boolean")) {
								objectSchema.addPropertySchema(term, 
										BooleanSchema
											.builder()											
											.build());
							} else if (typefound.get().getJsonSchemaType().equals("number") || typefound.get().getJsonSchemaType().equals("integer")) {
								objectSchema.addPropertySchema(term, NumberSchema.builder().build());
							}
						} else {
							objectSchema.addPropertySchema(term, StringSchema.builder().build() ); 
						}
						
					}

				}		
			}
		
			// sh:pattern
            ps.getShPattern().ifPresent(pattern -> {
                Schema patternObj = StringSchema
						.builder()
						.pattern(ps.getShPattern().get().toString())
						.build();
				
				objectSchema.addPropertySchema(term, patternObj);
            });
		
			// sh:hasValue	
            ps.getShHasValue().ifPresent(value -> {
                Schema hasValue = ConstSchema.builder()
						.permittedValue(this.uriMapper.mapToJson(ps.getShHasValue().get().asResource()))
						.build();
				objectSchema.addPropertySchema(term, hasValue);
            });
		
			//sh:node
			if (!ps.getShNode().isEmpty()) {				
				
				Schema propertySchema = null;
				if(ps.isEmbedNever()) {
					propertySchema = StringSchema.builder().format("iri-reference").build();
				} else {
					// make sure the NodeShape exists, otherwise the schema is inconsistent
                    propertySchema = ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(ps.getShNode().get().asResource())).build();
				}
				
				if (!ps.getShMaxCount().isPresent() || ps.getShMaxCount().get().asLiteral().getInt() > 1 ) {
					objectSchema.addPropertySchema(term,ArraySchema.builder().minItems(1).allItemSchema(propertySchema).build());
				} else {
					objectSchema.addPropertySchema(term,propertySchema);
				}
			}
				
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
				log.debug("testing if "+ns.getNodeShape().getURI()+" is a root node shape...");
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
                log.debug(ns.getNodeShape().getURI() + " "+isExplicitelyRootShape);

                return  isExplicitelyRootShape || !isEmbedded;
            }
            
        };

        return nodeShapes.stream().filter(predicate).collect(Collectors.toList());
    }
}
