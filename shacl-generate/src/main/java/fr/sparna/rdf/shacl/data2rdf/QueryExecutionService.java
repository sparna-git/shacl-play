package fr.sparna.rdf.shacl.data2rdf;

import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionBuilder;
import org.apache.jena.query.QueryExecutionDatasetBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTPBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueryExecutionService {

	private static final Logger log = LoggerFactory.getLogger(QueryExecutionService.class);
	
	private Model inputModel;
	private String endpointUrl;

	
	
	public QueryExecutionService(Model inputModel) {
		super();
		this.inputModel = inputModel;
	}
	
	

	public QueryExecutionService(String endpointUrl) {
		super();
		this.endpointUrl = endpointUrl;
	}



	public <R> R executeSelectQuery(String query, QuerySolution bindings, JenaResultSetHandler<R> resultSetHandler) {
		try(QueryExecution queryExecution = this.getQueryExecutionBuilder().query(query).substitution(bindings).build()) {
			ResultSet resultSet = queryExecution.execSelect();
			return resultSetHandler.handle(resultSet);
		}
	}

	public <R> R executeSelectQuery(String query, JenaResultSetHandler<R> resultSetHandler) {
		return this.executeSelectQuery(query, new QuerySolutionMap(), resultSetHandler);
	}
	
	public boolean executeAskQuery(String query, QuerySolution bindings) {
		try(QueryExecution queryExecution = this.getQueryExecutionBuilder().query(query).substitution(bindings).build()) {
			return queryExecution.execAsk();
		}
	}

	public Model executeConstructQuery(String query, QuerySolution bindings) {
		try(QueryExecution queryExecution = this.getQueryExecutionBuilder().query(query).substitution(bindings).build()) {
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
	
	public static QuerySolution buildQuerySolution(String varName, RDFNode value) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add(varName, value);
		return qs;
	}

	private QueryExecutionBuilder getQueryExecutionBuilder() {
		if(this.endpointUrl != null) {
			// here we can customize HTTP headers etc.
			return QueryExecutionHTTPBuilder.service(this.endpointUrl);
		} else {
			return QueryExecutionDatasetBuilder.create().dataset(DatasetFactory.create(this.inputModel));
		}
	}
	
}
