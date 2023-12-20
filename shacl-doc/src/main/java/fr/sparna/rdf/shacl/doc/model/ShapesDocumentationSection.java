package fr.sparna.rdf.shacl.doc.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(Include.NON_NULL)
public class ShapesDocumentationSection {

	
	/**
	 * The section ID, set to the URI short form
	 */
	private String sectionId;
	private String title;
	/**
	 * The subtitle to display, set to the full URI. Can be null
	 */
	private String subtitleUri;	
	private String description;
	
	private int numberOfInstances;
	
	private Link targetClass;
	
	// private String targetClassLabel;
	// private String targetClassUri;
	private String pattern;
	private String nodeKind;
	private Boolean closed;
	private String skosExample;
	private String color;
	//private List<String> MessageOfValidate = new ArrayList<>();
	@JacksonXmlElementWrapper(localName="MessageSeverities")
	@JacksonXmlProperty(localName = "MessageSeverity")
	protected List<String> MessageSeverities;
	
	/**
	 * The target of the shape when it is expressed using a SPARQL query
	 */
	private String sparqlTarget;
	
	@JacksonXmlElementWrapper(localName="superClasses")
	@JacksonXmlProperty(localName = "link")
	private List<Link> superClasses;
	
	@JacksonXmlElementWrapper(localName="properties")
	@JacksonXmlProperty(localName = "property")
	public List<PropertyShapeDocumentation> propertySections;

	@JacksonXmlElementWrapper(localName="Charts")
	@JacksonXmlProperty(localName = "Chart")
	protected List<ChartDataset> ChartDataSection;
	
	private String MessageResultOfStatistic;
	
	
	public String getMessageResultOfStatistic() {
		return MessageResultOfStatistic;
	}

	public void setMessageResultOfStatistic(String messageResultOfStatistic) {
		MessageResultOfStatistic = messageResultOfStatistic;
	}

	public List<ChartDataset> getChartDataSection() {
		return ChartDataSection;
	}

	public void setChartDataSection(List<ChartDataset> chartDataSection) {
		ChartDataSection = chartDataSection;
	}

	public List<Link> getSuperClasses() {
		return superClasses;
	}

	public void setSuperClasses(List<Link> superClasses) {
		this.superClasses = superClasses;
	}

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
	
	public String getSubtitleUri() {
		return subtitleUri;
	}

	public void setSubtitleUri(String subtitleUri) {
		this.subtitleUri = subtitleUri;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Link getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Link targetClass) {
		this.targetClass = targetClass;
	}

	public String getSparqlTarget() {
		return sparqlTarget;
	}

	public void setSparqlTarget(String sparqlTarget) {
		this.sparqlTarget = sparqlTarget;
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

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public List<String> getMessageSeverities() {
		return MessageSeverities;
	}

	public void setMessageSeverities(List<String> messageSeverities) {
		MessageSeverities = messageSeverities;
	}	
}
