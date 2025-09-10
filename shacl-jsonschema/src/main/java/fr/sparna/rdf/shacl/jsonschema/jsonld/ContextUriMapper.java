package fr.sparna.rdf.shacl.jsonschema.jsonld;

import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.SH;
import fr.sparna.rdf.shacl.jsonschema.ShaclReadingUtils;
import jakarta.json.JsonValue;

public class ContextUriMapper implements UriToJsonMapper {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());


	private ProbingJsonLdContextWrapper contextWrapper;

    public ContextUriMapper(JsonValue context) {
        this.contextWrapper = new ProbingJsonLdContextWrapper(context);
    }

    @Override
    public Triple<String,Boolean,Boolean> mapPath(
        Resource path,
        boolean isIriProperty,
        Resource datatype,
        String language
    ) {
        Triple<String,Boolean,Boolean> contextMapping = null;
        
        try {
            if(path.hasProperty(SH.inversePath)) {
                contextMapping = contextWrapper.testProperty(
                    path.getRequiredProperty(SH.inversePath).getResource().getURI(),
                    isIriProperty,
                    // true for inverse
                    true,
                    (datatype != null)?datatype.getURI():null,
                    language
                );
            } else if(path.isURIResource()) {
                contextMapping = contextWrapper.testProperty(
                    path.getURI(),
                    isIriProperty,
                    false,
                    (datatype != null)?datatype.getURI():null,
                    language
                );
            } 
        } catch (JsonLdException e) {
            e.printStackTrace();
        }
        

        if(contextMapping != null && !contextMapping.getLeft().equals(path.getURI())) {
            // Otherwise, returns the context mapping
            return contextMapping;
        } else {
            // If the context mapping is the same as the URI, reads the shortname annotation from the property shape
			Set<String> shortnames = ShaclReadingUtils.findShortNamesOfPath(path);
            if (shortnames.size() == 1) {
                // If there is a single shortname, returns it
                return Triple.of(shortnames.iterator().next(),false,false);
            } else if( shortnames.size() > 1) {
                String term = shortnames.iterator().next();
                // If there are multiple shortnames, returns the first one
                log.warn("Found multiple shortnames for path "+path+", will use only one : '"+term+"'");
                return Triple.of(term,false,false);
            }
        }

        // If there are : exception in context mapping, or no context mapping, or no shortname, returns the URI
        if(path.isURIResource()) {
            return Triple.of(path.getURI(),false,false);
        } else {
            // the path is not a URI resource (it is a property path), we don't know what to return
            return null;
        }
    }
    
    @Override
    public String mapValueURI(Resource uri) {
    	String contextMapping;
        try {
            contextMapping = contextWrapper.readTermFromValue(uri.getURI());
        } catch (JsonLdException e) {
            e.printStackTrace();
            return uri.getModel().shortForm(uri.getURI());
        }

        if(!contextMapping.equals(uri.getURI())) {
            // Otherwise, returns the context mapping
            return contextMapping;
        } else {
            // If the context mapping is the same as the URI, returns the full URI
            // do NOT use the local name from the SHACL specification, as we should rely only on the context
            return uri.getURI();
        }
    }

    @Override
    public String mapUriPatternToJsonPattern(String uriPattern) {
        try {
            return contextWrapper.simplifyPattern(uriPattern);
        } catch (JsonLdException e) {
            e.printStackTrace();
            return uriPattern;
        }
    }
    
} 