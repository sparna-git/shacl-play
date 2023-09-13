package fr.sparna.rdf.jena;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ModelQueryExecutionService  implements QueryExecutionService {

	private static final Logger log = LoggerFactory.getLogger(ModelQueryExecutionService.class);
	
	private Model inputModel;
	
	public ModelQueryExecutionService(Model inputModel) {
		super();
		this.inputModel = inputModel;
	}	

	public <R> R executeSelectQuery(String query, QuerySolution bindings, JenaResultSetHandler<R> resultSetHandler) {		
		try(QueryExecution queryExecution = this.getQueryExecutionBuilder().query(query).dataset(DatasetGraphFactory.create(this.inputModel.getGraph())).build()) {
			queryExecution.setInitialBinding(bindings);
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
		try(QueryExecution queryExecution = this.getQueryExecutionBuilder().query(query).dataset(DatasetGraphFactory.create(this.inputModel.getGraph())).build()) {
			queryExecution.setInitialBinding(bindings);
			if(log.isDebugEnabled()) {
				log.debug(queryExecution.getQuery().serialize());
			}
			return queryExecution.execAsk();
		}
	}

	public Model executeConstructQuery(String query, QuerySolution bindings) {
		try(QueryExecution queryExecution = this.getQueryExecutionBuilder().query(query).dataset(DatasetGraphFactory.create(this.inputModel.getGraph())).build()) {
			queryExecution.setInitialBinding(bindings);
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

	private QueryExecutionBuilder getQueryExecutionBuilder() {
		return QueryExecutionBuilder.create();
		/*
		if(this.endpointUrl != null) {
			// here we can customize HTTP headers etc.
			// we send Query 
			return QueryExecutionHTTPBuilder.service(this.endpointUrl).useGet();
		} else {
			return QueryExecutionDatasetBuilder.create().dataset(DatasetFactory.create(this.inputModel));
		}
		*/
	}
	
}
