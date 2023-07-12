package fr.sparna.rdf.shacl.excel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class TemplateSparnatural {
	
	private Resource nodeShape;
	
	protected Integer SHOrder;
	protected List<XslTemplate> shapesTemplate = new ArrayList<>();

	public List<XslTemplate> getShapesTemplate() {
		return shapesTemplate;
	}

	public void setShapesTemplate(List<XslTemplate> shapesTemplate) {
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

	public TemplateSparnatural (Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}
	
	
	
}
