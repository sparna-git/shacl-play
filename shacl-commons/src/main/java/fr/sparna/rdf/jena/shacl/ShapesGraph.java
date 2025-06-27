package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class ShapesGraph {
	
	private Model shaclGraph;
	private Model owlGraph;
	
	private List<NodeShape> allNodeShapes = new ArrayList<>();
	private OwlOntology ontology;
	
	public ShapesGraph(Model shaclGraph, Model owlGraph) {
		super();
		this.shaclGraph = shaclGraph;
		this.owlGraph = owlGraph;
		
		this.ontology = this.readOWL(shaclGraph);
		this.allNodeShapes = this.readAllNodeShapes(shaclGraph, owlGraph);
	}
	
	
	public List<NodeShape> getAllNodeShapes() {	
		return allNodeShapes; 
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
		return this.allNodeShapes.stream().filter(ns -> ns.getNodeShape().toString().equals(r.toString())).findFirst().orElse(null);
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
	
	private List<NodeShape> readAllNodeShapes(Model shaclGraph, Model owlGraph) {
		
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
					return ((ns1.getOrderFloat() - ns2.getOrderFloat()) > 0)?1:-1;
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
