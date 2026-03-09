package fr.sparna.rdf.shacl.app.jsonSchema;

import java.io.StringReader;
import java.nio.file.Files;
import java.security.InvalidParameterException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.jsonschema.JsonSchemaGenerator;
import fr.sparna.rdf.shacl.jsonschema.model.Schema;
import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;


public class GenerateJsonSchema implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsJsonSchema a = (ArgumentsJsonSchema)args;
		
		// read input file or URL
		Model shapesModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(shapesModel, a.getInput(), null);	
		
		// read optional context
		JsonValue context = null;
		if(a.getContextFile() != null) {
			if(!a.getContextFile().exists()) {
				throw new InvalidParameterException("Provided context file does not exist : "+a.getContextFile().getAbsolutePath());
			} else {
				// read JSON context as a JsonValue
				String contextJson = Files.readString(a.getContextFile().toPath());
            	try (JsonReader reader = Json.createReader(new StringReader(contextJson))) {
                	context = reader.readValue();
            	}
			}
		}
		
		// build generator with context that can be null
		JsonSchemaGenerator generator = new JsonSchemaGenerator("en", context);

		// convert the shacl shapes to json schema
		Schema output = generator.convertToJsonSchema(shapesModel, a.getNodeShapes());
		JSONObject jsonSchemaOutput = new JSONObject(output.toString());
		
		Files.write(a.getOutput().toPath(), jsonSchemaOutput.toString(2).getBytes());
		
	}

}
