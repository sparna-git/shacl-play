package fr.sparna.rdf.shacl.generate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.JenaResultSetHandlers;
import fr.sparna.rdf.jena.QueryExecutionService;

public class BaseShaclGeneratorDataProvider implements ShaclGeneratorDataProviderIfc {

	private static final Logger log = LoggerFactory.getLogger(BaseShaclGeneratorDataProvider.class);
	
	// root where SPARQL queries are located
	private static final String CLASSPATH_ROOT = "shacl/generate/";

	// executes SPARQL queries either on local Model or remote endpoint
	protected final QueryExecutionService queryExecutionService;

	// paginates through SPARQL queries
	protected final PaginatedQuery paginatedQuery;
	
	protected Consumer<String> messageListener;


	public BaseShaclGeneratorDataProvider(PaginatedQuery paginatedQuery, String endpointUrl) {
		super();
		this.queryExecutionService = new QueryExecutionService(endpointUrl);
		this.paginatedQuery = paginatedQuery;
	}

	public BaseShaclGeneratorDataProvider(PaginatedQuery paginatedQuery, Model inputModel) {
		super();
		this.queryExecutionService = new QueryExecutionService(inputModel);
		this.paginatedQuery = paginatedQuery;
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
	public List<String> getTypes() {
		List<Map<String, RDFNode>> rows = this.paginatedQuery.select(this.queryExecutionService, readQuery("select-types.rq"));
		List<String> types = JenaResultSetHandlers.convertSingleColumnUriToStringList(rows);
		return types;
	}

	@Override
	public List<String> getProperties(String classUri) {

		List<Map<String, RDFNode>> rows = this.paginatedQuery.select(
				this.queryExecutionService,
				readQuery("select-properties.rq"),
				QueryExecutionService.buildQuerySolution("type", ResourceFactory.createResource(classUri))
		);
		
		List<String> properties = JenaResultSetHandlers.convertSingleColumnUriToStringList(rows);

		
		return new ArrayList<String>(properties);
	}

	@Override
	public boolean hasInstanceWithoutProperty(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		return this.queryExecutionService.executeAskQuery(readQuery("has-instance-without-property.rq"), qs);
	}

	@Override
	public boolean hasInstanceWithTwoProperties(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		return this.queryExecutionService.executeAskQuery(readQuery("has-instance-with-two-properties.rq"), qs);
	}

	@Override
	public boolean hasIriObject(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		return this.queryExecutionService.executeAskQuery(readQuery("nodekind-is-iri.rq"), qs);
	}

	@Override
	public boolean hasLiteralObject(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		return this.queryExecutionService.executeAskQuery(readQuery("nodekind-is-literal.rq"), qs);
	}

	@Override
	public boolean hasBlankNodeObject(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		return this.queryExecutionService.executeAskQuery(readQuery("nodekind-is-blank.rq"), qs);
	}

	@Override
	public List<String> getDatatypes(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		
		return JenaResultSetHandlers.convertSingleColumnUriToStringList(
				this.queryExecutionService.executeSelectQuery(
						readQuery("select-datatypes.rq"),
						qs,
						JenaResultSetHandlers::convertToListOfMaps
						)
				);
	}

	@Override
	public boolean isNotUniqueLang(String classUri, String propertyUri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getLanguages(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		
		return JenaResultSetHandlers.convertSingleColumnLiteralToList(
				this.queryExecutionService.executeSelectQuery(
						readQuery("select-languages.rq"),
						qs,
						JenaResultSetHandlers::convertToListOfMaps
						)
				);
	}

	@Override
	public List<String> getObjectTypes(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		List<Map<String, RDFNode>> rows = this.paginatedQuery.select(this.queryExecutionService, readQuery("select-object-types.rq"), qs);
		List<String> types = JenaResultSetHandlers.convertSingleColumnUriToStringList(rows);
		return types;
	}

	@Override
	public String getName(String classOrPropertyUri, String lang) {
		// TODO : query to read a label, or dereference URI to get its description
		return ResourceFactory.createResource(classOrPropertyUri).getLocalName();
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
	public int countStatements(String subjectClassUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(subjectClassUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		
		int count = this.queryExecutionService.executeSelectQuery(
				readQuery("count-statements.rq"),
				qs,
				JenaResultSetHandlers::convertToInt
				
		);
		return count;
	}
	

	@Override
	public void registerMessageListener(Consumer<String> listener) {
		this.messageListener = listener;		
	}

	protected String readQuery(String resourceName) {
		try {
			return getResourceFileAsString(CLASSPATH_ROOT+resourceName);
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
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(fileName)) {
			if (is == null) return null;
			try (InputStreamReader isr = new InputStreamReader(is);
					BufferedReader reader = new BufferedReader(isr)) {
				return reader.lines().collect(Collectors.joining(System.lineSeparator()));
			}
		}
	}

}
