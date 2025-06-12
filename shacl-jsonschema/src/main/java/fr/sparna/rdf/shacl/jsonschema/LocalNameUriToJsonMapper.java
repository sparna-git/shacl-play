package fr.sparna.rdf.shacl.jsonschema;

import org.apache.jena.rdf.model.Resource;

public class LocalNameUriToJsonMapper implements UriToJsonMapper {

    @Override
    public String mapToJson(Resource uri) {
        return uri.getLocalName();
    }
    
    @Override
    public String mapToPrefix(Resource uri) {
    	return uri.getModel().shortForm(uri.getURI());
    }
    
} 