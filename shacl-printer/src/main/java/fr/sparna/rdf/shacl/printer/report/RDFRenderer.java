package fr.sparna.rdf.shacl.printer.report;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class RDFRenderer {

	public static String renderRDFNode(RDFNode n) {
		if(n == null) return "";
		
		if(n.isURIResource()) {
			return renderUri(n.asResource());
		} else if(n.isAnon()) {
			return renderBlankNode(n.asResource());
		} else {
			return renderLiteral(n.asLiteral());
		}
	}
	
	public static String renderResource(Resource r) {
		if(r == null) return "";
		
		if(r.isURIResource()) {
			return renderUri(r);
		} else {
			return renderBlankNode(r);
		}
	}
	
	public static String renderUri(Resource r) {
		if(r == null) return "";
		
		return r.getModel().shortForm(r.getURI());
	}
	
	public static String renderBlankNode(Resource r) {
		if(r == null) return "";
		
		return r.getId().getLabelString();
	}
	
	public static String renderLiteral(Literal l) {
		if(l == null) return "";
		
		StringBuffer s = new StringBuffer();
		s.append("\"");
		s.append(l.getLexicalForm());
		s.append("\"");
		if(l.getLanguage() != null && !l.getLanguage().equals("")) {
			s.append("<sup>@"+l.getLanguage()+"</sup>");
		} else if(l.getDatatype() != null) {
			s.append("<sup>^^"+l.getModel().shortForm(l.getDatatypeURI())+"</sup>");
		}
		return s.toString();
	}
	
}
