package fr.sparna.rdf.shacl.generate.providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.JenaResultSetHandlers;
import fr.sparna.rdf.jena.QueryExecutionService;
import fr.sparna.rdf.jena.QueryExecutionServiceImpl;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;

public class BaseShaclStatisticsDataProvider implements ShaclStatisticsDataProviderIfc {

	private static final Logger log = LoggerFactory.getLogger(BaseShaclGeneratorDataProvider.class);
	
	private static final String RDF_PREFIXES = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";

	// root where SPARQL queries are located
	private static final String CLASSPATH_ROOT = "shacl/statistics/";

	// executes SPARQL queries either on local Model or remote endpoint
	protected final QueryExecutionService queryExecutionService;

	// paginates through SPARQL queries
	protected final PaginatedQuery paginatedQuery;

	protected Consumer<String> messageListener;

	protected boolean assumeNoSubclassOf = false;

	public BaseShaclStatisticsDataProvider(PaginatedQuery paginatedQuery, String endpointUrl) {
		this.queryExecutionService = new QueryExecutionServiceImpl(endpointUrl);
		this.paginatedQuery = paginatedQuery;
	}

	public BaseShaclStatisticsDataProvider(PaginatedQuery paginatedQuery, Model inputModel) {
		this.queryExecutionService = new QueryExecutionServiceImpl(inputModel);
		this.paginatedQuery = paginatedQuery;
	}

	public BaseShaclStatisticsDataProvider(QueryExecutionService queryExecutionService) {
		super();
		this.queryExecutionService = queryExecutionService;
		this.paginatedQuery = new PaginatedQuery(100);
	}
	
	

	@Override
	public int countTriples() {
		int count = this.queryExecutionService.executeSelectQuery(
				readQuery("count-triples.rq"),
				JenaResultSetHandlers::convertToInt
				
		);
		return count;
	}

	
	
	@Override
	public int countInstances(String classUri) {
		int count = this.queryExecutionService.executeSelectQuery(
				readQuery("count-instances.rq"),
				QueryExecutionService.buildQuerySolution("type", ResourceFactory.createResource(classUri)),
				JenaResultSetHandlers::convertToInt
				
		);
		return count;
	}

	@Override
	public int countStatements(TargetDefinitionIfc targetDefinition, String propertyPath) {	
		Count count = new Count();

		// targetNode
		Optional.ofNullable(targetDefinition.getTargetNode()).ifPresent(l -> {
			// TODO : we don't know how to do that
		});

		// targetClass
		Optional.ofNullable(targetDefinition.getTargetClass()).ifPresent(l -> {
			l.stream().forEach(resource -> count.add(this.countStatementsWithTargetClass(resource, propertyPath)));
		});

		// targetSubjectsOf
		Optional.ofNullable(targetDefinition.getTargetSubjectsOf()).ifPresent(l -> {
			l.stream().forEach(resource -> count.add(this.countStatementsWithTargetSubjectsOf(resource, propertyPath)));
		});

		// targetObjectsOf
		Optional.ofNullable(targetDefinition.getTargetObjectsOf()).ifPresent(l -> {
			l.stream().forEach(resource -> count.add(this.countStatementsWithTargetObjectsOf(resource, propertyPath)));
		});

		// sparqltarget
		Optional.ofNullable(targetDefinition.getSparqlTarget()).ifPresent(l -> {
			l.stream().forEach(string -> count.add(this.countStatementsWithSparqlTarget(string, propertyPath)));
		});

		return count.count;
	}
	
