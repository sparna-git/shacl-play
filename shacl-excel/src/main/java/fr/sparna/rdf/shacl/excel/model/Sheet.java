package fr.sparna.rdf.shacl.excel.model;

import java.util.List;

import org.apache.jena.rdf.model.Model;

public class Sheet {
	
	// the NodeShape from which this sheet was read
	protected NodeShape inputNodeShape;
	protected String name;
	protected List<ColumnSpecification> Columns;
	protected List<String[]> outputData;
	
	protected List<String[]> headerValues;
	protected String b1Uri;
	
	
	
	public Sheet(NodeShape inputNodeShape) {
		super();
		this.inputNodeShape = inputNodeShape;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ColumnSpecification> getColumns() {
		return Columns;
	}
	public void setColumns(List<ColumnSpecification> columns) {
		Columns = columns;
	}
	
	public NodeShape getInputNodeShape() {
		return inputNodeShape;
	}
	public List<String[]> getOutputData() {
		return outputData;
	}
	public void setOutputData(List<String[]> outputData) {
		this.outputData = outputData;
	}
	public List<String[]> getHeaderValues() {
		return headerValues;
	}
	public void setHeaderValues(List<String[]> headerValues) {
		this.headerValues = headerValues;
	}
	public String getB1Uri() {
		return b1Uri;
	}
	public void setB1Uri(String b1Uri) {
		this.b1Uri = b1Uri;
	}
	
}
