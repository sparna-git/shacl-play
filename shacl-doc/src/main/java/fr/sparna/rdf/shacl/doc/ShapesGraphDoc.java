package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.jena.shacl.ShapesGraph;

public class ShapesGraphDoc extends ShapesGraph {
	
	private Model shaclGraph;
	private Model owlGraph;
	
	private List<NodeShapeDoc> allNodeShapes = new ArrayList<>();
	
	/**
	 * TODO : lang should not be here. It should be an accessor parameter in OwlOntology class
	 */
	public ShapesGraphDoc(Model shaclGraph, Model owlGraph, String lang) {
		super(shaclGraph,owlGraph);
		this.shaclGraph = shaclGraph;
		this.owlGraph = owlGraph;
		
		this.allNodeShapes = this.readAllNodeShapesDoc(shaclGraph, owlGraph, lang);
	}

	public NodeShapeDoc findNodeShapeByResource(Resource r) {
		return this.allNodeShapes.stream().filter(ns -> ns.getNodeShape().equals(r)).findFirst().orElse(null);
	}

	public List<NodeShapeDoc> getAllNodeShapesDoc() {	
		return allNodeShapes; 
	}

	public Model getShaclGraph() {
		return shaclGraph;
	}

	public Model getOwlGraph() {
		return owlGraph;
	}

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
