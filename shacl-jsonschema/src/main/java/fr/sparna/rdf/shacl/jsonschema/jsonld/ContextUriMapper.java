package fr.sparna.rdf.shacl.jsonschema.jsonld;

import java.util.Set;

import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.shacl.PropertyShape;
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
    public String mapPropertyURI(
        Resource property,
        boolean isIriProperty,
        String datatype,
        String language
    ) {

        String contextMapping;
        try {
            contextMapping = contextWrapper.readTermForProperty(
                property.getURI(),
                isIriProperty,
                datatype,
                language
            );
        } catch (JsonLdException e) {
            e.printStackTrace();
            return property.getLocalName();
        }

        if(!contextMapping.equals(property.getURI())) {
            // Otherwise, returns the context mapping
            return contextMapping;
        } else {
            // If the context mapping is the same as the URI, reads the shortname annotation from the property shape
            Resource path = new PropertyShape(property).getShPath().get().asResource();
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
            return property.getLocalName();
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
    
} 