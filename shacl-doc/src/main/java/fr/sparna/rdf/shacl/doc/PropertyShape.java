package fr.sparna.rdf.shacl.doc;

import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.shacl.doc.read.PropertyShapeDocumentationBuilder;

public class PropertyShape {

	private Resource resource;
	
	// can be a URI or a blank node corresponding to a property path
	protected Resource shPath;

	protected Resource shDatatype;
	protected Resource shNodeKind;
	protected Integer shMinCount;
	protected Integer shMaxCount;
	// currently not used
	protected Literal shPattern;
	protected Resource shNode;
	protected Resource shClass;
	protected List<Literal> shName;
	protected List<Literal> shDescription;
	protected List<RDFNode> shIn;
	protected Integer shOrder;
	protected RDFNode shValue;
	
	protected List<Resource> shOr;	
	
	public List<Resource> getShOr() {
		return shOr;
	}

	public void setShOr(List<Resource> shOr) {
		this.shOr = shOr;
	}

	public PropertyShape(Resource resource) {
		super();
		this.resource = resource;
	}
	
	public Resource getResource() {
		return resource;
	}
	public Resource getShPath() {
		return shPath;
	}

	public void setShPath(Resource shPath) {
		this.shPath = shPath;
	}

	public Resource getShDatatype() {
		return shDatatype;
	}
	public void setShDatatype(Resource shDatatype) {
		this.shDatatype = shDatatype;
	}
	public Resource getShNodeKind() {
		return shNodeKind;
	}
	public void setShNodeKind(Resource shNodeKind) {
		this.shNodeKind = shNodeKind;
	}
	
	public Integer getShMinCount() {
		return shMinCount;
	}

	public void setShMinCount(Integer shMinCount) {
		this.shMinCount = shMinCount;
	}

	public Integer getShMaxCount() {
		return shMaxCount;
	}

	public void setShMaxCount(Integer shMaxCount) {
		this.shMaxCount = shMaxCount;
	}

	// currently not used
	public Literal getShPattern() {
		return shPattern;
	}
	public void setShPattern(Literal shPattern) {
		this.shPattern = shPattern;
	}
	public Resource getShNode() {
		return shNode;
	}
	public void setShNode(Resource node) {
		this.shNode = node;
	}
	public Resource getShClass() {
		return shClass;
	}
	public void setShClass(Resource shClass) {
		this.shClass = shClass;
	}
	public List<Literal> getShName() {
		return shName;
	}
	public void setShName(List<Literal> shName) {
		this.shName = shName;
	}
	public List<Literal> getShDescription() {
		return shDescription;
	}
	public void setShDescription(List<Literal> shDescription) {
		this.shDescription = shDescription;
	}
	public List<RDFNode> getShIn() {
		return shIn;
	}
	public void setShIn(List<RDFNode> shIn) {
		this.shIn = shIn;
	}
	public Integer getShOrder() {
		return shOrder;
	}
	public void setShOrder(Integer shOrder) {
		this.shOrder = shOrder;
	}
	public RDFNode getShValue() {
		return shValue;
	}
	public void setShValue(RDFNode shValue) {
		this.shValue = shValue;
	}
	
	/**
	 * Returns the short form of the property or the property path already shortened
	 * @return
	 */
	public String getShPathAsString() {
		return (this.shPath.isURIResource())?PropertyShapeDocumentationBuilder.render(this.getShPath(), false):ConstraintValueReader.renderShaclPropertyPath(this.getShPath());
	}
	
	public String getShNameAsString() {
		return PropertyShapeDocumentationBuilder.render(this.getShName(), true);
	}
	
}
