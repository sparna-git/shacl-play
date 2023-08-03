package fr.sparna.rdf.shacl.excel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class InputDataset {
	
	private Resource nodeShape;
	
	protected Integer SHOrder;
	protected List<Statement> ClassesXLS = new ArrayList<>();
	protected List<ColumnsInputDatatype> col_classes = new ArrayList<>();
	protected List<Statement> PropertyXLS = new ArrayList<>();
	protected List<ColumnsInputDatatype> col_properties = new ArrayList<>();
	

	public List<Statement> getClassesXSL() {
		return ClassesXLS;
	}

	public void setClassesXSL(List<Statement> classesXSL) {
		ClassesXLS = classesXSL;
	}

	public List<Statement> getPropertyXSL() {
		return PropertyXLS;
	}

	public void setPropertyXSL(List<Statement> propertyXSL) {
		PropertyXLS = propertyXSL;
	}

	public List<ColumnsInputDatatype> getCol_classes() {
		return col_classes;
	}

	public void setCol_classes(List<ColumnsInputDatatype> col_classes) {
		this.col_classes = col_classes;
	}

	public List<ColumnsInputDatatype> getCol_properties() {
		return col_properties;
	}

	public void setCol_properties(List<ColumnsInputDatatype> col_properties) {
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
