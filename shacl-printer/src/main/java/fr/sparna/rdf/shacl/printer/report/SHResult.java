package fr.sparna.rdf.shacl.printer.report;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import fr.sparna.rdf.shacl.SH;

public class SHResult {

	private Resource resource;
	
	public SHResult(Resource result) {
		super();
		this.resource = result;
	}

	String getMessage() {
		return SHResult.getPropertyAsString(resource, SH.resultMessage);
	}

	RDFNode getFocusNode() {
		return SHResult.getProperty(this.resource, SH.focusNode);
	}

	Resource getPath() {
		return getPropertyResourceValue(SH.resultPath);
	}

	Resource getSeverity() {
		return getPropertyResourceValue(SH.resultSeverity);
	}

	Resource getSourceConstraint() {
		return getPropertyResourceValue(SH.sourceConstraint);
	}

	Resource getSourceConstraintComponent() {
		return getPropertyResourceValue(SH.sourceConstraintComponent);
	}

	Resource getSourceShape() {
		return getPropertyResourceValue(SH.sourceShape);
	}

	RDFNode getValue() {
		return SHResult.getProperty(this.resource, SH.value);
	}

	public Resource getPropertyResourceValue(Property p)
	{
		StmtIterator it = this.resource.listProperties(p) ;
		try {
			while (it.hasNext())
			{
				RDFNode n = it.next().getObject() ;
				if (n.isResource()) return (Resource)n ;
			}
			return null ;
		} finally { it.close() ; }
	}
	
	public static RDFNode getProperty(Resource subject, Property predicate) {
		Statement s = subject.getProperty(predicate);
		if(s != null) {
			return s.getObject();
		}
		else {
			return null;
		}
	}

	public static String getPropertyAsString(Resource subject, Property predicate) {
		RDFNode object = getProperty(subject, predicate);
		if(object != null && object.isLiteral()) {
			return object.asLiteral().getString();
		} else {
			return null;
		}
	}
	
}
