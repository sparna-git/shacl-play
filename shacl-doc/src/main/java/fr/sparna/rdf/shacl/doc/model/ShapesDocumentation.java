package fr.sparna.rdf.shacl.doc.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDFS;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import fr.sparna.rdf.shacl.doc.DcatDistribution;
import fr.sparna.rdf.shacl.doc.OwlOntology;

@JsonInclude(Include.NON_NULL)
public class ShapesDocumentation {

	protected String title;	
	protected String abstract_;
	protected String modifiedDate;
	protected String versionInfo;
	protected String descriptionDocument;
	protected String releaseNotes;
	protected String imgLogo;
	
	protected String datecreated;
	protected String dateissued;
	protected String yearCopyRighted;
	
	protected String jsonldOWL;
	
	@JacksonXmlElementWrapper(localName="diagrams")
	@JacksonXmlProperty(localName = "diagram")
	protected List<ShapesDocumentationDiagram> diagrams = new ArrayList<>();
	
	@JacksonXmlElementWrapper(localName="formats")
	@JacksonXmlProperty(localName = "format")
	protected List<DcatDistribution> format;
	
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
	@JacksonXmlElementWrapper(localName="depictions")
	@JacksonXmlProperty(localName = "depiction")
	protected List<String> depictions;
	@JacksonXmlElementWrapper(localName="prefixes")
	@JacksonXmlProperty(localName = "prefixe")
	protected List<NamespaceSection> prefixe;
	@JacksonXmlElementWrapper(localName="sections")
	@JacksonXmlProperty(localName = "section")
	protected List<ShapesDocumentationSection> sections;
	
	@JacksonXmlElementWrapper(localName="feedbacks")
	@JacksonXmlProperty(localName = "feedback")
	protected List<Link> feedback;
	
	
	
