package fr.sparna.rdf.shacl.jsonschema.jsonld;


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
     * Tests whether the property URI requires an array, that is if is using a "@container": "@set" in the context.
     * @param propertyUri
     * @return true if the property URI requires an array in the JSON-LD context, false otherwise.
     */
    public boolean requiresArray(String propertyUri, boolean isIriProperty, String datatype, String language)  throws JsonLdException;

    /**
     * Returns true if the property is set as @container: @language in the context.
     * @param propertyUri
     * @return
     * @throws JsonLdException
     */
    public boolean requiresContainerLanguage(String propertyUri)  throws JsonLdException;

    public String simplifyPattern(String regexPattern) throws JsonLdException;

}