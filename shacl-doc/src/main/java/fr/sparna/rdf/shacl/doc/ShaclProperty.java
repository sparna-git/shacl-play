package fr.sparna.rdf.shacl.doc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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

	private Resource propertyShape;
	
	protected String path;
	protected String datatype;
	protected String nodeKind;
	protected String cardinality;
	protected String pattern;
	protected String node;
	protected String class_node;
	protected String class_property;
	protected String name;
	protected String description;
	protected String shin;
	protected Integer shOrder;
	protected String shValue;
	
	ConstraintValueReader constraintValueReader = new ConstraintValueReader();
	
	
	public String getShValue() {
		return shValue;
	}

	public void setShValue(Resource constraint) {
		String value = null;
		if(constraint.hasProperty(SH.value)) {
			value = constraint.getProperty(SH.value).getLiteral().getString();
		}
		this.shValue = value;
	}

	public Integer getShOrder() {
		return shOrder;
	}

	public void setShOrder(Resource constraint) {
		Integer value = 0;
		if(constraint.hasProperty(SH.order)) {
			value = Integer.parseInt(constraint.getProperty(SH.order).getLiteral().getString());
		}
		this.shOrder = value;
	}

	public String getShin() {
		return shin;
	}

	public void setShin(Resource constraint) {
		String value = null;
		if (constraint.hasProperty(SH.in)) {
			Resource list = constraint.getProperty(SH.in).getList().asResource();
			RDFList rdflist = list.as(RDFList.class);
			ExtendedIterator<RDFNode> items = rdflist.iterator();
			value = "";
			while (items.hasNext()) {
				RDFNode item = items.next();
				String valueString;
				if(item.isURIResource()) {
					valueString = item.getModel().shortForm(((Resource)item).getURI());
				} else {
					valueString = item.toString();
				}
				
				value += valueString + " ,";
			}
			value.substring(0,(value.length()-2));
		}
		this.shin = value;
	}
	
	public String getname() {
		return name;
	}

	public void setname(Resource constraint, String lang) {
		String value = null;
		if(constraint.hasProperty(SH.name)) {
			value = constraintValueReader.readValueconstraint(constraint,SH.name,lang);
		}
		this.name = value;
	}

	public String getdescription() {
		return description;
	}

	public void setdescription(Resource constraint,String lang) {
		String value = null;
		if(constraint.hasProperty(SH.description)) {
			value = constraintValueReader.readValueconstraint(constraint, SH.description, null);
		}
		this.description = value;		
	}

	public String getpath() {
		return path;
	}

	public void setpath(Resource constraint) {
		this.path = constraintValueReader.readValueconstraint(constraint, SH.path,null);		
	}

	public String getdatatype() {
		return datatype;
	}

	public void setdatatype(Resource constraint) {
		this.datatype = constraintValueReader.readValueconstraint(constraint, SH.datatype,null);
	}

	public String getnodeKind() {
		return nodeKind;
	}

	public void setnodeKind(Resource constraint) {
		this.nodeKind = constraintValueReader.readValueconstraint(constraint, SH.nodeKind,null);
	}

	// Cardinality Constraint Components

	public String getcardinality() {
		return cardinality;
	}

	public void setcardinality(Resource constraint) {
		String minCount = "0";
		String maxCount = "*";
		String uml_code = null;
		if (constraint.hasProperty(SH.minCount)) {
			minCount = constraint.getProperty(SH.minCount).getObject().asLiteral().getString();
			if (minCount == "") {
				minCount = "0";
			}
		}
		if (constraint.hasProperty(SH.maxCount)) {
			maxCount = constraint.getProperty(SH.maxCount).getObject().asLiteral().getString();
			;
			if (maxCount == "") {
				maxCount = "*";
			}
		}
		if ((constraint.hasProperty(SH.minCount)) || (constraint.hasProperty(SH.maxCount))) {
			uml_code = minCount + ".." + maxCount;
		} else {
			uml_code = null;
		}

		this.cardinality = uml_code;
	}

	public String getpattern() {
		return pattern;
	}

	public void setpattern(Resource constraint) {
		this.pattern = constraintValueReader.readValueconstraint(constraint, SH.pattern,null);
	}

	// Shape-based Constraint Components

	public String getnode() {
		return node;
	}

	public void setnode(Resource constraint, List<ShaclBox> allBoxes) {
		// this.node = constraintValueReader.readValueconstraint(constraint, SH.node);

		// 1. Lire la valeur de sh:node
		String nodeValue = constraintValueReader.readValueconstraint(constraint, SH.node,null);
		// 2. Trouver le PlantUmlBox qui a ce nom
		if(nodeValue != null) {
			ShaclBox theBox = null;
			for (ShaclBox plantUmlBox : allBoxes) {
				if (plantUmlBox.getNameshape().equals(nodeValue)) {
					theBox = plantUmlBox;
					break;
				}
			}

			if (theBox == null) {
				// on ne l'a pas trouv�, on sort la valeur de sh:node
				this.node = nodeValue;
			}	
		}		
	}

	public String getclass_property() {
		return class_property;
	}

	public void setclass_property(Resource constraint, List<ShaclBox> allBoxes) {
		String value = null;

		// 1. Lire la valeur de sh:node
		if (constraint.hasProperty(SH.class_)) {
			Resource idclass = constraint.getProperty(SH.class_).getResource();
			List<Resource> nodetargets = constraint.getModel().listResourcesWithProperty(SH.targetClass, idclass)
					.toList();
			for (Resource nodeTarget : nodetargets) {
				if (value != null) {
					System.out.println("Problem !");
				}
				value = nodeTarget.getLocalName();
			}

			if (nodetargets.isEmpty()) {
				if (idclass.hasProperty(RDF.type, RDFS.Class) && idclass.hasProperty(RDF.type, SH.NodeShape)) {
					value = idclass.getLocalName();
				} else { // Section quand il n'y a pas une targetClass
					value = constraint.getProperty(SH.class_).getResource().getLocalName();
				}
			}
		}

		// 2. Trouver le PlantUmlBox qui a ce nom
		ShaclBox theBox = null;
		for (ShaclBox plantUmlBox : allBoxes) {
			if (plantUmlBox.getNameshape().equals(value)) {
				theBox = plantUmlBox;
				break;
			}
		}

		if (theBox == null) {
			// on ne l'a pas trouv�, on sort la valeur de sh:node
			this.class_property = value;
		}
	}

	public String getclass() {
		return class_node;
	}

	public void setclass(Resource constraint) {
		String value = null;
		if (constraint.hasProperty(SH.class_)) {
			Resource idclass = constraint.getProperty(SH.class_).getResource();

			List<Resource> nodetarget = constraint.getModel().listResourcesWithProperty(SH.targetClass, idclass)
					.toList();
			for (Resource nodeTarget : nodetarget) {
				value = nodeTarget.getModel().shortForm(constraint.getProperty(SH.class_).getResource().getURI());
			}

		}
		this.class_node = value;
	}
	
	
	
	public Resource getPropertyShape() {
		return propertyShape;
	}

	// Principal
	public ShaclProperty(Resource constraint, List<ShaclBox> allBoxes, String lang) {
		
		this.propertyShape = constraint;
		
		this.setpath(constraint);
		this.setdatatype(constraint);
		this.setnodeKind(constraint);
		this.setcardinality(constraint);
		this.setnode(constraint, allBoxes);
		this.setpattern(constraint);
		this.setclass(constraint);
		this.setclass_property(constraint, allBoxes); // Returne la valeur de TargetClass
		this.setdescription(constraint, lang);
		this.setname(constraint, lang);			
		this.setShin(constraint);
		this.setShValue(constraint);
		this.setShOrder(constraint);

	}

}
