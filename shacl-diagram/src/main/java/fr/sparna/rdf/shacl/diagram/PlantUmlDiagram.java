package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class PlantUmlDiagram {
	protected Resource resource;
	protected List<PlantUmlBox> boxes = new ArrayList<>();
	protected String title;
	protected String description;
	protected double orderDiagram;
	
	public PlantUmlBox findBoxById(String id) {
		return this.boxes.stream().filter(b -> b.getLabel().equals(id)).findFirst().orElse(null);
	}
	
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

	public double getOrderDiagram() {
		return orderDiagram;
	}

	public void setOrderDiagram(Double orderDiagram) {
		orderDiagram = orderDiagram;
	}
}
