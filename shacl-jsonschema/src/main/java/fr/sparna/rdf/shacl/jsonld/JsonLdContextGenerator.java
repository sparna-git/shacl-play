package fr.sparna.rdf.shacl.jsonld;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.PropertyShape;
import fr.sparna.rdf.jena.shacl.ShapesGraph;

public class JsonLdContextGenerator {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected String atIdMapping = "id";
	protected String atTypeMapping = "type";
	protected String atGraphMapping = "graph";

	protected boolean defaultContainerSet = true;
	protected boolean defaultContainerLanguage = true;

	protected transient ShapesGraph shapesGraph = null;
	
	public String generateJsonLdContext(Model model) {	
		// init the shapes graph
		this.shapesGraph = new ShapesGraph(model, null);
		
		// init final context
		JsonLdContext context = new JsonLdContext();
		
		// ### set hardcoded mappings
		if(this.atIdMapping != null) {
			context.add(new JsonLdMapping(this.atIdMapping, "@id"));
		}
		if(this.atTypeMapping != null) {
			context.add(new JsonLdMapping(this.atTypeMapping, "@type"));
		}
		if(this.atGraphMapping != null) {
			context.add(new JsonLdMapping(this.atGraphMapping, "@graph"));
		}
		
		// ### map each known prefixes
		List<String> keys = new ArrayList<>(model.getNsPrefixMap().keySet());

		// sort the list
		keys.sort((ns1,ns2) -> ns1.compareToIgnoreCase(ns2));
		boolean atLeastOne = false;
		for (String aKey : keys) {
			String idContext = model.getNsPrefixMap().get(aKey);
			if(!idContext.equals(SH.BASE_URI)) {
				// garantee we start a new section only if there is at least one prefix to add
				if(!atLeastOne) {
					context.startNewSection();
					atLeastOne = true;
				}
				context.add(new JsonLdMapping(aKey, idContext));
			}
		}

		
		
		// ### map each NodeShape
		List<NodeShape> nodeShapes = shapesGraph.getAllNodeShapes();
		List<JsonLdMapping> nodeShapeMappings = buildNodeShapesMappings(nodeShapes);
		
		// print
		if(nodeShapeMappings.size() > 0) {
			context.startNewSection();
			nodeShapeMappings.forEach(m -> context.add(m));
		}		

		// find each PropertyShape		
		List<PropertyShape> propertyShapes = shapesGraph.getAllPropertyShapes();
		// remove deactivated ones
		propertyShapes.removeIf(ps -> ps.isDeactivated());
		
		if(propertyShapes.size() > 0) {
			context.startNewSection();
		}

		// find each paths in property shapes
		List<Resource> paths = new ArrayList<>(propertyShapes.stream().map(p -> p.getShPath())
				// exclude blank nodes / property paths
				.filter(r -> r.isURIResource())
				.collect(Collectors.toSet())
		);
		// sort the list
		paths.sort((p1,p2) -> p1.getLocalName().compareToIgnoreCase(p2.getLocalName()));
		
		// ### map each paths...
		for(Resource path : paths) {
			// ### determine the term : the shacl-play:shortname annotation, or the localName by default
			Set<String> shortnames = findShortNamesOfPath(path, model);
			if(shortnames.isEmpty()) {
				shortnames.add(path.getLocalName());
			} 
			String term = shortnames.iterator().next();
			if(shortnames.size() > 1) {
				log.warn("Found multiple shortnames for path "+path+", will use only one : '"+term+"'");
			}

			JsonLdMapping mapping = new JsonLdMapping(term,path.getModel().shortForm(path.getURI()));
			
			// ### determine the @type
			// if there is a sh:datatype, set type as the datatype
			Set<Resource> datatypes = findDatatypesOfPath(path, model);
			if(datatypes.size() > 1) {
				log.warn("Found different datatypes declared for path "+path+", will declare only one");
			} else if(!datatypes.isEmpty()) {
				Resource theDatatype = datatypes.iterator().next();
				String datatype = theDatatype.getURI();
				
				if(datatype.startsWith(XSD.NS)) {
					if(!datatype.equals(XSD.xstring.getURI())) {
						// xsd:string is the default datatype in JSON-LD, so we don't need to declare it
						mapping.setType("xsd:"+theDatatype.getLocalName());
					}
					
				} else if (datatype.equals(RDF.langString.getURI())) {
					// if there is a sh:languageIn with a single value, set a @language to the value
					// otherwise map it to a @container: @language
					if(findShLanguageInOfPath(path, model).size() == 1) {
						List<Literal> languages = findShLanguageInOfPath(path, model).iterator().next();
						if(languages.size() == 1) {
							Literal langLiteral = languages.get(0);
							mapping.setLanguage(langLiteral.getString());
						}
					} else {
						if(this.defaultContainerLanguage) {
							mapping.setContainer("@language");
						}
					}
					
				} else {					
					mapping.setType(theDatatype.getModel().shortForm(theDatatype.getURI()));
				}

			}
			
			// if there are sh:class, or sh:nodeKind = sh:IRI, or sh:nodeKind = sh:BlankNodeOrIRI, set the type to @id
			// note that we don't read sh:node only as it can point to a Literal
			// nodeKinds and classes can be defined either on the property shape or on the node shape linked to the property shape
			Set<Resource> classes = findShClassOfPath(path, model);
			Set<Resource> nodeKinds = findShNodeKindOfPath(path, model);
			
			if(
					!classes.isEmpty()
					||
					nodeKinds.stream().anyMatch(r -> r.getURI().equals(SH.IRI.getURI()))
					||
					nodeKinds.stream().anyMatch(r -> r.getURI().equals(SH.BlankNodeOrIRI.getURI()))
			) {
				mapping.setType("@id");
			}

			// use the sh:pattern to produce an inner @context with @base if the pattern is a simple startsWith regex
			Set<Literal> patterns = findPatternsOfPath(path, model);
			if(patterns.size() > 1) {
				log.warn("Found multiple patterns for path "+path+", will use only one");
			}
			if(!patterns.isEmpty()) {
				Literal patternLiteral = patterns.iterator().next();
				String pattern = patternLiteral.getString();
				// check if it is a simple startsWith pattern
				String startsWith = RegexUtil.extractHttpBaseUriFromPattern(pattern);
				if(startsWith != null) {
					// create an inner context with @base
					JsonLdContext innerContext = new JsonLdContext();
					innerContext.add(new JsonLdMapping("@base", startsWith));
					mapping.setInnerContext(innerContext);
				}
			}
			
			if(defaultContainerSet) {
				if(datatypes.isEmpty() || !datatypes.iterator().next().getURI().equals(RDF.langString.getURI())) {
					Set<Integer> maxCounts = findShMaxCountOfPath(path, model);
					if(maxCounts.size() == 0) {
						mapping.setContainer("@set");
					} else {
						Integer maxCount = maxCounts.iterator().next();
						if(maxCount > 1) {
							mapping.setContainer("@set");
						}
					}
				}
			}
			

			context.add(mapping);
			
			
			// ### map @language
			// if the datatype is rdf:langString, then...
			// if there is a sh:languageIn with a single value, set a @language to the value
			// otherwise map it to a @container: @language
			
			// ### map controlled values
			// if we have an sh:hasValue or sh:in, then map the possible values to JSON terms
		}
		
		// then serialize and debug
		StringBuffer buffer = new StringBuffer();
		context.write(buffer);
		return buffer.toString();
	}	

