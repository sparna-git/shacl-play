package fr.sparna.rdf.shacl.excel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class Shapes {
	
	private Resource nodeShape;
	
	protected Integer SHOrder;
	protected List<ShapesValues> shapes = new ArrayList<>();
	protected List<ShapesValues> ClassesXSL = new ArrayList<>();
	protected List<ColumnsData> col_classes = new ArrayList<>();
	protected List<ShapesValues> PropertyXSL = new ArrayList<>();
	protected List<ColumnsData> col_properties = new ArrayList<>();
	protected List<XslTemplate> shapesTemplate = new ArrayList<>();

	
	
	
	public List<ShapesValues> getClassesXSL() {
		return ClassesXSL;
	}

	public void setClassesXSL(List<ShapesValues> classesXSL) {
		ClassesXSL = classesXSL;
	}

	public List<ColumnsData> getCol_classes() {
		return col_classes;
	}

	public void setCol_classes(List<ColumnsData> col_classes) {
		this.col_classes = col_classes;
	}

	public List<ShapesValues> getPropertyXSL() {
		return PropertyXSL;
	}

	public void setPropertyXSL(List<ShapesValues> propertyXSL) {
		PropertyXSL = propertyXSL;
	}

	public List<ColumnsData> getCol_properties() {
		return col_properties;
	}

	public void setCol_properties(List<ColumnsData> col_properties) {
		this.col_properties = col_properties;
	}

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
