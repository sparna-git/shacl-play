package fr.sparna.rdf.shacl.app.shacl2sparql;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.sparqlgen.SparqlGenerator;
import fr.sparna.rdf.shacl.sparqlgen.SparqlGeneratorFileOutputListener;

public class Shacl2Sparql implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsShacl2Sparql a = (ArgumentsShacl2Sparql)args;
		
		//first model
		Model iModel = ModelFactory.createDefaultModel();
		InputModelReader.populateModelFromFile(iModel, a.getInput(), null);
		
		//first model
		Model itofModel = null;
		if(a.getTargetsOverrideFile() != null) {
			Model tof = ModelFactory.createDefaultModel();
			InputModelReader.populateModelFromFile(tof, a.getTargetsOverrideFile(), null);

			itofModel = ModelFactory.createDefaultModel();
			itofModel.add(tof);
		}
		
		SparqlGeneratorFileOutputListener outputListener = new SparqlGeneratorFileOutputListener(a.getOutput());
		SparqlGenerator generator = new SparqlGenerator(outputListener);
		generator.generateSparql(iModel, itofModel, a.isUnionQuery());
		
	}

	
	
}
