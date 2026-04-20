package fr.sparna.rdf.shacl.diagram.model;

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;


public interface PlantUmlBoxIfc {
	
	public Resource getNodeShape();
	
	public List<Resource> getDepictionBox();
	
	public Optional<Resource> getTargetClassAsOptional();
	
	public List<Resource> getRdfsSubClassOf();

	public List<Resource> getShNodeBox();
	
	public boolean isTargetingBox(Resource classUri);
	
	public String getBackgroundColorStringBox();
	
	public String getColorStringBox();	
	
	public int countShNodeOrShClassReferencesTo(String id, PlantUmlDiagram diagram);
	
	public String getLabel();
	
	public String getPlantUmlQuotedBoxName();

	public List<PlantUmlProperty> getPropertiesBox();

	public void setPropertiesBox(List<PlantUmlProperty> propertiesBox);
	
	public String getLink();
}