package fr.sparna.jsonschema;

import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.jena.ext.com.google.common.collect.ImmutableMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

import fr.sparna.jsonschema.model.BooleanSchema;
import fr.sparna.jsonschema.model.ConstSchema;
import fr.sparna.jsonschema.model.EmptySchema;
import fr.sparna.jsonschema.model.NumberSchema;
import fr.sparna.jsonschema.model.ObjectSchema;
import fr.sparna.jsonschema.model.Schema;
import fr.sparna.jsonschema.model.Schema.Builder;
import fr.sparna.jsonschema.model.StringSchema;
import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.NodeShapeReader;
import fr.sparna.rdf.jena.shacl.OwlOntology;
import fr.sparna.rdf.jena.shacl.PropertyShape;
import fr.sparna.rdf.shacl.SH;

public class Main {
	
	@SuppressWarnings("deprecation")
	public static void main(String... args) throws Exception {   
		
		String shaclFile = args[0];
//		String shaclFile = "C:\\tmp\\shacl-play\\jsonSchema\\source\\eli-ep_adopted-texts.ttl";
		
		Model shaclGraph = ModelFactory.createDefaultModel();
		shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));	
		//shaclGraph.write(System.out)
		
		//Model owlGraph = ModelFactory.createDefaultModel();
		List<NodeShape> graph = readModel(shaclGraph);
		
		// required
		Schema data = new StringSchema();
		Schema context = new StringSchema();
	
	    
	    // JSON Schema
		
		// Create JSON Schema Empty
		//root
		ObjectSchema.Builder rootStructure = ObjectSchema.builder();
		rootStructure.title("TA");
		rootStructure.description("A test JSON schema for adopted texts");
		rootStructure.version("Version Ontology");
		rootStructure.unprocessedProperties(ImmutableMap.of("$schema", "http://example.org/nonexistent.json"));
		rootStructure.unprocessedProperties(ImmutableMap.of("$id", "https://data.europarl.europa.eu/def/adopted-texts"));
				
		//getRoot(graph);
		//rootStructure.build().getCustomsSchemas().get("schema");
		
		// Context 
		rootStructure.embeddedSchema("context", getContext());
		Schema contextData = getContext();
		
		rootStructure.embeddedSchema("container_language", getContainerLanguage());
		Schema container = getContainerLanguage();
		
		// Read nodeshape
		for (NodeShape ns : graph) {
			
			rootStructure.embeddedSchema(
					ns.getNodeShape().getLocalName()
					,
					// Properties
					generatePropertiesfromPropertyShape(ns.getProperties())
					//new StringSchema()
					);
		}
		
		// Create Property Object
		//rootStructure.
		/*
		.addPropertySchema("@context", ReferenceSchema.builder()
				.refValue("#/$defs/context")
				.build()
				);
		rootStructure.embeddedSchema()
		*/
		rootStructure.additionalProperties(false);
		
		System.out.println(rootStructure.build().toString());
		
		
		rootStructure.build().getClass().getResource("TestFormat");
		
		
		Schema testDemo = ObjectSchema.builder()
	            .addPropertySchema("numberProp", new NumberSchema())
	            .patternProperty("^string.*", new StringSchema())
	            .addPropertySchema("boolProp", BooleanSchema.INSTANCE)
	            .addRequiredProperty("boolProp")
	            .build();
		//System.out.println(testDemo.toString());
		
		
		Callable<ObjectSchema.Builder> newBuilder = () -> ObjectSchema.builder()
                .addPropertySchema("numberProp", new NumberSchema())
                .patternProperty("^string.*", new StringSchema())
                .addPropertySchema("boolProp", BooleanSchema.INSTANCE)
                .addRequiredProperty("boolProp");

        Schema nested2 = newBuilder.call().build();
        Schema nested1 = newBuilder.call().addPropertySchema("nested", nested2).build();
        Schema subject = newBuilder.call().addPropertySchema("nested", nested1).build();
        //System.out.println(subject.toString());
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
		
		return EmptySchema
	    		.builder()
	    		.comment("would contain always the fixed context URL")
	    		.unprocessedProperties(ImmutableMap.of("cons", "http://json-schema.org/draft-04/schema"))	    		
	    		.build();
	}
	
	public static Schema getContainerLanguage() {
		
		Schema containerLanguage = ObjectSchema.builder() 
				// Declare type object 
				.patternProperty("^[A-Za-z]{2,3}$", 
						StringSchema
						.builder()
						.comment("Overly simplified version of the regex. See https://github.com/w3c/wot-thing-description/blob/main/validation/td-json-schema-validation.json for a complete regex")
						.build())
				.additionalProperties(false)
				.build();
		
	return containerLanguage;
	}
	
	public static Schema generatePropertiesfromPropertyShape(List<PropertyShape> properties) throws Exception {
	
		ObjectSchema.Builder propertiesOutput = ObjectSchema.builder(); 
		ObjectSchema.Builder obj = ObjectSchema.builder();
		
		
		for (PropertyShape ps : properties) {
				
			// Name of Property
			obj.addPropertySchema(ps.getPathAsSparql(), new StringSchema());
			
			
			if (ps.getShDatatype().isPresent()) {				
				Optional<JSONTYPE> typefound = JSONTYPE.findTyeValue(ps.getShDatatype().get().getLocalName().toString());
				
				/*
				Schema datatype = ObjectSchema.builder()
						.expectedKeyword(typefound.get().getType().toString())
						.build();
						 */
				// Add Data Type
				//obj.addPropertySchema(ps.getPathAsSparql(), datatype);
				// Add Format
				if (!typefound.get().getType().toString().equals(ps.getShDatatype().get().getLocalName().toString())) {
					
					
				}
				
			}
			
			// sh:Pattern
			if (ps.getShPattern().isPresent()) {

				Schema patternObj = ObjectSchema.builder()
						.patternProperty(ps.getShPattern().get().toString(),StringSchema.builder().build())
						.build();
				
				obj.addPropertySchema(ps.getPathAsSparql(), patternObj);
				
			}
			
			// sh:hasValue
			
			if (ps.getShHasValue().isPresent()) {
				
				Schema hasValue = ConstSchema.builder()
						.permittedValue(ps.getShHasValue().get().toString())
						.build();
				
				obj.addPropertySchema(ps.getPathAsSparql(), hasValue);
			}
			

		}
				
		return obj.build();
		
	}
	
	public static List<NodeShape> readModel(Model shaclGraph) {
		
		// read everything typed as NodeShape
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
				
		// also read everything object of an sh:node or sh:qualifiedValueShape, 
		//that maybe does not have an explicit rdf:type sh:NodeShape
		/*
		List<RDFNode> nodesAndQualifedValueShapesValues = shaclGraph.listStatements(null, SH.node, (RDFNode)null)
				.andThen(shaclGraph.listStatements(null, SH.qualifiedValueShape, (RDFNode)null))
				.toList().stream()
				.map(s -> s.getObject())
				.collect(Collectors.toList());
		*/
			
		// add those to our list
		/*
		for (RDFNode n : nodesAndQualifedValueShapesValues) {
			if(n.isResource() && !nodeShapes.contains(n)) {
				nodeShapes.add(n.asResource());
			}
		}
		*/
				
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
}
