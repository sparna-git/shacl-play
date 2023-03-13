package fr.sparna.rdf.jena;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;


public abstract class JenaResultSetHandlers {

  public static List<Map<String, RDFNode>> convertToListOfMaps(ResultSet resultSet) {
    List<Map<String, RDFNode>> result = new ArrayList<>();


    while (resultSet.hasNext()) {
      QuerySolution querySolution = resultSet.next();
      result.add(convertRowToMap(resultSet.getResultVars(), querySolution));
    }

    return result;
  }
  
  /**
   * Since result currently is somewhat inflexible, we are adding flexibility with convertors.
   */
  public static List<String> convertSingleColumnUriToStringList(List<Map<String, RDFNode>> results) {
    return convertSingleColumnToList(results, input -> input.asResource().getURI());
  }
  
  public static List<RDFNode> convertSingleColumnUriToRDFNodeList(List<Map<String, RDFNode>> results) {
	return convertSingleColumnToList(results, input -> input.asResource());
  }
  
  public static List<Integer> convertSingleColumnToIntegerList(List<Map<String, RDFNode>> results) {
	return convertSingleColumnToList(results, input -> input.asLiteral().getInt());
  }
  
  /**
   * Reads the result of a count query and returns an int directly
   * 
   * @param results
   * @return
   */
  public static int convertToInt(ResultSet resultSet) {
	  List<Map<String, RDFNode>> results = JenaResultSetHandlers.convertToListOfMaps(resultSet);
	  if(results.size() != 1) {
		  throw new InvalidParameterException("Can only read count queries with a single row and single column, but got "+results.size());
	  }
	  String singleKey = results.get(0).keySet().iterator().next();
	  return results.get(0).get(singleKey).asLiteral().getInt();
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
  public static <T> List<T> convertSingleColumnToList(List<Map<String, RDFNode>> results,
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

  // note: please keep resultVars !!
  private static Map<String, RDFNode> convertRowToMap(List<String> resultVars, QuerySolution querySolution) {
    Map<String, RDFNode> result = new HashMap<>();
    resultVars.forEach(var -> result.put(var, querySolution.get(var)));

    return result;
  }

}
