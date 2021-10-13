package fr.sparna.rdf.shacl.shacl2xsd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;


public class ShaclXsdPropertyConstraintReader {
	
	protected List<ShaclXsdBox> allBoxes;
	protected ConstraintValueReader constraintValueReader = new ConstraintValueReader(); 


	public ShaclXsdPropertyConstraintReader(List<ShaclXsdBox> allBoxes) {
		super();
		this.allBoxes = allBoxes;
	}
	
	// Principal
	public ShaclXsdProperty readPlantUmlProperty(Resource constraint, Model owlGraph) {
		
		ShaclXsdProperty p = new ShaclXsdProperty(constraint);
		
		p.setValue_path(this.readShPath(constraint));		
		p.setValue_datatype(this.readShDatatype(constraint));
		
		p.setValue_nodeKind(this.readShNodeKind(constraint));
		/*
		p.setValue_range(this.readShMinInclusiveMaxInclusive(constraint));		
		p.setValue_length(this.readShMinLengthMaxLength(constraint));		
		p.setValue_pattern(this.readShPattern(constraint));		
		p.setValue_language(this.readShLanguageIn(constraint));		
		p.setValue_uniquelang(this.readShUniqueLang(constraint));
		*/		
		p.setValue_node(this.readShNode(constraint));
		p.setValue_class_property(this.readShClass(constraint));
		p.setValue_order_shacl(this.readShOrder(constraint));
		/*
		p.setValue_hasValue(this.readShHasValue(constraint));
		p.setValue_qualifiedvalueshape(this.readShQualifiedValueShape(constraint));
		p.setValue_qualifiedMaxMinCount(this.readShQualifiedMinCountQualifiedMaxCount(constraint));
		p.setValue_inverseOf(this.readOwlInverseOf(owlGraph, p.getValue_path()));
		p.setValue_shor(this.readShOrConstraint(constraint));
		*/
		p.setValue_description(this.readShDescription(constraint));
		p.setValue_maxCount(this.readShMaxCount(constraint));
		p.setValue_minCount(this.readShMinCount(constraint));

		return p;
	}
	
	public String readShDescription(Resource constraint) {
		String value_maxCount =null;
		
		if (constraint.hasProperty(SH.description)) {
			value_maxCount = constraintValueReader.readValueconstraint(constraint, SH.description);
		}
		
		return value_maxCount;
	}
	
	
	
	public String readShMaxCount(Resource constraint) {
		String value_maxCount ="unbounded";
		
		if (constraint.hasProperty(SH.maxCount)) {
			value_maxCount = constraintValueReader.readValueconstraint(constraint, SH.maxCount);
		}
		
		return value_maxCount;
	}
	
	public String readShMinCount(Resource constraint) {
		String value_minCount = "0";
		if (constraint.hasProperty(SH.minCount)){
			value_minCount = constraintValueReader.readValueconstraint(constraint, SH.minCount);
		}
		return value_minCount;
	}
	
	
	public List<String> readOwlInverseOf(Model owlGraph, String path) {
		List<String> inverBox = new ArrayList<>();
		if(path != null) {
			// read everything typed as NodeShape
			List<Resource> pathOWL = owlGraph.listResourcesWithProperty(RDF.type, OWL.ObjectProperty).toList();
			for(Resource inverseOfResource : pathOWL) {
				if(inverseOfResource.getLocalName().equals(path)){
					if(inverseOfResource.hasProperty(OWL.inverseOf)) {
						inverBox.add(inverseOfResource.getProperty(OWL.inverseOf).getResource().asResource().getLocalName().toString());
					}
				}
			}				
		}		
		return inverBox;
	}
	
	public List<ShaclXsdBox> readShOrConstraint (Resource constraint) {
		// 1. Lire la valeur de sh:or
		String OrValue = constraintValueReader.readValueconstraint(constraint, SH.or);
		// 2. Trouver le PlantUmlBox qui a ce nom
		List<ShaclXsdBox> theBox = new ArrayList<>();
		if (OrValue != null) {	
			for(String sValueOr : OrValue.split(",")) {				
				for (ShaclXsdBox plantUmlBox : allBoxes) {
					if(plantUmlBox.getLabel().equals(sValueOr)) {
						theBox.add(plantUmlBox);
						break;
					}
				}				
			}			
		}
		return theBox;
	}
	
	public String readShPath(Resource constraint) {		
		return constraintValueReader.readValueconstraint(constraint,SH.path);
	}
		
