package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Group of property shapes in the table, à la schema.org
 */
public class PropertyShapesGroupDocumentation {

	private Link targetClass;
	
	@JacksonXmlElementWrapper(localName="properties")
	@JacksonXmlProperty(localName = "property")
	public List<PropertyShapeDocumentation> properties;

	public List<PropertyShapeDocumentation> getProperties() {
		return properties;
	}
	
	public void setProperties(List<PropertyShapeDocumentation> properties) {
		this.properties = properties;
	}
	
	public Link getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Link targetClass) {
		this.targetClass = targetClass;
	}
	
}
