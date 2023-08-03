package fr.sparna.rdf.shacl.excel.model;

import java.util.List;

import org.apache.jena.rdf.model.Statement;

public class ModelStructure {
	
	protected String NameSheet;
	protected List<ShapeTemplateHeaderColumn> Columns;
	protected List<Statement> dataStatement;
	protected List<String[]> outputData;
	
	
	public String getNameSheet() {
		return NameSheet;
	}
	public void setNameSheet(String nameSheet) {
		NameSheet = nameSheet;
	}
	public List<ShapeTemplateHeaderColumn> getColumns() {
		return Columns;
	}
	public void setColumns(List<ShapeTemplateHeaderColumn> columns) {
		Columns = columns;
	}
	public List<Statement> getDataStatement() {
		return dataStatement;
	}
	public void setDataStatement(List<Statement> dataStatement) {
		this.dataStatement = dataStatement;
	}
	public List<String[]> getOutputData() {
		return outputData;
	}
	public void setOutputData(List<String[]> outputData) {
		this.outputData = outputData;
	}
	
	
}
