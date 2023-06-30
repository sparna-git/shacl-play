package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.vocabulary.DOAP;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class OwlOntology {
	
	public static String ADMS = "http://www.w3.org/ns/adms#";
	
	protected String rdfsLabel = null;
	protected String dctTitle = null;
	
	protected String dctermsAbstract = null;
	protected String rdfsComment = null;
	
	protected String owlVersionInfo = null;
	protected String description = null;
	protected String versionNotes = null;
	
	protected String dateModified = null;
	protected String dateCreated = null;
	protected String dateIssued = null;
	protected String dateCopyrighted = null;	
	protected List<RDFNode> license = null;
	protected List<RDFNode> creator = null;
	protected List<RDFNode> publisher = null;
	protected List<RDFNode> rightsHolder = null;		
	protected List<RDFNode> depiction = null;
	protected List<DcatDistribution> owlFormat = new ArrayList<DcatDistribution>(); 
	
	protected List<RDFNode> repository = null;
	
		
	public OwlOntology(Resource rOntology, String lang) {
		this.rdfsLabel = ModelReadingUtils.readLiteralInLangAsString(rOntology, RDFS.label, lang);
		this.dctTitle = ModelReadingUtils.readLiteralInLangAsString(rOntology, DCTerms.title, lang);
		
		this.dctermsAbstract = ModelReadingUtils.readLiteralInLangAsString(rOntology, DCTerms.abstract_, lang);
		this.rdfsComment = ModelReadingUtils.readLiteralInLangAsString(rOntology, RDFS.comment, lang);
		
		this.owlVersionInfo = ModelReadingUtils.readLiteralInLangAsString(rOntology,OWL.versionInfo, null);		
		this.description = ModelReadingUtils.readLiteralInLangAsString(rOntology,DCTerms.description, lang);
		this.versionNotes = ModelReadingUtils.readLiteralInLangAsString(rOntology,rOntology.getModel().createProperty(ADMS+"versionNotes"), lang);
		
		this.dateModified = ModelReadingUtils.readLiteralInLangAsString(rOntology,DCTerms.modified, null);			
		this.dateCreated = ModelReadingUtils.readLiteralInLangAsString(rOntology,DCTerms.created, null);
		this.dateIssued = ModelReadingUtils.readLiteralInLangAsString(rOntology,DCTerms.issued, null);
		
		this.dateCopyrighted = ModelReadingUtils.readLiteralInLangAsString(rOntology,DCTerms.dateCopyrighted, null);
		
		license = ModelReadingUtils.readObjectAsResourceOrLiteralInLang(rOntology, DCTerms.license, lang);
		creator = ModelReadingUtils.readObjectAsResourceOrLiteralInLang(rOntology, DCTerms.creator, lang);
		publisher = ModelReadingUtils.readObjectAsResourceOrLiteralInLang(rOntology, DCTerms.publisher, lang);
		rightsHolder = ModelReadingUtils.readObjectAsResourceOrLiteralInLang(rOntology, DCTerms.rightsHolder, lang);
		
		this.depiction = ModelReadingUtils.readObjectAsResource(rOntology, FOAF.depiction);
		
		this.owlFormat = readDcatDistibution(rOntology);
		
		this.repository = ModelReadingUtils.readObjectAsResourceOrLiteralInLang(rOntology, DOAP.repository, lang);
		
	}
	
	public List<DcatDistribution> readDcatDistibution(Resource owlOntology) {
		
		List<DcatDistribution> lOFormat = new ArrayList<DcatDistribution>();
		if(owlOntology.hasProperty(DCAT.distribution)) {
			List<Statement> rformat = owlOntology.listProperties(DCAT.distribution).toList();
			for (Statement read : rformat) {				
				if(read.getProperty(DCTerms.format).getResource().getURI().toString().equals("https://www.iana.org/assignments/media-types/text/turtle") ||
				   read.getProperty(DCTerms.format).getResource().getURI().toString().equals("https://www.iana.org/assignments/media-types/application/rdf+xml") ||
				   read.getProperty(DCTerms.format).getResource().getURI().toString().equals("https://www.iana.org/assignments/media-types/application/n-triples") ||
				   read.getProperty(DCTerms.format).getResource().getURI().toString().equals("https://www.iana.org/assignments/media-types/application/ld+json")
						) {
					DcatDistribution owlformat = new DcatDistribution();
					owlformat.setDctFormat(read.getProperty(DCTerms.format).getResource().getURI().toString());
					owlformat.setDcatURL(read.getProperty(DCAT.downloadURL).getResource().getURI().toString());
					lOFormat.add(owlformat);
				}
			}
		}
		
		String[] orderFormat = {
				"https://www.iana.org/assignments/media-types/application/ld+json",
				"https://www.iana.org/assignments/media-types/application/rdf+xml",
				"https://www.iana.org/assignments/media-types/application/n-triples",
				"https://www.iana.org/assignments/media-types/text/turtle"
		};
		
		List<DcatDistribution> OutFormat = new ArrayList<DcatDistribution>();
		for (String fmt : orderFormat) {
			for (DcatDistribution owlfmt : lOFormat) {
				if(owlfmt.getDctFormat().equals(fmt)) {
					OutFormat.add(owlfmt);
				}
			}
		}
		
		return OutFormat;		
	}
	
	
	
	
	public List<RDFNode> getRepository() {
		return repository;
	}

	public void setRepository(List<RDFNode> feedback) {
		this.repository = feedback;
	}

	public List<DcatDistribution> getOwlFormat() {
		return owlFormat;
	}

	public void setOwlFormat(List<DcatDistribution> owlFormat) {
		this.owlFormat = owlFormat;
	}

	public List<RDFNode> getDepiction() {
		return depiction;
	}
	public void setDepiction(List<RDFNode> depiction) {
		this.depiction = depiction;
	}
	public String getDctTitle() {
		return dctTitle;
	}

	public void setDctTitle(String dctTitle) {
		this.dctTitle = dctTitle;
	}

	public String getRdfsLabel() {
		return rdfsLabel;
	}

	public String getDctermsAbstract() {
		return dctermsAbstract;
	}

	public void setDctermsAbstract(String dctermsAbstract) {
		this.dctermsAbstract = dctermsAbstract;
	}

	public String getRdfsComment() {
		return rdfsComment;
	}

	public String getOwlVersionInfo() {
		return owlVersionInfo;
	}

	public String getDescription() {
		return description;
	}

	public String getDateModified() {
		return dateModified;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public String getDateIssued() {
		return dateIssued;
	}

	public String getDateCopyrighted() {
		return dateCopyrighted;
	}

	public List<RDFNode> getLicense() {
		return license;
	}

	public List<RDFNode> getCreator() {
		return creator;
	}

	public List<RDFNode> getPublisher() {
		return publisher;
	}

	public List<RDFNode> getRightsHolder() {
		return rightsHolder;
	}

	public String getVersionNotes() {
		return versionNotes;
	}

	public void setVersionNotes(String versionNotes) {
		this.versionNotes = versionNotes;
	}
	
	
	
}
