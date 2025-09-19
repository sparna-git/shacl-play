package fr.sparna.rdf.shacl.jsonschema.jsonld;

import org.apache.commons.lang3.tuple.Triple;

public interface JsonLdContextWrapper {
    
    /**
     * Maps a property URI to a JSON key by reading the context
     * @param propertyUri
     * @return How the property URI should be represented in JSON by interpreting the context (either a JSON term of the full URI if it is not mapped in the context)
     */
    public String readTermForProperty(String propertyUri, boolean isIriProperty, boolean isInverse, String datatype, String language)  throws JsonLdException;

    /**
     * Maps a value URI to a JSON key by reading the context
     * @param propertyUri an optional property URI, which can be used when the context contains a local context for a given property
     * @return How the value URI should be represented in JSON by interpreting the context (either a JSON term of the full URI if it is not mapped in the context)
     */
    public String readTermFromValue(String uri, String propertyUri)  throws JsonLdException;

    /**
     * Maps a property URI to a JSON key by reading the context, and also returns whether the property requires an array and whether it requires a language container
     * @param propertyUri
     * @return How the property URI should be represented in JSON by interpreting the context (either a JSON term of the full URI if it is not mapped in the context)
     */
    public Triple<String,Boolean,Boolean> testProperty(String propertyUri, boolean isIriProperty, boolean isInverse, String datatype, String language)  throws JsonLdException;

    /**
     * Simplifies a regex pattern in the context of a given property by removing the context base from it, if any.
     * propertyUri is optional
     * This is useful when the JSON-LD context contains a local context for a given property:
     * e.g.
     * {
     *   "@context": {
     *     "@base": "https://data.europarl.europa.eu/",
     *     "property": {
     *       "@id": "http://example.org/property",  
     *      "@type": "@id",
     *      "@context" : { "@base": "https://data.europarl.europa.eu/org/" }
     *   }
     *  }    
     */
    public String simplifyPattern(String regexPattern, String propertyUri) throws JsonLdException;

}