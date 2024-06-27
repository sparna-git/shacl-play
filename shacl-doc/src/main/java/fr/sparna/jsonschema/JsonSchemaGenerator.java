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
		
		// TODO : peupler à partir de l'ontologie
        rootSchema.schemaVersion(JSON_SCHEMA_VERSION);
		rootSchema.title("TA");		
		rootSchema.id("https://data.europarl.europa.eu/def/adopted-texts");
		rootSchema.description("A test JSON schema for adopted texts");
		rootSchema.version("Version Ontology");				
		
		/*
		 * 
		 * Generate Embed Properties
		 * 
		 */
		// Read nodeshape and generae own properties how to json schema
		for (NodeShape ns : nodeShapes) {
			
			rootSchema.embeddedSchema(
					ns.getNodeShape().getLocalName(),
					// Properties
					generatePropertiesFromPropertyShape(ns.getProperties())
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

        if(rootNodeShapes.size() > 1) {
            // if there are more than 1, use an AnyOf schema
            List<Schema> AnyOfList = new ArrayList<>();
            for (NodeShape nsroot : rootNodeShapes) {
                AnyOfList.add(ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(nsroot.getNodeShape())).build());
            }
            rootSchema.addPropertySchema("data", ArraySchema.builder().minItems(1).addItemSchema(CombinedSchema.anyOf(AnyOfList).build()).build());
        } else if(rootNodeShapes.size() == 1) {
            // if there is only one, use only this one
            Schema singleRootSchema = ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(rootNodeShapes.get(0).getNodeShape())).build();
            rootSchema.addPropertySchema("data", ArraySchema.builder().minItems(1).allItemSchema(singleRootSchema).build());
        }
		
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

    private Builder<EmptySchema> getRoot(OwlOntology owl) {
		
		Builder<EmptySchema> schema = EmptySchema.builder();
		
		// Title
		if (owl.getTitleOrLabel("en") != null) {
			schema.title(owl.getTitleOrLabel("en"));
		}
		
		// Version
		if (owl.getOwlVersionInfo() != null) {
			schema.version(owl.getOwlVersionInfo());			
		}

		// Description
		if (owl.getDescription("en") != null) {
			schema.description(owl.getDescription("en"));			
		}
		
		// Id
		if (owl.getOWLUri() != null) {
			//schema.unprocessedProperties(ImmutableMap.of("$id", owl.getOWLUri().toString()));			
			schema.id(owl.getOWLUri().toString()).getClass(); 
		}
		
		return schema;
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
	    		.comment("would contain always the fixed context URL")
	    		.build();
	}
	
	private Schema getContainerLanguage() {
		
        // TODO : c'est faux, il faut utiliser un "patternProperties"
		Schema containerLanguage = StringSchema.builder() 
				// Declare type object 
				.pattern("^[A-Za-z]{2,3}$")
				.comment("Overly simplified version of the regex. See https://github.com/w3c/wot-thing-description/blob/main/validation/td-json-schema-validation.json for a complete regex")
				.build();
		
	    return containerLanguage;
	}
	
    /*
	private CombinedSchema.Builder generateEmbedProperty(List<NodeShape> nodeShapes, List<Resource> NodeShapeRoot) {
		
		
		List<Schema> AnyOfList = new ArrayList<>();
		for (NodeShape ns : nodeShapes) {
			
			List<PropertyShape> embedProperty = ns.getProperties()
					.stream()
					.filter(f -> f.getEmbed().isPresent())
					.collect(Collectors.toList());
		
			
			if (embedProperty.size() > 0) {
				for (PropertyShape ps : embedProperty) {
					AnyOfList.add(ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(ps.getShNode().get().asResource())).build());				
				}
			}
		}
		
		if (AnyOfList.size() > 0) {
			for (Resource r : NodeShapeRoot) {
				AnyOfList.add(ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(r)).build());
			}
		}
		
		return CombinedSchema.anyOf(AnyOfList);
		
	}
         */
	
	private Schema generatePropertiesFromPropertyShape(List<PropertyShape> properties) throws Exception {
	
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
						Optional<JSONSchemaType> typefound = JSONSchemaType.findByDatatypeUri(theDatatype.getLocalName());
						
						if (typefound.isPresent()) {
							if (typefound.get().getType().toString().equals("string")) {
								
								objectSchema.addPropertySchema(term, StringSchema
										.builder()
										.format(typefound.get().getFormat())
										.build()
										);
							} else if (typefound.get().getType().equals("boolean")) {
								objectSchema.addPropertySchema(term, 
										BooleanSchema
											.builder()											
											.build());
							} else if (typefound.get().getType().equals("number") || typefound.get().getType().equals("integer")) {
								objectSchema.addPropertySchema(term, NumberSchema.builder().build());
							} else {
								objectSchema.addPropertySchema(term, 
										ObjectSchema
										.builder()
										.format(typefound.get().getFormat())
										.build());
							}
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
				
				// TODO : ce n'est pas bon, on peut aussi avoir le cas d'un Array avec EmbedNever qui n'est pas pris en compte ici
				if (ps.getEmbed().isPresent()) {
					if (ps.getEmbed().get().getURI().equals(SHACL_PLAY.EMBED_NEVER)) {
						objectSchema.addPropertySchema(term,StringSchema.builder().format("iri-reference").build());
					}
				} else {
                    Schema refDefault = ReferenceSchema.builder().refValue(JsonSchemaGenerator.buildSchemaReference(ps.getShNode().get().asResource())).build();
					if (ps.getShMaxCount().isPresent() && ps.getShMaxCount().get().asLiteral().getInt() > 1 ) {
						objectSchema.addPropertySchema(term,ArraySchema.builder().minItems(1)
							.addItemSchema(refDefault).build());
					}
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
		
        // TODO : do that depending on closed / not closed
		objectSchema.additionalProperties(false);
				
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

    /*
	private String getFormatDataType(String dataType) {		
		
		if (dataType.equals("date")) {
			return "date";
		} else if (dataType.equals("dateTime")) {
			return "date-time";
		} else if (dataType.equals("time")) {
			return "time";
		} else if (dataType.equals("iri")) {
			//return FormatValidator.forFormat(new URIFormatValidator().formatName());
			return "iri";
		// Resource
		} else if (dataType.equals("iri-reference")) {
			return "iri-reference";
		} else if (dataType.equals("regex")) {
			//return FormatValidator.forFormat("iri");
			return "iri-reference";
		} else {
			return null;
		}
	}
         */

}
