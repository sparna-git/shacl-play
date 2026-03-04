package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.SHACL_PLAY;

public class ShapesGraph {
	
	private Model shaclGraph;
	private Model owlGraph;
	
	private OwlOntology ontology;
	
	public ShapesGraph(Model shaclGraph, Model owlGraph) {
		super();
		this.shaclGraph = shaclGraph;
		this.owlGraph = owlGraph;
		
		this.ontology = this.readOWL(shaclGraph);
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
		return ontology;
	}

	public Model getShaclGraph() {
		return shaclGraph;
	}

	public Model getOwlGraph() {
		return owlGraph;
	}
	
	public NodeShape findNodeShapeByResource(Resource r) {
		return getAllNodeShapes().stream().filter(ns -> ns.getNodeShape().toString().equals(r.toString())).findFirst().orElse(null);
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

	/**
	 * @return the nodes shapes that target the node indicated in the sh:node of the given property shape
	 */
	public List<NodeShape> findNodeShapesByPropertyShapeShNode(PropertyShape ps) {
		return getAllNodeShapes().stream().filter(ns -> 
			ps.getShNode().map(n -> n.equals(ns.getNodeShape())).orElse(false)
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
		unusedNodeShapes.forEach(ns -> this.deleteNodeShape(ns.getNodeShape()));

		// do that a second time so that potential sh:node references to the deleted node shapes are also deleted
		// this should be a loop, of course
		unusedNodeShapes = getAllNodeShapes().stream().filter(
			ns -> (
				ns.isPureValueShape()
				&&
				!ns.isUsedInShapesGraph()
			)
		).collect(Collectors.toList());
		unusedNodeShapes.forEach(ns -> this.deleteNodeShape(ns.getNodeShape()));
	}


	public void deleteNodeShape(Resource nodeShape) {
		// delete any blank nodes that are linked to this node shape
		this.shaclGraph.listStatements(nodeShape, null, (RDFNode) null).toList()
		.stream().filter(s -> s.getObject().isAnon()).forEach(s -> this.shaclGraph.removeAll(s.getObject().asResource(), null, null));

		this.shaclGraph.removeAll(nodeShape, null, null);
		this.shaclGraph.removeAll(null, null, nodeShape);
	}
	
	
	private OwlOntology readOWL(Model shaclGraph) {
		
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

	class NodeShapeDisplayLabelComparator implements Comparator<NodeShape> {

		private String lang;		

		public NodeShapeDisplayLabelComparator(String lang) {
			this.lang = lang;
		}

		@Override
		public int compare(NodeShape ns1, NodeShape ns2) {
			if (ns1.getShOrder().orElse(null) != null) {
				if (ns2.getShOrder().orElse(null) != null) {
					return ((ns1.getShOrder().get() - ns2.getShOrder().get()) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if (ns2.getShOrder().orElse(null) != null) {
					return 1;
				} else {
					// both sh:order are null, try with their display label
					return ns1.getDisplayLabel(lang).compareTo(ns2.getDisplayLabel(lang));
				}
			}
		}
		
	}
	
}
