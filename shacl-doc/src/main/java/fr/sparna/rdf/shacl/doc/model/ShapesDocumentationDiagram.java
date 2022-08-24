package fr.sparna.rdf.shacl.doc.model;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.SVGGenerator;
import net.sourceforge.plantuml.code.TranscoderUtil;

@JsonInclude(Include.NON_NULL)
public class ShapesDocumentationDiagram {

	private String plantUmlString;
	private String svg;
	private String pngLink;
	private String displayTitle;
	private String diagramDescription;
	
	public ShapesDocumentationDiagram(PlantUmlDiagramOutput diagramGenerationOutput) {
		this.plantUmlString = diagramGenerationOutput.getPlantUmlString();
		try {
			this.pngLink = "http://www.plantuml.com/plantuml/png/"+TranscoderUtil.getDefaultTranscoder().encode(diagramGenerationOutput.getPlantUmlString());
			SVGGenerator svgGen = new SVGGenerator();
			this.svg = svgGen.generateSvgDiagram(diagramGenerationOutput.getPlantUmlString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// can be null for default diagram
		this.displayTitle = diagramGenerationOutput.getDisplayTitle();
		this.diagramDescription = diagramGenerationOutput.getDiagramDescription();
	}

	public String getPlantUmlString() {
		return plantUmlString;
	}

	public void setPlantUmlString(String plantUmlString) {
		this.plantUmlString = plantUmlString;
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String getPngLink() {
		return pngLink;
	}

	public void setPngLink(String pngLink) {
		this.pngLink = pngLink;
	}

	public String getDisplayTitle() {
		return displayTitle;
	}

	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}

	public String getDiagramDescription() {
		return diagramDescription;
	}

	public void setDiagramDescription(String diagramDescription) {
		this.diagramDescription = diagramDescription;
	}
	
	
	
}
