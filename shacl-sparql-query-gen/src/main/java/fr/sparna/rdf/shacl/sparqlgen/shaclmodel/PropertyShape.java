package fr.sparna.rdf.shacl.sparqlgen.shaclmodel;

import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class PropertyShape {
	
	protected Resource propertyShapeResource;
	
	// can be a URI or a SHACL property path
	protected Resource path;
	protected RDFNode hasValue;
	protected List<RDFNode> in;
	protected List<String> languageIn;
	protected NodeKind nodeKind;
	protected NodeShape node;
	protected List<NodeShape> orNode;
	protected Resource datatype;
	
	
	
	
	
	public PropertyShape(Resource propertyShapeResource) {
		super();
		this.propertyShapeResource = propertyShapeResource;
	}
	
	public boolean isPathURI() {
		return this.getPath().isURIResource();
	}
	
	public boolean hasSingleValue() {
		return this.getHasValue() != null || (this.getIn() != null && this.getIn().size() == 1);
	}
	
	public RDFNode getSingleValue() {
		if(this.getHasValue() != null) {
			return this.getHasValue();
		} else if(this.getIn() != null && this.getIn().size() == 1) {
			return this.getIn().get(0);
		} else {
			return null;
		}
	}
	
	public boolean requiresValues() {
		return this.getIn() != null && this.getIn().size() > 1;
	}
	
	
	public Resource getPath() {
		return path;
	}
	public void setPath(Resource path) {
		this.path = path;
	}
	public RDFNode getHasValue() {
		return hasValue;
	}
	public void setHasValue(RDFNode hasValue) {
		this.hasValue = hasValue;
	}
	public List<RDFNode> getIn() {
		return in;
	}
	public void setIn(List<RDFNode> in) {
		this.in = in;
	}
	public List<String> getLanguageIn() {
		return languageIn;
	}
	public void setLanguageIn(List<String> languageIn) {
		this.languageIn = languageIn;
	}
	public NodeKind getNodeKind() {
		return nodeKind;
	}
	public void setNodeKind(NodeKind nodeKind) {
		this.nodeKind = nodeKind;
	}
	public NodeShape getNode() {
		return node;
	}
	public void setNode(NodeShape node) {
		this.node = node;
	}
	public List<NodeShape> getOrNode() {
		return orNode;
	}
	public void setOrNode(List<NodeShape> orNode) {
		this.orNode = orNode;
	}
	public Resource getDatatype() {
		return datatype;
	}
	public void setDatatype(Resource datatype) {
		this.datatype = datatype;
	}
	public Resource getPropertyShapeResource() {
		return propertyShapeResource;
	}
	
	
	

}
