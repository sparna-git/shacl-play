package fr.sparna.rdf.jena;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes a SPARQL query with an additional VALUES clause to pass in values in batches
 */
public class ValuesQuery {

  private static final Logger log = LoggerFactory.getLogger(ValuesQuery.class);

  private final int batchSize;

  public ValuesQuery(int batchSize) {
    this.batchSize = batchSize;
  }

  public List<Map<String, RDFNode>> select(QueryExecutionService service, String query, String varName, List<RDFNode> values) {
	  // skip if there is already a VALUES clause
	  if (hasValuesAtEnd(query)) {
		  return runQuery(service, query);
	  }

	  int batchNumber = 0;
	  List<Map<String, RDFNode>> result = new ArrayList<>();
	  while (true) {
		  int fromIndex = batchNumber*this.batchSize;
		  int toIndex = (batchNumber*this.batchSize)+batchSize;
		  
		  // we have reached a size greater than the list length, so stop
		  if(fromIndex >= values.size()) {
			  break;
		  }
		  
		  List<RDFNode> valuesBatch = (values.size() < toIndex)?
				  values.subList(fromIndex, values.size())
				  :values.subList(fromIndex, toIndex);
		  
		  List<Map<String, RDFNode>> batchMap = selectBatch(service, query, varName, valuesBatch);
		  result.addAll(batchMap);
		  batchNumber += 1;
	  }

	  return result;
  }
  
  public List<Map<String, RDFNode>> select(QueryExecutionService service, String query) {
	    return this.select(service, query, null, null);
  }

  private boolean hasValuesAtEnd(String query) {
	String beforeValuesAccolade = StringUtils.substringBeforeLast(query.toLowerCase(), "{");
    String betweenLastAccoladeAndValuesAccolade = StringUtils.substringAfterLast(beforeValuesAccolade, "{");
	
    return betweenLastAccoladeAndValuesAccolade.toLowerCase().contains("values");
  }

  /**
   * Runs a single batch : append the VALUES clause at the end of the query
   * @param service
   * @param query
   * @param varName
   * @param values
   * @return
   */
  private List<Map<String, RDFNode>> selectBatch(
		  QueryExecutionService service,
		  String query,
		  String varName,
		  List<RDFNode> values
  ) {
    String valuesClause = "values ?" + varName + " { " + values.stream().map(v -> toSparqlString(v)).collect(Collectors.joining(" ")) + " } ";
    String batchQuery = StringUtils.substringBeforeLast(query, "}") + "\n" + valuesClause + "\n" + "}" + StringUtils.substringAfterLast(query, "}");

    return runQuery(service, batchQuery);
  }

  private static String toSparqlString(RDFNode node) {
	  if(node.isResource()) {
		  return "<"+node.asResource().getURI()+">";
	  } else if(node.isLiteral()) {
		  Literal l = node.asLiteral();
		  return "\""+l.getLexicalForm()+"\""+((l.getLanguage() != null)?"@"+l.getLanguage():"^^"+"<"+l.getDatatypeURI()+">");
	  } else {
		  return "cannot serialize node";
	  }
  }
  
  private List<Map<String, RDFNode>> runQuery(QueryExecutionService service, String query) {
    return service.executeSelectQuery(query, JenaResultSetHandlers::convertToListOfMaps);
  }

}
