package fr.sparna.rdf.shacl.excel.model;

import java.util.List;

import org.apache.jena.rdf.model.Model;

public class Sheet {
	
	// the NodeShape from which this sheet was read
	protected NodeShape inputNodeShape;
	
	// name of the sheet
	protected String name;
	
	// column specifications for the sheet
	protected List<ColumnSpecification> Columns;
	
	// data populating the table
	protected List<String[]> outputData;
	
	// A list of arrays with 2 values representing the lines to insert in the header of the sheet
	protected List<String[]> headerValues;
	
	// URI in B1 cell of that sheet
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
