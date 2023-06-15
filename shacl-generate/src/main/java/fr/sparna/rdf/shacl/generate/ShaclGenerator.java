package fr.sparna.rdf.shacl.generate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import fr.sparna.rdf.shacl.DatasetAwareShaclVisitorBase;
import fr.sparna.rdf.shacl.ShaclVisit;

/**
 * Algorithm to generation a SHACL model from data. Does not do any read operation by itself but reads its input from a data provider.
 * @author thomas
 *
 */
public class ShaclGenerator {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);
	
	private ShaclGeneratorDataProviderIfc dataProvider;


	/**
	 * Generates shapes using the given configuration and the given data provider
	 * 
	 * @param configuration
	 * @param dataProvider
	 * @return
	 */
	public Model generateShapes(
		Configuration configuration,
		ShaclGeneratorDataProviderIfc dataProvider
	) {
		this.dataProvider = dataProvider;
		return generateShapes(configuration);
	}
	
	/**
	 * Starts SHACL generation process
	 * 
	 * @param configuration
	 * @return
	 */
	private Model generateShapes(
		Configuration configuration
	) {
		Model shacl = ModelFactory.createDefaultModel();
		// add sh namespace, always
		shacl.setNsPrefix("sh", SHACLM.NS);
		shacl.setNsPrefix("xsd", XSD.NS);
		shacl.setNsPrefix("rdfs", RDFS.uri);
		shacl.setNsPrefix("rdf", RDF.uri);
		
		// add the prefix from the shapes namespace, if set
		if(configuration.getShapesNamespace() != null && configuration.getShapesNamespacePrefix() != null) {
			shacl.setNsPrefix(configuration.getShapesNamespacePrefix(), configuration.getShapesNamespace());
		}
		
		// add an ontology header
		if(configuration.getShapesOntology() != null) {
			addOntology(configuration, shacl);
		}
		
		// generate node shapes corresponding to types
		addTypes(configuration, shacl);
		log.debug("(generate) add types done");
		
		// post-process to assign datatypes
		ShaclVisit visit = new ShaclVisit(shacl);
		visit.visit(new AssignDatatypesVisitor(dataProvider));
		visit.visit(new AssignClassesVisitor(dataProvider, configuration));
		
		return shacl;
	}
	
	/**
	 * Adds an ontology header with a few basic metadata
	 * @param configuration
	 * @param shacl
	 */
	private void addOntology(
		Configuration configuration,
		Model shacl
	) {
		Resource onto = shacl.createResource(configuration.getShapesOntology());
		shacl.add(onto, RDF.type, OWL.Ontology);
		shacl.add(onto, DCTerms.created, shacl.createTypedLiteral(Calendar.getInstance()));
		
		concatOnProperty(onto, DCTerms.abstract_, "Created automatically by SHACL Play!");
	}
	

	/**
	 * Derives all NodeShapes from types in the data
	 * 
	 * @param configuration
	 * @param shacl
	 */
	private void addTypes(
			Configuration configuration,
			Model shacl
	) {
		this.dataProvider.registerMessageListener(
				s -> concatOnProperty(shacl.getResource(configuration.getShapesOntology()), RDFS.comment, s)
		);
		List<String> types = this.dataProvider.getTypes();
		log.debug("(addTypes) found {} types", types.size());
		types.forEach(type -> addType(configuration, shacl, type));
	}


	/**
	 * Derives a single NodeShape from a type
	 * 
	 * @param configuration
	 * @param shacl
	 * @param typeUri
	 */
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
		Resource typeShape = buildShapeURIFromResource(configuration, shacl, null, targetClass);

		// add the 2 triples in the output Model
		log.debug("(addType) shape name '{}' for targetClass '{}'", typeShape.getURI(), targetClass.getURI());
		shacl.add(typeShape, RDF.type, SHACLM.NodeShape);
		shacl.add(typeShape, SHACLM.targetClass, targetClass);

		// if the data provider send us any message, store them on the rdfs:comment property of the shape
		this.dataProvider.registerMessageListener(
				s -> concatOnProperty(typeShape, RDFS.comment, s)
		);
		
		// add the name
		String name = this.dataProvider.getName(typeUri, configuration.getLang());		
		if(name != null) {
			shacl.add(typeShape, RDFS.label, shacl.createLiteral(name, configuration.getLang()));
		}	
		
		// add the property shapes on this NodeShape
		addProperties(configuration, shacl, typeShape, targetClass);

	}
	
	/**
	 * Add all property shapes on a NodeShape
	 * 
	 * @param configuration
	 * @param shacl
	 * @param typeShape
	 * @param targetClass
	 */
	private void addProperties(
			Configuration configuration,
			Model shacl,
			Resource typeShape,
			Resource targetClass) {
		try {
			List<String> properties = this.dataProvider.getProperties(targetClass.getURI());
			log.debug("(addProperties) shape '{}' has {} properties", typeShape.getLocalName(), properties.size());

			properties.forEach(property -> addProperty(
					configuration,
					shacl,
					typeShape,
					targetClass,
					property));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Derives a single property shape from the given property on the given NodeShapes
	 * 
	 * @param configuration
	 * @param shacl
	 * @param typeShape
	 * @param targetClass
	 * @param property
	 */
	private void addProperty(
		Configuration configuration,
		Model shacl,
		Resource typeShape,
		Resource targetClass,
		String property
	) {
		
		Resource path = ResourceFactory.createResource(property);
		Resource propertyShape = buildShapeURIFromResource(configuration, shacl, targetClass.getLocalName(), path);

		// if the data provider sends us any message, store them on the sh:description property of the property shape
		this.dataProvider.registerMessageListener(
				s -> concatOnProperty(propertyShape, SHACLM.description, s)
		);
		
		log.debug("(addProperty) shape '{}' gets '{}'", typeShape.getLocalName(), propertyShape.getLocalName());

		shacl.add(typeShape, SHACLM.property, propertyShape);

		// add the sh:path triple to the output Model
		shacl.add(propertyShape, SHACLM.path, path);

		// add the name
		String name = this.dataProvider.getName(property, configuration.getLang());		
		if(name != null) {
			shacl.add(propertyShape, SHACLM.name, shacl.createLiteral(name, configuration.getLang()));
		}	
		
		// add min and max
		setMinCount(shacl, targetClass, path, propertyShape);
		setMaxCount(shacl, targetClass, path, propertyShape);
		
		// if it makes sense, try to find sh:valueIn
		if(configuration.getRequiresShValueInPredicate() != null && configuration.getRequiresShValueInPredicate().test(propertyShape)) {
			setInOrHasValue(configuration, shacl, targetClass, path, propertyShape);
		}
		
		// add nodeKind and other more detailed properties
		setNodeKind(configuration, shacl, targetClass, path, propertyShape);
	

	}

	/**
	 * Assigns the sh:nodeKind constraint on the property shape
	 * 
	 * @param configuration
	 * @param shacl
	 * @param targetClass
	 * @param path
	 * @param propertyShape
	 */
	private void setNodeKind(
			Configuration configuration,
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
	) {
		if (log.isTraceEnabled()) log.trace("(setNodeKind) start");

		boolean hasIri = this.dataProvider.hasIriObject(targetClass.getURI(), path.getURI());
		boolean hasBlank = this.dataProvider.hasBlankNodeObject(targetClass.getURI(), path.getURI());
		boolean hasLiteral = this.dataProvider.hasLiteralObject(targetClass.getURI(), path.getURI());

		Resource nodeKindValue = calculateNodeKind(hasIri, hasBlank, hasLiteral);
		if (nodeKindValue != null) {
			log.debug("  (setNodeKind) property shape '{}' gets node kind '{}'", propertyShape.getLocalName(), nodeKindValue.getLocalName());
			shacl.add(propertyShape, SHACLM.nodeKind, nodeKindValue);
		}
		else {
			log.warn("  (setNodeKind) no sh:nodeKind could be derived for '{}'", propertyShape.getURI());
		}
	}

	/**
	 * Computes sh:nodeKind value based on flags retrieved in the data
	 * 
	 * @param hasIri
	 * @param hasBlank
	 * @param hasLiteral
	 * @return
	 */
	private Resource calculateNodeKind(boolean hasIri, boolean hasBlank, boolean hasLiteral) {
		if (hasIri && !hasBlank && !hasLiteral) return SHACLM.IRI;
		if (!hasIri && hasBlank && !hasLiteral) return SHACLM.BlankNode;
		if (!hasIri && !hasBlank && hasLiteral) return SHACLM.Literal;
		if (hasIri && hasBlank && !hasLiteral) return SHACLM.BlankNodeOrIRI;
		if (hasIri && !hasBlank && hasLiteral) return SHACLM.IRIOrLiteral;
		if (!hasIri && hasBlank && hasLiteral) return SHACLM.BlankNodeOrLiteral;
		return null;
	}	


	private void setMinCount(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
			) {
		if (log.isTraceEnabled()) log.trace("(setMinCount) start");

		boolean hasInstanceWithoutProperty = this.dataProvider.hasInstanceWithoutProperty(targetClass.getURI(), path.getURI());
		if (!hasInstanceWithoutProperty) {
			log.debug("  (setMinCount) property shape '{}' gets sh:minCount '{}'", propertyShape.getLocalName(), 1);
			shacl.add(propertyShape, SHACLM.minCount, ResourceFactory.createTypedLiteral("1", XSDDatatype.XSDinteger));
		} else {
			log.debug("  (setMinCount) property shape '{}' cannot have sh:minCount", propertyShape.getLocalName());
		}
	}

	private void setMaxCount(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
			) {
		if (log.isTraceEnabled()) log.trace("(setMaxCount) start");

		boolean hasInstanceWithTwoProperties = this.dataProvider.hasInstanceWithTwoProperties(targetClass.getURI(), path.getURI());
		if (!hasInstanceWithTwoProperties) {
			log.debug("  (setMaxCount) property shape '{}' gets sh:maxCount '{}'", propertyShape.getLocalName(), 1);
			shacl.add(propertyShape, SHACLM.maxCount, ResourceFactory.createTypedLiteral("1", XSDDatatype.XSDinteger));
		} else {
			log.debug("  (setMaxCount) property shape '{}' cannot have sh:maxCount", propertyShape.getLocalName());
		}
	}
	
	private void setInOrHasValue(
			Configuration configuration,
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
	) {
		
		if (log.isTraceEnabled()) log.trace("(setInOrHasValue) start");

		List<RDFNode> distinctValues = this.dataProvider.listDistinctValues(targetClass.getURI(), path.getURI(), configuration.getValuesInThreshold()+1);
		if(distinctValues.size() <= configuration.getValuesInThreshold()) {
			log.debug("  (setInOrHasValue) found a maximum of '{}' distinct values, will set sh:in or sh:value", distinctValues.size());
			if(distinctValues.size() == 1) {
				shacl.add(propertyShape, SHACLM.value, distinctValues.get(0));
			} else {
				RDFList list = shacl.createList(distinctValues.iterator());
				shacl.add(propertyShape, SHACLM.in, list);
			}
		}
		
	}
	
	private Resource buildShapeURIFromResource(
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
					: firstPart + "_" + originalResource.getLocalName();
			
			// Create a resource with that URI
			Resource resource = shacl.createResource(configuration.getShapesNamespace() + localName);

			// determine if Resource with that URI already exists
			// deal with the case of edm:rights vs. dc:rights, edm:type vs. dc:type, etc.
			boolean hasSameTypeShape = shacl.contains(resource, null, (RDFNode) null);
			if (!hasSameTypeShape) {
				return resource;
			} else {
				// if there was already one, compute a second URI
				localName = localName+"_2";
				return shacl.createResource(configuration.getShapesNamespace() + localName);
			}	
		}
		
	}

	public static String shortenUri(Model shacl, String uri) {
		return shortenUri(shacl, ResourceFactory.createResource(uri));
	}

	public static String shortenUri(Model shacl, Resource resource) {
		if(resource.isAnon()) return resource.toString();
		
		String prefix = shacl.getNsURIPrefix(resource.getNameSpace());
		if (prefix == null) return resource.getURI();

		return prefix + ":" + resource.getLocalName();
	}
	
	public static List<String> shortenUri(Model shacl, List<String> uris) {
	    return uris.stream()
	               .map(uri -> shortenUri(shacl, uri))
	               .collect(Collectors.toList());
	  }

	public static String getMessage(String messagePattern, Object... parameters) {
		return MessageFormatter.arrayFormat(messagePattern, parameters)
				.getMessage();
	}
	
	public static void concatOnProperty(Resource r, Property p, String s) {
		if(r.getProperty(p) != null) {
			String currentValue = r.getProperty(p).getObject().asLiteral().getLexicalForm();
			r.removeAll(p);
			r.addProperty(p, currentValue+".\n"+s);
		} else {
			r.addProperty(p, s);
		}
	}
	
	
}
