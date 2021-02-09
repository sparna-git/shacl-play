package fr.sparna.rdf.shacl.doc.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NamespaceSections {
	private String output_prefix;
	private String output_namespace;
	
	
	public String getOutput_prefix() {
		return output_prefix;
	}

	public void setOutput_prefix(String output_prefix) {
		this.output_prefix = output_prefix;
	}

	public String getOutput_namespace() {
		return output_namespace;
	}

	public void setOutput_namespace(String output_namespace) {
		this.output_namespace = output_namespace;
	}


}
