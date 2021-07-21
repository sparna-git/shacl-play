package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ShapesDocumentation {

	protected String title;	
	protected String comment;
	protected String modifiedDate;
	protected String versionInfo;
	protected String svgDiagram;
	protected String plantumlSource;
	protected String pngDiagram;
	
	@JacksonXmlElementWrapper(localName="prefixes")
	@JacksonXmlProperty(localName = "prefixe")
	protected List<NamespaceSection> prefixe;
	
	@JacksonXmlElementWrapper(localName="sections")
	@JacksonXmlProperty(localName = "section")
	protected List<ShapesDocumentationSection> sections;
	
	
	
	
	public String getPngDiagram() {
		return pngDiagram;
	}
	public void setPngDiagram(String pngDiagram) {
		this.pngDiagram = pngDiagram;
	}
	
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	public String getSvgDiagram() {
		return svgDiagram;
	}
	public void setSvgDiagram(String svgDiagram) {
		this.svgDiagram = svgDiagram;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String commentOntology) {
		this.comment = commentOntology;
	}
	
	public String getVersionInfo() {
		return versionInfo;
	}
	public void setVersionInfo(String versionInfo) {
		this.versionInfo = versionInfo;
	}
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
	
	public List<NamespaceSection> getPrefixe() {
		return prefixe;
	}
	public void setPrefixe(List<NamespaceSection> prefixe) {
		this.prefixe = prefixe;
	}
	public String getPlantumlSource() {
		return plantumlSource;
	}
	public void setPlantumlSource(String plantumlSource) {
		this.plantumlSource = plantumlSource;
	}
	
	
	
}
