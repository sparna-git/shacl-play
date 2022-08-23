package fr.sparna.rdf.shacl.diagram;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.topbraid.shacl.vocabulary.SH;

public class ConstraintValueReader { 

	public String readValueconstraint(Resource constraint,Property property) {
		
		String value=null;
		try {
			if (constraint.hasProperty(property)) {
				if (constraint.getProperty(property).getObject().isURIResource()) {
					  //value = constraint.getProperty(property).getResource().getLocalName();
					  value = constraint.getProperty(property).getResource().getModel().shortForm(constraint.getProperty(property).getResource().getURI());
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
	
	public static List<Literal> readLiteralInLang(Resource constraint, Property property, String lang) {
		if (constraint.hasProperty(property)) {
			if (lang != null && constraint.listProperties(property, lang).toList().size() > 0) {
				return constraint.listProperties(property, lang).toList().stream()
						.map(s -> s.getObject().asLiteral())
						.collect(Collectors.toList());
			} else if(constraint.listProperties(property).toList().size() > 0) {
				// even if lang was provided, we still search the property with no language
				return constraint.listProperties(property).toList().stream()
						.map(s -> s.getObject().asLiteral())
						.collect(Collectors.toList());
			}
		}
		
		return null;
	}
	
	public static String readLiteralInLangAsString(Resource r, Property property, String lang) {
		return render(readLiteralInLang(r, property, lang), true);
	}
	
	public static String render(List<? extends RDFNode> list, boolean plainString) {
		if(list == null) {
			return null;
		}
		
		return list.stream().map(item -> {
			return render(item, plainString);
		}).collect(Collectors.joining(", "));
	}
	
	public static String render(RDFNode node, boolean plainString) {
		if(node == null) {
			return null;
		}
		
		if(node.isURIResource()) {
			return node.getModel().shortForm(node.asResource().getURI());
		} else if(node.isAnon()) {
			return node.toString();
		} else if(node.isLiteral()) {
			// if we asked for a plain string, just return the literal string
			if(plainString) {				
				return node.asLiteral().getLexicalForm();
			}
			
			if (node.asLiteral().getDatatype() != null && !node.asLiteral().getDatatypeURI().equals(RDF.langString.getURI())) {
				// nicely prints datatypes with their short form
				return "\"" + node.asLiteral().getLexicalForm() + "\"<sup>^^"
						+ node.getModel().shortForm(node.asLiteral().getDatatype().getURI())+"</sup>";
			} else if (node.asLiteral().getLanguage() != null) {
				return "\"" + node.asLiteral().getLexicalForm() + "\"<sup>@"
						+ node.asLiteral().getLanguage()+"</sup>";
			} else {
				return node.toString();
			}
		} else {
			// default, should never get there
			return node.toString();
		}
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
				return p.asResource().listProperties().nextStatement().getObject().asResource().getModel().shortForm(p.asResource().listProperties().nextStatement().getObject().asResource().getURI()); //.getLocalName();
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
