package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.ModelRenderingUtils;

/**
 * Represents one main diagram in the documentation, with its URI, title, description, order.
 * Also contains the list of PlantUmlBoxIfc that will be rendered in the diagram.
 */
public class PlantUmlDiagram {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Resource resource;
	protected String title;
	protected String description;
	protected double orderDiagram;

	protected List<PlantUmlBoxIfc> boxes = new ArrayList<>();
	
	public PlantUmlBoxIfc findBoxById(String id) {
		return this.boxes.stream().filter(b -> b.getLabel().equals(id)).findFirst().orElse(null);
	}
	
	public PlantUmlBoxIfc findBoxByResource(Resource r) {
		return findBoxByResource(r, this.boxes);
	}
	
	public PlantUmlBoxIfc findBoxByTargetClass(Resource classUri) {
		return findBoxByTargetClass(classUri, this.boxes);
	}
	
	public static PlantUmlBoxIfc findBoxByTargetClass(Resource classUri, List<PlantUmlBoxIfc> boxes) {
		return boxes.stream().filter(b -> b.isTargeting(classUri)).findFirst().orElse(null);
	}

	public static PlantUmlBoxIfc findBoxByResource(Resource r, List<PlantUmlBoxIfc> boxes) {
		return boxes.stream().filter(b -> b.getNodeShape().toString().equals(r.toString())).findFirst().orElse(null);
	}
	
	/**
	 * Returns the identifier of the arrow reference (a shortForm), either through an sh:node to an existing NodeShape,
	 * an sh:class to an existing NodeShape, or an sh:class to a class that is not a NodeShape or targeted by a NodeShape
	 * 
	 * @return
	 */
	public String resolvePropertyShapeShNodeOrShClass(PlantUmlProperty property) {
		if(property.getShNode().isPresent()) {
			PlantUmlBoxIfc box = this.findBoxByResource(property.getShNode().get());
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
			PlantUmlBoxIfc b = this.findBoxByTargetClass(cl);
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
	 * @return the PlantUmlBoxIfc corresponding to the sh:node reference is present, or the sh:class reference if present, or null
	 */
	public PlantUmlBoxIfc resolveShNodeOrShClassBox(PlantUmlProperty property) {
		if(property.getShNode().isPresent()) {
			return this.findBoxByResource(property.getShNode().get());
		} else if(property.getShClass().isPresent()) {			
			return this.resolveShClassBox(property);			
			// TODO : we may be interested to scan the references made through a sh:or
		}
		return null;
	}
	
	/**
	 * @return the PlantUmlBoxIfc corresponding to the sh:class reference of the provided property, or null if sh:class is not present, or the box cannot be found
	 */
	public PlantUmlBoxIfc resolveShClassBox(PlantUmlProperty property) {
		return property.getShClass().map(cl -> {
			return this.findBoxByTargetClass(cl);
		}).orElse(null);		
	}

	public boolean usesShGroup(List<PlantUmlBoxIfc> boxes) {
		// tester si sh:group est utilisé au moins une fois par une sh:property		
		return boxes.stream().map( p -> p.getProperties().stream().filter(pp -> pp.getShGroup().isPresent()).findAny().isPresent()).findAny().isPresent();
	}
	
	public List<PlantUmlBoxIfc> getBoxes() {
		return boxes;
	}
	public void setBoxes(List<PlantUmlBoxIfc> boxes) {
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
