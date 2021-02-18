package fr.sparna.rdf.shacl.doc;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclPrefix {
	
	private String Prefix_shpath;
	private String Prefix_shTargetClass;
	private String Prefix_shClass;
	private String Prefix_shdatatype;
	private String Prefix_shin;
	private String Prefix_shhasvalue;
	
	public String getPrefix_shpath() {
		return Prefix_shpath;
	}
	public void setPrefix_shpath(Resource nodeShape) {
		String Value = validateuri(nodeShape, SH.path);
		Prefix_shpath = Value;
	}
	public String getPrefix_shTargetClass() {
		return Prefix_shTargetClass;
	}
	public void setPrefix_shTargetClass(Resource nodeShape) {
		String Value = validateuri(nodeShape, SH.targetClass);
		Prefix_shTargetClass = Value;
	}
	public String getPrefix_shClass() {
		return Prefix_shClass;
	}
	public void setPrefix_shClass(Resource nodeShape) {
		String Value = validateuri(nodeShape, SH.class_);				
		Prefix_shClass = Value;
	}
	public String getPrefix_shdatatype() {
		return Prefix_shdatatype;
	}
	public void setPrefix_shdatatype(Resource nodeShape) {
		String Value = validateuri(nodeShape, SH.datatype);		
		Prefix_shdatatype = Value;
	}
	public String getPrefix_shin() {
		return Prefix_shin;
	}
	public void setPrefix_shin(Resource nodeShape) {
		String Value = validateuri(nodeShape, SH.in);		
		Prefix_shin = Value;
	}
	public String getPrefix_shhasvalue() {
		return Prefix_shhasvalue;
	}
	public void setPrefix_shhasvalue(Resource nodeShape) {
		String Value = validateuri(nodeShape, SH.hasValue);		
		Prefix_shhasvalue = Value;
	}
	
	public static String validateuri(Resource nodeShape,Property property) {
		if(nodeShape.hasProperty(property)) {
			return  nodeShape.getModel().qnameFor(nodeShape.getProperty(property).getObject().asNode().getURI());			
		}else {
			return null;
		}
	}
	
	public ShaclPrefix(Resource nodeShape) {
		this.setPrefix_shpath(nodeShape);
		this.setPrefix_shdatatype(nodeShape);
		this.setPrefix_shhasvalue(nodeShape);
		this.setPrefix_shin(nodeShape);
		this.setPrefix_shClass(nodeShape);
		this.setPrefix_shTargetClass(nodeShape);
		
	}
}
