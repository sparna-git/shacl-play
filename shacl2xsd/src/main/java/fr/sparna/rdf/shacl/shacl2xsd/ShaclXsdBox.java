package fr.sparna.rdf.shacl.shacl2xsd;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class ShaclXsdBox {
	
private Resource nodeShape;
	
	protected String label;
	protected String nametargetclass; 
	protected String packageName;
	protected Boolean useReference;
	protected Boolean xsdIsRoot;
	
	protected List<ShaclXsdProperty> properties = new ArrayList<>();
	protected List<ShaclXsdBox> superClasses = new ArrayList<>();

	
	public Boolean getXsdIsRoot() {
		return xsdIsRoot;
	}

	public void setXsdIsRoot(Boolean xsdIsRoot) {
		this.xsdIsRoot = xsdIsRoot;
	}

	public Boolean getUseReference() {
		return useReference;
	}

	public void setUseReference(Boolean useReference) {
		this.useReference = useReference;
	}

	public ShaclXsdBox(Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}
	
	public String getLabel() {
		return label;
	}	
	
	public void setLabel(String label) {
		this.label = label;
	}

	public List<ShaclXsdProperty> getProperties() {	
		return properties;
	}
	
	public void setProperties(List<ShaclXsdProperty> properties) {
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

	public List<ShaclXsdBox> getSuperClasses() {
		return superClasses;
	}

	public void setSuperClasses(List<ShaclXsdBox> superClasses) {
		this.superClasses = superClasses;
	}
	
}