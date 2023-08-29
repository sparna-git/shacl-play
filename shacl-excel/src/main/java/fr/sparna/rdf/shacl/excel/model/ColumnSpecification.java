package fr.sparna.rdf.shacl.excel.model;

import java.util.Objects;

import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.XSD;
import org.topbraid.shacl.vocabulary.SH;

public class ColumnSpecification {
	
	protected String propertyUri;
	protected String datatypeUri;
	protected String language;
	protected boolean isInverse = false;
	
	protected String label;
	protected String description;

	protected boolean forceValuesToBlankNodes = false;
	
	// the final header string, including "^^" or "@"
	protected String headerString;
	
	public ColumnSpecification(PropertyShape pShape, String language,String DataLanguage) {
		if(pShape.getSh_path().isURIResource()) {
			this.propertyUri = pShape.getSh_path().getURI();
		} else if(pShape.getSh_path().hasProperty(SH.inversePath)) {
			this.isInverse = true;
			this.propertyUri = pShape.getSh_path().getProperty(SH.inversePath).getObject().asResource().getURI();
		}
		
		//get the language, not a language list
		this.language = DataLanguage;
		this.datatypeUri = (pShape.getDatatype() != null)?pShape.getDatatype().getURI():null;
		this.label = pShape.getSh_name(language);
		this.description = pShape.getSh_description(language);
		this.recomputeHeaderString(pShape.getPropertyShape().getModel());
		
		// super specific : for sh:or, force blank nodes
		if(pShape.getSh_path().equals(SH.or)) {
			this.forceValuesToBlankNodes = true;
		}
	}
	
	public ColumnSpecification(String headerString, String label, String description) {
		this.headerString = headerString;
		this.label = label;
		this.description = description;
	}
	
	public ColumnSpecification(String propertyUri) {
		this.propertyUri = propertyUri;
	}
	
	public ColumnSpecification(Statement statement) {
		this.propertyUri = statement.getPredicate().getURI();
		if (statement.getObject().isLiteral()) {
			if (!statement.getObject().asLiteral().getLanguage().isEmpty()) {
				this.setLanguage(statement.getObject().asLiteral().getLanguage());
			} else if (!statement.getObject().asLiteral().getDatatypeURI().equals(XSD.xstring.getURI())) {
				this.setDatatypeUri(statement.getObject().asLiteral().getDatatypeURI());
			} 
		}
		
		this.recomputeHeaderString(statement.getModel());
	}
	
	
	public void recomputeHeaderString(PrefixMapping mappings) {	
		this.headerString = "";
		if(this.isInverse) {
			this.headerString += "^";
		}
		
		if (this.language != null & this.getDatatypeUri() == null) {
			this.headerString += mappings.shortForm(this.propertyUri)+"@"+ mappings.shortForm(this.language);
		} else if(this.getDatatypeUri() != null) {
			this.headerString += mappings.shortForm(this.propertyUri)+"^^"+ mappings.shortForm(this.datatypeUri);
		} else {
			this.headerString += mappings.shortForm(this.propertyUri);
		}
	}
	
	public String getHeaderString() {
		return headerString;
	}

	public void setHeaderString(String headerString) {
		this.headerString = headerString;
	}

	public String getPropertyUri() {
		return propertyUri;
	}

	public void setPropertyUri(String propertyUri) {
		this.propertyUri = propertyUri;
	}

	public String getDatatypeUri() {
		return datatypeUri;
	}

	public void setDatatypeUri(String datatypeUri) {
		this.datatypeUri = datatypeUri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isInverse() {
		return isInverse;
	}

	public void setInverse(boolean isInverse) {
		this.isInverse = isInverse;
	}

	public boolean isForceValuesToBlankNodes() {
		return forceValuesToBlankNodes;
	}

	public void setForceValuesToBlankNodes(boolean forceValuesToBlankNodes) {
		this.forceValuesToBlankNodes = forceValuesToBlankNodes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(datatypeUri, language, propertyUri);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnSpecification other = (ColumnSpecification) obj;
		return Objects.equals(datatypeUri, other.datatypeUri) && Objects.equals(language, other.language)
				&& Objects.equals(propertyUri, other.propertyUri);
	}
}
