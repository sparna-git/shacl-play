package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class ShaclClasses {
	
	private Resource nodeShape;
	
	protected List<ShapesValues> shapes = new ArrayList<>();
	protected List<ShaclProperties> properties = new ArrayList<>();
	
	
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

	
	
	public List<ShaclProperties> getProperties() {
		return properties;
	}
	
	public void setProperties(List<ShaclProperties> properties) {
		this.properties = properties;
	}
	
	public ShaclClasses (Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}	
}
