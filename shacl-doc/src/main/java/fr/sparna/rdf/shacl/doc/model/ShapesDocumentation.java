package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ShapesDocumentation {

	protected String title;	
	// RENAME : à supprimer
	protected String subtitle;
	// RENAME : comment
	protected String commentOntology;
	// RENAME : modifiedDate
	protected String DateModification;
	// RENAME : versionInfo
	protected String VersionOntology;
	// RENAME : svgDiagram
	protected String drawnImagenXML;
	
	// RENAME : prefixes
	@JacksonXmlElementWrapper(localName="shnamespaces")
	@JacksonXmlProperty(localName = "shnamespace")
	protected List<NamespaceSections> shnamespace;
	
	@JacksonXmlElementWrapper(localName="sections")
	@JacksonXmlProperty(localName = "section")
	protected List<ShapesDocumentationSection> sections;
	
	public String getDateModification() {
		return DateModification;
	}
	public void setDateModification(String dateModification) {
		DateModification = dateModification;
	}
	public String getDrawnImagenXML() {
		return drawnImagenXML;
	}
	public void setDrawnImagenXML(String drawnImagenXML) {
		this.drawnImagenXML = drawnImagenXML;
	}
	public String getCommentOntology() {
		return commentOntology;
	}
	public void setCommentOntology(String commentOntology) {
		this.commentOntology = commentOntology;
	}
	
	public String getVersionOntology() {
		return VersionOntology;
	}
	public void setVersionOntology(String versionOntology) {
		VersionOntology = versionOntology;
	}
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
