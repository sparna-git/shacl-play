package fr.sparna.rdf.shacl.jsonld;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

public class JsonLdContextGenerator {

	private static final String SHORTNAME = "https://shacl-play.sparna.fr/ontology#shortname";

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected String atIdMapping = "id";
	protected String atTypeMapping = "type";
	protected String atGraphMapping = "graph";
	
	public String generateJsonLdContext(Model model) {		
		JsonLdContext context = new JsonLdContext();
		
		// ### set hardcoded mappings
		if(this.atIdMapping != null) {
			context.add(new JsonLdMapping(this.atIdMapping, "\"@id\""));
		}
		if(this.atTypeMapping != null) {
			context.add(new JsonLdMapping(this.atTypeMapping, "\"@type\""));
		}
		if(this.atGraphMapping != null) {
			context.add(new JsonLdMapping(this.atGraphMapping, "\"@graph\""));
		}
		
		// ### map each known prefixes
		context.startNewSection();
		List<String> keys = new ArrayList<>(model.getNsPrefixMap().keySet());
		// sort the list
		keys.sort((ns1,ns2) -> ns1.compareToIgnoreCase(ns2));
		for (String aKey : keys) {
			String idContext = "\""+model.getNsPrefixMap().get(aKey)+"\"";
			context.add(new JsonLdMapping(aKey, idContext));
		}
		
		// ### map each NodeShape
		List<Resource> nodeShapes = JsonLdContextGenerator.findAllNodeShapes(model);
		// sort the list
		nodeShapes.sort((ns1,ns2) -> ns1.getLocalName().compareToIgnoreCase(ns2.getLocalName()));
		
		// print
		context.startNewSection();
		nodeShapes.stream().forEach(ns -> context.add(new JsonLdMapping(ns.getLocalName(),"\""+ns.getModel().shortForm(ns.getURI())+"\"")));
		
		// find each PropertyShape
		context.startNewSection();
		List<Resource> propertyShapes = JsonLdContextGenerator.findAllPropertyShapes(model);
		
		// find each paths in property shapes
		List<Resource> paths = new ArrayList<>(propertyShapes.stream().map(r -> r.getRequiredProperty(SH.path).getResource())
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
			
			// ### determine the @type
			// if there is a sh:datatype, set type as the datatype
			String type = null;
			Set<Resource> datatypes = JsonLdContextGenerator.findDatatypesOfPath(path, model);
			if(datatypes.size() > 1) {
				log.warn("Found different datatypes declared for path "+path+", will declare only one");
			} else if(!datatypes.isEmpty()) {
				Resource theDatatype = datatypes.iterator().next();
				type = theDatatype.getURI();
				if(type.startsWith(XSD.NS)) {
					type = "xsd:"+theDatatype.getLocalName();
				}
			}
			
			// if there are sh:class, sh:node, or sh:nodeKind = sh:IRI, or sh:nodeKind = sh:BlankNodeOrIRI, set the type to @id
			Set<Resource> classes = findShClassOfPath(path, model);
			Set<Resource> nodes = findShNodeOfPath(path, model);
			Set<Resource> nodeKinds = findShNodeKindOfPath(path, model);
			
			if(
					!classes.isEmpty()
					||
					!nodes.isEmpty()
					||
					nodeKinds.stream().anyMatch(r -> r.getURI().equals(SH.IRI.getURI()))
					||
					nodeKinds.stream().anyMatch(r -> r.getURI().equals(SH.BlankNodeOrIRI.getURI()))
			) {
				type = "@id";
			}
			
			
			Set<String> maxCount = this.findShMaxCount(path, model);
			String nMaxCount = maxCount.iterator().next();
			
			String idContext = "\"" +path.getModel().shortForm(path.getURI())+"\""  ;
			if ((Integer.valueOf(nMaxCount) > 1) && !type.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString") ) {
				idContext += ", \"@container\" : \"@set\"";
			} 
			
			if (type != null && type.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString")) {
				idContext += ", \"@container\" : \"@language\"";
			}
			
			//context.add(new JsonLdMapping(term,path.getModel().shortForm(path.getURI()), type));
			context.add(new JsonLdMapping(term,idContext, type));
			
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
	
	private static List<Resource> findAllNodeShapes(Model model) {
		return model.listSubjectsWithProperty(RDF.type, SH.NodeShape).toList().stream().filter(r -> r.isURIResource()).collect(Collectors.toList());
	}
	
	private static List<Resource> findAllPropertyShapes(Model model) {
		return model.listSubjectsWithProperty(SH.path).toList();
	}
	
	private static List<Resource> findPropertyShapesWithPath(Resource path, Model model) {
		return model.listSubjectsWithProperty(SH.path, path).toList();
	}
	
	private static Set<Resource> findDatatypesOfPath(Resource path, Model model) {
		List<Resource> propertyShapesWithPath = findPropertyShapesWithPath(path, model);
		Set<Resource> datatypes = new HashSet<>();
		for (Resource resource : propertyShapesWithPath) {
			// read sh:datatype constraint
			datatypes.addAll(JsonLdContextGenerator.readObjectProperty(resource, SH.datatype));
		}
		return datatypes;		
	}
	
	private static Set<Resource> findShClassOfPath(Resource path, Model model) {
		List<Resource> propertyShapesWithPath = findPropertyShapesWithPath(path, model);
		Set<Resource> classes = new HashSet<>();
		for (Resource resource : propertyShapesWithPath) {
			// read class constraint
			classes.addAll(JsonLdContextGenerator.readObjectProperty(resource, SH.class_));
		}
		return classes;		
	}
	
	private static Set<Resource> findShNodeOfPath(Resource path, Model model) {
		List<Resource> propertyShapesWithPath = findPropertyShapesWithPath(path, model);
		Set<Resource> nodes = new HashSet<>();
		for (Resource resource : propertyShapesWithPath) {
			// read node constraint
			nodes.addAll(JsonLdContextGenerator.readObjectProperty(resource, SH.node));
		}
		return nodes;		
	}
	
	private static Set<String> findShMaxCount(Resource path,Model model) {
		List<Resource> propertyShapesWithPath = findPropertyShapesWithPath(path, model);
		Set<String> shMaxCount = new HashSet<>();
		for (Resource r : propertyShapesWithPath) {
			if (r.hasProperty(SH.minCount)) {			
				if (r.hasProperty(SH.maxCount)) {
					shMaxCount.add(String.valueOf(r.getProperty(SH.maxCount).getObject().asLiteral().getInt()));
				} else {
					shMaxCount.add("2");
				}
			} else {
				shMaxCount.add("-1");
			}
		}
		
		
		return shMaxCount;
	}
	
	private static Set<Resource> findShNodeKindOfPath(Resource path, Model model) {
		List<Resource> propertyShapesWithPath = findPropertyShapesWithPath(path, model);
		Set<Resource> nodeKinds = new HashSet<>();
		for (Resource resource : propertyShapesWithPath) {
			// read nodeKind constraint
			nodeKinds.addAll(JsonLdContextGenerator.readObjectProperty(resource, SH.nodeKind));
		}
		return nodeKinds;		
	}

	private static Set<String> findShortNamesOfPath(Resource path, Model model) {
		List<Resource> propertyShapesWithPath = findPropertyShapesWithPath(path, model);
		Set<String> shortnames = new HashSet<>();
		for (Resource resource : propertyShapesWithPath) {
			// read shortname constraint
			shortnames.addAll(JsonLdContextGenerator.readDatatypeProperty(resource, model.createProperty(SHORTNAME)).stream().map(l -> l.getString()).collect(Collectors.toSet()));
		}
		return shortnames;		
	}
	
	private static List<Resource> readObjectProperty(Resource r, Property p) {
		return r.listProperties(p).toList().stream().map(s -> s.getObject()).filter(n -> n.isResource()).map(n -> n.asResource()).collect(Collectors.toList());
	}

	private static List<Literal> readDatatypeProperty(Resource r, Property p) {
		return r.listProperties(p).toList().stream().map(s -> s.getObject()).filter(n -> n.isLiteral()).map(n -> n.asLiteral()).collect(Collectors.toList());
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
