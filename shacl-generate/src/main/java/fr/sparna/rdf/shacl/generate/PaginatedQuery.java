package fr.sparna.rdf.shacl.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.JenaResultSetHandler;
import fr.sparna.rdf.jena.JenaResultSetHandlers;
import fr.sparna.rdf.jena.QueryExecutionService;

/**
 * Executes SPARQL queries in batches using LIMIT x OFFSET y to get large result sets
 * @author thomas
 *
 */
public class PaginatedQuery {

  private static final Logger log = LoggerFactory.getLogger(PaginatedQuery.class);

  private final long batchSize;

  public PaginatedQuery(long batchSize) {
    this.batchSize = batchSize;
  }

  public Model getModel(QueryExecutionService rdfStore, String constructQuery) {
    Model model = ModelFactory.createDefaultModel();

    long batchNumber = 0;
    while (true) {
      Model part = getModel(rdfStore, constructQuery, batchSize * batchNumber);
      model.add(part);
      batchNumber += 1;

      if (part.isEmpty()) break;
    }

    return model;
  }

  private Model getModel(QueryExecutionService service, String constructQuery, long offset) {
    String limit = " limit " + batchSize + " offset " + offset;
    String batchQuery = constructQuery + limit;

    return service.executeConstructQuery(batchQuery);
  }

  public List<Map<String, RDFNode>> select(QueryExecutionService service, String query, QuerySolution bindings) {
    // skip if there is a limit
    if (hasLimitAtEnd(query)) {
      return service.executeSelectQuery(query, bindings, JenaResultSetHandlers::convertToListOfMaps);
    }

    long batchNumber = 0;
    List<Map<String, RDFNode>> result = new ArrayList<>();
    while (true) {
      List<Map<String, RDFNode>> batchMap = select(service, query, bindings, batchNumber);
      result.addAll(batchMap);
      batchNumber += 1;

      if (batchMap.size() < batchSize) break;
    }

    return result;
  }
  
  public List<Map<String, RDFNode>> select(QueryExecutionService service, String query) {
	    return this.select(service, query, new QuerySolutionMap());
  }

  private boolean hasLimitAtEnd(String query) {
    String lastPart = StringUtils.substringAfterLast(query, "}");
    return lastPart.toLowerCase().contains("limit");
  }

  private List<Map<String, RDFNode>> select(QueryExecutionService service, String query, QuerySolution bindings, long batchNumber) {
    String limit = " limit " + batchSize + " offset " + (batchSize * batchNumber);
    String batchQuery = query + limit;

    return runQuery(service, batchQuery, bindings);
  }

  private List<Map<String, RDFNode>> runQuery(QueryExecutionService service, String query, QuerySolution bindings) {
    return service.executeSelectQuery(query, bindings, JenaResultSetHandlers::convertToListOfMaps);
  }

}
