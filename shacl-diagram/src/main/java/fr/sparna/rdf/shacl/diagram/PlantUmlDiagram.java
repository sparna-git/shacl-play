package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class PlantUmlDiagram {
	protected Resource resource;
	protected List<PlantUmlBox> boxes = new ArrayList<>();
	protected String label;
	
	public List<PlantUmlBox> getBoxes() {
		return boxes;
	}
	public void setBoxes(List<PlantUmlBox> boxes) {
		this.boxes = boxes;
	}
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
