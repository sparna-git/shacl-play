package fr.sparna.rdf.shacl.app.jsonSchema;

import java.nio.file.Files;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.jsonschema.JsonSchemaGenerator;
import fr.sparna.rdf.shacl.jsonschema.model.Schema;
import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.jsonld.JsonLdContextGenerator;

public class GenerateJsonSchema implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsJsonSchema a = (ArgumentsJsonSchema)args;
		
		// read input file or URL
		Model shapesModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(shapesModel, a.getInput(), null);
		
		
		JsonSchemaGenerator generator = new JsonSchemaGenerator("en", a.getNodeShapes());
		
		// convert the shacl shapes to json schema
		Schema output = generator.convertToJsonSchema(shapesModel);
		JSONObject jsonSchemaOutput = new JSONObject(output.toString());
		
		Files.write(a.getOutput().toPath(), jsonSchemaOutput.toString(2).getBytes());
		
	}

}
