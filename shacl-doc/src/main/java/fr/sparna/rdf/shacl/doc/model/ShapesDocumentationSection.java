package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import org.apache.jena.rdf.model.Resource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ShapesDocumentationSection {

	private String title;
	
	// RENAME : uri
	private String dURI;
	// RENAME : description
	private String comments;
	// RENAME : targetClassLabel
	private String pTargetClass;
	// RENAME : targetClassUri
	private String linkTargetClass;
	// RENAME : pattern
	private String patternNS;
	// RENAME : nodeKind
	private String NodeKindNS;
	// RENAME : closed
	private Boolean CloseNS;
	
	@JacksonXmlElementWrapper(localName="properties")
	@JacksonXmlProperty(localName = "property")
	public List<PropertyShapeDocumentation> propertySections;
	
	
	
	public String getLinkTargetClass() {
		return linkTargetClass;
	}

	public void setLinkTargetClass(String linkTargetClass) {
		this.linkTargetClass = linkTargetClass;
	}

	public String getpTargetClass() {
		return pTargetClass;
	}

	public void setpTargetClass(String pTargetClass) {
		this.pTargetClass = pTargetClass;
	}

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
		String value =null;
		if(nodeKindNS != null) {
			value = nodeKindNS.split(":")[1];
		}
		NodeKindNS = value;
	}

	public Boolean getCloseNS() {
		return CloseNS;
	}

	public void setCloseNS(Boolean closeNS) {
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