	public ShapesDocumentation(OwlOntology ontology, String lang) {
		// init markdown parser & renderer
		Parser parser = Parser.builder().build();
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		
		if(ontology != null) {
			
			this.setTitle(ontology.getTitleOrLabel(lang));
			
			String abstractString = ontology.getAbstractOrComment(lang);
			if(abstractString != null) {
				Node document = parser.parse(abstractString);					
				abstractString = renderer.render(document);	
				this.setAbstract_(abstractString);
			}
			
			this.setDatecreated(ontology.getDateCreated());
			this.setDateissued(ontology.getDateIssued());
			this.setYearCopyRighted(ontology.getDateCopyrighted());
			this.setModifiedDate(ontology.getDateModified());
			this.setVersionInfo(ontology.getOwlVersionInfo());
			
			Optional.ofNullable(ontology.getLicense(lang)).ifPresent(list -> {
				this.license = list.stream()
				.map(new RDFNodeToLinkMapper(lang))
				.collect(Collectors.toList());
			});
			
			Optional.ofNullable(ontology.getCreator(lang)).ifPresent(list -> {
				this.creator = list.stream()
				.map(new RDFNodeToLinkMapper(lang))
				.collect(Collectors.toList());
			});
			
			Optional.ofNullable(ontology.getPublisher(lang)).ifPresent(list -> {
				this.publisher = list.stream()
				.map(new RDFNodeToLinkMapper(lang))
				.collect(Collectors.toList());
			});
			
			Optional.ofNullable(ontology.getRightsHolder(lang)).ifPresent(list -> {
				this.rightsHolder = list.stream()
				.map(new RDFNodeToLinkMapper(lang))
				.collect(Collectors.toList());
			});
			
			if(ontology.getDescription(lang) != null) {	
				Node document = parser.parse(ontology.getDescription(lang));	
				String descriptionRendered = renderer.render(document);				
				this.setDescriptionDocument(descriptionRendered);
			}
			
			if(ontology.getVersionNotes(lang) != null) {				
				Node document = parser.parse(ontology.getVersionNotes(lang));	
				String versionNodeRendered = renderer.render(document);				
				this.setReleaseNotes(versionNodeRendered);
			}
			
			Optional.ofNullable(ontology.getDepiction()).ifPresent(list -> {
				this.depictions = list
						.stream()
						.map(u -> u.asResource().getURI())
						.collect(Collectors.toList());
			});
			
			this.setFormat(ontology.getDistributions());
			
			Optional.ofNullable(ontology.getRepository()).ifPresent(list -> {
				this.feedback = list.stream()
				.map(new RDFNodeToLinkMapper(lang))
				.collect(Collectors.toList());
			});
			
			// JSON Object
			org.json.JSONObject jOutput = new org.json.JSONObject();
			jOutput.put("@context","https://schema.org");
			jOutput.put("@type","TechArticle");
			jOutput.put("url",ontology.getOWLUri());
			jOutput.put("name",this.title);
			jOutput.put("datePublished",this.datecreated);
			jOutput.put("version",this.versionInfo);
			
			// License
			if (!this.getLicense().isEmpty()) {
				List<String> listURL = new ArrayList<>();
				for (Link l : this.getLicense()) {
					listURL.add(l.getHref());
				}
				jOutput.put("license",listURL);
			} 
			
			// Author
			if (!this.creator.isEmpty()) {
				org.json.JSONArray jcreator = new org.json.JSONArray(); 
				org.json.JSONObject jObj = new org.json.JSONObject();
				jObj.put("@type","Organization");
				
				for (Link l : this.getCreator()) {
					if (l.getHref() == l.getLabel()) {
						jObj.put("url",l.getHref());
					} else {
						jObj.put("name",l.getLabel());
						jObj.put("url",l.getHref());
					}
				}
				jcreator.put(jObj);
				jOutput.put("author",jcreator);
			}
			
			if (!this.publisher.isEmpty()) {
				org.json.JSONArray jpublisher = new org.json.JSONArray(); 
				org.json.JSONObject jObjPublisher = new org.json.JSONObject();
				
				jObjPublisher.put("@type","Organization");
				for (Link l : this.getCreator()) {
					if (l.getHref() == l.getLabel()) {
						jObjPublisher.put("url",l.getHref());
					} else {
						jObjPublisher.put("name",l.getLabel());
						jObjPublisher.put("url",l.getHref());
					}
				}
				jpublisher.put(jObjPublisher);
				jOutput.put("publisher",jpublisher);
				
			}
			
			if (jOutput.length() > 4) {
				this.setJsonldOWL(jOutput.toString());
			}	
		}		
	}
	
	/**
	 * @return true if this documentation is the documentation of a Dataset with statistics
	 */
	@JacksonXmlProperty(localName = "datasetDocumentation")
	public boolean isDatasetDocumentation() {
		return this.sections.stream().anyMatch(section -> {
			return (section.getNumberOfTargets() > 0) || section.getPropertiesInAllGroups().stream().anyMatch(ps -> ps.getDistinctObjects() > 0 || ps.getTriples() > 0);
		});
	}
	
	public ShapesDocumentationSection findSectionByUriOrId(String nodeShapeUriOrId) {
		return this.sections.stream().filter(s -> s.getNodeShapeUriOrId().equals(nodeShapeUriOrId)).findFirst().orElse(null);
	}
	
	
	public List<Link> getFeedback() {
		return feedback;
	}

	public void setFeedback(List<Link> feedback) {
		this.feedback = feedback;
	}
	
	public List<DcatDistribution> getFormat() {
		return format;
	}
	public void setFormat(List<DcatDistribution> format) {
		this.format = format;
	}
	public List<String> getDepictions() {
		return depictions;
	}
	public void setDepictions(List<String> depictions) {
		this.depictions = depictions;
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
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
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
	public List<ShapesDocumentationDiagram> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<ShapesDocumentationDiagram> diagrams) {
		this.diagrams = diagrams;
	}

	public String getReleaseNotes() {
		return releaseNotes;
	}

	public void setReleaseNotes(String releaseNotes) {
		this.releaseNotes = releaseNotes;
	}
	
	public String getJsonldOWL() {
		return jsonldOWL;
	}

	public void setJsonldOWL(String jsonldOWL) {
		this.jsonldOWL = jsonldOWL;
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
