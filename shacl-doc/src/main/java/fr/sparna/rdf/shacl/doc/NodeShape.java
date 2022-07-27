package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class NodeShape {

	private Resource nodeShape;
	
	protected Literal shPattern;
	protected Resource shTargetClass;
	protected String rdfsComment;
	protected String rdfsLabel;
	protected Integer shOrder;
	protected Resource shNodeKind;
	protected Boolean shClosed;
	protected RDFNode skosExample;
	protected List<Resource> rdfsSubClassOf;
	
	protected List<PropertyShape> properties = new ArrayList<>();
	
	
	
	public RDFNode getSkosExample() {
		return skosExample;
	}

	public void setSkosExample(RDFNode skosExample) {
		this.skosExample = skosExample;
	}

	public NodeShape(Resource nodeShape) {
		this.nodeShape = nodeShape;
	}

	public static List<RDFNode> asJavaList(Resource resource) {
		return (resource.as(RDFList.class)).asJavaList();
	}

	public Resource getNodeShape() {
		return nodeShape;
	}

	public List<PropertyShape> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyShape> properties) {
		this.properties = properties;
	}

	public Literal getShPattern() {
		return shPattern;
	}

	public void setShPattern(Literal shPattern) {
		this.shPattern = shPattern;
	}

	public Resource getShTargetClass() {
		return shTargetClass;
	}

	public void setShTargetClass(Resource shTargetClass) {
		this.shTargetClass = shTargetClass;
	}

	public String getRdfsComment() {
		return rdfsComment;
	}

	public void setRdfsComment(String rdfsComment) {
		this.rdfsComment = rdfsComment;
	}

	public String getRdfsLabel() {
		return rdfsLabel;
	}

	public void setRdfsLabel(String rdfsLabel) {
		this.rdfsLabel = rdfsLabel;
	}

	public Integer getShOrder() {
		return shOrder;
	}

	public void setShOrder(Integer shOrder) {
		this.shOrder = shOrder;
	}

	public Resource getShNodeKind() {
		return shNodeKind;
	}

	public void setShNodeKind(Resource shNodeKind) {
		this.shNodeKind = shNodeKind;
	}

	public Boolean getShClosed() {
		return shClosed;
	}

	public void setShClosed(Boolean shClosed) {
		this.shClosed = shClosed;
	}
	
	public String getShortForm() {
		return this.getNodeShape().getModel().shortForm(this.getNodeShape().getURI());
	}
	
	public String getLocalName() {
		return this.getNodeShape().getLocalName();
	}

	public List<Resource> getRdfsSubClassOf() {
		return rdfsSubClassOf;
	}

	public void setRdfsSubClassOf(List<Resource> rdfsSubClassOf) {
		this.rdfsSubClassOf = rdfsSubClassOf;
	}
	
}