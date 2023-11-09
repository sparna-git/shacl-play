package fr.sparna.rdf.jena;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueryExecutionServiceImpl  implements QueryExecutionService {

	private static final Logger log = LoggerFactory.getLogger(QueryExecutionServiceImpl.class);
	
	private Model inputModel;
	
	private String endpointUrl;
	
	public QueryExecutionServiceImpl(Model inputModel) {
		super();
		this.inputModel = inputModel;
	}	
	
	public QueryExecutionServiceImpl(String endpointUrl) {
		super();
		this.endpointUrl = endpointUrl;
	}	

	public <R> R executeSelectQuery(String query, QuerySolution bindings, JenaResultSetHandler<R> resultSetHandler) {			
		try(QueryExecution queryExecution = this.getQueryExecution(QueryExecutionServiceImpl.buildQueryWithBindings(query, bindings))) {
			if(log.isDebugEnabled()) {
				log.debug(queryExecution.getQuery().serialize());
			}
			ResultSet resultSet = queryExecution.execSelect();
			return resultSetHandler.handle(resultSet);
		}
	}

	public <R> R executeSelectQuery(String query, JenaResultSetHandler<R> resultSetHandler) {
		return this.executeSelectQuery(query, new QuerySolutionMap(), resultSetHandler);
	}
	
	public boolean executeAskQuery(String query, QuerySolution bindings) {	
		try(QueryExecution queryExecution = this.getQueryExecution(QueryExecutionServiceImpl.buildQueryWithBindings(query, bindings))) {
			if(log.isDebugEnabled()) {
				log.debug(queryExecution.getQuery().serialize());
			}
			return queryExecution.execAsk();
		}
	}

	public Model executeConstructQuery(String query, QuerySolution bindings) {		
		try(QueryExecution queryExecution = this.getQueryExecution(QueryExecutionServiceImpl.buildQueryWithBindings(query, bindings))) {
			if(log.isDebugEnabled()) {
				log.debug(queryExecution.getQuery().serialize());
			}
			return queryExecution.execConstruct();
		}
		catch (Exception e) {
			log.error("Query failed: \n{}", query);
			throw e;
		}
	}
	
	public Model executeConstructQuery(String query) {
	    return executeConstructQuery(query, new QuerySolutionMap());
	  }

	private QueryExecution getQueryExecution(String sparql) {

		if(this.endpointUrl != null) {
			// here we can customize HTTP headers etc.
			
		    HttpClient httpClient = HttpClientBuilder
		    	      .create()
		    	      .setRetryHandler(new DefaultHttpRequestRetryHandler(0, true))
		    	      .build();
			
			return QueryExecutionFactory.sparqlService(this.endpointUrl, sparql, httpClient);
		} else {
			return QueryExecutionFactory.create(sparql, inputModel);
		}

	}
	
	private static String buildQueryWithBindings(String sparql, QuerySolution bindings) {
		if(bindings == null || !bindings.varNames().hasNext()) {
			return sparql;
		}
		
		QuerySolutionMap qsm = new QuerySolutionMap();
		qsm.addAll(bindings);
		ParameterizedSparqlString pss = new ParameterizedSparqlString(sparql, qsm);
		return pss.toString();
	}
	
}
