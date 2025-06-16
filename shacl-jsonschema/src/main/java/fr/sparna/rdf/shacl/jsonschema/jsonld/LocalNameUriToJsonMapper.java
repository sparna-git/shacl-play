package fr.sparna.rdf.shacl.jsonschema.jsonld;

import org.apache.jena.rdf.model.Resource;

public class LocalNameUriToJsonMapper implements UriToJsonMapper {

    @Override
    public String mapPropertyURI(
        Resource property,
        boolean isIriProperty,
        String datatype,
        String language
    ) {
        return property.getLocalName();
    }
    
    @Override
    public String mapValueURI(Resource uri) {
    	return uri.getModel().shortForm(uri.getURI());
    }
    
} 