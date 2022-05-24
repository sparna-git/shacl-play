package fr.sparna.rdf.shacl.app.shacl2sparql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.sparqlgen.SparqlGenerator;

public class Shacl2Sparql implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsShacl2Sparql a = (ArgumentsShacl2Sparql)args;
		SparqlGenerator generator = new SparqlGenerator(a.getOutput());
		generator.generateSparql(a.getInput(), a.getTargetsOverrideFile());
		
	}

	
	
}
