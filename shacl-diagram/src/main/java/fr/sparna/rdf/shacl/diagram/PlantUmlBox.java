package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;;

public class PlantUmlBox {
	
	private Resource nodeShape;
	
	protected String label;
	// contains a shortForm of sh:targetClass
	protected String nametargetclass; 
	protected String packageName;	
	protected String version;
	protected String colorClass;
	protected String backgroundColor;
	protected List<Resource> diagramReferences;
	
	protected List<PlantUmlProperty> properties = new ArrayList<>();
	protected List<PlantUmlBox> superClasses = new ArrayList<>();

	public int countShNodeOrShClassReferencesTo(String id) {
		int count = 0;
		for (PlantUmlProperty p : this.properties) {
			if (
					p.getShNodeOrShClassReference() != null
					&&
					p.getShNodeOrShClassReference().equals(id)
			) {
				count++;
			}
		}
		return count;
	}
		
	
	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public List<Resource> getDiagramReferences() {
		return diagramReferences;
	}
	public void setDiagramReferences(List<Resource> diagramReferences) {
		this.diagramReferences = diagramReferences;
	}
	public String getColorClass() {
		return colorClass;
	}
	public void setColorClass(String colorClass) {
		this.colorClass = colorClass;
	}
	public PlantUmlBox(Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}
	
	public String getLabel() {
		return label;
	}	
	
	public void setLabel(String label) {
		this.label = label;
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
		if(!packageName.isEmpty()) {
			return packageName+"."+this.getLabel();
		}else {
			return this.getLabel();
		}
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