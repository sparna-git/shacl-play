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
	protected String descriptionDocument;
	protected String imgLogo;
	
	protected String datecreated;
	protected String dateissued;
	protected String yearCopyRighted;
	protected String license;
	protected String creator;
	protected String publisher;
	protected String rightsHolder;
	
	
	@JacksonXmlElementWrapper(localName="prefixes")
	@JacksonXmlProperty(localName = "prefixe")
	protected List<NamespaceSection> prefixe;
	
	@JacksonXmlElementWrapper(localName="sections")
	@JacksonXmlProperty(localName = "section")
	protected List<ShapesDocumentationSection> sections;
	
	
	public String getDatecreated() {
		return datecreated;
	}
	public void setDatecreated(String datecreated) {
		this.datecreated = datecreated;
	}
	public String getDateissued() {
		return dateissued;
	}
	public void setDateissued(String dateissued) {
		this.dateissued = dateissued;
	}
	public String getYearCopyRighted() {
		return yearCopyRighted;
	}
	public void setYearCopyRighted(String yearCopyRighted) {
		this.yearCopyRighted = yearCopyRighted;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getRightsHolder() {
		return rightsHolder;
	}
	public void setRightsHolder(String rightsHolder) {
		this.rightsHolder = rightsHolder;
	}
	public String getImgLogo() {
		return imgLogo;
	}
	public void setImgLogo(String imgLogo) {
		this.imgLogo = imgLogo;
	}
	public String getDescriptionDocument() {
		return descriptionDocument;
	}
	public void setDescriptionDocument(String descriptionDocument) {
		this.descriptionDocument = descriptionDocument;
	}
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
