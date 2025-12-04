package fr.sparna.rdf.jena.shacl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;

public class PropertyPath {
    
    protected Resource path;
    
    public PropertyPath(Resource r) {
        this.path = r;
    }

    public boolean isInverse() {
        return path.hasProperty(SHACLM.inversePath);
    }

    public Resource getShInversePath() {
        if(this.isInverse()) {
            return path.getPropertyResourceValue(SHACLM.inversePath);
        } else {
            return null;
        }
    }

    	/**
	 * Renders the  provided SHACL Property path as a SPARQL property path syntax, using prefixed URIs.
	 * 
	 * @param r the SHACL property path to render in SPARQL
	 * @return
	 */
	public String renderSparqlPropertyPath() {
		// by default, render the property path without prefixes
		return renderSparqlPropertyPath(true);
	}

	/**
	 * Renders the  provided SHACL Property path as a SPARQL property path syntax. If usePrefixes if true, will use prefixed URIs, otherwise will use full URIs.
	 * 
	 * @param r the SHACL property path to render in SPARQL
	 * @param usePrefixes true to use prefixes, false to use full URIs 
	 * @return
	 */
	public String renderSparqlPropertyPath(boolean usePrefixes) {
		String result = null;
		if(path == null) result = "";
		
		if(path.canAs(RDFList.class)) {
			List<RDFNode> l = path.as(RDFList.class).asJavaList();
			result = l.stream().map(i -> new PropertyPath(i.asResource()).renderSparqlPropertyPath(usePrefixes)).collect(Collectors.joining("/"));
		} else if(path.hasProperty(SHACLM.alternativePath)) {
			Resource alternatives = path.getPropertyResourceValue(SHACLM.alternativePath);
			RDFList rdfList = alternatives.as( RDFList.class );
			List<RDFNode> pathElements = rdfList.asJavaList();
			result = pathElements.stream().map(p -> new PropertyPath((Resource)p).renderSparqlPropertyPath(usePrefixes)).collect(Collectors.joining("|"));
		} else if(path.hasProperty(SHACLM.inversePath)) {
			Resource value = path.getPropertyResourceValue(SHACLM.inversePath);
			if(value.isURIResource()) {
				result = "^"+new PropertyPath(value).renderSparqlPropertyPath(usePrefixes);
			}
			else {
				result = "^("+new PropertyPath(value).renderSparqlPropertyPath(usePrefixes)+")";
			}
		} else if(path.hasProperty(SHACLM.zeroOrMorePath)) {
			Resource value = path.getPropertyResourceValue(SHACLM.zeroOrMorePath);
			if(value.isURIResource()) {
				result = new PropertyPath(value).renderSparqlPropertyPath(usePrefixes)+"*";
			}
			else {
				result = "("+new PropertyPath(value).renderSparqlPropertyPath(usePrefixes)+")*";
			}
		} else if(path.hasProperty(SHACLM.oneOrMorePath)) {
			Resource value = path.getPropertyResourceValue(SHACLM.oneOrMorePath);
			if(value.isURIResource()) {
				result = new PropertyPath(value).renderSparqlPropertyPath(usePrefixes)+"+";
			}
			else {
				result = "("+new PropertyPath(value).renderSparqlPropertyPath(usePrefixes)+")+";
			}
		} else if(path.isURIResource()) {
			// if we asked for prefixes, use the short form, otherwise use a complete URI
			result = (usePrefixes)?path.getModel().shortForm(path.getURI()):"<"+path.getURI()+">";
		}

		return result;
	}
}
