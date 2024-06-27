package fr.sparna.jsonschema;

import org.apache.jena.rdf.model.Resource;

public class LocalNameUriToJsonMapper implements UriToJsonMapper {

    @Override
    public String mapToJson(Resource uri) {
        return uri.getLocalName();
    }
    
} 