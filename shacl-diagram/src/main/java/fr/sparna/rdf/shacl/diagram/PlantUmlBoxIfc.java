package fr.sparna.rdf.shacl.diagram;

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;


public interface PlantUmlBoxIfc {
	
	public Resource getNodeShape();
	
	public List<Resource> getDepiction();
	
	public Optional<Resource> getTargetClass();
	
	public List<Resource> getRdfsSubClassOf();

	public List<Resource> getShNode();
	
	public boolean isTargeting(Resource classUri);
	
	public String getBackgroundColorString();
	
	public String getColorString();	
	
	public int countShNodeOrShClassReferencesTo(String id, PlantUmlDiagram diagram);
	
	public String getLabel();
	
	public String getPlantUmlQuotedBoxName();

	public List<PlantUmlProperty> getProperties();

	public void setProperties(List<PlantUmlProperty> properties);
	

}