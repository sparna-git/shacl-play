package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.listeners.NullListener;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.Metadata;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclBox {

	private Resource nodeShape;
	protected String nameshape;
	List<ShaclProperty> shacl_value = new ArrayList<>();
	List<ShaclPrefix> shacl_prefix = new ArrayList<>();
	protected String shpatternNodeShape;
	protected String nametargetclass;
	protected String rdfsComment;
	protected String rdfslabel;
	protected Integer shOrder;
	protected String shnodeKind;
	protected String shClose;
	
		
	public ShaclBox(Resource nodeShape, String lang) {
		this.nodeShape = nodeShape;
		this.setNameshape(nodeShape.getLocalName());
		this.setNametargetclass(nodeShape);
		this.setRdfsComment(nodeShape, lang);
		this.setRdfslabel(nodeShape, lang);
		this.setShpatternNodeShape(nodeShape);
		this.setShnodeKind(nodeShape);
		this.setShClose(nodeShape);
		this.setShOrder(nodeShape);
	}

	
	
	
	
	
	
	public List<ShaclPrefix> getShacl_prefix() {
		return shacl_prefix;
	}







	public void setShacl_prefix(List<ShaclPrefix> shacl_prefix) {
		this.shacl_prefix = shacl_prefix;
	}







	public String getShnodeKind() {
		return shnodeKind;
	}

	public void setShnodeKind(Resource nodeShape) {
		ConstraintValueReader constarget = new ConstraintValueReader();
		this.shnodeKind = constarget.readValueconstraint(nodeShape, SH.nodeKind,null);
	}

	public String getShClose() {
		return shClose;
	}

	public void setShClose(Resource nodeShape) {
		ConstraintValueReader constarget = new ConstraintValueReader();
		String value = null;
		value = constarget.readValueconstraint(nodeShape, SH.closed, null);
		this.shClose = value;
	}

	public Integer getShOrder() {
		return shOrder;
	}

	public void setShOrder(Resource nodeShape) {
		Integer value = 0;
		if(nodeShape.hasProperty(SH.order)) {
			value = Integer.parseInt(nodeShape.getProperty(SH.order).getLiteral().getString());
		}
		this.shOrder = value;
	}

	public String getShpatternNodeShape() {
		return shpatternNodeShape;
	}

	public void setShpatternNodeShape(Resource nodeShape) {
		String value = null;
		ConstraintValueReader constraintValueReader = new ConstraintValueReader();
		value = constraintValueReader.readValueconstraint(nodeShape, SH.pattern,null);
		this.shpatternNodeShape = value;
	}

	public String getRdfslabel() {
		return rdfslabel;
	}

	public void setRdfslabel(Resource nodeShape, String lang) {
		ConstraintValueReader constraintValue = new ConstraintValueReader();
		String value = null;
		value = constraintValue.readValueconstraint(nodeShape, RDFS.label, lang);
		this.rdfslabel = value;
	}

	public String getRdfsComment() {
		return rdfsComment;
	}

	public void setRdfsComment(Resource nodeShape, String lang) {
		ConstraintValueReader constraintValue = new ConstraintValueReader();
		String value = null;
		value = constraintValue.readValueconstraint(nodeShape, RDFS.comment, lang);
		this.rdfsComment = value;
	}

	public String getNameshape() {
		return nameshape;
	}

	public void setNameshape(String nameshape) {
		this.nameshape = nameshape;
	}

	public List<ShaclProperty> getProperties() {
		return shacl_value;
	}

	public String getNametargetclass() {
		return nametargetclass;
	}

	public void setNametargetclass(Resource nodeShape) {
		String value = null;
		ConstraintValueReader constargetclass = new ConstraintValueReader();
		value = constargetclass.readValueconstraint(nodeShape, SH.targetClass, null);
		this.nametargetclass = value;
	}

	public Resource getNodeShape() {
		return nodeShape;
	}

	public void setNodeShape(Resource nodeShape) {

		this.nodeShape = nodeShape;
	}

	public void readProperties(Resource nodeShape, List<ShaclBox> allBoxes, String lang) {
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<ShaclProperty> shacl_value = new ArrayList<>();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();

			if (object.isLiteral()) {
				System.out.println("Problem !");
			}

			Resource propertyShape = object.asResource();
			ShaclProperty plantvalueproperty = new ShaclProperty(propertyShape, allBoxes, lang);
			shacl_value.add(plantvalueproperty);			
		}
		
		List aPropertyOrder = new ArrayList<>();
		for(ShaclProperty shOrder : shacl_value) {
			aPropertyOrder.add(shOrder.getShOrder());
		}
		
		if(aPropertyOrder.contains(0)) {
			Collections.sort(shacl_value,Comparator.comparing(ShaclProperty::getname));
		}else {
			Collections.sort(shacl_value,Comparator.comparing(ShaclProperty::getShOrder));	
		}
		
		this.shacl_value = shacl_value;
	}
	
	
	
	public void readPropertiesPrefix(Resource nodeShape) {
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<ShaclPrefix> shaclprefix = new ArrayList<>();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			if (object.isLiteral()) {
				System.out.println("Problem !");
			}
			Resource propertyShapePrefix = object.asResource();
			ShaclPrefix plantvalueproperty = new ShaclPrefix(propertyShapePrefix);
			shaclprefix.add(plantvalueproperty);			
		}
		this.shacl_prefix = shaclprefix;
	}
	
	

	public static List<RDFNode> asJavaList(Resource resource) {
		return (resource.as(RDFList.class)).asJavaList();
	}

}