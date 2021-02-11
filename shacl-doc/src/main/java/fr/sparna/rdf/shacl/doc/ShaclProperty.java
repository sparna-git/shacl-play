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
	protected String shLanguage;
	protected Integer shOrder;
	
	ConstraintValueReader constraintValueReader = new ConstraintValueReader();
	
	public Integer getShOrder() {
		return shOrder;
	}

	public void setShOrder(Resource constraint) {
		Integer value = null;
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
	
	
	
	
	
	

	public String getShLanguage() {
		return shLanguage;
	}

	public void setShLanguage(String sh_name, String sh_description) {
		String value = null;
		
		if(sh_name != null) {
			String[] getLanguage = sh_name.split("@");
			value = getLanguage[getLanguage.length-1];	
		}else if(sh_description != null) {
			String[] getLanguage = sh_description.split("@");
			value = getLanguage[getLanguage.length-1];	
		}
		
		this.shLanguage = value;
	}

	public String getname() {
		return name;
	}

	public void setname(Resource constraint, String lang) {
		// here : read value in specified language, or without language if not found
		this.name = constraintValueReader.readValueconstraint(constraint, SH.name);
	}

	public String getdescription() {
		return description;
	}

	public void setdescription(Resource constraint) {
		this.description = constraintValueReader.readValueconstraint(constraint, SH.description);
		;
	}

	public String getpath() {
		return path;
	}

	public void setpath(Resource constraint) {
		this.path = constraintValueReader.readValueconstraint(constraint, SH.path);
	}

	public String getdatatype() {
		return datatype;
	}

	public void setdatatype(Resource constraint) {
		this.datatype = constraintValueReader.readValueconstraint(constraint, SH.datatype);
	}

	public String getnodeKind() {
		return nodeKind;
	}

	public void setnodeKind(Resource constraint) {
		this.nodeKind = constraintValueReader.readValueconstraint(constraint, SH.nodeKind);
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

		this.pattern = constraintValueReader.readValueconstraint(constraint, SH.pattern);
	}

	// Shape-based Constraint Components

	public String getnode() {
		return node;
	}

	public void setnode(Resource constraint, List<ShaclBox> allBoxes) {
		// this.node = constraintValueReader.readValueconstraint(constraint, SH.node);

		// 1. Lire la valeur de sh:node
		String nodeValue = constraintValueReader.readValueconstraint(constraint, SH.node);
		// 2. Trouver le PlantUmlBox qui a ce nom
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
	
	// Principal
	public ShaclProperty(Resource constraint, List<ShaclBox> allBoxes, String lang) {
		
		String sLanguage = null;

		this.setpath(constraint);
		this.setdatatype(constraint);
		this.setnodeKind(constraint);
		this.setcardinality(constraint);
		this.setnode(constraint, allBoxes);
		this.setpattern(constraint);
		this.setclass(constraint);
		this.setclass_property(constraint, allBoxes); // Returne la valeur de TargetClass
		this.setdescription(constraint);
		this.setname(constraint, lang);			
		this.setShin(constraint);
		this.setShLanguage(this.getname(), this.getdescription());
		this.setShOrder(constraint);

	}

}
