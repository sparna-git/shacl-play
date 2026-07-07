package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.ShapesGraph;

public class ShapesGraphDoc extends ShapesGraph {
	
	private Model shaclGraph;
	private Model owlGraph;
	
	private List<NodeShape> allNodeShapes = new ArrayList<>();
	
	/**
	 * TODO : lang should not be here. It should be an accessor parameter in OwlOntology class
	 */
	public ShapesGraphDoc(Model shaclGraph, Model owlGraph, String lang) {
		super(shaclGraph,owlGraph);
		this.shaclGraph = shaclGraph;
		this.owlGraph = owlGraph;
		
		this.allNodeShapes = this.readAllNodeShapes(shaclGraph, owlGraph, lang);
	}

	public NodeShape findNodeShapeByResource(Resource r) {
		return this.allNodeShapes.stream().filter(ns -> ns.getShape().equals(r)).findFirst().orElse(null);
	}

	public List<NodeShape> getAllNodeShapes() {	
		return allNodeShapes; 
	}

	public Model getShaclGraph() {
		return shaclGraph;
	}

	public Model getOwlGraph() {
		return owlGraph;
	}

	private ArrayList<NodeShape> readAllNodeShapes(Model shaclGraph, Model owlGraph, String lang) {
		
		// Convert nodehsape in nodeshape doc
		ArrayList<NodeShape> allNodeShapes = new ArrayList<>();

		super.getAllNodeShapes()
			.stream()
			.map( ns -> allNodeShapes.add(new NodeShape(ns.getShape())))
			.collect(Collectors.toList());

		// sort node shapes
		allNodeShapes.sort((NodeShape ns1, NodeShape ns2) -> {
			if (ns1.getShOrder().isPresent()) {
				if (ns2.getShOrder().isPresent()) {
					return ((ns1.getShOrder().get() - ns2.getShOrder().get()) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if (ns2.getShOrder().isPresent()) {
					return 1;
				} else {
					// both sh:order are null, try with their display label
					return ns1.getDisplayLabel(owlGraph, lang).compareToIgnoreCase(ns2.getDisplayLabel(owlGraph, lang));
				}
			}
		});

		
		return allNodeShapes; 
		
	}
	
}
