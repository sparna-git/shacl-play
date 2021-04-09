package fr.sparna.rdf.shacl.diagram;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.topbraid.shacl.vocabulary.SH;

public class ConstraintValueReader { 

	public String readValueconstraint(Resource constraint,Property property) {
		
		String value=null;
		try {
			if (constraint.hasProperty(property)) {
				if (constraint.getProperty(property).getObject().isURIResource()) {
					  value = constraint.getProperty(property).getResource().getLocalName();			
				}
				else if (constraint.getProperty(property).getObject().isLiteral()) {
					value = constraint.getProperty(property).getObject().asLiteral().getString();				
				} else if (constraint.getProperty(property).getObject().isAnon()) {
					value = renderShaclPropertyPath(constraint.getProperty(property).getObject().asResource());					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			value = null;
		}
		return value;
	}
	
	public String readValueconstraintAsShortForm(Resource constraint,Property property) {
		
		String value=null;
		try {
			if (constraint.hasProperty(property)) {
				if (constraint.getProperty(property).getObject().isURIResource()) {
					  value = constraint.getProperty(property).getResource().getModel().shortForm(constraint.getProperty(property).getResource().getURI());			
				} else {
					return readValueconstraint(constraint, property);
				}
			}
		} catch (Exception e) {
			value = null;
		}
		return value;
	}
	
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
			
			/*return pathElements.stream().map(p -> {
				return renderShaclPropertyPath((Resource)p);
				}).collect(Collectors.joining("/"));
			*/
			return pathElements.stream().map(p ->{
				return p.asResource().listProperties().nextStatement().getObject().asResource().getLocalName();
			}).collect(Collectors.joining(","));
			
			     
		} else if(r.hasProperty(SH.zeroOrMorePath)) {
			Resource value = r.getPropertyResourceValue(SH.zeroOrMorePath);
			if(value.isURIResource()) {
				return renderShaclPropertyPath(value)+"*";
			}
			else {
				return "("+renderShaclPropertyPath(value)+")*";
			}
		} else if(r.hasProperty(SH.oneOrMorePath)) {
			Resource value = r.getPropertyResourceValue(SH.oneOrMorePath);
			if(value.isURIResource()) {
				return renderShaclPropertyPath(value)+"+";
			}
			else {
				return "("+renderShaclPropertyPath(value)+")+";
			}
		} else {
			// if anonymous, return anonymous ID
			return r.toString();
		}
	}	
}