	public String readShDatatype(Resource constraint) {
		return constraintValueReader.readValueconstraintAsShortForm(constraint, SH.datatype);
	}

	public String readShNodeKind(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint,SH.nodeKind);
	}
	
	//Value Range Constraint Components
	public String readShMinInclusiveMaxInclusive(Resource constraint) {
		
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
		
		return uml_range ;
	}
	
	
	//String-based Constraint Components
	
	public String readShMinLengthMaxLength(Resource constraint) {
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
		return uml_code;
	}
	

	public String readShPattern(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint, SH.pattern);
	}
	

	public String readShLanguageIn(Resource constraint) {
		String value = null;
		if (constraint.hasProperty(SH.languageIn)) {
			Resource list = constraint.getProperty(SH.languageIn).getList().asResource();		
		    RDFList rdfList = list.as(RDFList.class);
		    ExtendedIterator<RDFNode> items = rdfList.iterator();
		    value = "";
		    while ( items.hasNext() ) {
		    	RDFNode item = items.next();
		        value += item+" ,";
		    }
		    value.substring(0, (value.length()-1));
		}
		return value;
	}
	

	public String readShUniqueLang(Resource constraint) {
		String value = null;
		if (
				constraint.hasProperty(SH.uniqueLang)
				&&
				constraintValueReader.readValueconstraint(constraint, SH.uniqueLang) != null
				&&
				!constraintValueReader.readValueconstraint(constraint, SH.uniqueLang).equals("")
		) {			
			value = "uniqueLang";
		}
	    return value;
	}
	
	// Shape-based Constraint Components

	
	public ShaclXsdBox readShNode(Resource constraint) {
			// 1. Lire la valeur de sh:node
			//Resource nodeValue = constraint.asResource().getProperty(SH.node).getResource();
			String nodeValue = null;
			nodeValue = constraintValueReader.readValueconstraint(constraint, SH.node);		
					
		
			// 2. Trouver le PlantUmlBox qui a ce nom
			ShaclXsdBox theBox = null;
			if (nodeValue != null) {
				for (ShaclXsdBox ShaclXsdBox : allBoxes) {
					if(ShaclXsdBox.getLabel().toString().equals(nodeValue.toString())) {
						theBox = ShaclXsdBox;
						break;
					}					
				}
			}
			System.out.println("*** SH Node:"+theBox);
			return theBox;
	}
	

	public String readShClass(Resource constraint) {
		String value = null;
		
		// 1. Lire la valeur de sh:node
		if (constraint.hasProperty(SH.class_)) {
			Resource idclass = constraint.getProperty(SH.class_).getResource();
			value = idclass.getLocalName();
		}	
		
		return value;
	}
	
	
	public Integer readShOrder(Resource constraint) {
		String v = constraintValueReader.readValueconstraint(constraint, SH.order);
		
		return (v != null)?Integer.parseInt(v):null;		
	}
		
	public String readShHasValue(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint, SH.hasValue);
	}

	public ShaclXsdBox readShQualifiedValueShape(Resource constraint) {
		
		// 1. Lire la valeur de sh:node
		String nodeValue = constraintValueReader.readValueconstraint(constraint, SH.qualifiedValueShape);
		
		// 2. Trouver le PlantUmlBox qui a ce nom
		ShaclXsdBox theBox = null;
		if (nodeValue != null) {
			for (ShaclXsdBox ShaclXsdBox : allBoxes) {
				if(ShaclXsdBox.getLabel().equals(nodeValue)) {
					theBox = ShaclXsdBox;
					break;
				}
			}
		}
		
		return theBox;
	} 

	public String readShQualifiedMinCountQualifiedMaxCount(Resource constraint) {
		String value_minCount = "0";
		String value_maxCount ="*";
		String uml_code =null;
		
		if (constraint.hasProperty(SH.qualifiedMinCount)){
			value_minCount = constraintValueReader.readValueconstraint(constraint, SH.qualifiedMinCount);
		}
		if (constraint.hasProperty(SH.qualifiedMaxCount)) {
			value_maxCount = constraintValueReader.readValueconstraint(constraint, SH.qualifiedMaxCount);
		}
		
		if ((constraint.hasProperty(SH.qualifiedMinCount)) || (constraint.hasProperty(SH.qualifiedMaxCount))){
			uml_code = "["+ value_minCount +".."+ value_maxCount +"]";
		} else {
			uml_code = null;
		}
		
		return uml_code;
		
	}
	
}
