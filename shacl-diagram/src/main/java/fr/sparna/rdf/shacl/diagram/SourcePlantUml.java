package fr.sparna.rdf.shacl.diagram;

import java.io.File;

public class SourcePlantUml {

	protected String uml_shape;
	protected String uml_path;
	protected String uml_datatype;
	protected String uml_nodekind;
	protected String uml_literal;
	protected String uml_pattern;
	protected String uml_uniquelang;
	protected String uml_node;
	protected String uml_class;
	protected String uml_class_property;
	protected String uml_nodekind_pattern_uniquelang;
	protected String uml_hasValue;
	
	
	public String getUml_shape() {
		return uml_shape;
	}
	public void setUml_shape(String uml_shape) {
		uml_shape = "\""+uml_shape+"\"";
		this.uml_shape = uml_shape;
	}
	public String getUml_path() {
		return uml_path;
	}
	public void setUml_path(String uml_path) {
		this.uml_path = uml_path;
	}
	public String getUml_datatype() {
		return uml_datatype;
	}
	public void setUml_datatype(String uml_datatype) {
		String value = "";
		
		if(uml_datatype == null) {
			value = "";
		} else {
			value = " : "+uml_datatype;
		}		
		this.uml_datatype = value;
	}
	public String getUml_nodekind() {
		return uml_nodekind;
	}
	public void setUml_nodekind(String uml_nodekind) {
		if (uml_nodekind == null) {
			uml_nodekind = "";
		}
		this.uml_nodekind = uml_nodekind;
	}
	public String getUml_literal() {				
		return uml_literal;
	}
	public void setUml_literal(String cardinality, String range, String length) {
		String value = "";
		
		if (cardinality != null) { //value cardinality
			value = cardinality; }
		else if (range != null) { //value range
			value = range;}
		else if (length != null) { //value length
			value = length;}
		this.uml_literal = value;
	}

	public String getUml_pattern(boolean flag) {
		if (flag) {
			if (uml_pattern != "") {
				uml_pattern = "{field}"+" "+"("+uml_pattern+")";
			}
		}
		return uml_pattern;
	}
	public void setUml_pattern(String uml_pattern, String uml_shape, String uml_path) {
		if (uml_pattern != null) { //value pattern
			uml_pattern = "("+uml_pattern+")";
	}else {uml_pattern = "";}
		this.uml_pattern = uml_pattern;
	}
	public String getUml_uniquelang() {
		return uml_uniquelang;
	}
	public void setUml_uniquelang(String uml_uniquelang,String uml_shape, String uml_path) {
		String value = null;
		if (uml_uniquelang!=null) { 
			value = uml_uniquelang;
		} else 	{value = "";}
		this.uml_uniquelang = value;
	}
	
	public String getUml_node() {
		return uml_node;
	}
	public void setUml_node(String uml_node, String uml_shape, String uml_path,String uml_datatype, String uml_literal,String uml_nodekind, String uml_pattern) {
		String value = null;
		if (uml_node != null) { //node 
			
			value = uml_shape+ " --> " +"\""+uml_node+"\""+" : "+uml_path+uml_datatype+" "+uml_literal+" "+uml_pattern+" "+uml_nodekind(uml_nodekind)+"\n";            		
        	}		
		this.uml_node = value;
	}
	
	public String getUml_class() {
		return uml_class;
	}
	public void setUml_class(String uml_class,String uml_class_property) {
		String value = null;
			
		this.uml_class = value;
	}
	
	public String getUml_class_property() {
		return uml_class_property;
	}
	public void setUml_class_property(String uml_class_property ,String uml_shape,String uml_path, String uml_literal, String uml_nodekind, String uml_pattern) {
		String value = null;
		if (uml_class_property != null) {
			
			value =  uml_shape+" --> "+"\""+uml_class_property+"\""+" : "+uml_path+uml_literal+" "+uml_pattern+" "+uml_nodekind+"\n";
			
		}
		this.uml_class_property = value;
	}
	
	
	public String uml_nodekind(String uml_nodekind) {
		String value = "";
		
		if (uml_nodekind != null) { //nodeKind
    		if (uml_nodekind != "IRI") {
    			value += uml_nodekind ;
    		}        		
    	}
		
	  return value;
	}
	
	public String getUml_hasValue() {
		return uml_hasValue;
	}
	public void setUml_hasValue(String uml_hasValue) {
		String value = "";
		
		if (uml_hasValue != null) { 
    		 value = "["+uml_hasValue+"]";
    		}    
		
		this.uml_hasValue = value;
	}
	
	public void codeuml(PlantUmlProperty plantUmlproperty, String nameshape) {
		this.setUml_shape(nameshape);
		this.setUml_path(plantUmlproperty.getValue_path());
		this.setUml_datatype(plantUmlproperty.getValue_datatype());
		this.setUml_literal(plantUmlproperty.getValue_cardinality(), plantUmlproperty.getValue_range(), plantUmlproperty.getValue_length());
		this.setUml_nodekind(plantUmlproperty.getValue_nodeKind());
		this.setUml_node(plantUmlproperty.getValue_node(), this.uml_shape, this.uml_path, this.uml_datatype, this.uml_literal,this.uml_nodekind,this.uml_pattern);
		this.setUml_pattern(plantUmlproperty.getValue_pattern(),this.uml_shape, plantUmlproperty.getValue_path());
		this.setUml_uniquelang(plantUmlproperty.getValue_uniquelang(),this.uml_shape, this.uml_path);
		this.setUml_class(plantUmlproperty.getValue_class(),plantUmlproperty.getValue_class_property());
		this.setUml_class_property(plantUmlproperty.getValue_class_property(),this.uml_shape,this.uml_path,this.uml_literal,this.uml_nodekind,this.uml_pattern);
		this.setUml_hasValue(plantUmlproperty.getValue_hasValue());
	}
	
	 
}
