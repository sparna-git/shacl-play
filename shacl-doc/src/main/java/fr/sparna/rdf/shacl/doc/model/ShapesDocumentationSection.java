package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import org.apache.jena.rdf.model.Resource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ShapesDocumentationSection {

	private String title;
	private String dURI;
	private String comments;
	private String patternNS;
	private String NodeKindNS;
	private String CloseNS;
	@JacksonXmlElementWrapper(localName="properties")
	@JacksonXmlProperty(localName = "property")
	public List<PropertyShapeDocumentation> propertySections;
	
		
	public String getPatternNS() {
		return patternNS;
	}

	public void setPatternNS(String patternNS) {
		this.patternNS = patternNS;
	}

	public String getNodeKindNS() {
		return NodeKindNS;
	}

	public void setNodeKindNS(String nodeKindNS) {
		NodeKindNS = nodeKindNS;
	}

	public String getCloseNS() {
		return CloseNS;
	}

	public void setCloseNS(String closeNS) {
		CloseNS = closeNS;
	}

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
