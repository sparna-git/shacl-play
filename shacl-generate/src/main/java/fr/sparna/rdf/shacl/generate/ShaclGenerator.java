package fr.sparna.rdf.shacl.generate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

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
		
		// count the total number of triples
		int count = this.dataProvider.countTriples();		
		if(count > 0) {
			log.debug("(count) ontology '{}' gets count '{}'", onto.getLocalName(), count);
			concatOnProperty(onto, DCTerms.abstract_, "Total triples in the data:"+count);
		}
		
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
				
		// add the count
		int count = this.dataProvider.countInstances(typeUri);		
		if(count > 0) {
			log.debug("  (count) node shape '{}' gets count '{}'", typeShape.getLocalName(), count);
			// TODO : find more suitable property to store number of instances
			concatOnProperty(typeShape, RDFS.comment, count+" instances");
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
		
		// add the count
		int count = this.dataProvider.countStatements(targetClass.getURI(), property);		
		if(count > 0) {
			log.debug("  (count) property shape '{}' gets count '{}'", propertyShape.getLocalName(), count);
			// TODO : find more suitable property to store number of instances
			concatOnProperty(propertyShape, SHACLM.description, count+" statements");
		}
		
		// add min and max
		setMinCount(shacl, targetClass, path, propertyShape);
		setMaxCount(shacl, targetClass, path, propertyShape);
		
		// if it makes sense, try to find sh:valueIn
		if(configuration.getRequiresShValueInPredicate() != null && configuration.getRequiresShValueInPredicate().test(propertyShape)) {
			setInOrHasValue(shacl, targetClass, path, propertyShape);
		}
		
		// add nodeKind and other more detailled properties
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

		if (nodeKindValue == SHACLM.Literal) {
			setShaclDatatype(configuration, shacl, targetClass, path, propertyShape);
		} else if (nodeKindValue == SHACLM.IRI) {
			setShaclClass(configuration, shacl, targetClass, path, propertyShape);
		}
	}

	private void setShaclDatatype(
			Configuration configuration,
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
	) {
		List<String> datatypes = this.dataProvider.getDatatypes(targetClass.getURI(), path.getURI());
		
		if(datatypes.size() > 1) {
			log.warn(datatypes.size()+" datatypes found for property '{}' in class '{}'", path.getURI(), targetClass.getURI());
			
			// add sh:or list to property shape:
			//    first create RDF list and then add it to property shape		
			List<Resource> orInstances = datatypes.stream().map(datatype -> {
				Resource orInstance = ResourceFactory.createResource(propertyShape.getURI() + "_datatype_" + datatypes.indexOf(datatype));
				shacl.add(orInstance, SHACLM.datatype, shacl.createResource(datatype));				
				return orInstance;
			}).collect(Collectors.toList());
			
			
			RDFList orInstancesList = shacl.createList(orInstances.iterator());
			shacl.add(propertyShape, SHACLM.or, orInstancesList);
		} else {
			log.debug("  (setShaclDatatype) property shape '{}' gets sh:datatype '{}'", propertyShape.getLocalName(), datatypes.get(0));
			shacl.add(propertyShape, SHACLM.datatype, shacl.createResource(datatypes.get(0)));
			
			if (RDF.langString.getURI().equals(datatypes.get(0))) {
		      setLanguageIn(shacl, targetClass, path, propertyShape);
		    }
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
	
	/**
	 * Assigns the sh:class constraint on the property shape
	 * 
	 * @param configuration
	 * @param shacl
	 * @param targetClass
	 * @param path
	 * @param propertyShape
	 */
	private void setShaclClass(
			Configuration configuration,
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
	) {
		List<String> classes = calculateClasses(configuration, targetClass, path);

		// always remove owl:NamedIndividual from the result
		classes.remove("http://www.w3.org/2002/07/owl#NamedIndividual");
		
		if (classes.isEmpty()) {
			String message = getMessage(
					"type '{}' and property '{}' is considered an 'rdfs:Resource'.",
					shortenUri(shacl, targetClass),
					shortenUri(shacl, path)
			);
			log.warn(message);
			return;
		}


		if (classes.size() > 1) {			
			// add sh:or list to property shape:
			//    first create RDF list and then add it to property shape
			List<Resource> orInstances = classes.stream().map(aClass -> {				
			Resource orInstance = ResourceFactory.createResource(propertyShape.getURI() + "_class_" + classes.indexOf(aClass));
				shacl.add(orInstance, SHACLM.class_, shacl.createResource(aClass));				
				return orInstance;
			}).collect(Collectors.toList());			
			
			RDFList orInstancesList = shacl.createList(orInstances.iterator());
			shacl.add(propertyShape, SHACLM.or, orInstancesList);
			return;
		}

		log.debug("  (setShaclClass) property shape '{}' gets sh:class '{}'", propertyShape.getLocalName(), classes.get(0));
		shacl.add(propertyShape, SHACLM.class_, shacl.createResource(classes.get(0)));
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

	private void setMinCount(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
			) {
		if (log.isTraceEnabled()) log.trace("(setMinCount) start");

		boolean hasInstanceWithoutProperty = this.dataProvider.hasInstanceWithoutProperty(targetClass.getURI(), path.getURI());
		if (!hasInstanceWithoutProperty) {
			shacl.add(propertyShape, SHACLM.minCount, ResourceFactory.createTypedLiteral("1", XSDDatatype.XSDinteger));
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
			shacl.add(propertyShape, SHACLM.maxCount, ResourceFactory.createTypedLiteral("1", XSDDatatype.XSDinteger));
		}
	}

	private void setLanguageIn(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape) {
		List<String> languages = this.dataProvider.getLanguages(targetClass.getURI(), path.getURI());

		List<Literal> languagesAsLiterals = languages.stream().map(s -> shacl.createLiteral(s)).collect(Collectors.toList());
		RDFList languagesList = shacl.createList(languagesAsLiterals.iterator());

		log.debug("  (setLanguageIn) property shape '{}' gets sh:languageIn '{}'", propertyShape.getLocalName(), languages);
		shacl.add(propertyShape, SHACLM.languageIn, languagesList);
	}
	
	private void setInOrHasValue(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
			) {
		
		if (log.isTraceEnabled()) log.trace("(setInOrHasValue) start");

		int valueCount = this.dataProvider.hasLessThanValues(targetClass.getURI(), path.getURI(), 10);
		if(valueCount > 0) {
			log.debug("  (setInOrHasValue) found '{}' different values", valueCount);
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

	private static String shortenUri(Model shacl, String uri) {
		return shortenUri(shacl, ResourceFactory.createResource(uri));
	}

	private static String shortenUri(Model shacl, Resource resource) {
		if(resource.isAnon()) return resource.toString();
		
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
	
	private static void concatOnProperty(Resource r, Property p, String s) {
		if(r.getProperty(p) != null) {
			String currentValue = r.getProperty(p).getObject().asLiteral().getLexicalForm();
			r.removeAll(p);
			r.addProperty(p, currentValue+".\n"+s);
		} else {
			r.addProperty(p, s);
		}
	}

}
