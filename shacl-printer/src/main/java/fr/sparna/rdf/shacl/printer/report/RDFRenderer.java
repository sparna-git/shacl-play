package fr.sparna.rdf.shacl.printer.report;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.shacl.SH;

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
	
		if(isShaclPropertyPath(r)) {
			return renderShaclPropertyPath(r);
		} else {
			return r.getId().getLabelString();			
		}

	}

	public static String renderShaclPropertyPath(Resource r) {
		if(r == null) return "";
	
		if(r.isURIResource()) {
			return renderUri(r);
		} else if(r.hasProperty(SH.alternativePath)) {
			Resource alternatives = r.getPropertyResourceValue(SH.alternativePath);
			RDFList rdfList = alternatives.as( RDFList.class );
			List<RDFNode> pathElements = rdfList.asJavaList();
			return pathElements.stream().map(p -> renderShaclPropertyPath((Resource)p)).collect(Collectors.joining("|"));
		} else if(r.hasProperty(SH.inversePath)) {
			Resource value = r.getPropertyResourceValue(SH.inversePath);
			if(value.isURIResource()) {
				return "^"+renderShaclPropertyPath(value);
			} else {
				return "^("+renderShaclPropertyPath(value)+")";
			}
		} else if(r.canAs( RDFList.class )) {
			RDFList rdfList = r.as( RDFList.class );
			List<RDFNode> pathElements = rdfList.asJavaList();
			return pathElements.stream().map(p -> {
				return renderShaclPropertyPath((Resource)p);
			}).collect(Collectors.joining("/"));
		} else {
			return "Unsupported path";
		}
		
	}
	
	
	public static boolean isShaclPropertyPath(Resource r) {
		if(
				r.hasProperty(SH.alternativePath)
				||
				r.hasProperty(SH.inversePath)
				||
				r.canAs( RDFList.class )
		) {
			return true;
		} else {
			return false;
		}
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
