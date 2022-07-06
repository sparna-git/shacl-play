package fr.sparna.rdf.shacl.doc.model;

import org.apache.jena.rdf.model.Resource;

public class PropertyShapeDocumentation {

	private String label;
	private String shortForm;
	private String shortFormUri;
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
		String value = null;
		if(shOr != null) {
			value = shOr;
		}
		this.Or = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String output_propriete, String shLabel) {
		String Value = null;
		if (output_propriete != null) {
			Value = output_propriete.split("@")[0];
		} else {
			Value = shLabel;
		}
		this.label = Value;
	}

	public String getShortForm() {
		return shortForm;
	}

	public void setShortForm(String shortForm) {
		this.shortForm = shortForm;
	}

	public String getShortFormUri() {
		return shortFormUri;
	}

	public void setShortFormUri(String shortFormUri) {
		this.shortFormUri = shortFormUri;
	}

	public String getExpectedValueLabel() {
		return expectedValueLabel;
	}

	public void setExpectedValueLabel(
			Resource shClass,
			String Value_node,
			String Value_Target,
			String Value_datatype,
			String Value_nodeKind,
			String URI,
			String value_shValue
	) {
		String value = null;

		// Classe
		if (value_shValue != null) {
			value = value_shValue;
		}else if (shClass != null) { //
			value = shClass.getModel().shortForm(shClass.getURI());
		} else if (Value_Target != null) { // La valeur d'un Node vers une NodeShape qui a sh:targetClass
			value = Value_Target;
		} else if (Value_node != null) {
			value = Value_node;
		} // Datatype : sh:datatype
		else if (Value_datatype != null) {
			value = Value_datatype;
		} // Type de noeud seulement : sh:nodeKind
		else if (Value_nodeKind != null) {
			if (Value_nodeKind.equals("sh:IRI")) {
				String[] ssplit = Value_nodeKind.split(":");
				value = ssplit[ssplit.length - 1];	
			} else {
				value = Value_nodeKind;
			}
			// value = Value_nodeKind;
		}
		
		this.expectedValueLabel = value;
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
		String value = null;
		if (cardinalite == null || cardinalite == "") {
			value = "0..*";
		} else {
			value = cardinalite;
		}
		this.cardinalite = value;
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
