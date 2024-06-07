package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ExpectedValue {

	// either linkNodeShape is populated and this is a link to a NodeShape indicated by linkNodeShapeUri...
	private Link linkNodeShape;

	// ... or expectedValueLabel is populated and this is rendered as a code
	private Link expectedValue;
	// ... or the or is populated
	private List<String> or;

	@JacksonXmlElementWrapper(localName="inValues")
	@JacksonXmlProperty(localName = "inValue")
	private List<Link> inValues;
	
	
	public List<String> getOr() {
		return or;
	}

	public void setOr(List<String> shOr) {
		this.or = shOr;
	}

	public Link getLinkNodeShape() {
		return linkNodeShape;
	}

	public void setLinkNodeShape(Link linkNodeShape) {
		this.linkNodeShape = linkNodeShape;
	}

	public Link getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(Link expectedValue) {
		this.expectedValue = expectedValue;
	}

	public List<Link> getInValues() {
		return inValues;
	}

	public void setInValues(List<Link> inValues) {
		this.inValues = inValues;
	}
	
}
