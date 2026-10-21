package fr.sparna.rdf.shacl.diagram.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.NodeShape;

public class ResourcePlantUmlBox implements PlantUmlBoxIfc {
	
	private NodeShape nodeShape;
	private boolean useLabels = true;
	
	protected List<PlantUmlProperty> propertiesBox = new ArrayList<>();

	protected String link;

	public ResourcePlantUmlBox(Resource nodeShape, boolean useLabels) {  
		this.nodeShape = new NodeShape(nodeShape);
		this.link = "#" + this.nodeShape.getShortFormOrId();
		this.useLabels = useLabels;
	}

	@Override
	public Resource getNodeShape() {
		return this.nodeShape.getResource();
	}

	@Override
	public Optional<Resource> getTargetClass() {
		return Optional.ofNullable(
			(this.nodeShape.getTargetClass().size() > 0)?this.nodeShape.getTargetClass().get(0):null
		);
	}
	
	public List<Resource> getDepictionBox() {
		List<Resource> depictionInput = this.nodeShape.getDepiction()
											.stream()
											.filter(f -> { 
												if (f.getURI().contains(".png") || f.getURI().contains(".jpg")) {
													return false;
												}
												return true;												
											} )
											.collect(Collectors.toList());
		
		
		return depictionInput;
	}
	
	public List<Resource> getRdfsSubClassOf() {
		return this.nodeShape.getSubClassOf();
	}

	public List<Resource> getShNodeBox() {
		return this.nodeShape.getShNodeAsList();
	}
	
	public boolean isTargetingClass(Resource classUri) {
		return this.nodeShape.getAllTargetedClasses()
				.stream()
				.filter(c -> c.equals(classUri))
				.findFirst()
				.isPresent();
	}
	
	public String getBackgroundColor() {
		return this.nodeShape.getDisplayBackgroundColor();
	}
	
	public String getColor() {
		return this.nodeShape.getDisplayColor();
	}
	
	public String getPlantUmlLabel(String lang) {
		String labelString = null;
		if(this.useLabels) {
			labelString = this.nodeShape.getDisplayLabel(this.nodeShape.getResource().getModel(), lang);
		}

		if(labelString != null && !labelString.equals("")) {
			return labelString;
		}

		// use the sh:targetClass if present, otherwise use the URI of the NodeShape
		String classLabels = this.getTargetClass().stream().map(targetClass -> ModelRenderingUtils.render(targetClass, true)).collect(Collectors.joining(", "));
		if(!classLabels.equals("")) {
			return classLabels;
		} else {
			if(this.nodeShape.getResource().isAnon()) return "anonymous";
			return ModelRenderingUtils.render(this.nodeShape.getResource(), true);
		}
	}

	public List<PlantUmlProperty> getPropertiesBox() {
		return propertiesBox;
	}

	public void setPropertiesBox(List<PlantUmlProperty> propertiesBox) {
		this.propertiesBox = propertiesBox;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}	

}