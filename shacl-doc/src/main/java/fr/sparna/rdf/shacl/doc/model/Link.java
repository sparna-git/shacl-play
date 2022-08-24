package fr.sparna.rdf.shacl.doc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * An hyperlink, with href and label.
 * Href can be null.
 *
 */
@JsonInclude(Include.NON_NULL)
public class Link {

	// do not include an empty <href /> in XML when null
	@JsonInclude(Include.NON_NULL)
	private String href;
	// do not include an empty <label /> in XML when null
	@JsonInclude(Include.NON_NULL)
	private String label;	
	
	public Link() {
		super();
	}

	public Link(String href, String label) {
		super();
		this.href = href;
		this.label = label;
	}
	
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	
	
}
