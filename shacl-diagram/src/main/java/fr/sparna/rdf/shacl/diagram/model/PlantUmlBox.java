package fr.sparna.rdf.shacl.diagram.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.NodeShape;

public class PlantUmlBox extends NodeShape implements PlantUmlBoxIfc {
	
	private Resource nodeShape;
	
	protected List<PlantUmlProperty> propertiesBox = new ArrayList<>();

	protected String link;

	public PlantUmlBox(Resource nodeShape) {  
		super(nodeShape);
	    this.nodeShape = nodeShape;		
		this.link = "#" + super.getShortFormOrId();
	}

	public Optional<Literal> getBackgroundColor() {
		return super.getShaclPlayBackgroundColor();
	}
	
	public Optional<Literal> getColor() {
		return getShaclPlayColor();
	}
	
	public List<Resource> getDepictionBox() {
		List<Resource> depictionInput = super.getDepiction()
											.stream()
											.filter(f -> { 
												if (f.getURI().contains(".png")) {
													return false;
												}
												if (f.getURI().contains(".jpg")) {
													return false;
												}
												return true;												
											} )
											.collect(Collectors.toList());
		
		
		return depictionInput;
	}
	
	public List<Resource> getRdfsSubClassOf() {
		return super.getSubClassOf();
	}

	public List<Resource> getShNodeBox() {
		return super.getShNodeAsList();
	}
	
	public boolean isTargetingBox(Resource classUri) {	
		boolean hasShTargetClass = super.isTargeting(classUri);
		boolean isItselfTheClass = 
		this.nodeShape.hasProperty(RDF.type, RDFS.Class)
		&&
		this.nodeShape.hasProperty(RDF.type, SH.NodeShape)
		&&
		this.nodeShape.equals(classUri);
		
		return hasShTargetClass || isItselfTheClass;
	}
	
	public String getBackgroundColorStringBox() {
		return super.getShaclPlayBackgroundColor().map(node -> node.asLiteral().getLexicalForm()).orElse(null);
	}
	
	public String getColorStringBox() {
		return super.getShaclPlayColor().map(node -> node.asLiteral().getLexicalForm()).orElse(null);
	}
	
	public int countShNodeOrShClassReferencesTo(String id, PlantUmlDiagram diagram) {
		int count = 0;
		for (PlantUmlProperty p : this.propertiesBox) {
			if (
					diagram.resolvePropertyShapeShNodeOrShClass(p) != null
					&&
					diagram.resolvePropertyShapeShNodeOrShClass(p).equals(id)
			) {
				count++;
			}
		}
		return count;
	}	
	
	public String getLabel() {
		// use the sh:targetClass if present, otherwise use the URI of the NodeShape
		return ModelRenderingUtils.render(this.nodeShape, true)+this.getTargetClassAsOptional().map(targetClass -> " ("+ModelRenderingUtils.render(targetClass, true)+")").orElse("");
	}
	
	public String getPlantUmlQuotedBoxName() {
		return "\"" + this.getLabel() + "\"";
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