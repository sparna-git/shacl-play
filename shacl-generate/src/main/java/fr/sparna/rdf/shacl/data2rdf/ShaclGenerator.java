package fr.sparna.rdf.shacl.data2rdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;


public class ShaclGenerator {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);

	// root where SPARQL queries are located
	private static final String CLASSPATH_ROOT = "shacl/data2shacl/";

	// executes SPARQL queries either on local Model or remote endpoint
	private QueryExecutionService queryExecutionService;

	// paginates through SPARQL queries
	private final PaginatedQuery paginatedQuery;

	public ShaclGenerator(PaginatedQuery paginatedQuery) {
		super();
		this.paginatedQuery = paginatedQuery;
	}

	/**
	 * Generates shapes from target remote endpoint
	 * 
	 * @param configuration
	 * @param endpointUrl
	 * @return
	 */
	public Model generateShapes(
		Configuration configuration,
		String endpointUrl
	) {
		this.queryExecutionService = new QueryExecutionService(endpointUrl);
		return generateShapes(configuration);
	}

	public Model generateShapes(
		Configuration configuration,
		Model inputModel
	) {
		this.queryExecutionService = new QueryExecutionService(inputModel);
		Model shacl = generateShapes(configuration);
		// add prefixes from input model
		shacl.setNsPrefixes(inputModel.getNsPrefixMap());
		// and add sh namespace
		shacl.setNsPrefix("sh", SHACLM.getURI());
		return shacl;
	}

	private Model generateShapes(
		Configuration configuration
	) {
		Model shacl = ModelFactory.createDefaultModel();
		addTypes(configuration, shacl);
		log.debug("(generate) add types done");

		return shacl;
	}

	private void addTypes(
			Configuration configuration,
			Model shacl
	) {
		List<String> types = getTypes(configuration);
		log.debug("(addTypes) found {} types", types.size());
		types.forEach(type -> addType(configuration, shacl, type));
	}

	private List<String> getTypes(Configuration configuration) {
		List<Map<String, RDFNode>> rows = this.paginatedQuery.select(this.queryExecutionService, readQuery("select-types.rq"));
		List<String> types = paginatedQuery.convertSingleColumnUriToStringList(rows);

		// types.sort(getIriComparator(configuration));

		return types;
	}

	private void addType(
			Configuration configuration,
			Model shacl,
			String typeUri
	) {
		if (configuration.isIgnoredType(typeUri)) {
			log.info(getMessage("ignoring type '{}'", shortenUri(shacl, typeUri)));
			return;
		}

		Resource targetClass = ResourceFactory.createResource(typeUri);
		Resource typeShape = calculateShapeBasedOnResource(configuration, shacl, null, targetClass);

		shacl.add(typeShape, RDF.type, SHACLM.NodeShape);
		shacl.add(typeShape, SHACLM.targetClass, targetClass);

		if (log.isDebugEnabled())
			log.debug("(addType) shape name '{}' for targetClass '{}'", typeShape.getURI(), targetClass.getURI());

		addProperties(configuration, shacl, typeShape, targetClass);
	}
	
	
	private void addProperties(
			Configuration configuration,
			Model shacl,
			Resource typeShape,
			Resource targetClass) {
		List<String> properties = getProperties(configuration, targetClass);
		if (log.isDebugEnabled())
			log.debug("(addProperties) shape '{}' has {} properties", typeShape.getLocalName(), properties.size());

		properties.forEach(property -> addProperty(
				configuration,
				shacl,
				typeShape,
				targetClass,
				property));
	}

	private List<String> getProperties(
		Configuration configuration,
		Resource targetClass
	) {
		
		List<Map<String, RDFNode>> rows = this.paginatedQuery.select(
				this.queryExecutionService,
				readQuery("select-properties-sample.rq"),
				QueryExecutionService.buildQuerySolution("type", targetClass)
		);
		
		List<String> properties = paginatedQuery.convertSingleColumnUriToStringList(rows);
		
		return properties;
	}
	
	
	private void addProperty(
		Configuration configuration,
		Model shacl,
		Resource typeShape,
		Resource targetClass,
		String property
	) {

		Resource path = ResourceFactory.createResource(property);
		Resource propertyShape = calculateShapeBasedOnResource(configuration, shacl, targetClass.getLocalName(), path);

		if (log.isDebugEnabled())
			log.debug("(addProperty) shape '{}' gets '{}'", typeShape.getLocalName(), propertyShape.getLocalName());

		shacl.add(typeShape, SHACLM.property, propertyShape);
		shacl.add(propertyShape, RDF.type, SHACLM.PropertyShape);

		shacl.add(propertyShape, SHACLM.path, path);

//		setMinCount(rdfStoreService, shacl, targetClass, path, propertyShape);
//		setMaxCount(rdfStoreService, shacl, targetClass, path, propertyShape);
//
//		setNodeKind(configuration, rdfStoreService, shacl, targetClass, path, propertyShape);
	}
	
	
	private Resource calculateShapeBasedOnResource(
		Configuration configuration,
		Model shacl,
		String firstPart,
		Resource originalResource
	) {

		if(configuration.getShapesNamespace() == null) {
			return shacl.createResource(originalResource.getURI());
		} else {

			// build localName of Shape URI
			String localName = firstPart == null ? originalResource.getLocalName()
					: firstPart + "/" + originalResource.getLocalName();
			// Create a resource with that URI
			Resource typeShape = ResourceFactory.createResource(configuration.getShapesNamespace() + localName);

			// determine if Resource with that URI already exists, in which case return it
			boolean hasSameTypeShape = shacl.contains(typeShape, null, (RDFNode) null);
			if (!hasSameTypeShape) {
				return typeShape;
			} else {
				return typeShape;
			}

			//		String namespacePrefix = shacl.getNsURIPrefix(originalResource.getNameSpace());
			//		if (namespacePrefix == null) {
			//			namespacePrefix = "ns" + getAvailableNamespaceIndex(shacl);
			//			shacl.setNsPrefix(namespacePrefix, originalResource.getNameSpace());
			//		}
			//
			//		String prefixLocalName = firstPart == null ? namespacePrefix + "_" + localName
			//				: firstPart + "_" + namespacePrefix + "_" + localName;
			//		return ResourceFactory.createResource(configuration.getShapesNamespace() + prefixLocalName);	
		}
		
	}

	private String shortenUri(Model shacl, String uri) {
		return shortenUri(shacl, ResourceFactory.createResource(uri));
	}

	private String shortenUri(Model shacl, Resource resource) {
		String prefix = shacl.getNsURIPrefix(resource.getNameSpace());
		if (prefix == null) return resource.getURI();

		return prefix + ":" + resource.getLocalName();
	}

	private String getMessage(String messagePattern, Object... parameters) {
		return MessageFormatter.arrayFormat(messagePattern, parameters)
				.getMessage();
	}

	private String readQuery(String resourceName) {
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
