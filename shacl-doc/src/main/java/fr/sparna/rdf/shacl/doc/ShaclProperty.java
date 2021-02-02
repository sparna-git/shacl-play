package fr.sparna.rdf.shacl.doc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.jenax.util.JenaDatatypes;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.arq.functions.HasShapeFunction;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclProperty {

	protected String value_path;
	protected String value_datatype;
	protected String value_nodeKind;
	protected String value_cardinality;	
	protected String value_range;	
	protected String value_length;
	protected String value_pattern;
	protected String value_node;
	protected String value_class;
	protected String value_class_property;
	protected String value_order_shacl;
	protected String value_name;
	protected String value_description;
	protected String value_property;
	
	ConstraintValueReader constraintValueReader = new ConstraintValueReader(); 
	//Value Type Constraint Components
	
	
	
	
	public String getValue_name() {
		return value_name;
	}

	public void setValue_name(Resource constraint) {
		this.value_name = constraintValueReader.readValueconstraint(constraint,SH.name);
	}

	public String getValue_description() {
		return value_description;
	}

	public void setValue_description(Resource constraint) {
		this.value_description = constraintValueReader.readValueconstraint(constraint,SH.description);
	}


	public String getValue_path() {
		return value_path;
	}
	
	public void setValue_path(Resource constraint) {		
		this.value_path = constraintValueReader.readValueconstraint(constraint,SH.path);
	}
		
	public String getValue_datatype() {
		return value_datatype;
	}
		
	public void setValue_datatype(Resource constraint) {
		this.value_datatype = constraintValueReader.readValueconstraint(constraint, SH.datatype);
	}
	public String getValue_nodeKind() {
		return value_nodeKind;
	}
	public void setValue_nodeKind(Resource constraint) {
		this.value_nodeKind = constraintValueReader.readValueconstraint(constraint,SH.nodeKind);
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
			value_minCount = constraintValueReader.readValueconstraint(constraint, SH.minCount);
			if (value_minCount == ""){value_minCount = "0";}
		}
		if (constraint.hasProperty(SH.maxCount)) {
			value_maxCount = constraintValueReader.readValueconstraint(constraint, SH.maxCount);
			if (value_maxCount == "") {value_maxCount = "*";}
		}
		if ((constraint.hasProperty(SH.minCount)) || (constraint.hasProperty(SH.maxCount))){
			uml_code = "["+ value_minCount +".."+ value_maxCount +"]";
		}
		else {uml_code = null;}
		
		this.value_cardinality = uml_code;
	}
	
	
	public String getValue_property() {
		return value_property;
	}

	public void setValue_property(Resource constraint) {
		this.value_property = constraintValueReader.readValueconstraint(constraint,SH.property);
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
			value_minIn = "{field} (range : ["+constraintValueReader.readValueconstraint(constraint, SH.minInclusive)+"-";			
		} 
		if (constraint.hasProperty(SH.maxInclusive)){
			a2 = true;
			value_maxIn = "-"+constraintValueReader.readValueconstraint(constraint, SH.maxInclusive)+"])";			
		} 
		if (constraint.hasProperty(SH.minExclusive)){
			a3 = true;
			value_minEx = "{field} (range : ]"+constraintValueReader.readValueconstraint(constraint,SH.minExclusive)+"-";			
		} 
		if(constraint.hasProperty(SH.maxExclusive)) {
			a4 = true;
			value_maxEx = "-"+constraintValueReader.readValueconstraint(constraint, SH.maxExclusive)+"[)";	
		}
		
		if ((a1) & (!a2)) {
			uml_range = value_minIn+"*[";					
		}
		else if ((a2) & (!a1)) {
			uml_range = "{field} (range : ]*"+value_maxIn;
		}
		else if ((a3) & (!a4)) {
			uml_range = value_minEx+"*[)";
		}
		else if ((a4) & (!a3)) {
			uml_range = "{field} (range : ]*"+value_maxEx;
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
			value_maxLength = constraintValueReader.readValueconstraint(constraint, SH.maxLength);			
		}				
		if (constraint.hasProperty(SH.minLength)){
			value_minLength = constraintValueReader.readValueconstraint(constraint, SH.minLength); 			
		}
		if ((constraint.hasProperty(SH.maxLength)) || (constraint.hasProperty(SH.minLength))){
			if(value_minLength=="") { value_minLength = "0"; }
			if(value_minLength=="") { value_minLength = "*"; }
			uml_code = "{field} (Length ["+value_minLength +".."+value_maxLength+"])";			
		}
		this.value_length = uml_code;
	}
	
	public String getValue_pattern() {
		return value_pattern;
	}
	public void setValue_pattern(Resource constraint) {
		this.value_pattern = constraintValueReader.readValueconstraint(constraint, SH.pattern);
	}	
	
	// Shape-based Constraint Components
	
	public String getValue_node() {
		return value_node;
	}
	
	public void setValue_node(Resource constraint, List<ShaclBox> allBoxes) {
		// this.value_node = constraintValueReader.readValueconstraint(constraint, SH.node);
		
		// 1. Lire la valeur de sh:node
		String nodeValue = constraintValueReader.readValueconstraint(constraint, SH.node);
		// 2. Trouver le PlantUmlBox qui a ce nom
		ShaclBox theBox = null;
		for (ShaclBox plantUmlBox : allBoxes) {
			if(plantUmlBox.getNameshape().equals(nodeValue)) {
				theBox = plantUmlBox;
				break;
			}
		}
		
		if(theBox == null) {
			// on ne l'a pas trouv�, on sort la valeur de sh:node
			this.value_node = nodeValue;
		} else {
			// 3. Lire le nom de la box avec son package devant
			this.value_node = theBox.getQualifiedName();
		}
	}
	
	public String getValue_class_property() {
		return value_class_property;
	}
	public void setValue_class_property(Resource constraint, List<ShaclBox> allBoxes) {
		String value = null;
		
		// 1. Lire la valeur de sh:node
		if (constraint.hasProperty(SH.class_)) {
			Resource idclass = constraint.getProperty(SH.class_).getResource();
			List<Resource> nodetargets = constraint.getModel().listResourcesWithProperty(SH.targetClass,idclass).toList();
			for(Resource nodeTarget : nodetargets) {
				if(value != null) {
					System.out.println("Problem !");
				}
				value = nodeTarget.getLocalName();				
			}
			
			if(nodetargets.isEmpty()) {
				if(idclass.hasProperty(RDF.type, RDFS.Class) && idclass.hasProperty(RDF.type, SH.NodeShape)) {
					value = idclass.getLocalName();	
				} 
				else {   // Section quand il n'y a pas une targetClass
					value = constraint.getProperty(SH.class_).getResource().getLocalName();
				}
			}
		}
		
		// 2. Trouver le PlantUmlBox qui a ce nom
		ShaclBox theBox = null;
		for (ShaclBox plantUmlBox : allBoxes) {
			if(plantUmlBox.getNameshape().equals(value)) {
				theBox = plantUmlBox;
				break;
			}
		}

		if(theBox == null) {
			// on ne l'a pas trouv�, on sort la valeur de sh:node
			this.value_class_property = value;
		} else {
			// 3. Lire le nom de la box avec son package devant
			this.value_class_property = theBox.getQualifiedName();
		}
		//this.value_class_property = value;
	}
	
	public String getValue_class() {
		return value_class;
	}
	
	public void setValue_class(Resource constraint) {
		String value = null;
		if (constraint.hasProperty(SH.class_)) {
			Resource idclass = constraint.getProperty(SH.class_).getResource();
			
			List<Resource> nodetarget = constraint.getModel().listResourcesWithProperty(SH.targetClass,idclass).toList();
			for(Resource nodeTarget : nodetarget) {
				value = nodeTarget.getLocalName();
			}
			
		}			
		this.value_class = value;
	}
	
	
	public String getValue_order_shacl() {
		return value_order_shacl;
	}
	
	
	public void setValue_order_shacl(Resource constraint) {
		this.value_order_shacl = constraintValueReader.readValueconstraint(constraint, SH.order);		
	}
	
		
	
	// Principal
	public ShaclProperty (Resource constraint, List<ShaclBox> allBoxes) {
		
		this.setValue_path(constraint);
		this.setValue_datatype(constraint);
		this.setValue_nodeKind(constraint);
		this.setValue_cardinality(constraint);		
		this.setValue_node(constraint, allBoxes);
		this.setValue_pattern(constraint);	
		this.setValue_class(constraint);
		this.setValue_class_property(constraint, allBoxes);  //Returne la valeur de TargetClass
		this.setValue_order_shacl(constraint);
		this.setValue_description(constraint);
		this.setValue_name(constraint);
		
		
	}
	
	
	
}
