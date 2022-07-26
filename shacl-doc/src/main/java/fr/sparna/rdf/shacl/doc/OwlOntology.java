package fr.sparna.rdf.shacl.doc;

import java.util.Optional;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
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
	protected RDFNode license = null;
	protected RDFNode creator = null;
	protected RDFNode publisher = null;
	protected RDFNode rightsHolder = null;		
	
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
		
		license = Optional.ofNullable(rOntology.getProperty(DCTerms.license)).map(s -> s.getObject()).orElse(null);
		creator = Optional.ofNullable(rOntology.getProperty(DCTerms.creator)).map(s -> s.getObject()).orElse(null);
		publisher = Optional.ofNullable(rOntology.getProperty(DCTerms.publisher)).map(s -> s.getObject()).orElse(null);
		rightsHolder = Optional.ofNullable(rOntology.getProperty(DCTerms.rightsHolder)).map(s -> s.getObject()).orElse(null);
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

	public RDFNode getLicense() {
		return license;
	}

	public RDFNode getCreator() {
		return creator;
	}

	public RDFNode getPublisher() {
		return publisher;
	}

	public RDFNode getRightsHolder() {
		return rightsHolder;
	}
	
	
}
