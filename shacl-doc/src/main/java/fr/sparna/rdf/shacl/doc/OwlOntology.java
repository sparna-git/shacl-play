package fr.sparna.rdf.shacl.doc;

import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class OwlOntology {
	
	protected String rdfsLabel = null;
	protected String dctTitle = null;
	protected String rdfsComment = null;
	protected String owlVersionInfo = null;
	protected String description = null;
	
	protected String dateModified = null;
	protected String dateCreated = null;
	protected String dateIssued = null;
	protected String dateCopyrighted = null;	
	protected List<RDFNode> license = null;
	protected List<RDFNode> creator = null;
	protected List<RDFNode> publisher = null;
	protected List<RDFNode> rightsHolder = null;		
	
	protected List<RDFNode> Depiction = null;
	
	protected List<RDFNode> FormatDistribution = null;
	
	protected List<OwlFormat> oFormat = new ArrayList<OwlFormat>(); 
	
		
	public OwlOntology(Resource rOntology, String lang) {
		this.rdfsLabel = ConstraintValueReader.readLiteralInLangAsString(rOntology, RDFS.label, lang);
		this.dctTitle = ConstraintValueReader.readLiteralInLangAsString(rOntology, DCTerms.title, lang);
		this.rdfsComment = ConstraintValueReader.readLiteralInLangAsString(rOntology, RDFS.comment, lang);
		this.owlVersionInfo = ConstraintValueReader.readLiteralInLangAsString(rOntology,OWL.versionInfo, null);		
		this.description = ConstraintValueReader.readLiteralInLangAsString(rOntology,DCTerms.description, lang);
		
		this.dateModified = ConstraintValueReader.readLiteralInLangAsString(rOntology,DCTerms.modified, null);			
		this.dateCreated = ConstraintValueReader.readLiteralInLangAsString(rOntology,DCTerms.created, null);
		this.dateIssued = ConstraintValueReader.readLiteralInLangAsString(rOntology,DCTerms.issued, null);
		
		this.dateCopyrighted = ConstraintValueReader.readLiteralInLangAsString(rOntology,DCTerms.dateCopyrighted, null);
		
		license = ConstraintValueReader.readObjectAsResourceOrLiteralInLang(rOntology, DCTerms.license, lang);
		creator = ConstraintValueReader.readObjectAsResourceOrLiteralInLang(rOntology, DCTerms.creator, lang);
		publisher = ConstraintValueReader.readObjectAsResourceOrLiteralInLang(rOntology, DCTerms.publisher, lang);
		rightsHolder = ConstraintValueReader.readObjectAsResourceOrLiteralInLang(rOntology, DCTerms.rightsHolder, lang);
		
		this.Depiction = ConstraintValueReader.readObjectAsResourceOrLiteralInLang(rOntology, FOAF.depiction, null);
		
		oFormat = readDactDistibution(rOntology);
		
		this.FormatDistribution = ConstraintValueReader.readObjectAsResourceOrLiteralInLang(rOntology, DCAT.distribution, null);
		
	}
	
	public List<OwlFormat> readDactDistibution(Resource owlOntology) {
		
		List<OwlFormat> lOFormat = new ArrayList<OwlFormat>();
		OwlFormat oFormat = new OwlFormat();
		
		if(owlOntology.hasProperty(DCAT.distribution)) {
			List<Statement> rDistribution = owlOntology.listProperties(DCAT.distribution, null).toList();
			for (Statement read : rDistribution) {
				
				switch (read.getResource().getURI()) {
				case "https://www.iana.org/assignments/media-types/text/turtle": {
					oFormat.setDctFormat(read.getResource());
					oFormat.setDcatURL(read.getResource());
					break;
				}
				case "https://www.iana.org/assignments/media-types/application/rdf+xml": {
					
					break;
				}
				case "https://www.iana.org/assignments/media-types/application/n-triples": {
					
					break;
				}
				case "https://www.iana.org/assignments/media-types/application/ld+json": {
					
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + read.getResource().getURI());
				}
				lOFormat.add(oFormat);
			}
		}		
		return lOFormat;		
	}
	
	public List<RDFNode> getFormatDistribution() {
		return FormatDistribution;
	}

	public void setFormatDistribution(List<RDFNode> FormatDistribution) {
		this.FormatDistribution = FormatDistribution;
	}
	
	
	public List<OwlFormat> getoFormat() {
		return oFormat;
	}

	public void setoFormat(List<OwlFormat> oFormat) {
		this.oFormat = oFormat;
	}

	public List<RDFNode> getDepiction() {
		return Depiction;
	}
	public void setDepiction(List<RDFNode> depiction) {
		Depiction = depiction;
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
	
}
