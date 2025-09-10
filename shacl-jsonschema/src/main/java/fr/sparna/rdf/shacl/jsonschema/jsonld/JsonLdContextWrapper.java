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
     * @param propertyUri
     * @return How the value URI should be represented in JSON by interpreting the context (either a JSON term of the full URI if it is not mapped in the context)
     */
    public String readTermFromValue(String uri)  throws JsonLdException;

    /**
     * Maps a property URI to a JSON key by reading the context, and also returns whether the property requires an array and whether it requires a language container
     * @param propertyUri
     * @return How the property URI should be represented in JSON by interpreting the context (either a JSON term of the full URI if it is not mapped in the context)
     */
    public Triple<String,Boolean,Boolean> testProperty(String propertyUri, boolean isIriProperty, boolean isInverse, String datatype, String language)  throws JsonLdException;


    public String simplifyPattern(String regexPattern) throws JsonLdException;

}