package fr.sparna.rdf.jena;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.List;

public interface QueryExecutionService {

	public <R> R executeSelectQuery(String query, QuerySolution bindings, JenaResultSetHandler<R> resultSetHandler);

	public <R> R executeSelectQuery(String query, JenaResultSetHandler<R> resultSetHandler) ;
	
	public boolean executeAskQuery(String query, QuerySolution bindings);
	
	public Model executeConstructQuery(String query, QuerySolution bindings) ;
	
	public Model executeConstructQuery(String query);

	public static String buildQueryWithBindings(String sparql, QuerySolution bindings) {
		if(bindings == null || !bindings.varNames().hasNext()) {
			return sparql;
		}
		QuerySolutionMap qsm = new QuerySolutionMap();
		qsm.addAll(bindings);
		ParameterizedSparqlString pss = new ParameterizedSparqlString(sparql, qsm);
		return pss.toString();
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
