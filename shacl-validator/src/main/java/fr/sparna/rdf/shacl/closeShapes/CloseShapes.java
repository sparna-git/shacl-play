package fr.sparna.rdf.shacl.closeShapes;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseShapes {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public Model closeShapes(Model model) {
		
		try {
			// Read query
			ParameterizedSparqlString q = new ParameterizedSparqlString(IOUtils.toString(this.getClass().getResourceAsStream("/fr/sparna/rdf/shacl/closeShapes/CloseShapes.rq"), "UTF-8"));			

			// Execute query
			Query sparqlQuery = q.asQuery();
			try(QueryExecution execution = QueryExecutionFactory.create(sparqlQuery, model)) {
				Model result = execution.execConstruct();
				log.debug("Close shapes added "+ result.size() + " triples.");
				model.add(result);			
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return model;
	}

	
	
}
