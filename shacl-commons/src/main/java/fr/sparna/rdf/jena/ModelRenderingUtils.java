package fr.sparna.rdf.jena;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import fr.sparna.rdf.shacl.SH;

public class ModelRenderingUtils {


	public static String render(List<? extends RDFNode> list, boolean plainString) {
		if(list == null || list.size() == 0) {
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
		} else if(node.canAs(RDFList.class)) {
			// recursive down the lists
			return render(node.as(RDFList.class).asJavaList(), plainString);
		} else if(node.isAnon()) {
			return node.toString();
		} else if(node.isLiteral()) {
			// if we asked for a plain string, just return the literal string
			if(plainString) {				
				
				try {
					if(node.asLiteral().getDatatypeURI().equals(XSD.date.getURI())) {
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");					
						Date date = formatter.parse(node.asLiteral().getLexicalForm());
						return formatter.format(date);
					} else if (node.asLiteral().getDatatypeURI().equals(XSD.dateTime.getURI())) {
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = formatter.parse(node.asLiteral().getLexicalForm());
						return outputformatter.format(date);
					} else {
						return node.asLiteral().getLexicalForm();
					}
				} catch (ParseException e) {
					e.printStackTrace();
					node.asLiteral().getLexicalForm();
				}
			}
			
			if (node.asLiteral().getDatatype() != null && !node.asLiteral().getDatatypeURI().equals(RDF.langString.getURI())) {
				if(!node.asLiteral().getDatatypeURI().equals(XSD.xstring.getURI())) {
					// nicely prints datatypes with their short form
					return "\"" + node.asLiteral().getLexicalForm() + "\"<sup>^^"
							+ node.getModel().shortForm(node.asLiteral().getDatatype().getURI())+"</sup>";
				} else {
					// if datatype is xsd:string, don't print it explicitely
					return "\"" + node.asLiteral().getLexicalForm() + "\"";
				}
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

	public static String renderSparqlPropertyPath(Resource r) {
		// by defalt, render the property path without prefixes
		return renderSparqlPropertyPath(r, true);
	}

	public static String renderSparqlPropertyPath(Resource r, boolean usePrefixes) {
		if(r == null) return "";
		
		if(r.isURIResource()) {
			// if we asked for prefixes, use the short form, otherwise use a complete URI
			return (usePrefixes)?r.getModel().shortForm(r.getURI()):"<"+r.getURI()+">";
		} else if(r.canAs(RDFList.class)) {
			List<RDFNode> l = r.as(RDFList.class).asJavaList();
			return l.stream().map(i -> renderSparqlPropertyPath(i.asResource(),usePrefixes)).collect(Collectors.joining("/"));
		} else if(r.hasProperty(SHACLM.alternativePath)) {
			Resource alternatives = r.getPropertyResourceValue(SHACLM.alternativePath);
			RDFList rdfList = alternatives.as( RDFList.class );
			List<RDFNode> pathElements = rdfList.asJavaList();
			return pathElements.stream().map(p -> renderSparqlPropertyPath((Resource)p,usePrefixes)).collect(Collectors.joining("|"));
		} else if(r.hasProperty(SHACLM.inversePath)) {
			Resource value = r.getPropertyResourceValue(SHACLM.inversePath);
			if(value.isURIResource()) {
				return "^"+renderSparqlPropertyPath(value,usePrefixes);
			}
			else {
				return "^("+renderSparqlPropertyPath(value,usePrefixes)+")";
			}
		} else if(r.hasProperty(SHACLM.zeroOrMorePath)) {
			Resource value = r.getPropertyResourceValue(SHACLM.zeroOrMorePath);
			if(value.isURIResource()) {
				return renderSparqlPropertyPath(value,usePrefixes)+"*";
			}
			else {
				return "("+renderSparqlPropertyPath(value,usePrefixes)+")*";
			}
		} else if(r.hasProperty(SHACLM.oneOrMorePath)) {
			Resource value = r.getPropertyResourceValue(SHACLM.oneOrMorePath);
			if(value.isURIResource()) {
				return renderSparqlPropertyPath(value,usePrefixes)+"+";
			}
			else {
				return "("+renderSparqlPropertyPath(value,usePrefixes)+")+";
			}
		} else {
			return null;
		}
	}
	
}
