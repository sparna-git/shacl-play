package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.vocabularies.SHACL_PLAY;

public class ShapesGraph {
	
	private Model shaclGraph;
	private Model owlGraph;
	
	private OwlOntology ontology;
	
	public ShapesGraph(Model shaclGraph, Model owlGraph) {
		super();
		this.shaclGraph = shaclGraph;
		this.owlGraph = owlGraph;
	}

	public ShapesGraph(Model shaclGraph) {
		super();
		this.shaclGraph = shaclGraph;
	}
	
	
	public List<NodeShape> getAllNodeShapes() {	
		return ShapesGraph.readAllNodeShapes(shaclGraph, owlGraph); 
	}

	/**
	 * @return All subjects of a sh:path in the graph
	 */
	public List<PropertyShape> getAllPropertyShapes() {	
		return shaclGraph.listSubjectsWithProperty(SH.path).toList().stream().map(r -> new PropertyShape(r)).collect(Collectors.toList());
	}

	public OwlOntology getOntology() {
		if(this.ontology == null) {
			this.ontology = readOntology(shaclGraph);
		}
		return this.ontology;
	}

	public Model getShaclGraph() {
		return shaclGraph;
	}

	public Model getOwlGraph() {
		return owlGraph;
	}
	
	
	public NodeShape findNodeShapeByResource(Resource r) {
		return getAllNodeShapes().stream().filter(ns -> ns.getResource().toString().equals(r.toString())).findFirst().orElse(null);
	}

	/**
	 * @param cl
	 * @return all NodeShapes that target the given class, based on sh:targetClass, or a shape that is itself a class with the requested URI, or an empty list if none found
	 */
	public List<NodeShape> findNodeShapeByTargetClass(Resource cl) {
		return getAllNodeShapes().stream().filter(ns -> 
			ns.isTargeting(cl)
		).collect(Collectors.toList());
	}

	public List<NodeShape> findNodeShapeByPropertyShape(PropertyShape ps) {
		return getAllNodeShapes()
				.stream()
				.filter( ns -> ns.getProperties()
					.stream()
					.filter( psp -> psp.getResource().equals(ps.getResource()))
					.findFirst()
					.isPresent()
				)
				.collect(Collectors.toList());
	}


	/**
	 * @return the nodes shapes that target the node indicated in the sh:node of the given property shape
	 */
	public List<NodeShape> findNodeShapesByPropertyShapeShNode(PropertyShape ps) {
		return getAllNodeShapes().stream().filter(ns -> 
			ps.getShNode().map(n -> n.equals(ns.getResource())).orElse(false)
		).collect(Collectors.toList());
	}

	/**
	 * @return the nodes shapes that target the class indicated in the sh:class of the given property shape, relying on findNodeShapeByTargetClass
	 */
	public List<NodeShape> findNodeShapesByPropertyShapeShClass(PropertyShape ps) {
		return ps.getShClass().map(cl -> this.findNodeShapeByTargetClass(cl)).orElse(new ArrayList<NodeShape>());
	}

	public List<PropertyShape> findPropertyShapesByPath(Resource path) {
		return shaclGraph.listSubjectsWithProperty(SH.path, path).toList().stream().map(r -> new PropertyShape(r)).collect(Collectors.toList());
	}

	/*
		Return the 
	 */
	public List<PropertyShape> findPropertyShapesByShortname(String shortname) {
		return shaclGraph.listSubjectsWithProperty(shaclGraph.createProperty(SHACL_PLAY.SHORTNAME), shaclGraph.createLiteral(shortname)).toList().stream().map(r -> new PropertyShape(r)).collect(Collectors.toList());
	}
	
	public void pruneEmptyAndUnusedNodeShapes() {
		List<NodeShape> unusedNodeShapes = getAllNodeShapes().stream().filter(
			ns -> (
				ns.isPureValueShape()
				&&
				!ns.isUsedInShapesGraph()
			)
		).collect(Collectors.toList());
		unusedNodeShapes.forEach(ns -> this.deleteNodeShape(ns.getResource()));

		// do that a second time so that potential sh:node references to the deleted node shapes are also deleted
		// this should be a loop, of course
		unusedNodeShapes = getAllNodeShapes().stream().filter(
			ns -> (
				ns.isPureValueShape()
				&&
				!ns.isUsedInShapesGraph()
			)
		).collect(Collectors.toList());
		unusedNodeShapes.forEach(ns -> this.deleteNodeShape(ns.getResource()));
	}

