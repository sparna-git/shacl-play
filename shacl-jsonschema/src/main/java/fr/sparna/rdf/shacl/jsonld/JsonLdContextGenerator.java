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
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.PropertyPath;
import fr.sparna.rdf.jena.shacl.PropertyShape;
import fr.sparna.rdf.jena.shacl.Shape;
import fr.sparna.rdf.jena.shacl.ShapesGraph;
import fr.sparna.rdf.shacl.SHACL_PLAY;

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
			// add the default type mapping only if rdf:type is not declared explicitly in the shapes
			// to avoid conflicts
			if(findAllShapesToConsider(this.atTypeMapping, RDF.type.asResource(), model).isEmpty()) {
				context.add(new JsonLdMapping(this.atTypeMapping, "@type"));
			}
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
			if(!idContext.equals(SH.BASE_URI) && !idContext.equals(SHACL_PLAY.NAMESPACE)) {
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
				// include only URI resources or inverse paths
				.filter(r -> r.isURIResource() || new PropertyPath(r).isInverse())
				.collect(Collectors.toSet())
		);
		// note : mappings are sorted in the JsonLdContext class
		
		// ### map each paths...
		for(Resource path : paths) {
			// ### determine the term : the shacl-play:shortname annotation, or the localName by default
			Set<String> shortnames = findShortNamesOfPath(path, model);
			if(shortnames.isEmpty()) {
				PropertyPath propertyPath = new PropertyPath(path);
				if(propertyPath.isInverse()) {
					// provide sensible default for inverse paths
					shortnames.add("inverse_"+propertyPath.getShInversePath().getLocalName());
				} else {
					shortnames.add(path.getLocalName());
				}
			}

			// iterate on all shortnames and generate a mapping for each shortname
			for(String shortname: shortnames) {
				JsonLdMapping mapping;
				PropertyPath propertyPath = getPathOfShortNameOrPath(shortname, path, model);
				if(propertyPath.isInverse()) {
					mapping = new JsonLdMapping(
						shortname,
						model.shortForm(propertyPath.getShInversePath().getURI()),
						 true
					);
				} else {
					if(path.getURI() != null && path.getURI().equals(RDF.type.getURI())) {
						// rdf:type is changed to @type, so that the compaction of @type works
						mapping = new JsonLdMapping(shortname,"@type");
					} else {
						mapping = new JsonLdMapping(shortname,path.getModel().shortForm(path.getURI()));
					}
						
				}

				// ### determine the @type
				// if there is a sh:datatype, set type as the datatype
				Set<Resource> datatypes = findDatatypesOfShortname(shortname, path, model);
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
						if(findShLanguageInOfShortname(shortname, path, model).size() == 1) {
							List<Literal> languages = findShLanguageInOfShortname(shortname, path, model).iterator().next();
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
				Set<Resource> classes = findShClassOfShortname(shortname, path, model);
				Set<Resource> nodeKinds = findShNodeKindOfShortname(shortname, path, model);
				
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
				Set<Literal> patterns = findPatternsOfShortname(shortname, path, model);
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
				} else {
					
					// look through sh:or to determine if there is a common base URI to all patterns
					Set<Literal> patternsThroughShOr = findPatternsOfShortnameThroughShOr(shortname, path, model);

					List<String> startsWithList = patternsThroughShOr.stream()
						.map(lit -> lit.getString())
						.map(pat -> RegexUtil.extractHttpBaseUriFromPattern(pat))
						.filter(sw -> sw != null)
						.collect(Collectors.toList());				
						
					if(startsWithList.size() > 0) {
						// now check if they have some common base
						String commonBase = RegexUtil.findCommonBaseUri(startsWithList);
						if(commonBase != null) {
							// create an inner context with @base
							JsonLdContext innerContext = new JsonLdContext();
							innerContext.add(new JsonLdMapping("@base", commonBase));
							mapping.setInnerContext(innerContext);
						}
					}
				}
				
				if(defaultContainerSet) {
					if(datatypes.isEmpty() || !datatypes.iterator().next().getURI().equals(RDF.langString.getURI())) {
						Set<Integer> maxCounts = findShMaxCountOfShortname(shortname, path, model);
						if(maxCounts.size() == 0) {
							Set<RDFNode> hasValues = findHasValueOfShortname(shortname, path, model);
							if(hasValues.size() == 0) {
								// if there is a sh:hasValue, it means that the property cannot have multiple values, hance we don't set @container to @set 
								mapping.setContainer("@set");
							}							
						} else {
							Integer maxCount = maxCounts.iterator().next();
							if(maxCount > 1) {
								mapping.setContainer("@set");
							}
						}
					}
				}

				// ### map @language
				// if the datatype is rdf:langString, then...
				// if there is a sh:languageIn with a single value, set a @language to the value
				// otherwise map it to a @container: @language
				
				// ### map controlled values
				// if we have an sh:hasValue or sh:in, then map the possible values to JSON terms

				context.add(mapping);
			}
			

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

	private Set<Resource> findDatatypesOfShortname(String shortname, Resource path, Model model) {
		List<Shape> shapes = findAllShapesToConsider(shortname, path, model);
		
		Set<Resource> result = shapes.stream()
			.map(ps -> ps.getShDatatype())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());
		return result;		
	}
	
	private Set<Resource> findShClassOfShortname(String shortname, Resource path, Model model) {
		List<Shape> shapes = findAllShapesToConsider(shortname, path, model);
		
		Set<Resource> result = shapes.stream()
			.map(ps -> ps.getShClass())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());
		return result;
	}

	private Set<List<Literal>> findShLanguageInOfShortname(String shortname, Resource path, Model model) {
		List<Shape> shapes = findAllShapesToConsider(shortname, path, model);

		Set<List<Literal>> languageIn = shapes.stream()
			.map(ps -> ps.getShLanguageIn())
			.filter(li -> !li.isEmpty())
			.collect(Collectors.toSet());

		return languageIn;		
	}

	private Set<Literal> findPatternsOfShortname(String shortname, Resource path, Model model) {
		List<Shape> shapes = findAllShapesToConsider(shortname, path, model);

		Set<Literal> result = shapes.stream()
			.map(ps -> ps.getShPattern())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());
		return result;		
	}

	private Set<RDFNode> findHasValueOfShortname(String shortname, Resource path, Model model) {
		// find the (unique) property shape with its shortname, or with a path (which can return multiple property shapes)
		List<PropertyShape> propertyShapesWithPath = new ArrayList<>();
		if(shortname != null) {
			propertyShapesWithPath = this.shapesGraph.findPropertyShapesByShortname(shortname);
		}
		// if nothing found, try with the path
		if(propertyShapesWithPath.isEmpty()) {
			propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		}

		Set<RDFNode> shHasValue = propertyShapesWithPath.stream()
			.map(ps -> ps.getShHasValue())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());
		
		return shHasValue;	
	}

	private Set<Literal> findPatternsOfShortnameThroughShOr(String shortname, Resource path, Model model) {
		List<Shape> shapes = findAllShapesToConsider(shortname, path, model);

		List<Shape> shapesToConsider = new ArrayList<>();

		shapesToConsider.addAll(shapes.stream()
			.filter(s -> (s instanceof PropertyShape))
			.map(ps -> ((PropertyShape)ps).getShOrShNode())
			.filter(list -> list != null)
			// flatten the list of lists
			.flatMap(List::stream)
			.map(s -> new NodeShape(s))
			.collect(Collectors.toList()));

		shapesToConsider.addAll(shapes.stream()
			.filter(s -> (s instanceof PropertyShape))
			.map(ps -> ((PropertyShape)ps).getShOrShClass())
			.filter(list -> list != null)
			// flatten the list of lists
			.flatMap(List::stream)
			.map(s -> this.shapesGraph.findNodeShapeByResource(s))
			.collect(Collectors.toList()));
			
		Set<Literal> result = shapesToConsider.stream()
			.map(ps -> ps.getShPattern())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());

		return result;		
	}
	
	private Set<Integer> findShMaxCountOfShortname(String shortname, Resource path,Model model) {
		// find the (unique) property shape with its shortname, or with a path (which can return multiple property shapes)
		List<PropertyShape> propertyShapesWithPath = new ArrayList<>();
		if(shortname != null) {
			propertyShapesWithPath = this.shapesGraph.findPropertyShapesByShortname(shortname);
		}
		// if nothing found, try with the path
		if(propertyShapesWithPath.isEmpty()) {
			propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		}

		Set<Integer> shMaxCount = propertyShapesWithPath.stream()
			.map(ps -> ps.getShMaxCount())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());
		return shMaxCount;	
	}
	
	private Set<Resource> findShNodeKindOfShortname(String shortname, Resource path, Model model) {
		List<Shape> shapes = findAllShapesToConsider(shortname, path, model);

		Set<Resource> result = shapes.stream()
			.map(ps -> ps.getShNodeKind())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());
		return result;	
	}

	private PropertyPath getPathOfShortNameOrPath(String shortname, Resource path, Model model) {
		List<PropertyShape> propertyShapesWithShortName = this.shapesGraph.findPropertyShapesByShortname(shortname);

		Set<Resource> paths = propertyShapesWithShortName.stream()
			.map(ps -> ps.getShPath() )
			.filter(r -> r != null)
			.collect(Collectors.toSet());

		if(paths.size() > 1) {
			log.warn("Multiple paths found for shortname "+shortname+", cannot determine if reverse or not");
		} else if(paths.isEmpty()) {
			// remember that the shortname we got might come from the localName of the path
			// hence it is possible that no property shape has this shortname declared
			// in that case, we return the original path directly
			return new PropertyPath(path);
		}
		Resource pathResource = paths.iterator().next();

		return new PropertyPath(pathResource);
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

	/**
	 * Primitive to retrieve all shapes (property shapes and node shapes) to consider for a given shortname, or path if shortname is null
	 * @param shortname the shortname to search for, or null
	 * @param path
	 * @param model
	 * @return
	 */
	private List<Shape> findAllShapesToConsider(String shortname, Resource path, Model model) {
		List<Shape> shapes = new ArrayList<>();
		// find the (unique) property shape with its shortname, or with a path (which can return multiple property shapes)
		List<PropertyShape> propertyShapesWithPath = new ArrayList<>();
		if(shortname != null) {
			propertyShapesWithPath = this.shapesGraph.findPropertyShapesByShortname(shortname);
		}
		// if nothing found, try with the path
		if(propertyShapesWithPath.isEmpty()) {
			propertyShapesWithPath = this.shapesGraph.findPropertyShapesByPath(path);
		}
		shapes.addAll(propertyShapesWithPath);
		
		shapes.addAll(propertyShapesWithPath.stream()
			.map(ps -> this.shapesGraph.findNodeShapesByPropertyShapeShNode(ps))
			// flatten the list of lists
			.flatMap(List::stream)
			.collect(Collectors.toList()));
		shapes.addAll(propertyShapesWithPath.stream()
			.map(ps -> this.shapesGraph.findNodeShapesByPropertyShapeShClass(ps))
			// flatten the list of lists
			.flatMap(List::stream)
			.collect(Collectors.toList()));

		return shapes;			
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
