package fr.sparna.rdf.shacl.doc.model;

public class PropertyShapeDocumentation {

	private String label;
	
	// null if sh:path is a property path
	private Link propertyUri;
	

	private String expectedValueAdditionnalInfoIn;
	private String expectedValueAdditionnalInfoValue;

	private String cardinalite;
	private String description;
		
	private ExpectedValue expectedValue = new ExpectedValue();


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

	public String getExpectedValueAdditionnalInfoIn() {
		return expectedValueAdditionnalInfoIn;
	}

	public void setExpectedValueAdditionnalInfoIn(String expectedValueAdditionnalInfoIn) {
		this.expectedValueAdditionnalInfoIn = expectedValueAdditionnalInfoIn;
	}

	public String getExpectedValueAdditionnalInfoValue() {
		return expectedValueAdditionnalInfoValue;
	}

	public void setExpectedValueAdditionnalInfoValue(String expectedValueAdditionnalInfoValue) {
		this.expectedValueAdditionnalInfoValue = expectedValueAdditionnalInfoValue;
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

}
