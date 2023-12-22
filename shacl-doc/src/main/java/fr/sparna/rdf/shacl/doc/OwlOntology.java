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
	public static String VERSION_NOTES = ADMS+"versionNotes";
	
	protected Resource resource;

		
	public OwlOntology(Resource rOntology) {
		this.resource = rOntology;		
	}	
	
	public List<RDFNode> getRepository() {
		return ModelReadingUtils.readObjectAsResource(this.resource, DOAP.repository);
	}

	public List<DcatDistribution> getDistributions() {
		List<DcatDistribution> lOFormat = new ArrayList<DcatDistribution>();
		if(this.resource.hasProperty(DCAT.distribution)) {
			List<Statement> rformat = this.resource.listProperties(DCAT.distribution).toList();
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
	
	public String getTitleOrLabel(String lang) {
		String title = this.getDctTitle(lang);
		if(title != null) {
			return title;
		} else {
			return this.getRdfsLabel(lang);
		}
	}
	
	public String getAbstractOrComment(String lang) {
		String a = this.getDctermsAbstract(lang);
		if(a != null) {
			return a;
		} else {
			return this.getRdfsComment(lang);
		}
	}

	public List<RDFNode> getDepiction() {
		return ModelReadingUtils.readObjectAsResource(this.resource, FOAF.depiction);
	}

	public String getDctTitle(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(this.resource, DCTerms.title, lang);
	}

	public String getRdfsLabel(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(this.resource, RDFS.label, lang);
	}

	public String getDctermsAbstract(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(this.resource, DCTerms.abstract_, lang);
	}

	public String getRdfsComment(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(this.resource, RDFS.comment, lang);
	}

	public String getOwlVersionInfo() {
		return ModelReadingUtils.readLiteralAsString(this.resource,OWL.versionInfo);
	}

	public String getDescription(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(this.resource,DCTerms.description, lang);
	}

	public String getDateModified() {
		return ModelReadingUtils.readLiteralAsString(this.resource,DCTerms.modified);
	}

	public String getDateCreated() {
		return ModelReadingUtils.readLiteralAsString(this.resource,DCTerms.created);
	}

	public String getDateIssued() {
		return ModelReadingUtils.readLiteralAsString(this.resource,DCTerms.issued);
	}

	public String getDateCopyrighted() {
		return ModelReadingUtils.readLiteralAsString(this.resource,DCTerms.dateCopyrighted);
	}

	public List<RDFNode> getLicense(String lang) {
		return ModelReadingUtils.readObjectAsResourceOrLiteralInLang(this.resource, DCTerms.license, lang);
	}

	public List<RDFNode> getCreator(String lang) {
		return ModelReadingUtils.readObjectAsResourceOrLiteralInLang(this.resource, DCTerms.creator, lang);
	}

	public List<RDFNode> getPublisher(String lang) {
		return ModelReadingUtils.readObjectAsResourceOrLiteralInLang(this.resource, DCTerms.publisher, lang);
	}

	public List<RDFNode> getRightsHolder(String lang) {
		return ModelReadingUtils.readObjectAsResourceOrLiteralInLang(this.resource, DCTerms.rightsHolder, lang);
	}

	public String getVersionNotes(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(this.resource,this.resource.getModel().createProperty(VERSION_NOTES), lang);
	}
	
	
	
}
