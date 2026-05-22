package fr.sparna.rdf.shacl.jsonschema.jsonld;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.jena.rdf.model.Resource;


public interface UriToJsonMapper {
    
    /**
     * Maps a property URI to a JSON key - this can typically be by taking the localName of the URI
     */
    public Triple<String,Boolean,Boolean> mapPath(
        Resource property,
        boolean isIriProperty,
        Resource datatype,
        String language
    );

    /**
     * Maps a value URI to a JSON key - this can typically be by taking the localName of the URI
     * propertyUri is optional, but can be used when the context contains a local context for a given property
     */
    public String mapValueURI(Resource uri, String propertyUri, boolean reverse);

    /**
     * Maps a URI pattern to a JSON pattern, typically by removing the context base from it, if any
     * propertyUri is optional, but can be used when the context contains a local context for a given property
     * reverse indicates whether the propertyUri is actually an inverse path in the context
     */
    public String mapUriPatternToJsonPattern(String uriPattern, String propertyUri, boolean reverse);

}