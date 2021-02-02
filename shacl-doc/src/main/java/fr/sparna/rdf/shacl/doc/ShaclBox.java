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
	List<String> shaclpattern = new ArrayList<>();
	protected String nametargetclass;
	protected String packageName;
	protected String rdfsComment;
	protected String rdfslabel;

	public ShaclBox(Resource nodeShape) {
		this.nodeShape = nodeShape;
		this.setNameshape(nodeShape.getLocalName());
		this.setPackageName(nodeShape);
		this.setNametargetclass(nodeShape);
		this.setRdfsComment(nodeShape);
		this.setRdfslabel(nodeShape);
	}

	public String getRdfslabel() {
		return rdfslabel;
	}

	public void setRdfslabel(Resource nodeShape) {
		String value = null;
		List<String> Language = new ArrayList<>();
		Language.add("en");
		Language.add("fr");
		List<String> ProprieteRdf = new ArrayList<>();
		ProprieteRdf.add("RDFS.label");
		ProprieteRdf.add("RDFS.comment");
		Iterator<String> label_comment = ProprieteRdf.iterator();
		JenaUtil label = new JenaUtil();

		// value = JenaUtil.getStringProperty(nodeShape, RDFS.label);
		this.rdfslabel = value;
	}

	public String getRdfsComment() {
		return rdfsComment;
	}

	public void setRdfsComment(Resource nodeShape) {
		String value = null;
		value = JenaUtil.getStringProperty(nodeShape, RDFS.comment);
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

	public void readProperties(Resource nodeShape, List<ShaclBox> allBoxes) {

		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<ShaclProperty> shacl_value = new ArrayList<>();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();

			if (object.isLiteral()) {
				System.out.println("Problem !");
			}

			Resource propertyShape = object.asResource();
			ShaclProperty plantvalueproperty = new ShaclProperty(propertyShape, allBoxes);
			shacl_value.add(plantvalueproperty);
		}
		
		ConstraintValueReader constraintValueReader = new ConstraintValueReader();
		List<Statement> propertyStatementsunique = nodeShape.listProperties(SH.pattern).toList();
		for (Statement aPropertyStatement : propertyStatementsunique) {
			shaclpattern.add(aPropertyStatement.getObject().asLiteral().getString());
		}

		this.shacl_value = shacl_value;
	}
	
	

	public String getNametargetclass() {
		return nametargetclass;
	}

	public void setNametargetclass(Resource nodeShape) {
		ConstraintValueReader constargetclass = new ConstraintValueReader();
		this.nametargetclass = constargetclass.readValueconstraint(nodeShape, SH.targetClass);
	}

	public String getQualifiedName() {

		return packageName + "." + this.nameshape;
	}

	public Resource getNodeShape() {
		return nodeShape;
	}

	public void setNodeShape(Resource nodeShape) {

		this.nodeShape = nodeShape;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(Resource nodeShape) {
		String idpackage = "";
		try {
			idpackage = nodeShape
					.getProperty(nodeShape.getModel().createProperty("https://shacl-play.sparna.fr/ontology#package"))
					.getResource().getLocalName();
		} catch (Exception e) {
			idpackage = "";
		}
		this.packageName = idpackage;
	}

}