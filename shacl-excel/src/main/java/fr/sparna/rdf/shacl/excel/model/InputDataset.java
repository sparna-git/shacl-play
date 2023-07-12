package fr.sparna.rdf.shacl.excel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class InputDataset {
	
	private Resource nodeShape;
	
	protected Integer SHOrder;
	protected List<InputValues> ClassesXLS = new ArrayList<>();
	protected List<ColumnsHeader_Input> col_classes = new ArrayList<>();
	protected List<InputValues> PropertyXLS = new ArrayList<>();
	protected List<ColumnsHeader_Input> col_properties = new ArrayList<>();
	

	public List<InputValues> getClassesXSL() {
		return ClassesXLS;
	}

	public void setClassesXSL(List<InputValues> classesXSL) {
		ClassesXLS = classesXSL;
	}

	public List<InputValues> getPropertyXSL() {
		return PropertyXLS;
	}

	public void setPropertyXSL(List<InputValues> propertyXSL) {
		PropertyXLS = propertyXSL;
	}

	public List<ColumnsHeader_Input> getCol_classes() {
		return col_classes;
	}

	public void setCol_classes(List<ColumnsHeader_Input> col_classes) {
		this.col_classes = col_classes;
	}

	public List<ColumnsHeader_Input> getCol_properties() {
		return col_properties;
	}

	public void setCol_properties(List<ColumnsHeader_Input> col_properties) {
		this.col_properties = col_properties;
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

	public InputDataset (Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}
	
	
	
}
