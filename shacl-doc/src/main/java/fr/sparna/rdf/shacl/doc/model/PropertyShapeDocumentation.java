package fr.sparna.rdf.shacl.doc.model;

public class PropertyShapeDocumentation {

	private String label;
	
	// short form of the URI or path - always present
	private String shortForm;
	// null if sh:path is a property path
	private String propertyUri;
	private String expectedValueLabel;

	private String expectedValueAdditionnalInfoIn;
	private String expectedValueAdditionnalInfoValue;

	private String cardinalite;
	private String description;
	private String Or;

	private String linkNodeShape;
	private String linkNodeShapeUri;	
	
	
	public String getOr() {
		return Or;
	}

	public void setOr(String shOr) {
		this.Or = shOr;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getShortForm() {
		return shortForm;
	}

	public void setShortForm(String shortForm) {
		this.shortForm = shortForm;
	}

	public String getPropertyUri() {
		return propertyUri;
	}

	public void setPropertyUri(String propertyUri) {
		this.propertyUri = propertyUri;
	}

	public String getExpectedValueLabel() {
		return expectedValueLabel;
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

	public void setExpectedValueLabel(String expectedValueLabel) {
		this.expectedValueLabel = expectedValueLabel;
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

	public String getLinkNodeShape() {
		return linkNodeShape;
	}

	public void setLinkNodeShape(String linkNodeShape) {
		this.linkNodeShape = linkNodeShape;
	}

	public String getLinkNodeShapeUri() {
		return linkNodeShapeUri;
	}

	public void setLinkNodeShapeUri(String linkNodeShapeUri) {
		this.linkNodeShapeUri = linkNodeShapeUri;
	}

}
