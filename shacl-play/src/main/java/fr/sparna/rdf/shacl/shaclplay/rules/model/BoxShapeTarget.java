package fr.sparna.rdf.shacl.shaclplay.rules.model;

import org.apache.jena.rdf.model.Resource;

public class BoxShapeTarget {
	
	
	protected Resource propertyTarget;
	
	protected String shPrefix;
	protected String shSelect;
	
	
	
	
	
	public String getShPrefix() {
		return shPrefix;
	}
	public void setShPrefix(String shPrefix) {
		this.shPrefix = shPrefix;
	}
	public String getShSelect() {
		return shSelect;
	}
	public void setShSelect(String shSelect) {
		this.shSelect = shSelect;
	}
	
	

}
