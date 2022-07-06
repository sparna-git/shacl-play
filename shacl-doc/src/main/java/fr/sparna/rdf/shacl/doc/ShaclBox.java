package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class ShaclBox {

	private Resource nodeShape;
	protected List<ShaclProperty> properties = new ArrayList<>();
	protected String shPattern;
	protected Resource shTargetClass;
	protected String rdfsComment;
	protected String rdfsLabel;
	protected Integer shOrder;
	protected String shNodeKind;
	protected Boolean shClosed;
	protected String skosExample;
	
	
	public String getSkosExample() {
		return skosExample;
	}

	public void setSkosExample(String skosExample) {
		this.skosExample = skosExample;
	}

	public ShaclBox(Resource nodeShape) {
		this.nodeShape = nodeShape;
	}

	public static List<RDFNode> asJavaList(Resource resource) {
		return (resource.as(RDFList.class)).asJavaList();
	}

	public Resource getNodeShape() {
		return nodeShape;
	}

	public List<ShaclProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<ShaclProperty> properties) {
		this.properties = properties;
	}

	public String getShPattern() {
		return shPattern;
	}

	public void setShPattern(String shPattern) {
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

	public String getShNodeKind() {
		return shNodeKind;
	}

	public void setShNodeKind(String shNodeKind) {
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

}