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

import fr.sparna.rdf.jena.EndpointExecutionService;
import fr.sparna.rdf.jena.JenaResultSetHandlers;
import fr.sparna.rdf.jena.ModelQueryExecutionService;
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
		this.queryExecutionService = new EndpointExecutionService(endpointUrl);
		this.paginatedQuery = paginatedQuery;
	}

	public BaseShaclGeneratorDataProvider(PaginatedQuery paginatedQuery, Model inputModel) {
		super();
		this.queryExecutionService = new ModelQueryExecutionService(inputModel);
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
	
	public List<String> getCoOccuringTypes(String classUri) {
		List<Map<String, RDFNode>> rows = this.paginatedQuery.select(
				this.queryExecutionService,
				readQuery("select-cooccuring-classes.rq"),
				QueryExecutionService.buildQuerySolution("type", ResourceFactory.createResource(classUri))
		);
		
		List<String> coOccurringClasses = JenaResultSetHandlers.convertSingleColumnUriToStringList(rows);

		
		return new ArrayList<String>(coOccurringClasses);
	}

	public boolean isEquivalentOrSuperSet(String classUri, String potentialSuperset) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("otherType", ResourceFactory.createResource(potentialSuperset));
		// if there NO instances that have type but not otherType, then otherType is a superSet of type
		return !this.queryExecutionService.executeAskQuery(readQuery("has-instance-without-type.rq"), qs);
	}
	
	public boolean isStrictSuperset(String classUri, String potentialSuperset) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("otherType", ResourceFactory.createResource(potentialSuperset));
		
		QuerySolutionMap qsReverse = new QuerySolutionMap();
		qsReverse.add("type", ResourceFactory.createResource(potentialSuperset));
		qsReverse.add("otherType", ResourceFactory.createResource(classUri));
		
		return
				// if there NO instances that have type but not otherType...
				!this.queryExecutionService.executeAskQuery(readQuery("has-instance-without-type.rq"), qs)
				&&
				// and there IS some instance that have otherType but not type
				// then otherType is a strict superset of type
				this.queryExecutionService.executeAskQuery(readQuery("has-instance-without-type.rq"), qsReverse)
		;
	}
	
	public boolean hasObjectOfTypeWithoutOtherType(
			String classUri,
			String propertyUri,
			String includedType,
			String excludedType
	) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		qs.add("includedType", ResourceFactory.createResource(includedType));
		qs.add("excludedType", ResourceFactory.createResource(excludedType));
		return this.queryExecutionService.executeAskQuery(readQuery("has-object-of-type-without-other-type.rq"), qs);
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
	public int countDistinctObjects(String subjectClassUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(subjectClassUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		
		int count = this.queryExecutionService.executeSelectQuery(
				readQuery("count-distinct-objects.rq"),
				qs,
				JenaResultSetHandlers::convertToInt
				
		);
		return count;
	}
	
	
	@Override
	public int countStatementsWithDatatypes(String subjectClassUri, String propertyUri, List<String> datatypes) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public int countStatementsWithObjectClasses(String subjectClassUri, String propertyUri,
			List<String> objectClassUris) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public List<RDFNode> listDistinctValues(String subjectClassUri, String propertyUri, int limit) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(subjectClassUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		// qs.add("limit", ResourceFactory.createTypedLiteral(Integer.toString(limit), XSDDatatype.XSDinteger));
		
		List<RDFNode> result = this.queryExecutionService.executeSelectQuery(
				readQuery("count-distinct-values.rq").replaceAll("\\$limit", Integer.toString(limit)),
				qs,
				JenaResultSetHandlers::convertToList
				
		);
		return result;
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

}
