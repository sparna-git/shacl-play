package fr.sparna.rdf.shacl.doc;

import java.io.File;

public class SourceShaclTransformation {

	protected String shacl_shape;
	protected String shacl_path;
	protected String shacl_datatype;
	protected String shacl_nodekind;
	protected String shacl_literal;
	protected String shacl_pattern;
	protected String shacl_node;
	protected String shacl_class;
	protected String shacl_class_property;
	protected String shacl_nodekind_pattern_uniquelang;
	
	public String getshacl_shape() {
		return shacl_shape;
	}
	public void setshacl_shape(String shacl_shape) {
		this.shacl_shape = shacl_shape;
	}
	public String getshacl_path() {
		return shacl_path;
	}
	public void setshacl_path(String shacl_path) {
		this.shacl_path = shacl_path;
	}
	public String getshacl_datatype() {
		return shacl_datatype;
	}
	public void setshacl_datatype(String shacl_datatype) {
		String value = "";
		
		if(shacl_datatype == null) {
			value = "";
		} else {
			value = " : "+shacl_datatype;
		}		
		this.shacl_datatype = value;
	}
	public String getshacl_nodekind() {
		return shacl_nodekind;
	}
	public void setshacl_nodekind(String shacl_nodekind) {
		String value = "";
		if (shacl_nodekind == null) {
			value = "";
		}
		this.shacl_nodekind = value;
	}
	public String getshacl_literal() {				
		return shacl_literal;
	}
	public void setshacl_literal(String cardinality, String range, String length) {
		String value = "";
		
		if (cardinality != null) { //value cardinality
			value = cardinality; }
		else if (range != null) { //value range
			value = range;}
		else if (length != null) { //value length
			value = length;}
		this.shacl_literal = value;
	}

	public String getshacl_pattern(boolean flag) {
		if (flag) {
			if (shacl_pattern != "") {
				shacl_pattern = "{field}"+" "+"("+shacl_pattern+")";
			}
		}
		return shacl_pattern;
	}
	public void setshacl_pattern(String shacl_pattern, String shacl_shape, String shacl_path) {
		String value = "";
		if (shacl_pattern != null) { //value pattern
			value = "("+shacl_pattern+")";
		}else {value = "";}
		this.shacl_pattern = value;
	}
	
	public String getshacl_node() {
		return shacl_node;
	}
	public void setshacl_node(String shacl_node, String shacl_shape, String shacl_path,String shacl_datatype, String shacl_literal,String shacl_nodekind, String shacl_pattern) {
		String value = null;
		if (shacl_node != null) {  			
			value = shacl_shape+ " --> " +"\""+shacl_node+"\""+" : "+shacl_path+shacl_datatype+" "+shacl_literal+" "+shacl_pattern+" "+shacl_nodekind(shacl_nodekind)+"\n";            		
        	}		
		this.shacl_node = value;
	}
	
	public String getshacl_class() {
		return shacl_class;
	}
	public void setshacl_class(String shacl_class,String shacl_class_property) {
		String value = null;
			
		this.shacl_class = value;
	}
	
	public String getshacl_class_property() {
		return shacl_class_property;
	}
	public void setshacl_class_property(String shacl_class_property ,String shacl_shape,String shacl_path, String shacl_literal, String shacl_nodekind, String shacl_pattern) {
		String value = null;
		
		if (shacl_class_property != null) {			
			value =  shacl_shape+" --> "+"\""+shacl_class_property+"\""+" : "+shacl_path+shacl_literal+" "+shacl_pattern+" "+shacl_nodekind+"\n";			
		}
		this.shacl_class_property = value;
	}
	
	
	public String shacl_nodekind(String shacl_nodekind) {
		String value = "";
		
		if (shacl_nodekind != null) { //nodeKind
    		if (shacl_nodekind != "IRI") {
    			value += shacl_nodekind ;
    		}        		
    	}
		
	  return value;
	}
	
	
	
	public void codeuml(ShaclProperty plantUmlproperty, String nameshape) {
		
		this.setshacl_shape(nameshape);
		this.setshacl_path(plantUmlproperty.getValue_path());
		this.setshacl_datatype(plantUmlproperty.getValue_datatype());
		this.setshacl_literal(plantUmlproperty.getValue_cardinality(), plantUmlproperty.getValue_range(), plantUmlproperty.getValue_length());
		this.setshacl_nodekind(plantUmlproperty.getValue_nodeKind());
		this.setshacl_pattern(plantUmlproperty.getValue_pattern(),this.shacl_shape, plantUmlproperty.getValue_path());
		this.setshacl_node(plantUmlproperty.getValue_node(), this.shacl_shape, this.shacl_path, this.shacl_datatype, this.shacl_literal,this.shacl_nodekind, this.getshacl_pattern(false));
		this.setshacl_class(plantUmlproperty.getValue_class(),plantUmlproperty.getValue_class_property());
		this.setshacl_class_property(plantUmlproperty.getValue_class_property(),this.shacl_shape,this.shacl_path,this.shacl_literal,this.shacl_nodekind,this.shacl_pattern);
		
	}
	
	 
}
