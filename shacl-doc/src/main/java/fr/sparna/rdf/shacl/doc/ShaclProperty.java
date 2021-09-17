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

import fr.sparna.rdf.shacl.diagram.PlantUmlBox;

public class ShaclProperty {

	private Resource resource;
	
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
	protected String shOr;
	
	public String getShOr() {
		return shOr;
	}

	public void setShOr(String shOr) {
		this.shOr = shOr;
	}

	public ShaclProperty(Resource resource) {
		super();
		this.resource = resource;
	}
	
	public Resource getResource() {
		return resource;
	}



	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getNodeKind() {
		return nodeKind;
	}
	public void setNodeKind(String nodeKind) {
		this.nodeKind = nodeKind;
	}
	public String getCardinality() {
		return cardinality;
	}
	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getClass_node() {
		return class_node;
	}
	public void setClass_node(String class_node) {
		this.class_node = class_node;
	}
	public String getClass_property() {
		return class_property;
	}
	public void setClass_property(String class_property) {
		this.class_property = class_property;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getShin() {
		return shin;
	}
	public void setShin(String shin) {
		this.shin = shin;
	}
	public Integer getShOrder() {
		return shOrder;
	}
	public void setShOrder(Integer shOrder) {
		this.shOrder = shOrder;
	}
	public String getShValue() {
		return shValue;
	}
	public void setShValue(String shValue) {
		this.shValue = shValue;
	}
}
