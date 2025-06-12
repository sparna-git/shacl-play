package fr.sparna.jsonschema;

import org.apache.jena.rdf.model.Resource;

/**
 * Maps a Resource to a JSON key - this can typically be by taking the localName of the URI
 */
public interface UriToJsonMapper {
    
    public String mapToJson(Resource uri);

	public String mapToPrefix(Resource uri);

}