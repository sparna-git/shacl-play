package fr.sparna.rdf.shacl.diagram.model;

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
	
	public PlantUmlBoxIfc findBoxByBoxReference(String boxReference, String lang) {
		return this.boxes.stream().filter(b -> b.getPlantUmlLabel(lang).equals(boxReference)).findFirst().orElse(null);
	}
	
	public PlantUmlBoxIfc findBoxByResource(Resource r) {
		return findBoxByResource(r, this.boxes);
	}
	
	public PlantUmlBoxIfc findBoxByTargetClass(Resource classUri) {
		return findBoxByTargetClass(classUri, this.boxes);
	}

	/**
	 * Counts the number of references to a box through sh:node or sh:class references in the diagram
	 * @param boxReference
	 * @return
	 */
	public int countReferencesTo(String boxReference, String lang) {
		int nCount = 0;
		for (PlantUmlBoxIfc plantumlbox : this.getBoxes()) {
			for (PlantUmlProperty p : plantumlbox.getPropertiesBox()) {
				String ref = this.resolvePropertyShapeShNodeOrShClass(p, lang);
				if(ref != null && ref.equals(boxReference)) {
					nCount++;
				}
			}
		}
		return nCount;
	}
	
	public static PlantUmlBoxIfc findBoxByTargetClass(Resource classUri, List<PlantUmlBoxIfc> boxes) {
		return boxes.stream().filter(b -> b.isTargetingClass(classUri)).findFirst().orElse(null);
	}

	public static PlantUmlBoxIfc findBoxByResource(Resource r, List<PlantUmlBoxIfc> boxes) {
		return boxes.stream().filter(b -> b.getNodeShape().equals(r)).findFirst().orElse(null);
	}


	
	/**
	 * Returns the identifier of the arrow reference (a shortForm), either through an sh:node to an existing NodeShape,
	 * an sh:class to an existing NodeShape, or an sh:class to a class that is not a NodeShape or targeted by a NodeShape
	 * 
	 * @return
	 */
	public String resolvePropertyShapeShNodeOrShClass(PlantUmlProperty property, String lang) {
		if(property.getShNode().isPresent()) {
			return resolveShNodeReference(property.getShNode().get(), lang);
		} else if(property.getShClass().isPresent()) {			
			// sh:class may not be targeted to a NodeShape
			// PlantUML will make up a box with the class shortForm automatically
			// we need to return it to indicate the property will generate an arrow in the diagram
			return this.resolveShClassReference(property.getShClass().get(), lang);
			
			// TODO : we may be interested to get the references made through a sh:or ?
		}
		return null;
	}

	public String resolveShNodeReference(Resource reference, String lang) {
		PlantUmlBoxIfc box = this.findBoxByResource(reference);
		if(box != null) {
			return box.getPlantUmlLabel(lang);
		} else {
			return ModelRenderingUtils.render(reference, true);
		}		
	}

	public String resolveShClassReference(Resource reference, String lang) {
		PlantUmlBoxIfc b = this.findBoxByTargetClass(reference);
		if(b != null) {
			return b.getPlantUmlLabel(lang);
		} else {
			if (reference.isURIResource()) { 
				return reference.getModel().shortForm(reference.getURI());
			} else {
				log.warn("Found a blank sh:class reference "+reference+", cannot handle it");
				return reference.toString();
			}
		}		
	}

	public boolean usesShGroup(List<PlantUmlBoxIfc> boxes) {
		// tester si sh:group est utilisé au moins une fois par une sh:property		
		return boxes.stream().map( p -> p.getPropertiesBox().stream().filter(pp -> pp.getShGroup().isPresent()).findAny().isPresent()).findAny().isPresent();
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
