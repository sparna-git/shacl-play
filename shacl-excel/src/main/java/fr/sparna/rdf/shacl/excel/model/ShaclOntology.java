package fr.sparna.rdf.shacl.excel.model;

public class ShaclOntology {
	protected String ShapeUri;
	protected String owlProperty;
	protected String owlValue;
	
	public String getShapeUri() {
		return ShapeUri;
	}
	public void setShapeUri(String shapeUri) {
		ShapeUri = shapeUri;
	}
	public String getOwlProperty() {
		return owlProperty;
	}
	public void setOwlProperty(String owlProperty) {
		this.owlProperty = owlProperty;
	}
	public String getOwlValue() {
		return owlValue;
	}
	public void setOwlValue(String owlValue) {
		this.owlValue = owlValue;
	}
	
	
}
