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

public class PlantUmlBox implements PlantUmlBoxIfc {
	
	private NodeShape nodeShape;
	
	protected List<PlantUmlProperty> propertiesBox = new ArrayList<>();

	protected String link;

	public PlantUmlBox(Resource nodeShape) {  
		this.nodeShape = new NodeShape(nodeShape);
		this.link = "#" + this.nodeShape.getShortFormOrId();
	}

	@Override
	public Resource getNodeShape() {
		return this.nodeShape.getNodeShape();
	}

	@Override
	public Optional<Resource> getTargetClass() {
		return Optional.ofNullable(
			(this.nodeShape.getTargetClass().size() > 0)?this.nodeShape.getTargetClass().get(0):null
		);
	}

	public Optional<Literal> getBackgroundColor() {
		return this.nodeShape.getShaclPlayBackgroundColor();
	}
	
	public Optional<Literal> getColor() {
		return this.nodeShape.getShaclPlayColor();
	}
	
	public List<Resource> getDepictionBox() {
		List<Resource> depictionInput = this.nodeShape.getDepiction()
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
		return this.nodeShape.getSubClassOf();
	}

	public List<Resource> getShNodeBox() {
		return this.nodeShape.getShNodeAsList();
	}
	
	public boolean isTargetingBox(Resource classUri) {	
		boolean hasShTargetClass = this.nodeShape.isTargeting(classUri);
		boolean isItselfTheClass = 
		this.nodeShape.getNodeShape().hasProperty(RDF.type, RDFS.Class)
		&&
		this.nodeShape.getNodeShape().hasProperty(RDF.type, SH.NodeShape)
		&&
		this.nodeShape.equals(classUri);
		
		return hasShTargetClass || isItselfTheClass;
	}
	
	public String getBackgroundColorStringBox() {
		return this.nodeShape.getShaclPlayBackgroundColor().map(node -> node.asLiteral().getLexicalForm()).orElse(null);
	}
	
	public String getColorStringBox() {
		return this.nodeShape.getShaclPlayColor().map(node -> node.asLiteral().getLexicalForm()).orElse(null);
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
		String classLabels = this.getTargetClass().stream().map(targetClass -> ModelRenderingUtils.render(targetClass, true)).collect(Collectors.joining(", "));
		return ModelRenderingUtils.render(this.nodeShape.getNodeShape(), true)+(classLabels.equals("")?"":" ("+classLabels+")");
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