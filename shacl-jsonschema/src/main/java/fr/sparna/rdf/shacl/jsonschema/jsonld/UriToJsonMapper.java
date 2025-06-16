package fr.sparna.rdf.shacl.jsonschema.jsonld;

import org.apache.jena.rdf.model.Resource;


public interface UriToJsonMapper {
    
    /**
     * Maps a property URI to a JSON key - this can typically be by taking the localName of the URI
     */
    public String mapPropertyURI(
        Resource property,
        boolean isIriProperty,
        String datatype,
        String language
    );

    /**
     * Maps a value URI to a JSON key - this can typically be by taking the localName of the URI
     */
    public String mapValueURI(Resource uri);

}