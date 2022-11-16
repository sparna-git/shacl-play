package fr.sparna.rdf.shacl.app.jsonld;

import java.nio.file.Files;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.jsonld.JsonLdContextGenerator;

public class GenerateContext implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsGenerateContext a = (ArgumentsGenerateContext)args;
		
		// read input file or URL
		Model shapesModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModel(shapesModel, a.getInput(), null);
		
		JsonLdContextGenerator contextGenerator = new JsonLdContextGenerator();
		String context = contextGenerator.generateJsonLdContext(shapesModel);

		Files.write(a.getOutput().toPath(), context.getBytes("UTF-8"));
		
	}

}
