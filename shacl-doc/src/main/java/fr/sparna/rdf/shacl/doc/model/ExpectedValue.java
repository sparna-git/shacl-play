package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ExpectedValue {

	// the expected value link, can be either a local link to a NodeShape in the same document
	// or an external link to a class URI
	// it is possible that only the label is populated, without the href
	private Link expectedValue;

	// ... or the or is populated
	@JacksonXmlElementWrapper(localName="ors")
	@JacksonXmlProperty(localName = "or")
	private List<Link> or;

	@JacksonXmlElementWrapper(localName="inValues")
	@JacksonXmlProperty(localName = "inValue")
	private List<Link> inValues;
	
	private String pattern;
	
	
	public List<Link> getOr() {
		return or;
	}

	public void setOr(List<Link> shOr) {
		this.or = shOr;
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

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
}
