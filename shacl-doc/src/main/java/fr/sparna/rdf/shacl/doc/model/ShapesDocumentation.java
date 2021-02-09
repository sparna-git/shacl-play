package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ShapesDocumentation {

	public String title;	
	public String subtitle;
	@JacksonXmlElementWrapper(localName="shnamespaces")
	@JacksonXmlProperty(localName = "shnamespace")
	public List<NamespaceSections> shnamespace;
	@JacksonXmlElementWrapper(localName="sections")
	@JacksonXmlProperty(localName = "section")
	public List<ShapesDocumentationSection> sections;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public List<ShapesDocumentationSection> getSections() {
		return sections;
	}
	public void setSections(List<ShapesDocumentationSection> sections) {
		this.sections = sections;
	}
	public List<NamespaceSections> getShnamespace() {
		return shnamespace;
	}
	public void setShnamespace(List<NamespaceSections> shnamespace) {
		this.shnamespace = shnamespace;
	}
	
}