	/**
	 * @return a map of namespace prefixes to URIs for all namespaces that are actually used in the shapes graph
	 */
	public Map<String, String> getNamespaces() {
		// Collect all necessary prefix names from the shapes graph
		Set<String> necessaryPrefixes = new HashSet<>();
		
		// Get all node shapes
		List<NodeShape> allNodeShapes = getAllNodeShapes();
		
		// For each node shape, collect prefixes from its properties and its property shapes
		for (NodeShape nodeShape : allNodeShapes) {
			Resource shapeResource = nodeShape.getResource();
			collectPrefixesFromResource(necessaryPrefixes, shapeResource);
			
			// Also collect prefixes from property shapes
			for (PropertyShape propertyShape : nodeShape.getProperties()) {
				Resource psResource = propertyShape.getResource();
				collectPrefixesFromResource(necessaryPrefixes, psResource);
			}
		}
		
		// Filter the model's namespace map to only include necessary prefixes
		return gatherNecessaryPrefixes(shaclGraph.getNsPrefixMap(), necessaryPrefixes);
	}

	/**
	 * Collects all prefix names used in the given resource's properties
	 * @param prefixes Set to collect prefix names into
	 * @param resource Resource to extract prefixes from
	 */
	private void collectPrefixesFromResource(Set<String> prefixes, Resource resource) {
		// Properties that may contain URIs that need prefixes
		Property[] propertiesToCheck = {
			SH.path,
			SH.targetClass,
			SH.class_,
			SH.datatype,
			SH.in,
			SH.hasValue,
			SH.target,
			SH.targetNode,
			SH.targetSubjectsOf,
			SH.targetObjectsOf
		};
		
		for (Property property : propertiesToCheck) {
			appendPrefix(prefixes, resource, property);
		}
	}

	/**
	 * Appends the prefix name from a property value to the prefixes set
	 * @param prefixes Set to collect prefix names into
	 * @param resource Resource to check
	 * @param property Property to check on the resource
	 */
	private void appendPrefix(Set<String> prefixes, Resource resource, Property property) {
		if(resource.hasProperty(property)) {
			RDFNode object = resource.getProperty(property).getObject();
			if(object.isURIResource()) {
				String qname = resource.getModel().qnameFor(object.asNode().getURI());
				// can be null if cannot be abbreviated
				if(qname != null) {
					prefixes.add(qname.split(":")[0]);
				}
			} else if(object.canAs(RDFList.class)) {
				List<RDFNode> nodes = object.as(RDFList.class).asJavaList();
				for (RDFNode aNode : nodes) {
					if(aNode.isURIResource()) {
						String qname = resource.getModel().qnameFor(aNode.asResource().getURI());
						if(qname != null) {
							prefixes.add(qname.split(":")[0]);
						}
					}
				}
			}
		}
	}

	/**
	 * Filters a map of all prefixes to only include those that are in the necessaryPrefixes set
	 * @param allPrefixes Map of all available prefixes (prefix -> URI)
	 * @param necessaryPrefixes Set of prefix names that are needed
	 * @return Filtered map containing only necessary prefixes
	 */
	private Map<String, String> gatherNecessaryPrefixes(Map<String, String> allPrefixes, Set<String> necessaryPrefixes) {
		HashMap<String, String> result = new HashMap<>();
		
		for (String aPrefix : necessaryPrefixes) {
			String uri = allPrefixes.get(aPrefix);
			if(uri != null) {
				result.put(aPrefix, uri);
			}
		}
		
		return result;
	}

	public void deleteNodeShape(Resource nodeShape) {
		// delete any blank nodes that are linked to this node shape
		this.shaclGraph.listStatements(nodeShape, null, (RDFNode) null).toList()
		.stream().filter(s -> s.getObject().isAnon()).forEach(s -> this.shaclGraph.removeAll(s.getObject().asResource(), null, null));

		this.shaclGraph.removeAll(nodeShape, null, null);
		this.shaclGraph.removeAll(null, null, nodeShape);
	}
	
	private OwlOntology readOntology(Model shaclGraph) {
		
		// Lecture de OWL
		// this is tricky, because we can have multiple ones if SHACL is merged with OWL or imports OWL
		List<Resource> sOWL = shaclGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		
		// let's decide first to exclude the ones that are owl:import-ed from others
		List<Resource> filteredOWL = sOWL.stream().filter(onto1 -> {
			return !sOWL.stream().anyMatch(onto2 -> onto2.hasProperty(OWL.imports, onto1));
		}).collect(Collectors.toList());
		
		OwlOntology ontologyObject = null;
		if(filteredOWL.size() > 0) {
			ontologyObject = new OwlOntology(filteredOWL.get(0));
		}
				
		return ontologyObject;
	}
	
	/**
	 * @return all instances of sh:NodeShape in the graph, as NodeShape objects
	 */
	private static List<NodeShape> readAllNodeShapes(Model shaclGraph, Model owlGraph) {
		
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		// 1. Lire toutes les classes		
		List<NodeShape> allNodeShapes = new ArrayList<>();
		for (Resource nodeShape : nodeShapes) {
			allNodeShapes.add(new NodeShape(nodeShape));
		}
		
		return allNodeShapes; 		
	}
	
}
