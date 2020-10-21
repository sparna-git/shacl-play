package fr.sparna.rdf.shacl.diagram;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class PlantUmlProperty {

	protected String value_path;
	protected String value_datatype;
	protected String value_nodeKind;
	protected String value_cardinality;	
	protected String value_range;	
	protected String value_length;
	protected String value_pattern;
	protected String value_language;
	protected String value_uniquelang;
	protected String value_node;
	protected String value_class;
	protected String value_class_property;
	
	
	ConstraintValue valuedata = new ConstraintValue(); 
	//Value Type Constraint Components

	public String getValue_path() {
		return value_path;
	}
	public void setValue_path(Resource constraint,Property property) {
		valuedata.setValueconstraint(constraint,property);
		this.value_path = valuedata.getValueconstraint();
	}
		
	public String getValue_datatype() {
		return value_datatype;
	}
		
	public void setValue_datatype(Resource constraint,Property property) {
		valuedata.setValueconstraint(constraint, property);
		this.value_datatype = valuedata.getValueconstraint();
	}
	public String getValue_nodeKind() {
		return value_nodeKind;
	}
	public void setValue_nodeKind(Resource constraint,Property property) {
		valuedata.setValueconstraint(constraint,property);
		this.value_nodeKind = valuedata.getValueconstraint();
	}
	
	//Cardinality Constraint Components
	
	public String getValue_cardinality() {
		return value_cardinality;
	}
	public void setValue_cardinality(Resource constraint) {
		String value_minCount = "0";
		String value_maxCount ="*";
		String uml_code =null;
		if (constraint.hasProperty(SH.minCount)){
			valuedata.setValueconstraint(constraint, SH.minCount);
			value_minCount = valuedata.getValueconstraint();
			if (value_minCount == ""){value_minCount = "0";}
		}
		if (constraint.hasProperty(SH.maxCount)) {
			valuedata.setValueconstraint(constraint, SH.maxCount);
			value_maxCount = valuedata.getValueconstraint();
			if (value_maxCount == "") {value_maxCount = "*";}
		}
		if ((constraint.hasProperty(SH.minCount)) || (constraint.hasProperty(SH.maxCount))){
			uml_code = "["+ value_minCount +".."+ value_maxCount +"]";
		}
		else {uml_code = null;}
		
		this.value_cardinality = uml_code;
	}
	
	//Value Range Constraint Components
	
	public String getValue_range() {
		return value_range;
	}
	public void setValue_range(Resource constraint) {
		
		boolean a1 = false;
		boolean a2 = false;
		boolean a3 = false;
		boolean a4 = false;
		String value_minIn ="";
		String value_maxIn = "";
		String value_minEx = "";
		String value_maxEx = "";
		String uml_range =null;
		
		if (constraint.hasProperty(SH.minInclusive)) {
			a1 = true;
			valuedata.setValueconstraint(constraint, SH.minInclusive);
			value_minIn = "(range : ["+valuedata.getValueconstraint()+"-";			
		} 
		if (constraint.hasProperty(SH.maxInclusive)){
			a2 = true;
			valuedata.setValueconstraint(constraint, SH.maxInclusive);
			value_maxIn = "-"+valuedata.getValueconstraint()+"])";			
		} 
		if (constraint.hasProperty(SH.minExclusive)){
			a3 = true;
			valuedata.setValueconstraint(constraint,SH.minExclusive);
			value_minEx = "(range : ]"+valuedata.getValueconstraint()+"-";			
		} 
		if(constraint.hasProperty(SH.maxExclusive)) {
			a4 = true;
			valuedata.setValueconstraint(constraint, SH.maxExclusive);
			value_maxEx = "-"+valuedata.getValueconstraint()+"[)";	
		}
		
		if ((a1) & (!a2)) {
			uml_range = value_minIn+"*[";					
		}
		else if ((a2) & (!a1)) {
			uml_range = "(range : ]*"+value_maxIn;
		}
		else if ((a3) & (!a4)) {
			uml_range = value_minEx+"*[)";
		}
		else if ((a4) & (!a3)) {
			uml_range = "(range : ]*"+value_maxEx;
		} else {uml_range = null;}
		
		this.value_range = uml_range ;
	}
	
	
	//String-based Constraint Components
	
	public String getValue_length() {
		return value_length;
	}
	
	public void setValue_length(Resource constraint) {
		String value_maxLength = "";
		String value_minLength = "";
		String uml_code = null;
		
		if(constraint.hasProperty(SH.maxLength)){
			valuedata.setValueconstraint(constraint, SH.maxLength);
			value_maxLength = valuedata.getValueconstraint();			
		}				
		if (constraint.hasProperty(SH.minLength)){
			valuedata.setValueconstraint(constraint, SH.minLength);
			value_minLength = valuedata.getValueconstraint(); 			
		}
		if ((constraint.hasProperty(SH.maxLength)) || (constraint.hasProperty(SH.minLength))){
			if(value_minLength=="") { value_minLength = "0"; }
			if(value_minLength=="") { value_minLength = "*"; }
			uml_code = "(Length ["+value_minLength +".."+value_maxLength+"])";			
		}
		this.value_length = uml_code;
	}
	
	public String getValue_pattern() {
		return value_pattern;
	}
	public void setValue_pattern(Resource constraint,Property property) {
		String value_pattern ="";
		
		valuedata.setValueconstraint(constraint, property);
		value_pattern = valuedata.getValueconstraint();
		
		this.value_pattern = value_pattern;
	}
	
	public String getValue_language() {
		return value_language;
	}	
	public void setValue_language(Resource constraint,Property property) {
		String value = null;
		if (constraint.hasProperty(property)) {
			Resource list = constraint.getProperty(property).getList().asResource();		
		    RDFList rdfList = list.as(RDFList.class);
		    ExtendedIterator<RDFNode> items = rdfList.iterator();
		    value = "";
		    while ( items.hasNext() ) {
		    	RDFNode item = items.next();
		        value += item+" ,";
		    }
		    value.substring(0, (value.length()-1));
		}
		this.value_language = value;
	}
	
	public String getValue_uniquelang() {
		return value_uniquelang;
	}
	public void setValue_uniquelang(Resource constraint,Property property) {
		String value = null;
		if (constraint.hasProperty(property)) {
			valuedata.setValueconstraint(constraint, property);
			value_uniquelang = valuedata.getValueconstraint();
			if (!value_uniquelang.isBlank()) {
				value = "uniqueLang";			
			} else {value_uniquelang = null;}
			
		}
	    this.value_uniquelang = value;
	}
	
	// Shape-based Constraint Components
	
	public String getValue_node() {
		return value_node;
	}
	
	public void setValue_node(Resource constraint,Property property) {
		
		valuedata.setValueconstraint(constraint, property);
		this.value_node = valuedata.getValueconstraint();
	}
	
	public String getValue_class_property() {
		return value_class_property;
	}
	public void setValue_class_property(Resource constraint) {
		String value = null;
		
		if (constraint.hasProperty(SH.class_)) {
			valuedata.setValueconstraint(constraint, SH.class_);
			Resource idclass = constraint.getProperty(SH.class_).getResource();
			List<Resource> nodetargets = constraint.getModel().listResourcesWithProperty(SH.targetClass,idclass).toList();
			for(Resource nodeTarget : nodetargets) {
				if(value != null) {
					System.out.println("Problem !");
				}
				value = nodeTarget.getLocalName();
				
			}
		}	
		this.value_class_property = value;
	}
	
	public String getValue_class() {
		return value_class;
	}
	public void setValue_class(Resource constraint) {
		String value = null;
		if (constraint.hasProperty(SH.class_)) {
			valuedata.setValueconstraint(constraint, SH.class_);
			Resource idclass = constraint.getProperty(SH.class_).getResource();
			List<Resource> nodetarget = constraint.getModel().listResourcesWithProperty(SH.targetClass,idclass).toList();
			for(Resource nodeTarget : nodetarget) {
				value = nodeTarget.getLocalName();
			}
				
		}		
		
		this.value_class = value;
	}
	
	
	// Principal
	public PlantUmlProperty (Resource constraint) {
		
		this.setValue_path(constraint, SH.path);
		this.setValue_datatype(constraint, SH.datatype);
		this.setValue_nodeKind(constraint, SH.nodeKind);
		this.setValue_cardinality(constraint);		
		this.setValue_range(constraint);		
		this.setValue_length(constraint);		
		this.setValue_pattern(constraint, SH.pattern);		
		this.setValue_language(constraint, SH.languageIn);		
		this.setValue_uniquelang(constraint, SH.uniqueLang);		
		this.setValue_node(constraint, SH.node);
		this.setValue_class(constraint);
		this.setValue_class_property(constraint);
		
		
	}
	
	
	
}
