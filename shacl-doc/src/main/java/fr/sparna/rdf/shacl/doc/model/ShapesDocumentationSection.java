package fr.sparna.rdf.shacl.doc.model;

import java.util.List;

import org.apache.jena.rdf.model.Resource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ShapesDocumentationSection {

	private String title;
	private String uri;
	private String description;
	private String targetClassLabel;
	private String targetClassUri;
	private String pattern;
	private String nodeKind;
	private Boolean closed;
	private String skosExample;
	
	@JacksonXmlElementWrapper(localName="properties")
	@JacksonXmlProperty(localName = "property")
	public List<PropertyShapeDocumentation> propertySections;
	
	
	
	
	
	public String getSkosExample() {
		return skosExample;
	}

	public void setSkosExample(String skosExample) {
		this.skosExample = skosExample;
	}

	public String getTitle() {
		return title;		
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTargetClassLabel() {
		return targetClassLabel;
	}

	public void setTargetClassLabel(String targetClassLabel) {
		this.targetClassLabel = targetClassLabel;
	}
	
	public String getTargetClassUri() {
		return targetClassUri;
	}

	public void setTargetClassUri(String targetClassUri) {
		this.targetClassUri = targetClassUri;
	}
	
	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public String getNodeKind() {
		return nodeKind;
	}

	public void setNodeKind(String nodeKind) {
		String value =null;
		if(nodeKind != null) {
			if(nodeKind.contains(":")) {
				value = nodeKind.split(":")[1];
			}else {
				value = nodeKind;
			}
			
		}
		this.nodeKind = nodeKind;
	}

	public Boolean getClosed() {
		return closed;
	}

	public void setClosed(Boolean closed) {
		this.closed = closed;
	}
	
	public List<PropertyShapeDocumentation> getPropertySections() {
		return propertySections;
	}
	public void setPropertySections(List<PropertyShapeDocumentation> propertySections) {
		this.propertySections = propertySections;
	}	
}
