package fr.sparna.rdf.jena.shacl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;

/**
 * A SHACL Property Path, as defined in https://www.w3.org/TR/shacl/#property-paths
 */
public class PropertyPath {
    
    protected Resource resource;
    
    public PropertyPath(Resource r) {
        this.resource = r;
    }

	/**
	 * @return the underlying resource
	 */
	public Resource getResource() {
		return resource;
	}

    public boolean isInverse() {
        return resource.hasProperty(SHACLM.inversePath);
    }

    public Resource getShInversePath() {
        if(this.isInverse()) {
            return resource.getPropertyResourceValue(SHACLM.inversePath);
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
		if(resource == null) result = "";
		
		if(resource.canAs(RDFList.class)) {
			List<RDFNode> l = resource.as(RDFList.class).asJavaList();
			result = l.stream().map(i -> new PropertyPath(i.asResource()).renderSparqlPropertyPath(usePrefixes)).collect(Collectors.joining("/"));
		} else if(resource.hasProperty(SHACLM.alternativePath)) {
			Resource alternatives = resource.getPropertyResourceValue(SHACLM.alternativePath);
			RDFList rdfList = alternatives.as( RDFList.class );
			List<RDFNode> pathElements = rdfList.asJavaList();
			result = pathElements.stream().map(p -> new PropertyPath((Resource)p).renderSparqlPropertyPath(usePrefixes)).collect(Collectors.joining("|"));
		} else if(resource.hasProperty(SHACLM.inversePath)) {
			Resource value = resource.getPropertyResourceValue(SHACLM.inversePath);
			if(value.isURIResource()) {
				result = "^"+new PropertyPath(value).renderSparqlPropertyPath(usePrefixes);
			}
			else {
				result = "^("+new PropertyPath(value).renderSparqlPropertyPath(usePrefixes)+")";
			}
		} else if(resource.hasProperty(SHACLM.zeroOrMorePath)) {
			Resource value = resource.getPropertyResourceValue(SHACLM.zeroOrMorePath);
			if(value.isURIResource()) {
				result = new PropertyPath(value).renderSparqlPropertyPath(usePrefixes)+"*";
			}
			else {
				result = "("+new PropertyPath(value).renderSparqlPropertyPath(usePrefixes)+")*";
			}
		} else if(resource.hasProperty(SHACLM.oneOrMorePath)) {
			Resource value = resource.getPropertyResourceValue(SHACLM.oneOrMorePath);
			if(value.isURIResource()) {
				result = new PropertyPath(value).renderSparqlPropertyPath(usePrefixes)+"+";
			}
			else {
				result = "("+new PropertyPath(value).renderSparqlPropertyPath(usePrefixes)+")+";
			}
		} else if(resource.isURIResource()) {
			// if we asked for prefixes, use the short form, otherwise use a complete URI
			result = (usePrefixes)?resource.getModel().shortForm(resource.getURI()):"<"+resource.getURI()+">";
		}

		return result;
	}
}
