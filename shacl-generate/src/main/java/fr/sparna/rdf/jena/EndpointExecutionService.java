package fr.sparna.rdf.jena;

import java.util.List;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EndpointExecutionService implements QueryExecutionService {

	private static final Logger log = LoggerFactory.getLogger(EndpointExecutionService.class);
	
	private String endpointUrl;	

	public EndpointExecutionService(String endpointUrl) {
		super();
		this.endpointUrl = endpointUrl;
	}

	public <R> R executeSelectQuery(String query, QuerySolution bindings, JenaResultSetHandler<R> resultSetHandler) {
		QueryExecution queryExecution = QueryExecutionBuilder.create().query(query).build();
		queryExecution.setInitialBinding(bindings);
		String sparqlString = queryExecution.getQuery().serialize();
		
		// TODO
		return null;
	}

	public <R> R executeSelectQuery(String query, JenaResultSetHandler<R> resultSetHandler) {
		return this.executeSelectQuery(query, new QuerySolutionMap(), resultSetHandler);
	}
	
	public boolean executeAskQuery(String query, QuerySolution bindings) {
		QueryExecution queryExecution = QueryExecutionBuilder.create().query(query).build();
		queryExecution.setInitialBinding(bindings);
		String sparqlString = queryExecution.getQuery().serialize();
		
		// TODO
		return false;
	}

	public Model executeConstructQuery(String query, QuerySolution bindings) {
		QueryExecution queryExecution = QueryExecutionBuilder.create().query(query).build();
		queryExecution.setInitialBinding(bindings);
		String sparqlString = queryExecution.getQuery().serialize();
		
		// TODO
		return null;
	}
	
	public Model executeConstructQuery(String query) {
	    return executeConstructQuery(query, new QuerySolutionMap());
	  }
	
	public static QuerySolution buildQuerySolution(String varName, RDFNode value) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add(varName, value);
		return qs;
	}
	
	public static QuerySolution buildQuerySolution(String varName, List<RDFNode> values) {
		QuerySolutionMap qs = new QuerySolutionMap();
		values.stream().forEach(v -> qs.add(varName, v));		
		return qs;
	}
	
}
