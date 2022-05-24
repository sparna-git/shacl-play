package fr.sparna.rdf.shacl.sparqlgen.shaclmodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Resource;

public class NodeShape {
	
	private Resource nodeShapeResource;
	
	protected Query targetSelect;
	protected Resource targetClass;
	
	protected List<PropertyShape> properties = new ArrayList<>();	
	
	
	public boolean hasShInOrShHasValues() {
		return this.properties.stream().anyMatch(ps -> ps.getHasValue() != null || (ps.getIn() != null && !ps.getIn().isEmpty()));
	}
	
	public NodeShape(Resource nodeShapeResource) {
		this.nodeShapeResource = nodeShapeResource;
	}
	

	public Resource getNodeShapeResource() {
		return nodeShapeResource;
	}

	public Query getTargetSelect() {
		return targetSelect;
	}

	public void setTargetSelect(Query targetSelect) {
		this.targetSelect = targetSelect;
	}

	public Resource getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Resource targetClass) {
		this.targetClass = targetClass;
	}

	public List<PropertyShape> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyShape> properties) {
		this.properties = properties;
	}
	
	
	
}
