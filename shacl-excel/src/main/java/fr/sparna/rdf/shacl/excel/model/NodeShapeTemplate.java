package fr.sparna.rdf.shacl.excel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class NodeShapeTemplate {
	
	private Resource nodeShape;
	
	protected Integer SHOrder;
	protected Resource SHTargetClass;
	protected Resource SHTargetObjectOf;
	protected Resource SHTargetSubjectsOf;
	
	public Resource getSHTargetClass() {
		return SHTargetClass;
	}

	public void setSHTargetClass(Resource sHTargetClass) {
		SHTargetClass = sHTargetClass;
	}

	public Resource getSHTargetObjectOf() {
		return SHTargetObjectOf;
	}

	public void setSHTargetObjectOf(Resource sHTargetObjectOf) {
		SHTargetObjectOf = sHTargetObjectOf;
	}

	public Resource getSHTargetSubjectsOf() {
		return SHTargetSubjectsOf;
	}

	public void setSHTargetSubjectsOf(Resource sHTargetSubjectsOf) {
		SHTargetSubjectsOf = sHTargetSubjectsOf;
	}

	protected List<PropertyShapeTemplate> shapesTemplate = new ArrayList<>();

	public List<PropertyShapeTemplate> getShapesTemplate() {
		return shapesTemplate;
	}

	public void setShapesTemplate(List<PropertyShapeTemplate> shapesTemplate) {
		this.shapesTemplate = shapesTemplate;
	}

	public Integer getSHOrder() {
		return SHOrder;
	}

	public void setSHOrder(Integer sHOrder) {
		SHOrder = sHOrder;
	}

	public Resource getNodeShape() {
		return nodeShape;
	}

	public void setNodeShape(Resource nodeShape) {
		this.nodeShape = nodeShape;
	}

	public NodeShapeTemplate (Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}
	
	
	
}
