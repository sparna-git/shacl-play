package fr.sparna.rdf.shacl.jsonschema.jsonld;

import java.util.Set;

import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.SH;
import fr.sparna.rdf.shacl.jsonschema.ShaclReadingUtils;
import jakarta.json.JsonValue;

public class ContextUriMapper implements UriToJsonMapper {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private JsonValue context;
	private ProbingJsonLdContextWrapper contextWrapper;

    public ContextUriMapper(JsonValue context) {
        this.context = context;
        this.contextWrapper = new ProbingJsonLdContextWrapper(context);
    }

    @Override
    public String mapPath(
        Resource path,
        boolean isIriProperty,
        Resource datatype,
        String language
    ) {

       

        String contextMapping = null;
        
        try {
            if(path.hasProperty(SH.inversePath)) {
                contextMapping = contextWrapper.readTermForProperty(
                    path.getRequiredProperty(SH.inversePath).getResource().getURI(),
                    isIriProperty,
                    // true for inverse
                    true,
                    (datatype != null)?datatype.getURI():null,
                    language
                );
            } else if(path.isURIResource()) {
                contextMapping = contextWrapper.readTermForProperty(
                    path.getURI(),
                    isIriProperty,
                    false,
                    (datatype != null)?datatype.getURI():null,
                    language
                );
            } 
        } catch (JsonLdException e) {
            e.printStackTrace();
            return path.getLocalName();
        }
        

        if(contextMapping != null && !contextMapping.equals(path.getURI())) {
            // Otherwise, returns the context mapping
            return contextMapping;
        } else {
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
            // If the context mapping is the same as the URI, returns the short form of the URI
            return uri.getModel().shortForm(uri.getURI());
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