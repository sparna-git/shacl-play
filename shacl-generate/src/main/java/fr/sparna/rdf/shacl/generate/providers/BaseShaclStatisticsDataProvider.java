package fr.sparna.rdf.shacl.generate.providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.JenaResultSetHandlers;
import fr.sparna.rdf.jena.QueryExecutionService;
import fr.sparna.rdf.jena.QueryExecutionServiceImpl;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;

public class BaseShaclStatisticsDataProvider implements ShaclStatisticsDataProviderIfc {

	private static final Logger log = LoggerFactory.getLogger(BaseShaclGeneratorDataProvider.class);
	
	// root where SPARQL queries are located
	private static final String CLASSPATH_ROOT = "shacl/statistics/";

	// executes SPARQL queries either on local Model or remote endpoint
	protected final QueryExecutionService queryExecutionService;

	// paginates through SPARQL queries
	protected final PaginatedQuery paginatedQuery;

	protected Consumer<String> messageListener;

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
	public int countStatements(String subjectClassUri, String propertyPath) {		
		// manually replace variables to deal with property paths
		String query = readQuery("count-statements.rq");
		query = query.replaceAll("\\$type", "<"+subjectClassUri+">");
		query = query.replaceAll("\\$property", propertyPath);
		
		int count = this.queryExecutionService.executeSelectQuery(
				query,
				JenaResultSetHandlers::convertToInt
				
		);
		return count;
	}
	
	@Override
	public int countDistinctObjects(String subjectClassUri, String propertyPath) {
		// manually replace variables to deal with property paths
		String query = readQuery("count-distinct-objects.rq");
		query = query.replaceAll("\\$type", "<"+subjectClassUri+">");
		query = query.replaceAll("\\$property", propertyPath);
		
		
		int count = this.queryExecutionService.executeSelectQuery(
				query,
				JenaResultSetHandlers::convertToInt
				
		);
		return count;
	}
	
	
	@Override
	public int countStatementsWithDatatypes(
			String subjectClassUri,
			String propertyUri,
			List<String> datatypes
	) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public int countStatementsWithObjectClasses(
			String subjectClassUri,
			String propertyUri,
			List<String> objectClassUris
	) {
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public Map<RDFNode, Integer> countValues(String subjectClassUri, String propertyPath, int limit) {
		// manually replace variables to deal with property paths
		String query = readQuery("count-values.rq");
		query = query.replaceAll("\\$type", "<"+subjectClassUri+">");
		query = query.replaceAll("\\$property", propertyPath);
		query = query.replaceAll("\\$limit", Integer.toString(limit));


		Map<RDFNode, Integer> result = this.queryExecutionService.executeSelectQuery(
				query,
				JenaResultSetHandlers::convertToMap				
		);
		return result;
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

	public static void main(String... args) {
		String directTypeQuery = BaseShaclGeneratorDataProvider.makeDirectTypeQuery("select (count(?uri) as ?count) {\n" + //
						"  ?uri a $type.\n" + //
						"}");
		System.out.println(directTypeQuery);
	}

}
