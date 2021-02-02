package fr.sparna.rdf.shacl.doc.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PropertyShapeDocumentation {

	private String path;
	private String label;
	private String cardinalities;
	private String expectedValue;
	private String description;
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getCardinalities() {
		return cardinalities;
	}
	public void setCardinalities(String cardinalities) {
		this.cardinalities = cardinalities;
	}
	public String getExpectedValue() {
		return expectedValue;
	}
	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
