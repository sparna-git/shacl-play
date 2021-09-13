package fr.sparna.rdf.shacl.shaclplay.rules.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class BoxRules {
	
	protected String label;
	protected String comments;	
	protected List<BoxNameSpace> NameSpaceRules = new ArrayList<>();
	protected List<BoxShape> ShapeRules = new ArrayList<>();
	
	
	public BoxRules() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public List<BoxNameSpace> getNameSpaceRules() {
		return NameSpaceRules;
	}

	public void setNameSpaceRules(List<BoxNameSpace> nameSpaceRules) {
		NameSpaceRules = nameSpaceRules;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public List<BoxShape> getShapeRules() {
		return ShapeRules;
	}
	
	public void setShapeRules(List<BoxShape> shapeRules) {
		ShapeRules = shapeRules;
	}

}
