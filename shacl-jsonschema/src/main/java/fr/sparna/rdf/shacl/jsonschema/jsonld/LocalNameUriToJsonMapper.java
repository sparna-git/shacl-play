package fr.sparna.rdf.shacl.jsonschema.jsonld;

import java.util.Set;

import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.jsonschema.ShaclReadingUtils;

public class LocalNameUriToJsonMapper implements UriToJsonMapper {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public String mapPath(
        Resource path,
        boolean isIriProperty,
        Resource datatype,
        String language
    ) {
        // If the context mapping is the same as the URI, reads the shortname annotation from the property shape
        Set<String> shortnames = ShaclReadingUtils.findShortNamesOfPath(path);
        if (shortnames.size() == 1) {
            // If there is a single shortname, returns it
            return shortnames.iterator().next();
        } else if( shortnames.size() > 1) {
            String term = shortnames.iterator().next();
            // If there are multiple shortnames, returns the first one
            log.warn("Found multiple shortnames for path "+path+", will use only one : '"+term+"'");
            return term;
        }
        // If there are no shortnames, returns the local name of the property
        if(path.isURIResource()) {
            return path.getLocalName();
        } else {
            return null;
        }
    }
    
    @Override
    public String mapValueURI(Resource uri) {
    	return uri.getModel().shortForm(uri.getURI());
    }

    @Override
    public String mapUriPatternToJsonPattern(String uriPattern) {
        return uriPattern;
    }
    
} 