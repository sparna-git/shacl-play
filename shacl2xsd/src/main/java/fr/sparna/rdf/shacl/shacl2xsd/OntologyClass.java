package fr.sparna.rdf.shacl.shacl2xsd;

public class OntologyClass {
	
	protected String ClassName;
	protected String CommentRDFS;
	protected String subClassOfRDFS;
	protected String commentObjectProperty;
	
	
	public String getCommentObjectProperty() {
		return commentObjectProperty;
	}
	public void setCommentObjectProperty(String commentObjectProperty) {
		this.commentObjectProperty = commentObjectProperty;
	}
	public String getSubClassOfRDFS() {
		return subClassOfRDFS;
	}
	public void setSubClassOfRDFS(String subClassOfRDFS) {
		this.subClassOfRDFS = subClassOfRDFS;
	}
	public String getClassName() {
		return ClassName;
	}
	public void setClassName(String className) {
		ClassName = className;
	}
	public String getCommentRDFS() {
		return CommentRDFS;
	}
	public void setCommentRDFS(String commentRDFS) {
		CommentRDFS = commentRDFS;
	}
	
	

}
