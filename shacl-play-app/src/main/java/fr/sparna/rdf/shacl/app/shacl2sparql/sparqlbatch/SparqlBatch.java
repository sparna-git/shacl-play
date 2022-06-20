package fr.sparna.rdf.shacl.app.shacl2sparql.sparqlbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.sparqlgen.SparqlBatchRunner;

public class SparqlBatch implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsSparqlBatch a = (ArgumentsSparqlBatch)args;
		SparqlBatchRunner generator = new SparqlBatchRunner(a.getOutput());
		generator.generateSparqlResult(a.getDir(), a.getSparqlEndpoint(), a.getPrefix());
		
	}

}
