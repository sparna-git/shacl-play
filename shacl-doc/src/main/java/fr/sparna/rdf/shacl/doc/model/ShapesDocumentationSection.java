package fr.sparna.rdf.shacl.doc.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import fr.sparna.rdf.shacl.doc.Depiction;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;


/**
 * One section in the final documentation, corresponding to one NodeShape
 */
@JsonInclude(Include.NON_NULL)
public class ShapesDocumentationSection {

	/**
	 * Full corresponding NodeShape URI
	 */
	private String nodeShapeUriOrId;
	
	/**
	 * The section ID, set to the URI short form
	 */
	private String sectionId;
	private String title;

	/**
	 * The subtitle to display, set to the full URI. Can be null, or can point to an empty URL
	 */
	private Link subtitleUri;
	private String description;
	
	private List<Link> targetClass;
	
	private String pattern;
	private String nodeKind;
	private Boolean closed;
	private String skosExample;
	private String color;
	
	private String targetSubjectsOf;
	private String targetObjectsOf;
	
	private String hasValue;
	
	/**
	 * The target of the shape when it is expressed using a SPARQL query
	 */
	private String sparqlTarget;
	
	/**
	 * Diagram inside nodeshape
	 */
	private PlantUmlDiagramOutput nsDiagram;
	
	
	@JacksonXmlElementWrapper(localName="superClasses")
	@JacksonXmlProperty(localName = "link")
	private List<Link> superClasses;
	
	/*
	@JacksonXmlElementWrapper(localName="properties")
	@JacksonXmlProperty(localName = "property")
	public List<PropertyShapeDocumentation> propertySections;
	*/
	
	@JacksonXmlElementWrapper(localName="propertyGroups")
	@JacksonXmlProperty(localName = "propertyGroup")
	public List<PropertyShapesGroupDocumentation> propertyGroups;

	@JacksonXmlElementWrapper(localName="charts")
	@JacksonXmlProperty(localName = "chart")
	protected List<Chart> charts;
	
	//private List<String> MessageOfValidate = new ArrayList<>();
	@JacksonXmlElementWrapper(localName="messages")
	@JacksonXmlProperty(localName = "message")
	protected List<String> messages;
	
	@JacksonXmlElementWrapper(localName="depictions")
	@JacksonXmlProperty(localName = "depiction")
	private List<Depiction> depictions;
	
	private int numberOfTargets;
	
	@JacksonXmlElementWrapper(localName="sectionDiagrams")
	@JacksonXmlProperty(localName = "sectionDiagram")
	protected List<ShapesDocumentationDiagram> sectionDiagrams = new ArrayList<>();
	
	@JsonIgnore
	public PropertyShapeDocumentation findPropertyShapeDocumentationSectionByUriOrId(String propertyUri) {
		return getPropertiesInAllGroups().stream().filter(s -> s.getPropertyShapeUriOrId().equals(propertyUri)).findFirst().orElse(null);
	}
	
	@JsonIgnore
	public List<PropertyShapeDocumentation> getPropertiesInAllGroups() {
		List<PropertyShapeDocumentation> allProperties = new ArrayList<>();
		for (PropertyShapesGroupDocumentation aGroup : propertyGroups) {
			allProperties.addAll(aGroup.getProperties());
		}
		return allProperties;
	}

	public List<Chart> getCharts() {
		return charts;
	}

	public void setCharts(List<Chart> charts) {
		this.charts = charts;
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
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Link> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(List<Link> targetClass) {
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

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public int getNumberOfTargets() {
		return numberOfTargets;
	}

	public void setNumberOfTargets(int numberOfTargets) {
		this.numberOfTargets = numberOfTargets;
	}

	public String getNodeShapeUriOrId() {
		return nodeShapeUriOrId;
	}

	public void setNodeShapeUriOrId(String nodeShapeUriOrId) {
		this.nodeShapeUriOrId = nodeShapeUriOrId;
	}

	public List<PropertyShapesGroupDocumentation> getPropertyGroups() {
		return propertyGroups;
	}

	public void setPropertyGroups(List<PropertyShapesGroupDocumentation> propertyGroups) {
		this.propertyGroups = propertyGroups;
	}

	public String getTargetSubjectsOf() {
		return targetSubjectsOf;
	}

	public String getTargetObjectsOf() {
		return targetObjectsOf;
	}

	public void setTargetSubjectsOf(String targetSubjectsOf) {
		this.targetSubjectsOf = targetSubjectsOf;
	}

	public void setTargetObjectsOf(String targetObjectsOf) {
		this.targetObjectsOf = targetObjectsOf;
	}

	public List<Depiction> getDepictions() {
		return depictions;
	}

	public void setDepictions(List<Depiction> depictions) {
		this.depictions = depictions;
	}

	public PlantUmlDiagramOutput getNsDiagram() {
		return nsDiagram;
	}

	public void setNsDiagram(PlantUmlDiagramOutput nsDiagram) {
		this.nsDiagram = nsDiagram;
	}

	public List<ShapesDocumentationDiagram> getSectionDiagrams() {
		return sectionDiagrams;
	}

	public void setSectionDiagrams(List<ShapesDocumentationDiagram> sectionDiagrams) {
		this.sectionDiagrams = sectionDiagrams;
	}

	public String getHasValue() {
		return hasValue;
	}

	public void setHasValue(String hasValue) {
		this.hasValue = hasValue;
	}

	public Link getSubtitleUri() {
		return subtitleUri;
	}

	public void setSubtitleUri(Link subtitleUri) {
		this.subtitleUri = subtitleUri;
	}	
}
