package fr.sparna.rdf.jena;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class QueryExecutionRemoteService implements QueryExecutionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutionRemoteService.class);

    private URI endpoint;

    public QueryExecutionRemoteService(URI endpoint) {
        super();
        this.endpoint = endpoint;
    }

    @Override
    public <R> R executeSelectQuery(String query, QuerySolution bindings, JenaResultSetHandler<R> resultSetHandler) {
        try(QueryExecution queryExecution = QueryExecution.service(this.endpoint.toString(), query)) {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug(queryExecution.getQuery().serialize());
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
        try(QueryExecution queryExecution = QueryExecution.service(this.endpoint.toString(), QueryExecutionService.buildQueryWithBindings(query, bindings))) {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug(queryExecution.getQuery().serialize());
            }
            return queryExecution.execAsk();
        }
    }

    @Override
    public Model executeConstructQuery(String query, QuerySolution bindings) {
        try(QueryExecution queryExecution = QueryExecution.service(this.endpoint.toString(), QueryExecutionService.buildQueryWithBindings(query, bindings))) {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug(queryExecution.getQuery().serialize());
            }
            return queryExecution.execConstruct();
        }
        catch (Exception e) {
            LOGGER.error("Query failed: \n{}", query);
            throw e;
        }
    }

    @Override
    public Model executeConstructQuery(String query) {
        return executeConstructQuery(query, new QuerySolutionMap());
    }
}
