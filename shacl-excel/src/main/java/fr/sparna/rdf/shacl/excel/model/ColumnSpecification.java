package fr.sparna.rdf.shacl.excel.model;

import java.util.Objects;

import org.apache.jena.shared.PrefixMapping;
import org.topbraid.shacl.vocabulary.SH;

public class ColumnSpecification {
	
	protected String propertyUri;
	protected String datatypeUri;
	protected String language;
	protected boolean isInverse = false;
	
	protected String label;
	protected String description;	
	
	protected String headerString;
	
	public ColumnSpecification(PropertyShapeTemplate pShape) {
		if(pShape.getSh_path().isURIResource()) {
			this.propertyUri = pShape.getSh_path().getURI();
		} else if(pShape.getSh_path().hasProperty(SH.inversePath)) {
			this.isInverse = true;
			this.propertyUri = pShape.getSh_path().getProperty(SH.inversePath).getObject().asResource().getURI();
		}
		
		this.datatypeUri = pShape.getDatatype();
		this.label = pShape.getSh_name();
		this.description = pShape.getSh_description();
		this.recomputeHeaderString(pShape.getPropertyShape().getModel());
	}
	
	public ColumnSpecification(String headerString, String label, String description) {
		this.headerString = headerString;
		this.label = label;
		this.description = description;
	}
	
	public ColumnSpecification(String propertyUri) {
		this.propertyUri = propertyUri;
	}
	
	
	public void recomputeHeaderString(PrefixMapping mappings) {	
		this.headerString = "";
		if(this.isInverse) {
			this.headerString += "^";
		}
		if(this.getDatatypeUri() != null) {
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
