package fr.sparna.jsonschema;

import org.apache.jena.rdf.model.Resource;

public interface UriToJsonMapper {
    
    public String mapToJson(Resource uri);

}
