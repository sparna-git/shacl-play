package fr.sparna.rdf.shacl.app.jsonld;

import java.nio.file.Files;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.WatchService.watchFile;
import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.jsonld.JsonLdContextGenerator;

public class GenerateContext implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private ArgumentsGenerateContext a;
	
	@Override
	public void execute(Object args) throws Exception {
		this.a = (ArgumentsGenerateContext)args;
		
		if (this.a.getWatch()) {
			//
			this.generateJsonLd();
			System.out.println("The file :" + this.a.getOutput() + " was generated.");
			// WatchService 
			watchFile wf = new watchFile(a.getInput().get(0), () -> {
				try {
					this.generateJsonLd();
				} catch (Exception e) {
					log.error("Error regenerating documentation on file change", e);
				}
			});
			wf.runWatchFile();
		} else {
			this.generateJsonLd();
		}		
	}

	public void generateJsonLd() throws Exception {

		// read input file or URL
		Model shapesModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(shapesModel, a.getInput(), null);
		
		JsonLdContextGenerator contextGenerator = new JsonLdContextGenerator();
		String context = contextGenerator.generateJsonLdContext(shapesModel);

		Files.write(a.getOutput().toPath(), context.getBytes("UTF-8"));

	}

}
