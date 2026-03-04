package fr.sparna.rdf.shacl.diagram.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.SHACL_PLAY;;

public class PlantUmlBox implements PlantUmlBoxIfc {
	
	private Resource nodeShape;
	
	protected List<PlantUmlProperty> properties = new ArrayList<>();

	protected String link;

	public PlantUmlBox(Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
		// init the link
		if(this.nodeShape.isURIResource()) {
			this.link = "#" + this.nodeShape.getModel().shortForm(this.nodeShape.getURI());
		} else {
			// to deal with blank node shapes
			this.link = "#" + this.nodeShape.asResource().toString();
		}
	}
	
	public Resource getNodeShape() {
		return nodeShape;
	}
	
	public Optional<Literal> getBackgroundColor() {
		return ModelReadingUtils.getOptionalLiteral(nodeShape, nodeShape.getModel().createProperty(SHACL_PLAY.BACKGROUNDCOLOR));
	}
	
	public Optional<Literal> getColor() {
		return ModelReadingUtils.getOptionalLiteral(nodeShape, nodeShape.getModel().createProperty(SHACL_PLAY.COLOR));
	}
	
	public List<Resource> getDepiction() {
		List<Resource> depictionInput = ModelReadingUtils.readObjectAsResource(nodeShape, FOAF.depiction).stream()
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
	
	public Optional<Resource> getTargetClass() {
		return ModelReadingUtils.getOptionalResource(nodeShape, SH.targetClass);
	}
	
	public List<Resource> getRdfsSubClassOf() {
		return nodeShape.listProperties(RDFS.subClassOf).toList().stream()
				.map(s -> s.getResource())
				.filter(r -> { return r.isURIResource() && !r.getURI().equals(OWL.Thing.getURI()); })
				.collect(Collectors.toList());
	}

	public List<Resource> getShNode() {
		return nodeShape.listProperties(SH.node).toList().stream().map(s -> s.getResource()).collect(Collectors.toList());
	}
	
	
	public boolean isTargeting(Resource classUri) {	
		boolean hasShTargetClass = this.getTargetClass().filter(c -> c.equals(classUri)).isPresent();		
		boolean isItselfTheClass = 
		this.nodeShape.hasProperty(RDF.type, RDFS.Class)
		&&
		this.nodeShape.hasProperty(RDF.type, SH.NodeShape)
		&&
		this.nodeShape.equals(classUri);
		
		return hasShTargetClass || isItselfTheClass;
	}
	
	public String getBackgroundColorString() {
		return this.getBackgroundColor().map(node -> node.asLiteral().getLexicalForm()).orElse(null);
	}
	
	public String getColorString() {
		return this.getColor().map(node -> node.asLiteral().getLexicalForm()).orElse(null);
	}
	
	
	public int countShNodeOrShClassReferencesTo(String id, PlantUmlDiagram diagram) {
		int count = 0;
		for (PlantUmlProperty p : this.properties) {
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
		return ModelRenderingUtils.render(this.nodeShape, true)+this.getTargetClass().map(targetClass -> " ("+ModelRenderingUtils.render(targetClass, true)+")").orElse("");
	}
	
	public String getPlantUmlQuotedBoxName() {
		return "\"" + this.getLabel() + "\"";
	}

	public List<PlantUmlProperty> getProperties() {	
		return properties;
	}
	
	public void setProperties(List<PlantUmlProperty> properties) {
		this.properties = properties;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	

}