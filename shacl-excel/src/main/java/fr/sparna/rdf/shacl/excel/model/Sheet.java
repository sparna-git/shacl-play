package fr.sparna.rdf.shacl.excel.model;

import java.util.List;

import org.apache.jena.rdf.model.Model;

public class Sheet {
	
	// not sure this is useful - this is here just in case - see if this can be deleted
	protected Model templateModel;
	protected String NameSheet;
	protected List<ColumnSpecification> Columns;
	protected List<String[]> outputData;
	
	
	public String getNameSheet() {
		return NameSheet;
	}
	public void setNameSheet(String nameSheet) {
		NameSheet = nameSheet;
	}
	public List<ColumnSpecification> getColumns() {
		return Columns;
	}
	public void setColumns(List<ColumnSpecification> columns) {
		Columns = columns;
	}
	public Model getTemplateModel() {
		return templateModel;
	}
	public void setTemplateModel(Model templateModel) {
		this.templateModel = templateModel;
	}
	public List<String[]> getOutputData() {
		return outputData;
	}
	public void setOutputData(List<String[]> outputData) {
		this.outputData = outputData;
	}
	
	
}
