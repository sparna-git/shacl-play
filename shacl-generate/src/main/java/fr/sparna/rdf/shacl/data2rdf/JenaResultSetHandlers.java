package fr.sparna.rdf.shacl.data2rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  // note: please keep resultVars !!
  private static Map<String, RDFNode> convertRowToMap(List<String> resultVars, QuerySolution querySolution) {
    Map<String, RDFNode> result = new HashMap<>();
    resultVars.forEach(var -> result.put(var, querySolution.get(var)));

    return result;
  }

}
