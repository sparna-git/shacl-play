package fr.sparna.rdf.shacl.doc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.doc.model.Link;

public class ModelRenderingUtils {


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
			return r.getModel().shortForm(r.getURI());
		} else if(r.canAs(RDFList.class)) {
			List<RDFNode> l = r.as(RDFList.class).asJavaList();
			return l.stream().map(i -> renderShaclPropertyPath(i.asResource())).collect(Collectors.joining("/"));
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
			return null;
		}
	}
	
}
