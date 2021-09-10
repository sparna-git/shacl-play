package fr.sparna.rdf.shacl.shaclplay.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class BoxShape {

	private Resource nodeShape;
	
	protected String label;

	protected List<BoxShapeTarget> target = new ArrayList<>();
	protected List<BoxShapeRules> Rules = new ArrayList<>();
	protected List<BoxShape> SuperClass = new ArrayList<>();

	public BoxShape(Resource nodeShape) {
		this.nodeShape = nodeShape;
	}
	
	public String getLabel() {
		return label;
	}



	public void setLabel(String label) {
		this.label = label;
	}



	public List<BoxShapeTarget> getTarget() {
		return target;
	}

	public void setTarget(List<BoxShapeTarget> target) {
		this.target = target;
	}

	public List<BoxShape> getSuperClass() {
		return SuperClass;
	}

	public void setSuperClass(List<BoxShape> superClass) {
		SuperClass = superClass;
	}

	public Resource getNodeShape() {
		return nodeShape;
	}

	public void setNodeShape(Resource nodeShape) {
		this.nodeShape = nodeShape;
	}

	public List<BoxShapeRules> getRules() {
		return Rules;
	}

	public void setRules(List<BoxShapeRules> rules) {
		Rules = rules;
	}

}
