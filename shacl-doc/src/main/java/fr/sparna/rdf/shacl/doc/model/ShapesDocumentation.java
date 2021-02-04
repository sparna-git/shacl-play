package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ShapesDocumentation {

	public String title;	
	@JacksonXmlElementWrapper(localName="sections")
	@JacksonXmlProperty(localName = "section")
	public List<ShapesDocumentationSection> sections;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<ShapesDocumentationSection> getSections() {
		return sections;
	}
	public void setSections(List<ShapesDocumentationSection> sections) {
		this.sections = sections;
	}
	
	
	
	
}
