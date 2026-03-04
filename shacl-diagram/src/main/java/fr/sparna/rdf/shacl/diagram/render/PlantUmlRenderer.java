package fr.sparna.rdf.shacl.diagram.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.diagram.model.PlantUmlBoxIfc;
import fr.sparna.rdf.shacl.diagram.model.PlantUmlDiagram;

public class PlantUmlRenderer {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected boolean generateAnchorHyperlink = false;
	protected boolean displayPatterns = false;
	protected boolean avoidArrowsToEmptyBoxes = true;
	protected boolean includeSubclassLinks = true;
	protected boolean hideProperties = false;
	// true to indicate that the diagram is a section diagram
	protected boolean renderSectionDiagram = false;


	// the current diagram being rendered	
	protected transient PlantUmlDiagram diagram;

	public PlantUmlRenderer() {
		super();
	}
	
	public String renderDiagram(PlantUmlDiagram diagram) {
		this.diagram = diagram;
		
		StringBuffer sourceuml = new StringBuffer();	
		sourceuml.append("@startuml\n");
		// this allows to have dots in unescaped classes names, to avoid that are interpreted as namespaces
		// see https://github.com/sparna-git/shacl-play/issues/122
		// see https://forum.plantuml.net/221/dots-in-class-names
		sourceuml.append("set namespaceSeparator none\n");
		sourceuml.append("skinparam classFontSize 14"+"\n");
		sourceuml.append("!define LIGHTORANGE\n");
		sourceuml.append("skinparam componentStyle uml2\n");
		sourceuml.append("skinparam wrapMessageWidth 100\n");
		sourceuml.append("skinparam ArrowColor #Maroon\n");
		sourceuml.append("set namespaceSeparator none \n"); // Command for not create an package uml
		
		
		if (this.renderSectionDiagram) {
			// sourceuml.append("left to right direction\n");
		}
		
		// print all boxes
		BoxRenderer boxRenderer = new BoxRenderer(includeSubclassLinks, generateAnchorHyperlink, renderSectionDiagram, displayPatterns, diagram);
		for (PlantUmlBoxIfc plantUmlBox : diagram.getBoxes()) {
			sourceuml.append(boxRenderer.renderNodeShape(plantUmlBox,this.avoidArrowsToEmptyBoxes));
		}
		
		sourceuml.append("hide circle\n");
		sourceuml.append("hide methods\n");
		sourceuml.append("hide empty members\n");
		
		if (this.hideProperties) {
			sourceuml.append("hide fields\n");
		}
		
		// we don't set remove @unlinked if the diagram contains a single box otherwise
		// PlantUML crashes
		if (diagram.usesShGroup(diagram.getBoxes()) && diagram.getBoxes().size() > 1) {
			sourceuml.append("remove @unlinked\n");
		}
		
		sourceuml.append("@enduml\n");
		
		// return output
		return sourceuml.toString();
	}

	public boolean isGenerateAnchorHyperlink() {
		return generateAnchorHyperlink;
	}

	public void setGenerateAnchorHyperlink(boolean generateAnchorHyperlink) {
		this.generateAnchorHyperlink = generateAnchorHyperlink;
	}

	public boolean isDisplayPatterns() {
		return displayPatterns;
	}

	public void setDisplayPatterns(boolean displayPatterns) {
		this.displayPatterns = displayPatterns;
	}

	public boolean isAvoidArrowsToEmptyBoxes() {
		return avoidArrowsToEmptyBoxes;
	}

	public void setAvoidArrowsToEmptyBoxes(boolean avoidArrowsToEmptyBoxes) {
		this.avoidArrowsToEmptyBoxes = avoidArrowsToEmptyBoxes;
	}

	public boolean isIncludeSubclassLinks() {
		return includeSubclassLinks;
	}

	public void setIncludeSubclassLinks(boolean includeSubclassLinks) {
		this.includeSubclassLinks = includeSubclassLinks;
	}

	public boolean isHideProperties() {
		return hideProperties;
	}

	public void setHideProperties(boolean hideProperties) {
		this.hideProperties = hideProperties;
	}

	public boolean isRenderSectionDiagram() {
		return renderSectionDiagram;
	}

	public void setRenderSectionDiagram(boolean renderSectionDiagram) {
		this.renderSectionDiagram = renderSectionDiagram;
	}

}
