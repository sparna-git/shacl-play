package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.jena.shacl.ShapesGraph;

public class ShapesGraphDoc extends ShapesGraph {
	
	private Model shaclGraph;
	private Model owlGraph;
	
	private List<NodeShapeDoc> allNodeShapes = new ArrayList<>();
	//private OwlOntology ontologyObject;
	
	/**
	 * TODO : lang should not be here. It should be an accessor parameter in OwlOntology class
	 */
	public ShapesGraphDoc(Model shaclGraph, Model owlGraph, String lang) {
		super(shaclGraph,owlGraph);
		this.shaclGraph = shaclGraph;
		this.owlGraph = owlGraph;
		
		//this.ontologyObject = super.getOntology(); //this.readOWLDoc(shaclGraph, lang);
		this.allNodeShapes = this.readAllNodeShapesDoc(shaclGraph, owlGraph, lang);
	}

	public List<NodeShapeDoc> getAllNodeShapesDoc() {	
		return allNodeShapes; 
	}

	/*
	public OwlOntology getOntologyObject() {
		return ontologyObject;
	}
	*/

	public Model getShaclGraph() {
		return shaclGraph;
	}

	public Model getOwlGraph() {
		return owlGraph;
	}
	
	/* 
	public NodeShapeDoc findNodeShapeByResource(Resource r) {
		return this.allNodeShapes.stream().filter(ns -> ns.getNodeShape().toString().equals(r.toString())).findFirst().orElse(null);
	}
	
	
	@Override
	public NodeShape findNodeShapeByResource(Resource r) {
		// TODO Auto-generated method stub
		return super.findNodeShapeByResource(r);
	}
	*/

	/* 
	private OwlOntology readOWLDoc(Model shaclGraph, String lang) {
		
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
	*/

	private ArrayList<NodeShapeDoc> readAllNodeShapesDoc(Model shaclGraph, Model owlGraph, String lang) {
		
		// Convert nodehsape in nodeshape doc
		ArrayList<NodeShapeDoc> allNodeShapes = new ArrayList<>();
		NodeShapeReader reader = new NodeShapeReader(lang);
		super.getAllNodeShapes()
			.stream()
			.map( ns -> allNodeShapes.add(new NodeShapeDoc(ns.getNodeShape())))
			.collect(Collectors.toList());

		// sort node shapes
		allNodeShapes.sort((NodeShapeDoc ns1, NodeShapeDoc ns2) -> {
			if (ns1.getShOrderDoc() != null) {
				if (ns2.getShOrderDoc() != null) {
					return ((ns1.getShOrderDoc() - ns2.getShOrderDoc()) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if (ns2.getShOrderDoc() != null) {
					return 1;
				} else {
					// both sh:order are null, try with their display label
					return ns1.getDisplayLabel(owlGraph, lang).compareToIgnoreCase(ns2.getDisplayLabel(owlGraph, lang));
				}
			}
		});

		// 2. Lire les propriétés
		for (NodeShapeDoc aBox : allNodeShapes) {
			aBox.setPropertiesDoc(reader.readProperties(aBox.getNodeShape(), allNodeShapes, owlGraph));
		}
		
		return allNodeShapes; 
		
	}
	
}
