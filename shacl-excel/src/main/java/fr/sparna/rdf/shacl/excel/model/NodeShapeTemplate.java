package fr.sparna.rdf.shacl.excel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class NodeShapeTemplate {
	
	private Resource nodeShape;
	
	protected Integer SHOrder;
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
