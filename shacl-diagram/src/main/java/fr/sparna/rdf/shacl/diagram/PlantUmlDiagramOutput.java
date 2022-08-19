package fr.sparna.rdf.shacl.diagram;

public class PlantUmlDiagramOutput {

	private String plantUmlString;
	private String diagramUri;
	private String diagramLabel;
	private String diagramDescription;
	
	public PlantUmlDiagramOutput(String plantUmlString) {
		super();
		this.plantUmlString = plantUmlString;
	}

	public PlantUmlDiagramOutput(String plantUmlString, String diagramUri) {
		super();
		this.plantUmlString = plantUmlString;
		this.diagramUri = diagramUri;
	}

	public String getDiagramUri() {
		return diagramUri;
	}

	public void setDiagramUri(String diagramUri) {
		this.diagramUri = diagramUri;
	}

	public String getDiagramLabel() {
		return diagramLabel;
	}

	public void setDiagramLabel(String diagramLabel) {
		this.diagramLabel = diagramLabel;
	}

	public String getDiagramDescription() {
		return diagramDescription;
	}

	public void setDiagramDescription(String diagramDescription) {
		this.diagramDescription = diagramDescription;
	}

	public String getPlantUmlString() {
		return plantUmlString;
	}
	
}
