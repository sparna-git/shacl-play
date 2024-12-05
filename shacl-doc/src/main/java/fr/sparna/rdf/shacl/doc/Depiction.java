package fr.sparna.rdf.shacl.doc;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(Include.NON_NULL)
public class Depiction {
	
	// image URL
	private String depiction;
	private String title;
	private String description;
	// order of the diagram
	private Double shorder;
	
	public String getDepiction() {
		return depiction;
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
	public void setDepiction(String depiction) {
		this.depiction = depiction;
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
