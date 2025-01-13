package fr.sparna.rdf.shacl.diagram;

/**
 * The output of a PlantUML diagram generation, 
 * containing the PlantUML source code and some metadata about the diagram.
 * This is inserted in the final documentation data structure.
 */
public class PlantUmlDiagramOutput {

	private String plantUmlString;
	private String diagramUri;
	private String diagramTitle;
	private String diagramDescription;
	private double diagramOrder;
	
	public PlantUmlDiagramOutput(PlantUmlDiagram d, PlantUmlRenderer renderer) {
		super();
		this.plantUmlString = renderer.renderDiagram(d);
		if(d.getResource() != null) {
			this.diagramUri = d.getResource().getURI();
		}
		this.diagramTitle = d.getTitle();
		this.diagramDescription = d.getDescription();
		this.diagramOrder = d.getOrderDiagram();
	}
	
	public PlantUmlDiagramOutput(String plantUmlString) {
		super();
		this.plantUmlString = plantUmlString;
	}

	public PlantUmlDiagramOutput(String plantUmlString, String diagramUri) {
		super();
		this.plantUmlString = plantUmlString;
		this.diagramUri = diagramUri;
	}
	
	public String getDisplayTitle() {
		if(this.diagramTitle != null) {
			return diagramTitle;
			// can be null for default diagram
		} else if (this.diagramUri != null){
			return getLocalName(this.diagramUri);
		} else {
			return null;
		}
	}
	
	private static String getLocalName(String uri) {
		if(uri.contains("#")) {
			return uri.substring(uri.lastIndexOf('#')+1);
		} else {
			return uri.substring(uri.lastIndexOf('/')+1);
		}
	}

	public String getDiagramUri() {
		return diagramUri;
	}

	public void setDiagramUri(String diagramUri) {
		this.diagramUri = diagramUri;
	}

	public String getDiagramTitle() {
		return diagramTitle;
	}

	public void setDiagramTitle(String diagramTitle) {
		this.diagramTitle = diagramTitle;
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

	public double getDiagramOrder() {
		return diagramOrder;
	}

	public void setDiagramOrder(double diagramOrder) {
		this.diagramOrder = diagramOrder;
	}

}
