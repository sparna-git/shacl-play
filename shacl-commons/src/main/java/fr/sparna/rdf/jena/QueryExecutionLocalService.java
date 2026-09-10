package fr.sparna.rdf.jena;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueryExecutionLocalService implements QueryExecutionService {

	private static final Logger log = LoggerFactory.getLogger(QueryExecutionLocalService.class);
	
	private Model inputModel;

	public QueryExecutionLocalService(Model inputModel) {
		super();
		this.inputModel = inputModel;
	}

	@Override
	public <R> R executeSelectQuery(String query, QuerySolution bindings, JenaResultSetHandler<R> resultSetHandler) {			
		try(QueryExecution queryExecution = QueryExecution.create(query, this.inputModel)) {
			if(log.isDebugEnabled()) {
				log.debug(queryExecution.getQuery().serialize());
			}
			ResultSet resultSet = queryExecution.execSelect();
			return resultSetHandler.handle(resultSet);
		}
	}

	@Override
	public <R> R executeSelectQuery(String query, JenaResultSetHandler<R> resultSetHandler) {
		return this.executeSelectQuery(query, new QuerySolutionMap(), resultSetHandler);
	}

	@Override
	public boolean executeAskQuery(String query, QuerySolution bindings) {	
		try(QueryExecution queryExecution = QueryExecution.create(QueryExecutionService.buildQueryWithBindings(query, bindings), this.inputModel)) {
			if(log.isDebugEnabled()) {
				log.debug(queryExecution.getQuery().serialize());
			}
			return queryExecution.execAsk();
		}
	}

	@Override
	public Model executeConstructQuery(String query, QuerySolution bindings) {		
		try(QueryExecution queryExecution = QueryExecution.create(QueryExecutionService.buildQueryWithBindings(query, bindings), this.inputModel)) {
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

	@Override
	public Model executeConstructQuery(String query) {
	    return executeConstructQuery(query, new QuerySolutionMap());
	  }


}
