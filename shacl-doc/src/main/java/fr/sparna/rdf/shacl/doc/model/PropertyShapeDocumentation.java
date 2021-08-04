package fr.sparna.rdf.shacl.doc.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import fr.sparna.rdf.shacl.diagram.PlantUmlBox;
import fr.sparna.rdf.shacl.diagram.PlantUmlProperty;
import fr.sparna.rdf.shacl.doc.ShaclBox;
import fr.sparna.rdf.shacl.doc.ShaclProperty;

public class PropertyShapeDocumentation {

	private String label;
	private String shortForm;
	private String shortFormUri;
	private String expectedValueLabel;

	private String expectedValueAdditionnalInfoPattern;
	private String expectedValueAdditionnalInfoIn;
	private String expectedValueAdditionnalInfoValue;

	private String cardinalite;
	private String description;
	private String Or;

	private String linknameNodeShape;
	private String linknameNodeShapeuri;
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

	public void setExpectedValueLabel(String Valeu_class, String Value_node, String Value_Target, String Value_datatype,
			String Value_nodeKind, String URI, String value_shValue) {
		String value = null;

		// Classe
		if (value_shValue != null) {
			value = value_shValue;
		}else if (Valeu_class != null) { //
			value = Valeu_class;
		} else if (Value_Target != null) { // La valeur d'un Node vers une NodeShape qui a sh:targetClass
			value = Value_Target;
		} else if (Value_node != null) {
			value = Value_node;
		} // Datatype : sh:datatype
		else if (Value_datatype != null) {
			value = Value_datatype;
		} else if (Value_datatype != null && Value_node != null) {
			value = Value_node;
		} // Type de noeud seulement : sh:nodeKind
		else if (Value_nodeKind != null && Value_node == null) {
			if (Value_nodeKind.equals("sh:IRI")) {
				String[] ssplit = Value_nodeKind.split(":");
				value = ssplit[ssplit.length - 1];	
			} else {
				value = Value_nodeKind;
			}
			// value = Value_nodeKind;
		} else if (Value_nodeKind != null && Value_node != null) {
			value = Value_node;
		}
		this.expectedValueLabel = value;
	}

	public String getExpectedValueAdditionnalInfoPattern() {
		return expectedValueAdditionnalInfoPattern;
	}

	public void setExpectedValueAdditionnalInfoPattern(String Value_pattern_propriete, String PatternNodeShape,
			String PatternoNodeShape, String Valeu_class, String Value_node, String Value_Target, String Value_datatype,
			String Value_nodeKind, String URI) {
		String value = null;

		// Classe

		if (Valeu_class != null) { //
			value = Value_pattern_propriete;
		} else if (Value_Target != null) { // La valeur d'un Node vers une NodeShape qui a sh:targetClass
			value = PatternoNodeShape;
		} else if (Value_node != null) {
			// value = PatternoNodeShape;
		} else if (Value_datatype != null) {
			value = Value_pattern_propriete;
		} else if (Value_datatype != null && Value_node != null) {
			value = PatternoNodeShape;
		} // Type de noeud seulement : sh:nodeKind
		else if (Value_nodeKind != null && Value_node == null) {
			value = Value_pattern_propriete;
		} else if (Value_nodeKind != null && Value_node != null) {
			value = PatternoNodeShape;
		}
		this.expectedValueAdditionnalInfoPattern = expectedValueAdditionnalInfoPattern;
		// this.expectedValueAdditionnalInfoPattern = value;

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

	public String getLinknameNodeShape() {
		return linknameNodeShape;
	}

	public void setLinknameNodeShape(String linknameNodeShape) {
		this.linknameNodeShape = linknameNodeShape;
	}

	public String getLinknameNodeShapeuri() {
		return linknameNodeShapeuri;
	}

	public void setLinknameNodeShapeuri(String linknameNodeShapeuri) {
		this.linknameNodeShapeuri = linknameNodeShapeuri;
	}

}
