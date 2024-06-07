package fr.sparna.rdf.shacl.doc.model;

public class PropertyShapeDocumentation {

	/**
	 * Full URI of original property shape
	 */
	private String propertyShapeUriOrId;
	
	private String label;
	
	// Note : URI of the Link will be null if sh:path is a property path
	private Link propertyUri;

	private String cardinalite;
	private String description;
		
	private ExpectedValue expectedValue = new ExpectedValue();

	
	private String color;
	private int triples;
	private int distinctObjects;
	
	private String sparqlQueryProperty;

	private String distinctObjectsSparqlQuery;
	
	public String getSparqlQueryProperty() {
		return sparqlQueryProperty;
	}

	public void setSparqlQueryProperty(String sparqlQueryProperty) {
		this.sparqlQueryProperty = sparqlQueryProperty;
	}

	public ExpectedValue getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(ExpectedValue expectedValue) {
		this.expectedValue = expectedValue;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Link getPropertyUri() {
		return propertyUri;
	}

	public void setPropertyUri(Link propertyUri) {
		this.propertyUri = propertyUri;
	}

	public String getCardinalite() {
		return cardinalite;
	}

	public void setCardinalite(String cardinalite) {
		this.cardinalite = cardinalite;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getTriples() {
		return triples;
	}

	public void setTriples(int triples) {
		this.triples = triples;
	}

	public int getDistinctObjects() {
		return distinctObjects;
	}

	public void setDistinctObjects(int distinctObjects) {
		this.distinctObjects = distinctObjects;
	}

	public String getPropertyShapeUriOrId() {
		return propertyShapeUriOrId;
	}

	public void setPropertyShapeUriOrId(String propertyShapeUriOrId) {
		this.propertyShapeUriOrId = propertyShapeUriOrId;
	}	

}
