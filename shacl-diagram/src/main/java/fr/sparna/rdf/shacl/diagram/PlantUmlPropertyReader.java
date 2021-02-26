package fr.sparna.rdf.shacl.diagram;

import java.util.List;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class PlantUmlPropertyReader {
	
	protected List<PlantUmlBox> allBoxes;
	protected ConstraintValueReader constraintValueReader = new ConstraintValueReader(); 


	public PlantUmlPropertyReader(List<PlantUmlBox> allBoxes) {
		super();
		this.allBoxes = allBoxes;
	}
	
	// Principal
	public PlantUmlProperty readPlantUmlProperty(Resource constraint) {
		
		PlantUmlProperty p = new PlantUmlProperty(constraint);
		
		p.setValue_path(this.readShPath(constraint));
		p.setValue_datatype(this.readShDatatype(constraint));
		p.setValue_nodeKind(this.readShNodeKind(constraint));
		p.setValue_cardinality(this.readShMinCountMaxCount(constraint));		
		p.setValue_range(this.readShMinInclusiveMaxInclusive(constraint));		
		p.setValue_length(this.readShMinLengthMaxLength(constraint));		
		p.setValue_pattern(this.readShPattern(constraint));		
		p.setValue_language(this.readShLanguageIn(constraint));		
		p.setValue_uniquelang(this.readShUniqueLang(constraint));		
		p.setValue_node(this.readShNode(constraint));
		p.setValue_class_property(this.readShClass(constraint));
		p.setValue_order_shacl(this.readShOrder(constraint));
		p.setValue_hasValue(this.readShHasValue(constraint));
		p.setValue_qualifiedvalueshape(this.readShQualifiedValueShape(constraint));
		p.setValue_qualifiedMaxMinCount(this.readShQualifiedMinCountQualifiedMaxCount(constraint));
		
		return p;
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
	
	//Cardinality Constraint Components
	

	public String readShMinCountMaxCount(Resource constraint) {
		String value_minCount = "0";
		String value_maxCount ="*";
		String uml_code =null;
		if (constraint.hasProperty(SH.minCount)){
			value_minCount = constraintValueReader.readValueconstraint(constraint, SH.minCount);
		}
		if (constraint.hasProperty(SH.maxCount)) {
			value_maxCount = constraintValueReader.readValueconstraint(constraint, SH.maxCount);
		}
		
		if ((constraint.hasProperty(SH.minCount)) || (constraint.hasProperty(SH.maxCount))){
			uml_code = "["+ value_minCount +".."+ value_maxCount +"]";
		} else {
			uml_code = null;
		}
		
		return uml_code;
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

	
	public String readShNode(Resource constraint) {
		// this.value_node = constraintValueReader.readValueconstraint(constraint, SH.node);
		
		// 1. Lire la valeur de sh:node
		String nodeValue = constraintValueReader.readValueconstraint(constraint, SH.node);
		// 2. Trouver le PlantUmlBox qui a ce nom
		PlantUmlBox theBox = null;
		for (PlantUmlBox plantUmlBox : allBoxes) {
			if(plantUmlBox.getNameshape().equals(nodeValue)) {
				theBox = plantUmlBox;
				break;
			}
		}
		
		if(theBox == null) {
			// on ne l'a pas trouv�, on sort la valeur de sh:node
			return nodeValue;
		} else {
			// 3. Lire le nom de la box avec son package devant
			return theBox.getQualifiedName();
		}
	}
	

	public String readShClass(Resource constraint) {
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
		PlantUmlBox theBox = null;
		for (PlantUmlBox plantUmlBox : allBoxes) {
			if(plantUmlBox.getNameshape().equals(value)) {
				theBox = plantUmlBox;
				break;
			}
		}

		if(theBox == null) {
			// on ne l'a pas trouv�, on sort la valeur de sh:node
			return value;
		} else {
			// 3. Lire le nom de la box avec son package devant
			return theBox.getQualifiedName();
		}
		//this.value_class_property = value;
	}
	
	
	public Integer readShOrder(Resource constraint) {
		String v = constraintValueReader.readValueconstraint(constraint, SH.order);
		
		return (v != null)?Integer.parseInt(v):null;		
	}
		
	public String readShHasValue(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint, SH.hasValue);
	}

	public String readShQualifiedValueShape(Resource constraint) {
		String value = null;
		if (constraint.hasProperty(SH.qualifiedValueShape)) {
			Resource qualifiedValueShape = constraint.getPropertyResourceValue(SH.qualifiedValueShape);
			
			// Cas 1. : si la valeur est directement une référence à un NodeShape
			boolean foundAsNodeShape = false;
			for (PlantUmlBox aBox : allBoxes) {
				if(aBox.getNodeShape().getURI().equals(qualifiedValueShape.getURI())) {
					foundAsNodeShape = true;
					break;
				}
			}
			
			// utilisation des streams
//			if(allBoxes.stream().filter(box -> box.getNodeShape().getURI().equals(qualifiedValueShape.getURI())).findAny().isPresent()) {
//				
//			}
			
			if(foundAsNodeShape) {
				value = qualifiedValueShape.getLocalName();
			} else {
				// Cas 2. : si la valeur a un sh:class, on cherche la NodeShape qui a un sh:targetClass qui a la même valeur que le sh:class
				if(qualifiedValueShape.hasProperty(SH.class_)) {
					Resource shclassValue = qualifiedValueShape.getPropertyResourceValue(SH.class_);
					
					List<Resource> nodetarget = constraint.getModel().listResourcesWithProperty(SH.targetClass,shclassValue).toList();
					// Attention, s'il y a plusieurs valeurs, on ne sait en lire qu'une seule.
					for(Resource nodeTarget : nodetarget) {
						value = nodeTarget.getLocalName();
					}
					
					/*if(nodetarget.isEmpty()) {
						if(shclassValue.hasProperty(RDF.type, RDFS.Class) && shclassValue.hasProperty(RDF.type, SH.NodeShape)) {
							value = shclassValue.getLocalName();	
							} 
							else {   // Section quand il n'y a pas une targetClass
								value = shclassValue.getLocalName();  //constraint.getProperty(SH.class_).getResource().getLocalName();
							}
						}	*/				
				}
			}
		}
		
		return value;
	} 

	public String readShQualifiedMinCountQualifiedMaxCount(Resource constraint) {
		String value_minCount = "0";
		String value_maxCount ="*";
		String uml_code =null;
		
		if (constraint.hasProperty(SH.qualifiedMinCount)){
			value_minCount = constraintValueReader.readValueconstraint(constraint, SH.minCount);
		}
		if (constraint.hasProperty(SH.qualifiedMaxCount)) {
			value_maxCount = constraintValueReader.readValueconstraint(constraint, SH.maxCount);
		}
		
		if ((constraint.hasProperty(SH.qualifiedMinCount)) || (constraint.hasProperty(SH.qualifiedMaxCount))){
			uml_code = "["+ value_minCount +".."+ value_maxCount +"]";
		} else {
			uml_code = null;
		}
		
		return uml_code;
		
	}

	
}
