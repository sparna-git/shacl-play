package fr.sparna.jsonschema;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.ext.com.google.common.collect.ImmutableMap;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
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

public class Main {

	
	private static final String SHORTNAME = "https://shacl-play.sparna.fr/ontology#shortname";	
	public static final String JSON_SCHEMA_VERSION = "https://json-schema.org/draft/2020-12/schema";
	
	
	@SuppressWarnings("deprecation")
	public static void main(String... args) throws Exception {   
		
		String shaclFile = args[0];
		
		Model shaclGraph = ModelFactory.createDefaultModel();
		shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));	
		//shaclGraph.write(System.out)
		
		//Model owlGraph = ModelFactory.createDefaultModel();
		List<NodeShape> nodeShapes = readModel(shaclGraph);
		    
	    /*
	     *  JSON Schema
	     */
		
		// Create JSON Schema Empty
		//root
		ObjectSchema.Builder rootSchema = ObjectSchema.builder();
		
		// Test........ get 
		rootSchema.title("TA");
		rootSchema.schemaVersion("https://json-schema.org/draft/2020-12/schema");
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
					ns.getNodeShape().getLocalName()
					,
					// Properties
					generatePropertiesfromPropertyShape(
							// properties
							ns.getProperties(),
							// NodeShape
							ns.getNodeShape(),
							//model
							shaclGraph
							)
					);
		}
		
		// Context Default
		rootSchema.embeddedSchema("@context", getContext());
		// Default
		rootSchema.embeddedSchema("container_language", getContainerLanguage());
		
		
		/*
		 * Generate Properties
		 * 
		 */
		
		// the @context property is present all time
		rootSchema.addPropertySchema("@context", 
				ReferenceSchema
					.builder()
					.refValue("#/$defs/context")
					.build());
		
		// find the root property, is not reference to sh:node or sh:class
		// if is not found, use the URI 
		List<Resource> NodeShapeRoot = getNodeShapeRoot(shaclGraph);
		ReferenceSchema.Builder nodeShapeRef = ReferenceSchema.builder();
		if (NodeShapeRoot.size() > 0) {
			// Add 
			for (Resource nsroot : NodeShapeRoot) {
				nodeShapeRef
						.refValue("#/$defs/"+nsroot.getLocalName())
						.build();
			}
		}

		CombinedSchema.Builder embedData = generateEmbedProperty(nodeShapes,NodeShapeRoot);		
		Schema schemaData = embedData != null ? embedData.build():nodeShapeRef.build();
		if (schemaData != null) {
			rootSchema.addPropertySchema("data", ArraySchema.builder().minItems(1).addItemSchema(schemaData).build());
		}
		
		/*
		 * Generate Required Property
		 */		
		rootSchema.addRequiredProperty("data");
		rootSchema.addRequiredProperty("@context");
		rootSchema.additionalProperties(false);
		
		// Print Output
		System.out.println(rootSchema.build().toString());
		
		
	}
	
	public static Builder<EmptySchema> getRoot(OwlOntology owl) {
		
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

		// Root Schema Resource
		schema.unprocessedProperties(ImmutableMap.of("$schema", "http://json-schema.org/draft-04/schema"));
		
		// Id
		if (owl.getOWLUri() != null) {
			//schema.unprocessedProperties(ImmutableMap.of("$id", owl.getOWLUri().toString()));			
			schema.id(owl.getOWLUri().toString()).getClass(); 
		}
		
		return schema;
	}
	
	public static Schema getContext() {
		
		/*
		 *  Get URI of Ontology
		 *  
		 *  if not exist, get URI of NodeShape Header
		 */
		
		return ConstSchema
	    		.builder()
	    		.permittedValue(JSON_SCHEMA_VERSION)
	    		.comment("would contain always the fixed context URL")
	    		.build();
	}
	
	public static Schema getContainerLanguage() {
		
		Schema containerLanguage = StringSchema.builder() 
				// Declare type object 
				.pattern("^[A-Za-z]{2,3}$")
				.comment("Overly simplified version of the regex. See https://github.com/w3c/wot-thing-description/blob/main/validation/td-json-schema-validation.json for a complete regex")
				.build();
		
	return containerLanguage;
	}
	
	public static CombinedSchema.Builder generateEmbedProperty(List<NodeShape> nodeShapes, List<Resource> NodeShapeRoot) {
		
		
		List<Schema> AnyOfList = new ArrayList<>();
		for (NodeShape ns : nodeShapes) {
			
			List<PropertyShape> embedProperty = ns.getProperties()
					.stream()
					.filter(f -> f.getEmbed().isPresent())
					.collect(Collectors.toList());
		
			
			if (embedProperty.size() > 0) {
				for (PropertyShape ps : embedProperty) {
					AnyOfList.add(ReferenceSchema.builder().refValue("#/$defs/"+ps.getShNode().get().asResource().getLocalName()).build());				
				}
			}
		}
		
		if (AnyOfList.size() > 0) {
			for (Resource r : NodeShapeRoot) {
				AnyOfList.add(ReferenceSchema.builder().refValue("#/$defs/"+r.getLocalName()).build());
			}
		}
		
		return CombinedSchema.anyOf(AnyOfList);
		
	}
	
	public static Schema generatePropertiesfromPropertyShape(List<PropertyShape> properties,Resource nodeShape ,Model model) throws Exception {
	
		ObjectSchema.Builder propertySchema = ObjectSchema.builder();
		for (PropertyShape ps : properties) {
				
			// Name of Property
			Set<String> pathName = findShortNamesOfPath(ps.getShPath().get().asResource(),model);
			String path = "";
			if (pathName.size() > 0) {
				path = pathName.iterator().next();
			} else {
				path = "";
			}
		
			propertySchema.addPropertySchema(path, new StringSchema());
		
			// NodeKind
			if (ps.getShNodeKind().isPresent()) {
				if (ps.getShNodeKind().get().asResource().getLocalName().equals("IRI")) {
					if (ps.getShClass().isPresent() && ps.getShNode().isPresent()) {
						propertySchema.addPropertySchema(path, StringSchema
								.builder()
								.format("iri-reference")
								.build()
								);
					}
				}
			}
				
				
				
			if (ps.getShDatatype().isPresent()) {				
			
				Set<Resource> datatypes = findDatatypesOfPath(ps.getShPath().get().asResource(), model);
				if(datatypes.size() > 1) {
					System.out.println("Found different datatypes declared for path "+path+", will declare only one");
				} else if(!datatypes.isEmpty()) {
					Resource theDatatype = datatypes.iterator().next();
					String datatype = theDatatype.getURI();
					if (datatype.equals(RDF.langString.getURI())) {						
						Schema refSchema = ReferenceSchema
								.builder()
								.refValue("#/$defs/container_language")
								.build();
						
						propertySchema.addPropertySchema(path, refSchema);
						
					} else {
						//
						Optional<JSONSchemaType> typefound = JSONSchemaType.findTyeValue(theDatatype.getLocalName());
						
						if (typefound.isPresent()) {
							if (typefound.get().getType().toString().equals("string")) {
								
								propertySchema.addPropertySchema(path, StringSchema
										.builder()
										//.formatValidator(getFormatDataType(typefound.get().getType().toString()))
										.format(getFormatDataType(theDatatype.getLocalName()))
										.build()
										);
							} else if (typefound.get().getType().equals("boolean")) {
								propertySchema.addPropertySchema(path, 
										BooleanSchema
											.builder()											
											.build());
							} else if (typefound.get().getType().equals("number") || typefound.get().getType().equals("integer")) {
								propertySchema.addPropertySchema(path, NumberSchema.builder().build());
							} else {
								propertySchema.addPropertySchema(path, 
										ObjectSchema
										.builder()
										.format(getFormatDataType(theDatatype.getLocalName()))
										.build());
							}
						}
						
					}

				}		
			}
		
			// sh:Pattern
			if (ps.getShPattern().isPresent()) {

				Schema patternObj = StringSchema
						.builder()
						.pattern(ps.getShPattern().get().toString())
						.build();
				
				propertySchema.addPropertySchema(path, patternObj);
				
			}
		
			// sh:hasValue			
			if (ps.getShHasValue().isPresent()) {
				
				//ps.getShHasValue().get().asResource()
				
				Schema hasValue = ConstSchema.builder()
						.permittedValue(ps.getShHasValue().get().asResource().getLocalName())
						.build();
				propertySchema.addPropertySchema(path, hasValue);
			}
		
			//sh:node
			if (!ps.getShNode().isEmpty()) {
				
				Schema refDefault = ReferenceSchema.builder().refValue("#/$defs/"+ps.getShNode().get().asResource().getLocalName()).build();
				
				// 
				if (ps.getEmbed().isPresent()) {
					if (ps.getEmbed().get().asResource().getLocalName().equals("embedNever")) {
						;
						propertySchema.addPropertySchema(path,StringSchema.builder().format("iri-reference").build());
					}
				} else {
					if (ps.getShMaxCount().isPresent() && ps.getShMaxCount().get().asLiteral().getInt() > 1 ) {
						propertySchema.addPropertySchema(path,ArraySchema.builder().minItems(1)
							.addItemSchema(refDefault).build());
					}
				}					
			}
				
			if (ps.getShMinCount().isPresent()) {
				if (ps.getShMinCount().get().getInt() > 0) {
					propertySchema.addRequiredProperty(path);
				}
			}
			
		}			
		
		
		propertySchema.addPropertySchema("id", StringSchema.builder().format("iri-reference").build() );
		propertySchema.addRequiredProperty("id");
		
		propertySchema.additionalProperties(false);
				
		return propertySchema.build();
		
	}
	
	public static List<NodeShape> readModel(Model shaclGraph) {
		
		// read everything typed as NodeShape
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
				
		// 1. Lire toutes les box
		NodeShapeReader nodeShapeReader = new NodeShapeReader();
		List<NodeShape> Boxes = nodeShapes.stream().map(res -> nodeShapeReader.read(res, nodeShapes)).sorted((b1,b2) -> {
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
		
		for (NodeShape aBox : Boxes) {
			aBox.setProperties(nodeShapeReader.readProperties(aBox.getNodeShape()));
		}
		
		return Boxes;
	}	

	public static List<Resource> getNodeShapeRoot(Model model) {
		
		List<Resource> nodeShapes = model.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
		List<Resource> properties = model.listSubjectsWithProperty(SH.path).toList();
		
		//
		List<Resource> ns = new ArrayList<>();
		for (Resource p : properties) {
			if (p.hasProperty(SH.node)) {
				List<Resource> nsFound = nodeShapes
						.stream()
						.filter(f -> f.getLocalName().equals(p.getProperty(SH.node).getResource().getLocalName()))
						.collect(Collectors.toList());
				
				ns.addAll(nsFound);
			}
			
			if (p.hasProperty(SH.class_)) {
				List<Resource> classFound = nodeShapes
						.stream()
						.filter(f -> f.getLocalName().equals(p.getProperty(SH.class_).getResource().getLocalName()))
						.collect(Collectors.toList());
				
				ns.addAll(classFound);
			}
		}
		
		Set<String> nodeShapeReference = ns.stream().map(n -> n.getLocalName()).collect(Collectors.toSet());
		List<Resource> nodeRoot = nodeShapes
			.stream()
			.filter(n -> !nodeShapeReference.contains(n.getLocalName()))
			.collect(Collectors.toList());
		
		
		
		return nodeRoot;
	}
	
	// read properties	
	private static Set<String> findShortNamesOfPath(Resource path, Model model) {
		List<Resource> propertyShapesWithPath = findPropertyShapesWithPath(path, model);
		Set<String> shortnames = new HashSet<>();
		for (Resource resource : propertyShapesWithPath) {
			// read shortname constraint
			shortnames.addAll(readDatatypeProperty(resource, model.createProperty(SHORTNAME)).stream().map(l -> l.getString()).collect(Collectors.toSet()));
		}
		
		if (shortnames.size() == 0) {
			
		}
		
		
		
		return shortnames;		
	}
	
	private static List<Literal> readDatatypeProperty(Resource r, Property p) {
		return r.listProperties(p).toList().stream().map(s -> s.getObject()).filter(n -> n.isLiteral()).map(n -> n.asLiteral()).collect(Collectors.toList());
	}
		
	private static Set<Resource> findDatatypesOfPath(Resource path, Model model) {
		List<Resource> propertyShapesWithPath = findPropertyShapesWithPath(path, model);
		Set<Resource> datatypes = new HashSet<>();
		for (Resource resource : propertyShapesWithPath) {
			// read sh:datatype constraint
			datatypes.addAll(readObjectProperty(resource, SH.datatype));
		}
		return datatypes;		
	}
	
	private static List<Resource> findPropertyShapesWithPath(Resource path, Model model) {
		return model.listSubjectsWithProperty(SH.path, path).toList();
	}
	
	private static List<Resource> readObjectProperty(Resource r, Property p) {
		return r.listProperties(p).toList().stream().map(s -> s.getObject()).filter(n -> n.isResource()).map(n -> n.asResource()).collect(Collectors.toList());
	}

	private static String getFormatDataType(String dataType) {		
		
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

}
