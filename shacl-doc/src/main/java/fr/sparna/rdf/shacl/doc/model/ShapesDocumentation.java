package fr.sparna.rdf.shacl.doc.model;

import java.util.List;
import java.util.Optional;

import org.apache.jena.vocabulary.RDFS;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import fr.sparna.rdf.shacl.doc.OwlOntology;

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
	protected Link license;
	protected Link creator;
	protected Link publisher;
	protected Link rightsHolder;
	
	
	@JacksonXmlElementWrapper(localName="prefixes")
	@JacksonXmlProperty(localName = "prefixe")
	protected List<NamespaceSection> prefixe;
	
	@JacksonXmlElementWrapper(localName="sections")
	@JacksonXmlProperty(localName = "section")
	protected List<ShapesDocumentationSection> sections;
	
	public ShapesDocumentation(OwlOntology ontology, String lang) {
		if(ontology != null) {
			this.setTitle(ontology.getRdfsLabel());			
			this.setComment(ontology.getRdfsComment());
			
			this.setDatecreated(ontology.getDateCreated());
			this.setDateissued(ontology.getDateIssued());
			this.setYearCopyRighted(ontology.getDateCopyrighted());
			this.setModifiedDate(ontology.getDateModified());
			this.setVersionInfo(ontology.getOwlVersionInfo());
			Optional.ofNullable(ontology.getLicense()).ifPresent(s -> {
				if(s.isURIResource() && !s.asResource().listProperties(RDFS.label, lang).toList().isEmpty()) {
					this.setLicense(new Link(s.asResource().getURI(), s.asResource().listProperties(RDFS.label, lang).toList().get(0).getLiteral().getLexicalForm()));
				} else {
					this.setLicense(new Link(s.toString(), s.toString()));
				}
			});
			Optional.ofNullable(ontology.getCreator()).ifPresent(s -> {
				if(s.isURIResource() && !s.asResource().listProperties(RDFS.label, lang).toList().isEmpty()) {
					this.setCreator(new Link(s.asResource().getURI(), s.asResource().listProperties(RDFS.label, lang).toList().get(0).getLiteral().getLexicalForm()));
				} else {
					this.setCreator(new Link(s.toString(), s.toString()));
				}
			});
			Optional.ofNullable(ontology.getPublisher()).ifPresent(s -> {
				if(s.isURIResource() && !s.asResource().listProperties(RDFS.label, lang).toList().isEmpty()) {
					this.setPublisher(new Link(s.asResource().getURI(), s.asResource().listProperties(RDFS.label, lang).toList().get(0).getLiteral().getLexicalForm()));
				} else {
					this.setPublisher(new Link(s.toString(), s.toString()));
				}
			});
			Optional.ofNullable(ontology.getRightsHolder()).ifPresent(s -> {
				if(s.isURIResource() && !s.asResource().listProperties(RDFS.label, lang).toList().isEmpty()) {
					this.setRightsHolder(new Link(s.asResource().getURI(), s.asResource().listProperties(RDFS.label, lang).toList().get(0).getLiteral().getLexicalForm()));
				} else {
					this.setRightsHolder(new Link(s.toString(), s.toString()));
				}
			});
			
			this.setDescriptionDocument(ontology.getDescription());			
		}
	}
	
	
	
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
	public Link getLicense() {
		return license;
	}
	public void setLicense(Link license) {
		this.license = license;
	}
	public Link getCreator() {
		return creator;
	}
	public void setCreator(Link creator) {
		this.creator = creator;
	}
	public Link getPublisher() {
		return publisher;
	}
	public void setPublisher(Link publisher) {
		this.publisher = publisher;
	}
	public Link getRightsHolder() {
		return rightsHolder;
	}
	public void setRightsHolder(Link rightsHolder) {
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
