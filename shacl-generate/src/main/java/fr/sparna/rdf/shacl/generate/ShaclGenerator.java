package fr.sparna.rdf.shacl.generate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;


public class ShaclGenerator {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);
	
	private ShaclGeneratorDataProviderIfc dataProvider;


	/**
	 * Generates shapes from target remote endpoint
	 * 
	 * @param configuration
	 * @param endpointUrl
	 * @return
	 */
	public Model generateShapes(
		Configuration configuration,
		ShaclGeneratorDataProviderIfc dataProvider
	) {
		this.dataProvider = dataProvider;
		return generateShapes(configuration);
	}
	
	private Model generateShapes(
		Configuration configuration
	) {
		Model shacl = ModelFactory.createDefaultModel();
		// add sh namespace, always
		shacl.setNsPrefix("sh", SHACLM.NS);
		// add the prefix from the shapes namespace, if set
		if(configuration.getShapesNamespace() != null && configuration.getShapesNamespacePrefix() != null) {
			shacl.setNsPrefix(configuration.getShapesNamespacePrefix(), configuration.getShapesNamespace());
		}
		addTypes(configuration, shacl);
		log.debug("(generate) add types done");

		return shacl;
	}

	private void addTypes(
			Configuration configuration,
			Model shacl
	) {
		List<String> types = this.dataProvider.getTypes();
		log.debug("(addTypes) found {} types", types.size());
		types.forEach(type -> addType(configuration, shacl, type));
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
		try {
			List<String> properties = this.dataProvider.getProperties(targetClass.getURI());
			if (log.isDebugEnabled())
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
		
		// this is not mandatory, so remove it
		// shacl.add(propertyShape, RDF.type, SHACLM.PropertyShape);

		shacl.add(propertyShape, SHACLM.path, path);

//		setMinCount(rdfStoreService, shacl, targetClass, path, propertyShape);
//		setMaxCount(rdfStoreService, shacl, targetClass, path, propertyShape);
//
		setNodeKind(configuration, shacl, targetClass, path, propertyShape);
	}

	
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
			shacl.add(propertyShape, SHACLM.nodeKind, nodeKindValue);
			if (log.isDebugEnabled())
				log.debug("  (setNodeKind) property shape '{}' gets node kind '{}'", propertyShape.getLocalName(), nodeKindValue.getLocalName());

		}
		else {
			log.warn("No sh:nodeKind could be derived for '{}'", propertyShape.getURI());
		}

		if (nodeKindValue == SHACLM.Literal) {
			// setShaclDatatype(rdfStore, shacl, targetClass, path, propertyShape);
		}
		else if (nodeKindValue == SHACLM.IRI) {
			setShaclClass(configuration, shacl, targetClass, path, propertyShape);
		}
	}

	private Resource calculateNodeKind(boolean hasIri, boolean hasBlank, boolean hasLiteral) {
		if (hasIri && !hasBlank && !hasLiteral) return SHACLM.IRI;
		if (!hasIri && hasBlank && !hasLiteral) return SHACLM.BlankNode;
		if (!hasIri && !hasBlank && hasLiteral) return SHACLM.Literal;
		if (hasIri && hasBlank && !hasLiteral) return SHACLM.BlankNodeOrIRI;
		if (hasIri && !hasBlank && hasLiteral) return SHACLM.IRIOrLiteral;
		if (!hasIri && hasBlank && hasLiteral) return SHACLM.BlankNodeOrLiteral;
		return null;
	}	
	
	private void setShaclClass(
			Configuration configuration,
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
	) {
		List<String> classes = calculateClasses(configuration, targetClass, path);

		if (classes.isEmpty()) {
			String message = getMessage("type '{}' and property '{}' is considered an 'rdfs:Resource'.",
					shortenUri(shacl, targetClass), shortenUri(shacl, path));
			log.warn(message);
			return;
		}


		if (classes.size() != 1) {
			String message = getMessage(
					"type '{}' and property '{}' does not have exactly one class: {}",
					shortenUri(shacl, targetClass),
					shortenUri(shacl, path),
					shortenUri(shacl, classes)
			);
			log.warn(message);
			return;
		}

		Resource classValue = ResourceFactory.createResource(classes.get(0));
		shacl.add(propertyShape, SHACLM.class_, classValue);
	}
	
	private List<String> calculateClasses(
			Configuration configuration,
			Resource targetClass,
			Resource path) {
		
		List<String> classes = this.dataProvider.getObjectTypes(targetClass.getURI(), path.getURI());
		// return is 0 or 1 result
		if (classes.size() <= 1) return classes;

		// try to translate lots of types to 1
		Set<String> classSet = new HashSet<>(classes);
		// String translation = configuration.getTypeTranslation(classSet);
		String translation = null;
		if (translation != null) return Collections.singletonList(translation);

		// cleanup unused types
		configuration.getIgnoredClasses().forEach(classSet::remove);

		// we tried, return as what's left
		return new ArrayList<>(classes);
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
					: firstPart + "_" + originalResource.getLocalName();
			
			return shacl.createResource(configuration.getShapesNamespace() + localName);
			
			// Create a resource with that URI
			// Resource typeShape = ResourceFactory.createResource(configuration.getShapesNamespace() + localName);

			// determine if Resource with that URI already exists, in which case return it
//			boolean hasSameTypeShape = shacl.contains(typeShape, null, (RDFNode) null);
//			if (!hasSameTypeShape) {
//				return typeShape;
//			} else {
//				return typeShape;
//			}

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

	private static String shortenUri(Model shacl, String uri) {
		return shortenUri(shacl, ResourceFactory.createResource(uri));
	}

	private static String shortenUri(Model shacl, Resource resource) {
		String prefix = shacl.getNsURIPrefix(resource.getNameSpace());
		if (prefix == null) return resource.getURI();

		return prefix + ":" + resource.getLocalName();
	}
	
	private static List<String> shortenUri(Model shacl, List<String> uris) {
	    return uris.stream()
	               .map(uri -> shortenUri(shacl, uri))
	               .collect(Collectors.toList());
	  }

	private String getMessage(String messagePattern, Object... parameters) {
		return MessageFormatter.arrayFormat(messagePattern, parameters)
				.getMessage();
	}

}
