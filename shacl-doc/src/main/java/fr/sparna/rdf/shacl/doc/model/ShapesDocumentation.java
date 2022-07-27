package fr.sparna.rdf.shacl.doc.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDFS;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import fr.sparna.rdf.shacl.doc.OwlOntology;

@JsonInclude(Include.NON_NULL)
public class ShapesDocumentation {

	protected String title;	
	protected String abstract_;
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
	@JacksonXmlElementWrapper(localName="licenses")
	@JacksonXmlProperty(localName = "license")
	protected List<Link> license;
	@JacksonXmlElementWrapper(localName="creators")
	@JacksonXmlProperty(localName = "creator")
	protected List<Link> creator;
	@JacksonXmlElementWrapper(localName="publishers")
	@JacksonXmlProperty(localName = "publisher")
	protected List<Link> publisher;
	@JacksonXmlElementWrapper(localName="rightsHolders")
	@JacksonXmlProperty(localName = "rightsHolder")
	protected List<Link> rightsHolder;
	
	
	@JacksonXmlElementWrapper(localName="prefixes")
	@JacksonXmlProperty(localName = "prefixe")
	protected List<NamespaceSection> prefixe;
	
	@JacksonXmlElementWrapper(localName="sections")
	@JacksonXmlProperty(localName = "section")
	protected List<ShapesDocumentationSection> sections;
	
	public ShapesDocumentation(OwlOntology ontology, String lang) {
		if(ontology != null) {
			
			if(ontology.getDctTitle() != null) {
				this.setTitle(ontology.getDctTitle());
			} else {
				this.setTitle(ontology.getRdfsLabel());
			}
						
			this.setAbstract_(ontology.getRdfsComment());
			
			this.setDatecreated(ontology.getDateCreated());
			this.setDateissued(ontology.getDateIssued());
			this.setYearCopyRighted(ontology.getDateCopyrighted());
			this.setModifiedDate(ontology.getDateModified());
			this.setVersionInfo(ontology.getOwlVersionInfo());
			
			Optional.ofNullable(ontology.getLicense()).ifPresent(list -> {
				this.license = list.stream()
				.map(new RDFNodeToLinkMapper(lang))
				.collect(Collectors.toList());
			});
			
			Optional.ofNullable(ontology.getCreator()).ifPresent(list -> {
				this.creator = list.stream()
				.map(new RDFNodeToLinkMapper(lang))
				.collect(Collectors.toList());
			});
			
			Optional.ofNullable(ontology.getPublisher()).ifPresent(list -> {
				this.publisher = list.stream()
				.map(new RDFNodeToLinkMapper(lang))
				.collect(Collectors.toList());
			});
			
			Optional.ofNullable(ontology.getRightsHolder()).ifPresent(list -> {
				this.rightsHolder = list.stream()
				.map(new RDFNodeToLinkMapper(lang))
				.collect(Collectors.toList());
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
	public List<Link> getLicense() {
		return license;
	}
	public void setLicense(List<Link> license) {
		this.license = license;
	}
	public List<Link> getCreator() {
		return creator;
	}
	public void setCreator(List<Link> creator) {
		this.creator = creator;
	}
	public List<Link> getPublisher() {
		return publisher;
	}
	public void setPublisher(List<Link> publisher) {
		this.publisher = publisher;
	}
	public List<Link> getRightsHolder() {
		return rightsHolder;
	}
	public void setRightsHolder(List<Link> rightsHolder) {
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
	
	
	public String getAbstract_() {
		return abstract_;
	}



	public void setAbstract_(String abstract_) {
		this.abstract_ = abstract_;
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
	
	class RDFNodeToLinkMapper implements Function<RDFNode, Link>{
		protected String lang;

		public RDFNodeToLinkMapper(String lang) {
			super();
			this.lang = lang;
		}

		@Override
		public Link apply(RDFNode node) {
			if(node.isURIResource() && !node.asResource().listProperties(RDFS.label, lang).toList().isEmpty()) {
				return new Link(node.asResource().getURI(), node.asResource().listProperties(RDFS.label, lang).toList().get(0).getLiteral().getLexicalForm());
			} else if(node.isURIResource()) {
				return new Link(node.toString(), node.toString());
			} else if(node.isLiteral()) {
				return new Link(null, node.asLiteral().getLexicalForm());
			} else {
				return new Link(null, node.toString());
			}
		}
		
		
	}
	
}