	@Override
	public int countDistinctObjects(TargetDefinitionIfc targetDefinition, String propertyPath) {
		Count count = new Count();

		// targetNode
		Optional.ofNullable(targetDefinition.getTargetNode()).ifPresent(l -> {
			// TODO : we don't know how to do that
		});

		// targetClass
		Optional.ofNullable(targetDefinition.getTargetClass()).ifPresent(l -> {
			l.stream().forEach(resource -> count.add(this.countDistinctObjectsWithTargetClass(resource, propertyPath)));
		});

		// targetSubjectsOf
		Optional.ofNullable(targetDefinition.getTargetSubjectsOf()).ifPresent(l -> {
			l.stream().forEach(resource -> count.add(this.countDistinctObjectsWithTargetSubjectsOf(resource, propertyPath)));
		});

		// targetObjectsOf
		Optional.ofNullable(targetDefinition.getTargetObjectsOf()).ifPresent(l -> {
			l.stream().forEach(resource -> count.add(this.countDistinctObjectsWithTargetObjectsOf(resource, propertyPath)));
		});

		// sparqltarget
		Optional.ofNullable(targetDefinition.getSparqlTarget()).ifPresent(l -> {
			l.stream().forEach(string -> count.add(this.countDistinctObjectsWithSparqlTarget(string, propertyPath)));
		});

		return count.count;
	}
	
	
	@Override
	public int countStatementsWithDatatypes(
		TargetDefinitionIfc targetDefinition,
			String propertyUri,
			List<String> datatypes
	) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public int countStatementsWithObjectClasses(
		TargetDefinitionIfc targetDefinition,
			String propertyUri,
			List<String> objectClassUris
	) {
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public Map<RDFNode, Integer> countByValues(TargetDefinitionIfc targetDefinition, String propertyPath, int limit) {
		Map<RDFNode, Integer> result = new HashMap<RDFNode, Integer>();

		// targetNode
		Optional.ofNullable(targetDefinition.getTargetNode()).ifPresent(l -> {
			// TODO : we don't know how to do that
		});

		// targetClass
		Optional.ofNullable(targetDefinition.getTargetClass()).ifPresent(l -> {
			l.stream().forEach(resource -> mergeValueMaps(result, (this.countByValuesWithTargetClass(resource, propertyPath, limit))));
		});

		// targetSubjectsOf
		Optional.ofNullable(targetDefinition.getTargetSubjectsOf()).ifPresent(l -> {
			l.stream().forEach(resource -> mergeValueMaps(result, (this.countByValuesWithTargetSubjectsOf(resource, propertyPath, limit))));
		});

		// targetObjectsOf
		Optional.ofNullable(targetDefinition.getTargetObjectsOf()).ifPresent(l -> {
			l.stream().forEach(resource -> mergeValueMaps(result, (this.countByValuesWithTargetObjectsOf(resource, propertyPath, limit))));
		});

		// sparqltarget
		Optional.ofNullable(targetDefinition.getSparqlTarget()).ifPresent(l -> {
			l.stream().forEach(string -> mergeValueMaps(result, (this.countByValuesWithSparqlTarget(string, propertyPath, limit))));
		});

		return result;
	
	}

	private static void mergeValueMaps(Map<RDFNode, Integer> result, Map<RDFNode, Integer> other) {
		other.entrySet().stream().forEach(entry -> {
			if(!result.containsKey(entry.getKey())) {
				result.put(entry.getKey(), entry.getValue());
			} else {
				result.put(entry.getKey(), result.get(entry.getKey())+entry.getValue());
			}
		});
	}
	

	@Override
	public void registerMessageListener(Consumer<String> listener) {
		this.messageListener = listener;		
	}


	protected String readQuery(String resourceName) {
		try {
			String originalString = getResourceFileAsString(CLASSPATH_ROOT+resourceName);
			return originalString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads given resource file as a string.
	 *
	 * @param fileName path to the resource file
	 * @return the file's contents
	 * @throws IOException if read fails for any reason
	 */
	static String getResourceFileAsString(String fileName) throws IOException {
		// ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		ClassLoader classLoader = BaseShaclGeneratorDataProvider.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(fileName)) {
			if (is == null) return null;
			try (InputStreamReader isr = new InputStreamReader(is);
					BufferedReader reader = new BufferedReader(isr)) {
				return reader.lines().collect(Collectors.joining(System.lineSeparator()));
			}
		}
	}


	@Override
	public int countTargets(TargetDefinitionIfc targetDefinition) {

		Count count = new Count();

		// targetNode
		Optional.ofNullable(targetDefinition.getTargetNode()).ifPresent(l -> {
			l.stream().forEach(resource -> count.add(this.countTargetNode(resource)));
		});

		// targetClass
		Optional.ofNullable(targetDefinition.getTargetClass()).ifPresent(l -> {
			l.stream().forEach(resource -> count.add(this.countTargetClass(resource)));
		});

		// targetSubjectsOf
		Optional.ofNullable(targetDefinition.getTargetSubjectsOf()).ifPresent(l -> {
			l.stream().forEach(resource -> count.add(this.countTargetSubjectsOf(resource)));
		});

		// targetObjectsOf
		Optional.ofNullable(targetDefinition.getTargetObjectsOf()).ifPresent(l -> {
			l.stream().forEach(resource -> count.add(this.countTargetObjectsOf(resource)));
		});

		// sparqltarget
		Optional.ofNullable(targetDefinition.getSparqlTarget()).ifPresent(l -> {
			l.stream().forEach(string -> count.add(this.countSparqlTarget(string)));
		});

		return count.count;
	}

	private int countTargetClass(Resource targetClass) {
		String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
		String select = "SELECT (COUNT(?s) AS ?count)\n";
		String where = "WHERE { ?s rdf:type"+((this.assumeNoSubclassOf)?"":"/rdfs:subClassOf*")+" <"+targetClass.getURI()+"> }";
		return doCountQuery(prefixes+select+where);
	}

	private int countTargetSubjectsOf(Resource targetSubjectsOf) {
		String prefixes = "";
		String select = "SELECT (COUNT(?s) AS ?count)\n";
		String where = "WHERE { ?s <"+targetSubjectsOf.getURI()+"> ?o  }";
		return doCountQuery(prefixes+select+where);
	}

	private int countTargetNode(Resource targetNode) {
		return 1;
	}

	private int countTargetObjectsOf(Resource targetObjectsOf) {
		String prefixes = "";
		String select = "SELECT (COUNT(?o) AS ?count)\n";
		String where = "WHERE { ?s <"+targetObjectsOf.getURI()+"> ?o  }";
		return doCountQuery(prefixes+select+where);
	}

	private int countSparqlTarget(String sparql) {
		String prefixes = extractPrefixes(sparql);
		String select = "SELECT (COUNT(?this) AS ?count)\n";
		sparql = sparql.replace("select", "SELECT");
		int selectIndex = sparql.indexOf("SELECT");
		String where = "WHERE { { "+sparql.substring(selectIndex)+" } }";
		return doCountQuery(prefixes+select+where);
	}

	private int countStatementsWithTargetClass(Resource targetClass, String propertyPath) {
		String prefixes = RDF_PREFIXES;
		String select = "SELECT (COUNT(?value) AS ?count)\n";
		String where = buildWhereTargetClassAndPath(targetClass, propertyPath);
		return doCountQuery(prefixes+select+where);
	}

	private int countStatementsWithTargetSubjectsOf(Resource targetSubjectsOf, String propertyPath) {
		String prefixes = "";
		String select = "SELECT (COUNT(?value) AS ?count)\n";
		String where = buildWhereTargetSubjectsOfAndPath(targetSubjectsOf, propertyPath);
		return doCountQuery(prefixes+select+where);
	}

	private int countStatementsWithTargetObjectsOf(Resource targetObjectsOf, String propertyPath) {
		String prefixes = "";
		String select = "SELECT (COUNT(?value) AS ?count)\n";
		String where = buildWhereTargetObjectsOfAndPath(targetObjectsOf, propertyPath);
		return doCountQuery(prefixes+select+where);
	}

	private int countStatementsWithSparqlTarget(String sparqlTarget, String propertyPath) {
		String prefixes = extractPrefixes(sparqlTarget);
		String select = "SELECT (COUNT(?value) AS ?count)\n";
		String where = buildWhereSparqlTargetAndPath(sparqlTarget, propertyPath);
		return doCountQuery(prefixes+select+where);
	}

	private int countDistinctObjectsWithTargetClass(Resource targetClass, String propertyPath) {
		String prefixes = RDF_PREFIXES;
		String select = "SELECT (COUNT(DISTINCT ?value) AS ?count)\n";
		String where = buildWhereTargetClassAndPath(targetClass, propertyPath);
		return doCountQuery(prefixes+select+where);
	}

	private int countDistinctObjectsWithTargetSubjectsOf(Resource targetSubjectsOf, String propertyPath) {
		String prefixes = "";
		String select = "SELECT (COUNT(DISTINCT ?value) AS ?count)\n";
		String where = buildWhereTargetSubjectsOfAndPath(targetSubjectsOf, propertyPath);
		return doCountQuery(prefixes+select+where);
	}

	private int countDistinctObjectsWithTargetObjectsOf(Resource targetObjectsOf, String propertyPath) {
		String prefixes = "";
		String select = "SELECT (COUNT(DISTINCT ?value) AS ?count)\n";
		String where = buildWhereTargetObjectsOfAndPath(targetObjectsOf, propertyPath);
		return doCountQuery(prefixes+select+where);
	}

	private int countDistinctObjectsWithSparqlTarget(String sparqlTarget, String propertyPath) {
		String prefixes = extractPrefixes(sparqlTarget);
		String select = "SELECT (COUNT(DISTINCT ?value) AS ?count)\n";
		String where = buildWhereSparqlTargetAndPath(sparqlTarget, propertyPath);
		return doCountQuery(prefixes+select+where);
	}

	private Map<RDFNode, Integer> countByValuesWithTargetClass(Resource targetClass, String propertyPath, int limit) {
		String prefixes = RDF_PREFIXES;
		String select = "SELECT ?value (COUNT(?s) AS ?count)\n";
		String where = buildWhereTargetClassAndPath(targetClass, propertyPath);
		String limitClause = "GROUP BY ?value LIMIT "+limit;
		return doMapQuery(prefixes+select+where+limitClause);
	}

	private Map<RDFNode, Integer> countByValuesWithTargetSubjectsOf(Resource targetClass, String propertyPath, int limit) {
		String prefixes = "";
		String select = "SELECT ?value (COUNT(?s) AS ?count)\n";
		String where = buildWhereTargetSubjectsOfAndPath(targetClass, propertyPath);
		String limitClause = "GROUP BY ?value LIMIT "+limit;
		return doMapQuery(prefixes+select+where+limitClause);
	}

	private Map<RDFNode, Integer> countByValuesWithTargetObjectsOf(Resource targetClass, String propertyPath, int limit) {
		String prefixes = "";
		String select = "SELECT ?value (COUNT(?s) AS ?count)\n";
		String where = buildWhereTargetObjectsOfAndPath(targetClass, propertyPath);
		String limitClause = "GROUP BY ?value LIMIT "+limit;
		return doMapQuery(prefixes+select+where+limitClause);
	}

	private Map<RDFNode, Integer> countByValuesWithSparqlTarget(String sparqlTarget, String propertyPath, int limit) {
		String prefixes = extractPrefixes(sparqlTarget);
		// note the use of ?this variable
		String select = "SELECT ?value (COUNT(?this) AS ?count)\n";
		String where = buildWhereSparqlTargetAndPath(sparqlTarget, propertyPath);
		String limitClause = "GROUP BY ?value LIMIT "+limit;
		return doMapQuery(prefixes+select+where+limitClause);
	}

	private String buildWhereTargetClassAndPath(Resource targetClass, String propertyPath) {
		return "WHERE { ?s rdf:type"+((this.assumeNoSubclassOf)?"":"/rdfs:subClassOf*")+" <"+targetClass.getURI()+"> . ?s "+propertyPath+" ?value }";
	}

	private String buildWhereTargetSubjectsOfAndPath(Resource targetSubjectsOf, String propertyPath) {
		return "WHERE { ?s <"+targetSubjectsOf.getURI()+"> ?o . ?s "+propertyPath+" ?value }";
	}

	private String buildWhereTargetObjectsOfAndPath(Resource targetObjectsOf, String propertyPath) {
		return "WHERE { ?s ^<"+targetObjectsOf.getURI()+"> ?o . ?s "+propertyPath+" ?value }";
	}

	private String buildWhereSparqlTargetAndPath(String sparqlTarget, String propertyPath) {
		// We strip out prefixes
		sparqlTarget = sparqlTarget.replace("select", "SELECT");
		int selectIndex = sparqlTarget.indexOf("SELECT");
		return "WHERE {\n { "+sparqlTarget.substring(selectIndex)+" }\n ?this "+propertyPath+" ?value }";
	}

	private String extractPrefixes(String sparqlQuery) {
		// We strip out prefixes
		sparqlQuery = sparqlQuery.replace("select", "SELECT");
		return sparqlQuery.substring(0, sparqlQuery.indexOf("SELECT"));
	}

	private int doCountQuery(String sparqlQuery) {
		try {
			return this.queryExecutionService.executeSelectQuery(sparqlQuery, JenaResultSetHandlers::convertToInt);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private Map<RDFNode, Integer> doMapQuery(String sparqlQuery) {
		try {
			return this.queryExecutionService.executeSelectQuery(
				sparqlQuery,
				JenaResultSetHandlers::convertToMap	
			);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public boolean isAssumeNoSubclassOf() {
		return assumeNoSubclassOf;
	}

	public void setAssumeNoSubclassOf(boolean assumeNoSubclassOf) {
		this.assumeNoSubclassOf = assumeNoSubclassOf;
	}

	public static void main(String... args) {
		String directTypeQuery = BaseShaclGeneratorDataProvider.makeDirectTypeQuery("select (count(?uri) as ?count) {\n" + //
						"  ?uri a $type.\n" + //
						"}");
		System.out.println(directTypeQuery);
	}

}

// to avoid problem with non-final int variable
class Count {
	public int count;

	public void add(int more) {
		this.count += more;
	}
}