	public List<JsonLdMapping> buildNodeShapesMappings(List<NodeShape> nodeShapes) {
		// for each node shape, read its sh:targetClass if there is one
		// and add a mapping with the localName of the target class as term, and the short form of the class URI as value
		List<JsonLdMapping> mappings = new ArrayList<>();
		for (NodeShape nodeShape : nodeShapes) {
			List<Resource> targetClasses = nodeShape.getTargetClasses();
			for (Resource targetClass : targetClasses) {
				JsonLdMapping mapping = new JsonLdMapping(targetClass.getLocalName(), targetClass.getModel().shortForm(targetClass.getURI()));
				mappings.add(mapping);
			}
		}
		// sort mappings by term
		mappings.sort((m1,m2) -> m1.getTerm().compareToIgnoreCase(m2.getTerm()));

		return mappings;
	}
	
	public boolean isDefaultContainerSet() {
		return defaultContainerSet;
	}

	public void setDefaultContainerSet(boolean useContainerSet) {
		this.defaultContainerSet = useContainerSet;
	}

	public boolean isDefaultContainerLanguage() {
		return defaultContainerLanguage;
	}

	public void setDefaultContainerLanguage(boolean defaultContainerLanguage) {
		this.defaultContainerLanguage = defaultContainerLanguage;
	}
	
	private Set<Resource> findDatatypesOfPath(Resource path, Model model) {
		List<PropertyShape> propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		Set<Resource> datatypes = propertyShapesWithPath.stream()
			.map(ps -> ps.getShDatatype())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());

