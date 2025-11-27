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

import fr.sparna.rdf.jena.shacl.PropertyPath;
import fr.sparna.rdf.shacl.SH;

public class ModelRenderingUtils {

	/**
	 * Render a list of RDFNode as a string, in plain text
	 * @param list
	 * @return
	 */
	public static String render(List<? extends RDFNode> list) {
		return ModelRenderingUtils.render(list, true);
	}
	
	/**
	 * Render an RDFNode as a String, in plain text
	 * 
	 * @param node
	 * @return
	 */
	public static String render(RDFNode node) {
		return ModelRenderingUtils.render(node, true);
	}

	/**
	 * Render a list of RDFNode as a comma-separated string. If plainString is true, make it a plainString, otherwise uses HTML markup to display e.g. datatypes and languages in "sup" tags
	 * @param list the list of RDFNode to be displayed
	 * @param plainString true to retrieve a plainString, false to retrieve a pice of HTML
	 * @return
	 */
	public static String render(List<? extends RDFNode> list, boolean plainString) {
		if(list == null || list.size() == 0) {
			return null;
		}
		
		return list.stream().map(item -> {
			return render(item, plainString);
		}).collect(Collectors.joining(", "));
	}
	
	/**
	 * Render an RDFNode as a String. If plainString is true, make it a plainString, otherwise uses HTML markup to display e.g. datatypes and languages in "sup" tags
	 * 
	 * @param node The node to be renderd as String
	 * @param plainString true to retrieve a plainString, false to retrieve a pice of HTML
	 * @return
	 */
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

	/**
	 * Renders the  provided SHACL Property path as a SPARQL property path syntax, using prefixed URIs.
	 * @deprecated use PropertyPath.renderSparqlPropertyPath instead 
	 * @param r the SHACL property path to render in SPARQL
	 * @return
	 */
	public static String renderSparqlPropertyPath(Resource r) {
		return new PropertyPath(r).renderSparqlPropertyPath();
	}

	/**
	 * Renders the  provided SHACL Property path as a SPARQL property path syntax. If usePrefixes if true, will use prefixed URIs, otherwise will use full URIs.
	 * 
	 * @deprecated use PropertyPath.renderSparqlPropertyPath instead
	 * @param r the SHACL property path to render in SPARQL
	 * @param usePrefixes true to use prefixes, false to use full URIs 
	 * @return
	 */
	public static String renderSparqlPropertyPath(Resource r, boolean usePrefixes) {
		return new PropertyPath(r).renderSparqlPropertyPath(usePrefixes);
	}
	
}
