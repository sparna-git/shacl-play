package fr.sparna.rdf.shacl.generate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
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

import fr.sparna.rdf.shacl.generate.progress.NoOpProgressMonitor;
import fr.sparna.rdf.shacl.generate.progress.ProgressMonitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignClassesVisitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignDatatypesVisitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignMinCountAndMaxCountVisitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignNodeKindVisitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignValueOrInVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisitorIfc;

/**
 * Algorithm to generation a SHACL model from data. Does not do any read operation by itself but reads its input from a data provider.
 * @author thomas
 *
 */
public class ShaclGenerator {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);
	
	private static final String DEFAULT_MESSAGE_LANG = "en";
	
	private ShaclGeneratorDataProviderIfc dataProvider;
	
	protected ProgressMonitor progressMonitor;	

	/**
	 * Whether to skip datatype assignement
	 */
	protected boolean skipDatatypes = false;

	/**
	 * Whether sh:name and rdfs:label should be generated on the generated shapes, from the URI
	 */
	protected boolean generateLabels = true;
	
	/**
	 * Base visitors - cannot be changed
	 */
	protected transient List<ShaclVisitorIfc> visitors;

	/**
	 * Extra visitors that can be set from the outside
	 */
	protected List<ShaclVisitorIfc> extraVisitors = new ArrayList<>();
	
	
	
	public ShaclGenerator() {
		super();
		this.progressMonitor = new NoOpProgressMonitor();
	}

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
		
		// add base visitors
		this.visitors = new ArrayList<ShaclVisitorIfc>();
		this.visitors.add(new AssignNodeKindVisitor(dataProvider));
		this.visitors.add(new AssignClassesVisitor(dataProvider, configuration.getModelProcessor()));
		if(!skipDatatypes) {
			this.visitors.add(new AssignDatatypesVisitor(dataProvider));
		}
		this.visitors.add(new AssignMinCountAndMaxCountVisitor(dataProvider));
		this.visitors.add(new AssignValueOrInVisitor(dataProvider));
		
		// add extra visitors
		if(this.extraVisitors != null) {
			log.debug("(generate) Add "+this.extraVisitors.size()+" extra visitors");
			this.visitors.addAll(this.extraVisitors);
		}
		
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
		this.progressMonitor.beginTask("Shapes generation", this.visitors.size()+1);
		
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
		this.progressMonitor.subTask("Creating node shapes and property shapes");
		addTypes(configuration, shacl);
		this.progressMonitor.worked(1);
		log.debug("(generate) add types done");
		
		// post-process to assign nodeKind, datatypes and classes, etc.
		ShaclVisit visit = new ShaclVisit(shacl);
		for (ShaclVisitorIfc visitor : this.visitors) {
			this.progressMonitor.subTask(visitor.getClass().getSimpleName());
			visit.visit(visitor);
			this.progressMonitor.worked(1);
		}
		
		this.progressMonitor.done();
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
		
		concatOnProperty(onto, DCTerms.abstract_, "Generated by SHACL Play!", DEFAULT_MESSAGE_LANG);
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
		// if the data provider send us any message, store them on the rdfs:comment property of the ontology
		this.dataProvider.registerMessageListener(
				s -> concatOnProperty(shacl.getResource(configuration.getShapesOntology()), RDFS.comment, s, DEFAULT_MESSAGE_LANG)
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
		if (configuration.getModelProcessor().isIgnoredType(typeUri)) {
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
				s -> concatOnProperty(typeShape, RDFS.comment, s, DEFAULT_MESSAGE_LANG)
		);
		
		if(this.generateLabels) {
			// add the name
			String name = this.dataProvider.getName(typeUri, configuration.getLang());		
			if(name != null) {
				shacl.add(typeShape, RDFS.label, shacl.createLiteral(name, configuration.getLang()));
			}
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
				s -> concatOnProperty(propertyShape, SHACLM.description, s, DEFAULT_MESSAGE_LANG)
		);
		
		log.debug("(addProperty) shape '{}' gets '{}'", typeShape.getLocalName(), propertyShape.getLocalName());

		shacl.add(typeShape, SHACLM.property, propertyShape);

		// add the sh:path triple to the output Model
		shacl.add(propertyShape, SHACLM.path, path);

		if(this.generateLabels) {
			// add the name
			String name = this.dataProvider.getName(property, configuration.getLang());		
			if(name != null) {
				shacl.add(propertyShape, SHACLM.name, shacl.createLiteral(name, configuration.getLang()));
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

	public List<ShaclVisitorIfc> getExtraVisitors() {
		return extraVisitors;
	}

	public void setExtraVisitors(List<ShaclVisitorIfc> extraVisitors) {
		this.extraVisitors = extraVisitors;
	}

	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public boolean isSkipDatatypes() {
		return skipDatatypes;
	}

	public void setSkipDatatypes(boolean skipDatatypes) {
		this.skipDatatypes = skipDatatypes;
	}

	public boolean isGenerateLabels() {
		return generateLabels;
	}

	public void setGenerateLabels(boolean generateLabels) {
		this.generateLabels = generateLabels;
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
		concatOnProperty(r, p, s, null);
	}
	
	public static void concatOnProperty(Resource r, Property p, String s, String lang) {
		if(r.getProperty(p) != null) {
			String currentValue = r.getProperty(p).getObject().asLiteral().getLexicalForm();
			r.removeAll(p);
			if(lang != null) {
				r.addProperty(p, currentValue+".\n"+s, lang);
			} else {
				r.addProperty(p, currentValue+".\n"+s);
			}
		} else {
			if(lang != null) {
				r.addProperty(p, s, lang);
			} else {
				r.addProperty(p, s);
			}
		}
	}
	
	
}
