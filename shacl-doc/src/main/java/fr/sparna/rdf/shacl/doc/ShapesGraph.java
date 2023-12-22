package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
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
	private OwlOntology ontologyObject;
	
	/**
	 * TODO : lang should not be here. It should be an accessor parameter in OwlOntology class
	 */
	public ShapesGraph(Model shaclGraph, Model owlGraph, String lang) {
		super();
		this.shaclGraph = shaclGraph;
		this.owlGraph = owlGraph;
		
		this.ontologyObject = this.readOWL(shaclGraph, lang);
		this.allNodeShapes = this.readAllNodeShapes(shaclGraph, owlGraph, lang);
	}
	
	
	public List<NodeShape> getAllNodeShapes() {	
		return allNodeShapes; 
	}

	public OwlOntology getOntologyObject() {
		return ontologyObject;
	}

	public Model getShaclGraph() {
		return shaclGraph;
	}

	public Model getOwlGraph() {
		return owlGraph;
	}
	
	
	private OwlOntology readOWL(Model shaclGraph, String lang) {
		
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
	
	private ArrayList<NodeShape> readAllNodeShapes(Model shaclGraph, Model owlGraph, String lang) {
		
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		// 1. Lire toutes les classes		
		ArrayList<NodeShape> allNodeShapes = new ArrayList<>();
		NodeShapeReader reader = new NodeShapeReader(lang);
		for (Resource nodeShape : nodeShapes) {
			allNodeShapes.add(new NodeShape(nodeShape));
		}

		// sort node shapes
		allNodeShapes.sort((NodeShape ns1, NodeShape ns2) -> {
			if (ns1.getShOrder() != null) {
				if (ns2.getShOrder() != null) {
					return ((ns1.getShOrder() - ns2.getShOrder()) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if (ns2.getShOrder() != null) {
					return 1;
				} else {
					// both sh:order are null, try with their display label
					return ns1.getDisplayLabel(owlGraph, lang).compareTo(ns2.getDisplayLabel(owlGraph, lang));
				}
			}
		});

		// 2. Lire les propriétés
		for (NodeShape aBox : allNodeShapes) {
			aBox.setProperties(reader.readProperties(aBox.getNodeShape(), allNodeShapes, owlGraph));
		}
		
		return allNodeShapes; 
		
	}
	
}
