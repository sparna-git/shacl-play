package fr.sparna.rdf.shacl.doc;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(Include.NON_NULL)
public class Depiction {
	
	// image URL
	private String src;
	private String title;
	private String description;
	// order of the diagram
	private Double shorder;

	public Depiction() {
		// default constructor
	}

	public Depiction(String src) {
		this.src = src;
	}
	
	public String getSrc() {
		return src;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public Double getShorder() {
		return shorder;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setShorder(Double shorder) {
		this.shorder = shorder;
	}
	
	
}
