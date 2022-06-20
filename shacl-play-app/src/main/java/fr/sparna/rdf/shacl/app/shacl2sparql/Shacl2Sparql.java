package fr.sparna.rdf.shacl.app.shacl2sparql;

import java.io.File;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.sparqlgen.SparqlGenerator;

public class Shacl2Sparql implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsShacl2Sparql a = (ArgumentsShacl2Sparql)args;
		
		File outputDir = null;
		if(a.getOutput() == null) {
			String home=System.getProperty("user.home");
			outputDir = new File(home+"/Downloads/");
		} else {
			outputDir = a.getOutput();
		}
		
		//first model
		Model iModel = ModelFactory.createDefaultModel();
		InputModelReader.populateModel(iModel, a.getInput(), null);
		
		//first model
		Model itofModel = null;
		if(a.getTargetsOverrideFile() != null) {
			Model tof = ModelFactory.createDefaultModel();
			InputModelReader.populateModel(tof, a.getTargetsOverrideFile(), null);

			itofModel = ModelFactory.createDefaultModel();
			itofModel.add(tof);
		}
		
		SparqlGenerator generator = new SparqlGenerator(outputDir);
		generator.generateSparql(iModel, itofModel, a.getType());
		
	}

	
	
}
