package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;;

public class PlantUmlBox {
	
	private Resource nodeShape;
	
	protected String nametargetclass; 
	protected String packageName;	
	
	protected List<PlantUmlProperty> properties = new ArrayList<>();
	protected List<PlantUmlBox> superClasses = new ArrayList<>();

	public PlantUmlBox(Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}
	
	public String getLabel() {
		// strip out hyphens
		return (nodeShape.isURIResource())?nodeShape.getLocalName().replaceAll("-", ""):nodeShape.toString();
	}	
	
	public List<PlantUmlProperty> getProperties() {	
		return properties;
	}
	
	public void setProperties(List<PlantUmlProperty> properties) {
		this.properties = properties;
	}	
		
	public String getNametargetclass() {
		return nametargetclass;
	}
	
	public String getQualifiedName() {		
		return packageName+"."+this.getLabel();
	}

	public Resource getNodeShape() {
		return nodeShape;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public void setNametargetclass(String nametargetclass) {
		this.nametargetclass = nametargetclass;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<PlantUmlBox> getSuperClasses() {
		return superClasses;
	}

	public void setSuperClasses(List<PlantUmlBox> superClasses) {
		this.superClasses = superClasses;
	}

}