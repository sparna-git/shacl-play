package fr.sparna.rdf.shacl.doc.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.OwlOntology;

public class ParserModel {
	
	private ArrayList<NodeShape> allNodeShapes = new ArrayList<>();
	private OwlOntology ontologyObject;
	private List<NamespaceSection> namespaceSections = new ArrayList<>();
	private Model shaclGraph;
	private Model owlGraph;
	
	public ArrayList<NodeShape> getAllNodeShapes() {
		return allNodeShapes;
	}
	public void setAllNodeShapes(ArrayList<NodeShape> allNodeShapes) {
		this.allNodeShapes = allNodeShapes;
	}
	public OwlOntology getOntologyObject() {
		return ontologyObject;
	}
	public void setOntologyObject(OwlOntology ontologyObject) {
		this.ontologyObject = ontologyObject;
	}
	public List<NamespaceSection> getNamespaceSections() {
		return namespaceSections;
	}
	public void setNamespaceSections(List<NamespaceSection> namespaceSections) {
		this.namespaceSections = namespaceSections;
	}
	public Model getShaclGraph() {
		return shaclGraph;
	}
	public void setShaclGraph(Model shaclGraph) {
		this.shaclGraph = shaclGraph;
	}
	public Model getOwlGraph() {
		return owlGraph;
	}
	public void setOwlGraph(Model owlGraph) {
		this.owlGraph = owlGraph;
	}	
	
	
	
}
