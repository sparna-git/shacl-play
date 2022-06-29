package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class ShaclBox {

	private Resource nodeShape;
	List<ShaclProperty> shacl_value = new ArrayList<>();
	protected String shpatternNodeShape;
	protected String nametargetclass;
	protected String rdfsComment;
	protected String rdfslabel;
	protected Integer shOrder;
	protected String shnodeKind;
	protected Boolean shClose;
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

	public List<ShaclProperty> getShacl_value() {
		return shacl_value;
	}

	public void setShacl_value(List<ShaclProperty> shacl_value) {
		this.shacl_value = shacl_value;
	}

	public String getShpatternNodeShape() {
		return shpatternNodeShape;
	}

	public void setShpatternNodeShape(String shpatternNodeShape) {
		this.shpatternNodeShape = shpatternNodeShape;
	}

	public String getNametargetclass() {
		return nametargetclass;
	}

	public void setNametargetclass(String nametargetclass) {
		this.nametargetclass = nametargetclass;
	}

	public String getRdfsComment() {
		return rdfsComment;
	}

	public void setRdfsComment(String rdfsComment) {
		this.rdfsComment = rdfsComment;
	}

	public String getRdfslabel() {
		return rdfslabel;
	}

	public void setRdfslabel(String rdfslabel) {
		this.rdfslabel = rdfslabel;
	}

	public Integer getShOrder() {
		return shOrder;
	}

	public void setShOrder(Integer shOrder) {
		this.shOrder = shOrder;
	}

	public String getShnodeKind() {
		return shnodeKind;
	}

	public void setShnodeKind(String shnodeKind) {
		this.shnodeKind = shnodeKind;
	}

	public Boolean getShClose() {
		return shClose;
	}

	public void setShClose(Boolean shClose) {
		this.shClose = shClose;
	}
	
	public String getShortForm() {
		return this.getNodeShape().getModel().shortForm(this.getNodeShape().getURI());
	}
	
	public String getLocalName() {
		return this.getNodeShape().getLocalName();
	}

}