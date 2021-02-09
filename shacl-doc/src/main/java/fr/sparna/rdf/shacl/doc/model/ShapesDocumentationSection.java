package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import org.apache.jena.rdf.model.Resource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ShapesDocumentationSection {

	private String title;
	private String dURI;
	private String comments;
	@JacksonXmlElementWrapper(localName="properties")
	@JacksonXmlProperty(localName = "property")
	public List<PropertyShapeDocumentation> propertySections;
	
	
		
	public String getdURI() {
		return dURI;
	}

	public void setdURI(String dURI) {
		this.dURI = dURI;
	}

	public String getTitle() {
		return title;		
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}	
	
	public List<PropertyShapeDocumentation> getPropertySections() {
		return propertySections;
	}
	public void setPropertySections(List<PropertyShapeDocumentation> propertySections) {
		this.propertySections = propertySections;
	}	
}
