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
import fr.sparna.jsonschema.model.Schema.Builder;
import fr.sparna.jsonschema.model.StringSchema;
import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.NodeShapeReader;
import fr.sparna.rdf.jena.shacl.OwlOntology;
import fr.sparna.rdf.jena.shacl.PropertyShape;
import fr.sparna.rdf.shacl.SHACL_PLAY;

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

    public JsonSchemaGenerator(String targetContextUrl, String rootShape) {
        this(targetContextUrl, Collections.singletonList(rootShape));
    }

    public Schema convertToJsonSchema(Model shaclGraph) throws Exception {
        log.info("Generating JSON schema...");
        List<NodeShape> nodeShapes = readModel(shaclGraph);
		    
		// Create JSON Schema 
        // root schema
		ObjectSchema.Builder rootSchema = ObjectSchema.builder();		
		
        rootSchema.schemaVersion(JSON_SCHEMA_VERSION);

        // Read Ontology
        getRoot(shaclGraph,rootSchema,rootShapes);
        
		// TODO : peupler à partir de l'ontologie
        /*
        rootSchema.title("TA");		
		rootSchema.id("https://data.europarl.europa.eu/def/adopted-texts");
		rootSchema.description("A test JSON schema for adopted texts");
		rootSchema.version("Version Ontology");				
		*/
        
        
		/*
		 * 
		 * Generate Embed Properties
		 * 
		 */
		// Read nodeshape and generate own properties how to json schema
		for (NodeShape ns : nodeShapes) {
			
			boolean flagSHClose = ns.getShClose().isPresent() ? ns.getShClose().get().getBoolean(): false;
			
			rootSchema.embeddedSchema(
					ns.getNodeShape().getLocalName(),
					// Properties
					generatePropertiesFromPropertyShape(ns.getProperties(), shaclGraph, flagSHClose)
					);
		}
		
		// Context Default
		rootSchema.embeddedSchema(CONTEXT, getContext());
		// Default
		rootSchema.embeddedSchema(CONTAINER_LANGUAGE, getContainerLanguage());
		
		
		/*
		 * Generate Properties
		 * 
		 */
		
		// the @context property is present all time
		rootSchema.addPropertySchema(CONTEXT, 
				ReferenceSchema
					.builder()
					.refValue("#/$defs/"+CONTEXT)
					.build());
		
		// find the root node shapes
		List<NodeShape> rootNodeShapes = findRootNodeShapes(nodeShapes);

		Schema dataSchema;
        if(rootNodeShapes.size() > 1) {
            // if there are more than 1, use an AnyOf schema
            List<Schema> anyOfList = new ArrayList<>();
            for (NodeShape nsroot : rootNodeShapes) {
                anyOfList.add(ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(nsroot.getNodeShape())).build());
            }
			dataSchema = ArraySchema.builder().minItems(1).allItemSchema(CombinedSchema.anyOf(anyOfList).build()).build();
        } else if(rootNodeShapes.size() == 1) {
            // if there is only one, use only this one
            Schema singleRootSchema = ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(rootNodeShapes.get(0).getNodeShape())).build();
            dataSchema = ArraySchema.builder().minItems(1).allItemSchema(singleRootSchema).build();
        } else {
			throw new Exception("Could not determine a root schema");
		}

		rootSchema.addPropertySchema("data", dataSchema);
		
		/*
		 * Generate Required Property
		 */		
		rootSchema.addRequiredProperty("data");
		rootSchema.addRequiredProperty(CONTEXT);
		rootSchema.additionalProperties(false);
		
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

    private void getRoot(Model shaclGraph,ObjectSchema.Builder rootSchema, List<String> rootShape) {
    	
    	// Generate OWL
    	OwlOntology owl = getOntology(shaclGraph);
    	
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
		
		/*
		 *  Write URI if EXIST in the ontology, if not get ID in the RootNodeShape
		 */
		
		if (owl.getOWLUri() != null) {
			rootSchema.id(owl.getOWLUri().toString()); 
		} else {
			rootShape
				.stream()
				.map(rs -> rootSchema.id(rs.toString()));
		}
		
	}
	
	private Schema getContext() {
		
		/*
		 *  Get URI of Ontology
		 *  
		 *  if not exist, get URI of NodeShape Header
		 */
		
		return ConstSchema
	    		.builder()
	    		.permittedValue(this.targetContextUrl)
	    		.comment("The fixed context URL")
	    		.build();
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
	
	private Schema generatePropertiesFromPropertyShape(List<PropertyShape> properties, Model model, boolean flagAdditionalProperties) throws Exception {
	
		ObjectSchema.Builder objectSchema = ObjectSchema.builder();
		for (PropertyShape ps : properties) {
				
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
                        // TODO : changer l'appel pour passer l'URI complète du datatype
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
		
			// sh:Pattern
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
		
		// always set an id property
		objectSchema.addPropertySchema("id", StringSchema.builder().format("iri-reference").build() );
		objectSchema.addRequiredProperty("id");
		
		if (flagAdditionalProperties) {
			objectSchema.additionalProperties(false);
		}
		
        return objectSchema.build();
		
	}
	
	private List<NodeShape> readModel(Model shaclGraph) {
		
		// read everything typed as NodeShape
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
				
		// 1. Lire toutes les box
		NodeShapeReader nodeShapeReader = new NodeShapeReader();
		List<NodeShape> sortedNodeShapes = nodeShapes.stream().map(res -> nodeShapeReader.read(res, nodeShapes)).sorted((b1,b2) -> {
			if(b1.getNodeShape().isAnon()) {
				if(!b2.getNodeShape().isAnon()) {
					return b1.getNodeShape().toString().compareTo(b2.getNodeShape().toString());
				}else {
					return -1;
				}
			}else {
				if(!b2.getNodeShape().isAnon()) {
					return 1;
				} else {
					return b1.getLabel().compareTo(b2.getLabel());
				}
			}
		}).collect(Collectors.toList());
		
		for (NodeShape aNodeShape : sortedNodeShapes) {
			aNodeShape.setProperties(nodeShapeReader.readProperties(aNodeShape.getNodeShape()));
		}
		
		return sortedNodeShapes;
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
                            propertyShape.getShNode().filter(r -> r.getURI().equals(nodeShape.getNodeShape().getURI())).isPresent()
                            ||
                            propertyShape.getShClass().filter(r -> {
                                return 
                                r.getURI().equals(nodeShape.getNodeShape().getURI())
                                ||
                                (nodeShape.getTargetClass().isPresent() && r.getURI().equals(nodeShape.getTargetClass().get().getURI()))
                                ;
                            }).isPresent()
                        ) {
                            if(propertyShape.isEmbedNever()) {
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

    private OwlOntology getOntology(Model shaclGraph) {
    	
    	/*
    	 * Lecture de OWL 
    	*/
    	
		// this is tricky, because we can have multiple ones if SHACL is merged with OWL or imports OWL
		List<Resource> sOWL = shaclGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		
		// let's decide first to exclude the ones that are owl:import-ed from others
		List<Resource> owl = sOWL.stream().filter(onto1 -> {
			return !sOWL.stream().anyMatch(onto2 -> onto2.hasProperty(OWL.imports, onto1));
		}).collect(Collectors.toList());
		
		OwlOntology ontologyObject = null;
		if(owl.size() > 0) {
			ontologyObject = new OwlOntology(owl.get(0));
		}
		
		return ontologyObject;
    	
    }
}