		// append datatypes defined in node shapes linked to this path
		Set<Resource> nodes = findShNodeOfPath(path, model);
		for(Resource node : nodes) {
			NodeShape nodeShape = this.shapesGraph.findNodeShapeByResource(node);
			if(nodeShape != null) {
				Optional<Resource> nodeDatatype = nodeShape.getShDatatype();
				if(nodeDatatype.isPresent()) {
					datatypes.add(nodeDatatype.get());
				}
			}
		}

		return datatypes;		
	}
	
	private Set<Resource> findShClassOfPath(Resource path, Model model) {
		List<PropertyShape> propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		Set<Resource> classes = propertyShapesWithPath.stream()
			.map(ps -> ps.getShClass())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());

		// append classes defined in node shapes linked to this path
		Set<Resource> nodes = findShNodeOfPath(path, model);
		for(Resource node : nodes) {
			NodeShape nodeShape = this.shapesGraph.findNodeShapeByResource(node);
			if(nodeShape != null) {
				Optional<Resource> nodeClass = nodeShape.getShClass();
				if(nodeClass.isPresent()) {
					classes.add(nodeClass.get());
				}
			}
		}

		return classes;		
	}

	private Set<List<Literal>> findShLanguageInOfPath(Resource path, Model model) {
		List<PropertyShape> propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		Set<List<Literal>> languageIn = propertyShapesWithPath.stream()
			.map(ps -> ps.getShLanguageIn())
			.filter(li -> !li.isEmpty())
			.collect(Collectors.toSet());

		return languageIn;		
	}

	private Set<Literal> findPatternsOfPath(Resource path, Model model) {
		List<PropertyShape> propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		Set<Literal> patterns = propertyShapesWithPath.stream()
			.map(ps -> ps.getShPattern())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());

		// append patterns defined in node shapes linked to this path
		Set<Resource> nodes = findShNodeOfPath(path, model);
		for(Resource node : nodes) {
			NodeShape nodeShape = this.shapesGraph.findNodeShapeByResource(node);
			if(nodeShape != null) {
				Optional<Literal> nodePattern = nodeShape.getShPattern();
				if(nodePattern.isPresent()) {
					patterns.add(nodePattern.get());
				}
			}
		}

		return patterns;		
	}
	
	private Set<Resource> findShNodeOfPath(Resource path, Model model) {
		List<PropertyShape> propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		Set<Resource> nodes = propertyShapesWithPath.stream()
			.map(ps -> ps.getShNode())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());
		return nodes;		
	}
	
	private Set<Integer> findShMaxCountOfPath(Resource path,Model model) {
		List<PropertyShape> propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		Set<Integer> shMaxCount = propertyShapesWithPath.stream()
			.map(ps -> ps.getShMaxCount())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());
		return shMaxCount;	
	}
	
	private Set<Resource> findShNodeKindOfPath(Resource path, Model model) {
		List<PropertyShape> propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		Set<Resource> nodeKinds = propertyShapesWithPath.stream()
			.map(ps -> ps.getShNodeKind())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());

		// append nodeKind defined in node shapes linked to this path
		Set<Resource> nodes = findShNodeOfPath(path, model);
		for(Resource node : nodes) {
			NodeShape nodeShape = this.shapesGraph.findNodeShapeByResource(node);
			if(nodeShape != null) {
				Optional<Resource> nodeNodeKind = nodeShape.getShNodeKind();
				if(nodeNodeKind.isPresent()) {
					nodeKinds.add(nodeNodeKind.get());
				}
			}
		}

		return nodeKinds;			
	}

	private Set<String> findShortNamesOfPath(Resource path, Model model) {
		List<PropertyShape> propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		Set<String> shortnames = propertyShapesWithPath.stream()
			.map(ps -> ps.getShaclPlayShortName())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(l -> l.getString())
			.collect(Collectors.toSet());
		return shortnames;	
	}

	
	public static void main(String... args) throws Exception {
		Model shaclGraph = ModelFactory.createDefaultModel();
		
		String shaclFile = args[0];
		String outputContext = args[1];
		
		if(shaclFile.startsWith("http")) {
			shaclGraph.read(shaclFile, RDF.uri, FileUtils.guessLang(shaclFile, "Turtle"));
		} else {
			shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));
		}
		
		JsonLdContextGenerator generator = new JsonLdContextGenerator();
		String context = generator.generateJsonLdContext(shaclGraph);
		System.out.println(context);
	}
	
}
