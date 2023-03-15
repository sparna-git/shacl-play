package fr.sparna.rdf.shacl.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.JenaResultSetHandler;
import fr.sparna.rdf.jena.JenaResultSetHandlers;
import fr.sparna.rdf.jena.QueryExecutionService;

/**
 * Executes SPARQL queries by taking a sample of results over the entire triplestore.
 * Uses LIMIT x OFFSET y but not in consecutive pages. Increment the offset by a stepSize parameter.
 * 
 * @author thomas
 *
 */
public class SamplingQuery {

  private static final Logger log = LoggerFactory.getLogger(SamplingQuery.class);

  // LIMIT parameter : number of items to read at each iteration
  private final long batchSize;
  // size of increment of the OFFSET at each iteration
  private final long stepSize;
  // maximum number of iterations that the sampling process will use
  private final long maxIterations;

  public SamplingQuery(long batchSize, long stepSize, long maxIterations) {
    this.batchSize = batchSize;
    this.stepSize = stepSize;
    this.maxIterations = maxIterations;
  }

  public List<Map<String, RDFNode>> select(QueryExecutionService service, String query, QuerySolution bindings) {
    // skip if there is a limit
    if (hasLimitAtEnd(query)) {
      return service.executeSelectQuery(query, bindings, JenaResultSetHandlers::convertToListOfMaps);
    }

    long batchCount = 0;
    List<Map<String, RDFNode>> result = new ArrayList<>();
    while (true) {
      List<Map<String, RDFNode>> batchMap = select(service, query, bindings, batchCount);
      result.addAll(batchMap);
      batchCount += 1;

      if (
    		  (batchMap.size() < batchSize)
    		  ||
    		  (this.maxIterations > 0 && batchCount > this.maxIterations)
      ) {
    	  break;
      }
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
	  String limit;
	  // first iteration : take more instances
	  if(batchNumber == 0) {
		  limit = " limit " + (batchSize*10);
	  } else {
		  limit = " limit " + batchSize + " offset " + (stepSize * batchNumber);
	  }
	  
	  
    String batchQuery = query + limit;

    return runQuery(service, batchQuery, bindings);
  }

  private List<Map<String, RDFNode>> runQuery(QueryExecutionService service, String query, QuerySolution bindings) {
    JenaResultSetHandler<List<Map<String, RDFNode>>> convertToListOfMaps = JenaResultSetHandlers::convertToListOfMaps;
    return service.executeSelectQuery(query, bindings, convertToListOfMaps);
  }

}
