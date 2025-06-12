package fr.sparna.jsonschema;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

import org.json.JSONObject;

import fr.sparna.jsonschema.model.Schema;

public class Main {	
	
	public static void main(String... args) throws Exception {   
		
		String shaclFile = args[0];
		
		Model shaclGraph = ModelFactory.createDefaultModel();
		shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));	
		
		List<String> uris = new ArrayList<>();
		uris.add("https://data.europarl.europa.eu/def/adopted-texts#AdoptedText");
		
		JsonSchemaGenerator generator = new JsonSchemaGenerator(
			"en",uris);
		Schema output = generator.convertToJsonSchema(shaclGraph);
		
		// Print Output
		JSONObject o = new JSONObject(output.toString());
		System.out.println(o.toString(2));
	}

}
