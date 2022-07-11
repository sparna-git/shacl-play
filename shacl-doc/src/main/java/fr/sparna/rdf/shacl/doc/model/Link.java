package fr.sparna.rdf.shacl.doc.model;

public class Link {

	private String href;
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
