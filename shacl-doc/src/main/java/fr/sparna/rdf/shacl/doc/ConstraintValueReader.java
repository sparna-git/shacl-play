package fr.sparna.rdf.shacl.doc;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

public class ConstraintValueReader { 

	public String readValueconstraint(Resource constraint,Property property, String lang) {
		
		String value=null;
		try {
			if (constraint.hasProperty(property)) {
				if (constraint.getProperty(property).getObject().isURIResource()) {
					value = constraint.getModel().shortForm(constraint.getProperty(property).getResource().getURI());
				} else if (
						lang != null
						&&
						constraint.listProperties(property, lang).toList().size() > 0
				) {
					value = constraint.listProperties(property, lang).toList().stream()
							.map(s -> s.getObject().asLiteral().getLexicalForm())
							.collect(Collectors.joining(", "));
					// value = constraint.getProperty(property).getObject().asLiteral().toString();
				} else if (lang != null
						) {
					value = constraint.getProperty(property).getObject().asLiteral().toString();
				} else if (
						lang == null
						&&
						constraint.getProperty(property).getObject().isLiteral()
				) {
					value = constraint.getProperty(property).getObject().asLiteral().toString();
				} 
				else if (constraint.getProperty(property).getObject().isAnon()) {
					value = renderShaclPropertyPath(constraint.getProperty(property).getObject().asResource());
				}
			}
		} catch (Exception e) {
			value = null;
		}
		return value;
	}
	
//	public String readValueconstraint(Resource constraint,Property property) {
//		return readValueconstraint(constraint,property, null);
//	}
	
	public static String renderShaclPropertyPath(Resource r) {
		if(r == null) return "";
		
		if(r.isURIResource()) {
			return r.getLocalName();
		} else if(r.hasProperty(SH.alternativePath)) {
			Resource alternatives = r.getPropertyResourceValue(SH.alternativePath);
			RDFList rdfList = alternatives.as( RDFList.class );
			List<RDFNode> pathElements = rdfList.asJavaList();
			return pathElements.stream().map(p -> renderShaclPropertyPath((Resource)p)).collect(Collectors.joining("|"));
		} else if(r.hasProperty(SH.inversePath)) {
			Resource value = r.getPropertyResourceValue(SH.inversePath);
			if(value.isURIResource()) {
				return "^"+renderShaclPropertyPath(value);
			}
			else {
				return "^("+renderShaclPropertyPath(value)+")";
			}
		} else if(r.canAs( RDFList.class )) {
			RDFList rdfList = r.as( RDFList.class );
			List<RDFNode> pathElements = rdfList.asJavaList();
			return pathElements.stream().map(p -> {
				return renderShaclPropertyPath((Resource)p);}).collect(Collectors.joining("/"));
		} else {
			return "Unsupported path";
		}
	}	
	
	public static List<RDFNode> asJavaList( Resource resource )
	  {
	  return (resource.as( RDFList.class )).asJavaList();
	  }
	
	
	
}
