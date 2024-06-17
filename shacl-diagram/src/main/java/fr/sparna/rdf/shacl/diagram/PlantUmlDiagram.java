package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.ModelRenderingUtils;

public class PlantUmlDiagram {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Resource resource;
	protected List<PlantUmlBox> boxes = new ArrayList<>();
	protected String title;
	protected String description;
	protected double orderDiagram;
	
	public PlantUmlBox findBoxById(String id) {
		return this.boxes.stream().filter(b -> b.getLabel().equals(id)).findFirst().orElse(null);
	}
	
	public PlantUmlBox findBoxByResource(Resource r) {
		return this.boxes.stream().filter(b -> b.getNodeShape().toString().equals(r.toString())).findFirst().orElse(null);
	}
	
	public PlantUmlBox findBoxByTargetClass(Resource classUri) {
		return findBoxByTargetClass(classUri, this.boxes);
	}
	
	public static PlantUmlBox findBoxByTargetClass(Resource classUri, List<PlantUmlBox> boxes) {
		return boxes.stream().filter(b -> b.isTargeting(classUri)).findFirst().orElse(null);
	}
	
	/**
	 * Returns the identifier of the arrow reference (a shortForm), either through an sh:node to an existing NodeShape,
	 * an sh:class to an existing NodeShape, or an sh:class to a class that is not a NodeShape or targeted by a NodeShape
	 * 
	 * @param allBoxes
	 * @return
	 */
	public String resolvePropertyShapeShNodeOrShClass(PlantUmlProperty property) {
		if(property.getShNode().isPresent()) {
			PlantUmlBox box = this.findBoxByResource(property.getShNode().get());
			if(box != null) {
				return box.getLabel();
			} else {
				return ModelRenderingUtils.render(property.getShNode().get(), true);
			}
		} else if(property.getShClass().isPresent()) {			
			// sh:class may not be targeted to a NodeShape
			// PlantUML will make up a box with the class shortForm automatically
			// we need to return it to indicate the property will generate an arrow in the diagram
			// we don't jave to search in the PlantUmlBoxes
			return this.resolveShClassReference(property);
			
			// TODO : we may be interested to get the references made through a sh:or ?
		}
		return null;
	}
	
	public String resolveShClassReference(PlantUmlProperty property) {
		return property.getShClass().map(cl -> {
			PlantUmlBox b = this.findBoxByTargetClass(cl);
			if(b != null) {
				return b.getLabel();
			} else {
				if (cl.isURIResource()) { 
					return cl.getModel().shortForm(cl.getURI());
				} else {
					log.warn("Found a blank sh:class reference on a shape with sh:path "+cl+", cannot handle it");
					return null;
				}
			}
		}).orElse(null);		
	}

	/**
	 * @return the PlantUmlBox corresponding to the sh:node reference is present, or the sh:class reference if present, or null
	 */
	public PlantUmlBox resolveShNodeOrShClassBox(PlantUmlProperty property) {
		if(property.getShNode().isPresent()) {
			return this.findBoxByResource(property.getShNode().get());
		} else if(property.getShClass().isPresent()) {			
			return this.resolveShClassBox(property);			
			// TODO : we may be interested to scan the references made through a sh:or
		}
		return null;
	}
	
	/**
	 * @return the PlantUmlBox corresponding to the sh:class reference of the provided property, or null if sh:class is not present, or the box cannot be found
	 */
	public PlantUmlBox resolveShClassBox(PlantUmlProperty property) {
		return property.getShClass().map(cl -> {
			return this.findBoxByTargetClass(cl);
		}).orElse(null);		
	}

	public boolean usesShGroup(List<PlantUmlBox> boxes) {
		// tester si sh:group est utilisé au moins une fois par une sh:property		
		return boxes.stream().map( p -> p.getProperties().stream().filter(pp -> pp.getShGroup().isPresent()).findAny().isPresent()).findAny().isPresent();
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

	public void setOrderDiagram(double orderDiagram) {
		this.orderDiagram = orderDiagram;
	}

}
