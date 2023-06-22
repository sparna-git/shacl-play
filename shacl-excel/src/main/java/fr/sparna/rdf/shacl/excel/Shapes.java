package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class Shapes {
	
	private Resource nodeShape;
	
	protected Integer SHOrder;
	protected List<ShapesValues> shapes = new ArrayList<>();
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

	public List<ShapesValues> getShapes() {
		return shapes;
	}

	public void setShapes(List<ShapesValues> shapes) {
		this.shapes = shapes;
	}

	
	public Shapes (Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}	
}
