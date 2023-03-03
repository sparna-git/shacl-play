package fr.sparna.rdf.shacl.data2rdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
      return service.executeSelectQuery(query, JenaResultSetHandlers::convertToListOfMaps);
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
    JenaResultSetHandler<List<Map<String, RDFNode>>> convertToListOfMaps = JenaResultSetHandlers::convertToListOfMaps;
    return service.executeSelectQuery(query, bindings, convertToListOfMaps);
  }

  /**
   * Since result currently is somewhat inflexible, we are adding flexibility with convertors.
   */
  public List<String> convertSingleColumnUriToStringList(List<Map<String, RDFNode>> results) {
    return convertSingleColumnToList(results, input -> input.asResource().getURI());
  }

  /**
   * Converts single column result set to a certain type using a conversion function.
   *
   * @param results    rows after query
   * @param conversion conversion function
   * @param <T>        type of elements in the list
   * @return list of results
   * @throws RuntimeException in case there is not exactly one column
   */
  public <T> List<T> convertSingleColumnToList(List<Map<String, RDFNode>> results,
                                               Function<RDFNode, T> conversion) {
    if (results.isEmpty()) return Collections.emptyList();

    Map<String, RDFNode> firstRow = results.get(0);
    if (firstRow.size() != 1)
      throw new RuntimeException("Expected exactly one result per row. Found row size of '" + firstRow.size() + "'");

    String columnName = firstRow.keySet().stream().findFirst().get();
    return results.stream()
                  .map(row -> row.get(columnName))
                  .map(conversion)
                  .collect(Collectors.toList());
  }

}
