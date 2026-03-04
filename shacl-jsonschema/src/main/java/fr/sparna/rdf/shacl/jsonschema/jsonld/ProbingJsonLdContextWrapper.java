package fr.sparna.rdf.shacl.jsonschema.jsonld;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;

import fr.sparna.rdf.shacl.jsonld.RegexUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;

public class ProbingJsonLdContextWrapper implements JsonLdContextWrapper {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final JsonValue context;

    public ProbingJsonLdContextWrapper(JsonValue context) {
        this.context = context;
    }

    @Override
    public String readTermForProperty(
        String propertyUri,
        boolean isIriProperty,
        boolean isInverse,
        String datatype,
        String language
    ) throws JsonLdException {
        log.trace("Probing JSON-LD context for property URI: {}", propertyUri);
        try {
            JsonObject probeDocument = prepareProbePropertyDocument(propertyUri, isIriProperty, isInverse, datatype, language);
            //debug the input and output            
            log.trace("Probe document: {}", probeDocument);
            JsonObject compactedDocument = doCompact(probeDocument, this.context);
            Entry<String, JsonValue> firstEntry = getFirstNonContextEntry(compactedDocument);
            if (firstEntry != null) {
                return firstEntry.getKey(); // Return the first non-context entry key
            } else {
                return propertyUri; // If no such entry exists, return the full URI
            }
        } catch (JsonLdError e) {
            throw new JsonLdException("Error compacting JSON-LD document for property URI: " + propertyUri, e);
        }
    }

    @Override
    public Triple<String,Boolean,Boolean> testProperty(
        String propertyUri,
        boolean isIriProperty,
        boolean isInverse,
        String datatype,
        String language
    ) throws JsonLdException {
        log.trace("Probing JSON-LD context for property URI: {}", propertyUri);
        try {
            JsonObject probeDocument = prepareProbePropertyDocument(propertyUri, isIriProperty, isInverse, datatype, language);
            //debug the input and output            
            log.trace("Probe document: {}", JsonUtils.prettyPrint(probeDocument));
            JsonObject compactedDocument = doCompact(probeDocument, this.context);
            Entry<String, JsonValue> firstEntry = getFirstNonContextEntry(compactedDocument);

            // If no such entry exists, default to the full URI
            String term = propertyUri;
            Boolean requiresArray = false;
            Boolean requiresContainerLanguage = false;

            if (firstEntry != null && !firstEntry.getKey().equals("@reverse")) {
                // exclude the case where the result contains a @reverse,
                // which indicates it was an inverse property path, but not mapped in the context

                term = firstEntry.getKey(); // Return the first non-context entry key
                requiresArray = firstEntry.getValue().getValueType() == JsonValue.ValueType.ARRAY; // Check if the value is an array
                
                // if the first entry is an object, and if it contains a single key that has 2 letters, then it is a language container
                if(firstEntry.getValue().getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject valueObject = firstEntry.getValue().asJsonObject();
                    if(valueObject.size() == 1) {
                        String key = valueObject.keySet().iterator().next();
                        if(key.length() == 2 && key.matches("[a-zA-Z]{2}")) {
                            requiresContainerLanguage = true; // It is a language container
                        }   
                    }
                }
            }

            return Triple.of(term, requiresArray, requiresContainerLanguage);

        } catch (JsonLdError e) {
            throw new JsonLdException("Error compacting JSON-LD document for property URI: " + propertyUri, e);
        }
    }

    @Override
    public String readTermFromValue(String uri, String propertyUri) throws JsonLdException {
        log.trace("Probing JSON-LD context for value URI: {} in the context of property URI: {}", uri, propertyUri);

        try {
            // first try with an @vocab to see if the value can be simplified based on the terms declared in the context
            JsonObject probeDocument = prepareProbeValueDocument(uri, propertyUri, true);
            log.debug("readTermFromValue Probe document before compaction: {}", JsonUtils.prettyPrint(probeDocument));
            JsonObject compactedDocument = doCompact(probeDocument, probeDocument.get("@context"));
            log.debug("readTermFromValue Probe document after compaction: {}", JsonUtils.prettyPrint(compactedDocument));
            Entry<String, JsonValue> firstEntry = getFirstNonContextEntry(compactedDocument);

            String finalResult = uri;

            if (firstEntry != null) {
                if(firstEntry.getValue().getValueType() == JsonValue.ValueType.STRING) {
                    // returns the string value
                    finalResult = firstEntry.getValue().toString().replaceAll("^\"|\"$", ""); // Remove quotes
                } else if(firstEntry.getValue().getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject valueObject = firstEntry.getValue().asJsonObject();
                    // read the first entry of the object, which should be the @id
                    JsonValue firstValue = valueObject.entrySet().iterator().next().getValue();
                    finalResult = firstValue.getValueType() == JsonValue.ValueType.STRING ? firstValue.toString().replaceAll("^\"|\"$", "") : null;                    
                }  else if (firstEntry.getValue().getValueType() == JsonValue.ValueType.ARRAY) {
                    // if the property in the context has @type:@id and @container:@set, we will have an array of string, we take the first one
                    JsonValue firstValue = firstEntry.getValue().asJsonArray().get(0);
                    finalResult = firstValue.getValueType() == JsonValue.ValueType.STRING ? firstValue.toString().replaceAll("^\"|\"$", "") : null;                    
                }
            }

            if(finalResult.equals(uri)) {
                // second try with an "@id" to test if the URI can be simplified with a @base declaration

                JsonObject probeDocument2 = prepareProbeValueDocument(uri, propertyUri, false);      
                log.trace("Probe document 2: {}", probeDocument2);
                JsonObject compactedDocument2 = doCompact(probeDocument2, probeDocument2.get("@context"));
                Entry<String, JsonValue> firstEntry2 = getFirstNonContextEntry(compactedDocument2);

                if (firstEntry2 != null) {
                    if(firstEntry2.getValue().getValueType() == JsonValue.ValueType.STRING) {
                        // returns the string value
                        finalResult = firstEntry2.getValue().toString().replaceAll("^\"|\"$", ""); // Remove quotes
                    } else if(firstEntry.getValue().getValueType() == JsonValue.ValueType.OBJECT) {
                        JsonObject valueObject = firstEntry.getValue().asJsonObject();
                        // read the first entry of the object, which should be the @id
                        JsonValue firstValue = valueObject.entrySet().iterator().next().getValue();
                        finalResult = firstValue.getValueType() == JsonValue.ValueType.STRING ? firstValue.toString().replaceAll("^\"|\"$", "") : null;                    
                    } else if (firstEntry.getValue().getValueType() == JsonValue.ValueType.ARRAY) {
                        // if the property in the context has @type:@id and @container:@set, we will have an array of string, we take the first one
                        JsonValue firstValue = firstEntry.getValue().asJsonArray().get(0);
                        finalResult = firstValue.getValueType() == JsonValue.ValueType.STRING ? firstValue.toString().replaceAll("^\"|\"$", "") : null;                    
                    }
                }
            }

            return finalResult;

        } catch (JsonLdError e) {
            throw new JsonLdException("Error compacting JSON-LD document for value URI: " + uri, e);
        }
    }

    @Override
    public String simplifyPattern(String regexPattern, String propertyUri) throws JsonLdException {
        log.trace("Probing JSON-LD context for regex: {} in the context of property: {}", regexPattern, propertyUri);
        // System.out.println("Probing JSON-LD context for regex: "+regexPattern);
        try {
            String testValue = RegexUtil.generateMatchingString(regexPattern);
            JsonObject probeDocument = prepareProbeRegex(testValue, propertyUri);
            
            log.trace("Probe document before compaction: {}", JsonUtils.prettyPrint(probeDocument));
            //debug the input and output            
            JsonObject compactedDocument = doCompact(probeDocument, probeDocument.get("@context"));
            log.trace("Probe document after compaction: {}", JsonUtils.prettyPrint(compactedDocument));
            
            Entry<String, JsonValue> firstEntry = getFirstNonContextEntry(compactedDocument);
            if (firstEntry != null) {
                String returnedValue = null;
                // read the @id of the JsonValue, which should be a string
                if(firstEntry.getValue().getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject valueObject = firstEntry.getValue().asJsonObject();
                    // read the first entry of the object, which should be the @id
                    JsonValue firstValue = valueObject.entrySet().iterator().next().getValue();
                    returnedValue = firstValue.getValueType() == JsonValue.ValueType.STRING ? firstValue.toString().replaceAll("^\"|\"$", "") : null;                    
                } else if (firstEntry.getValue().getValueType() == JsonValue.ValueType.STRING) {
                    // if the property in the context has @type:@id, we will have only a string
                    returnedValue = firstEntry.getValue().toString().replaceAll("^\"|\"$", "");
                } else if (firstEntry.getValue().getValueType() == JsonValue.ValueType.ARRAY) {
                    // if the property in the context has @type:@id and @container:@set, we will have an array of string, we take the first one
                    JsonValue firstValue = firstEntry.getValue().asJsonArray().get(0);
                    returnedValue = firstValue.getValueType() == JsonValue.ValueType.STRING ? firstValue.toString().replaceAll("^\"|\"$", "") : null;                    
                }

                if(returnedValue == null) {
                    log.trace("Cannot extract returned value from compacted JSON-LD");
                    return regexPattern; // If we cannot extract a returned value, return the original pattern
                }

                log.trace("Extracted returned value from compaction: {}", returnedValue);

                if(!(
                    returnedValue.length() < testValue.length() && testValue.endsWith(returnedValue)
                    )
                ) {
                    log.trace("No simplification applied for regex pattern: {}", regexPattern);
                    return regexPattern; // If the returned value is not a simplification of the test value, return the original pattern
                }

                // now we now that some simplification was applied in the context
                // most probably due to @base

                // determine the piece of the string that was removed
                String removed = testValue.substring(0, testValue.length() - returnedValue.length());
                // and return the simplified regex pattern
                String simplifiedPattern = regexPattern.replaceAll(removed, "");

                log.trace("Final simplified regex pattern: {}", simplifiedPattern);
                
                return simplifiedPattern;
            }
        } catch (JsonLdError e) {
            throw new JsonLdException("Error compacting JSON-LD document for probing regex: " + regexPattern, e);
        }
        return regexPattern;
    }

    private JsonObject prepareProbePropertyDocument(
        String propertyUri,
        boolean isIriProperty,
        boolean isInverse,
        String datatype,
        String language
    ) {
        // Prepare a JSON-LD document that probes the context for the given property URI
        // This method should create a document that includes the property URI and is ready for compaction
        
        if(propertyUri.equals(RDF.type.getURI())) {
            return Json.createObjectBuilder()
            .add("@type", "https://probe.org/test") // Add the property URI to the document
            // and the context
            .add("@context", context)
            .build();
        }

        JsonValue value = JsonValue.NULL; // Default value is null
        if(isIriProperty) {
            value = Json.createObjectBuilder()
                .add("@id", "https://probe.org/test") // Use @id for IRI properties
                .build();
        } else if (language != null) {
            value = Json.createObjectBuilder()
                .add("@language", language) // Use @language for language-tagged properties
                .add("@value", "probe")
                .build();
        } else if (datatype != null) {
            if (RDF.dtLangString.getURI().equals(datatype)) {
                value = Json.createObjectBuilder()
                .add("@language", "fr") // Use @language for language-tagged properties
                .add("@value", "probe")
                .build();

            } else {
                value = Json.createObjectBuilder()
                .add("@type", datatype) // Use @type for datatype properties
                .add("@value", "probe")
                .build();
            }
        } 

        JsonObject probeDocument;

        // create the object with the context + value, potentially @reverse
        if(isInverse) {            
            probeDocument = Json.createObjectBuilder()
                .add("@reverse", Json.createObjectBuilder().add(propertyUri, value).build())
                // and the context
                .add("@context", context)
                .build();
        } else {
            probeDocument = Json.createObjectBuilder()            
                .add(propertyUri, value) // Add the property URI to the document
                // and the context
                .add("@context", context)
                .build();
        }

        return probeDocument;
    }

    private JsonObject prepareProbeValueDocument(
        String valueUri,
        String propertyUri,
        boolean vocabTest
    ) {
        // Prepare a JSON-LD document that probes the context for the given values

        // prepare the complete @context, which is an array containing the input context, with an additionnal entry of @type = @vocab
        /*
         "@context": {
            "shaclplay" : {
                "@type":"@vocab",
                "@id":"https://example.com/foo"
            }
            }
         */
        JsonObject additionnalContext = Json.createObjectBuilder()
            .add("shaclplay", Json.createObjectBuilder()
                .add("@type", (vocabTest)?"@vocab":"@id")
                .add("@id", "https://shacl-play.sparna.fr/contextProbing"))
            .build();

        JsonValue completeContext;
        
        // if the provided context is already an array, and the additionnal context into it
        // otherwise, create a new array with the context and the additionnal context
        if (this.context.getValueType() == JsonValue.ValueType.ARRAY) {
            completeContext = Json.createArrayBuilder(this.context.asJsonArray()) // Add the original context
                .add(additionnalContext)
                .build();
        } else {
            completeContext = Json.createArrayBuilder()
                .add(this.context) // Add the original context
                .add(additionnalContext) // Add the additional context
                .build();
        }
                    
        JsonValue value = Json.createObjectBuilder()
                .add("@id", valueUri)
                .build();

        // create our complete JSON object
        JsonObjectBuilder probeDocumentBuilder = Json.createObjectBuilder();
        if(propertyUri == null) {
            probeDocumentBuilder.add("shaclplay", value); // Add the property URI to the document
        } else if(propertyUri.equals(RDF.type.getURI())) {
            probeDocumentBuilder.add("@type", valueUri); // Add the property URI to the document, not with an @id
        } else {
            probeDocumentBuilder.add(propertyUri, value); // Add the property URI to the document
        }
            
        // and the context
        probeDocumentBuilder.add("@context", completeContext);
        
        JsonObject probeDocument = probeDocumentBuilder.build();

        return probeDocument;
    }

    private JsonObject prepareProbeRegex(
        String testValue,
        String propertyUri
    ) {
        // 2. Use this matching String as a value of an @id property in the JSON-LD document
        JsonValue value = Json.createObjectBuilder()
            .add("@id", testValue) // Use @id for IRI properties
            .build();

        // 3. create an empty JSON object with the value and the context
        if(propertyUri == null) {
            propertyUri = "https://shacl-play.sparna.fr/regex";
        }

        // create an empty JSON object
        JsonObject probeDocument = Json.createObjectBuilder()
            .add(propertyUri, value) // Add the property URI to the document
            // and the context
            .add("@context", context)
            .build();

        return probeDocument;
    }

    private JsonObject doCompact(JsonObject probeDocument, JsonValue theContext) throws JsonLdError {
        // Use the JSON-LD library to compact the probe document with the context
        if(theContext.getValueType() == JsonValue.ValueType.STRING) {
            return com.apicatalog.jsonld.JsonLd.compact(JsonDocument.of(probeDocument), theContext.toString())
                .loader(JsonLdDocumentLoader.getInstance().getLoader())
                .compactToRelative(false)
                .get();
        } else {
            return com.apicatalog.jsonld.JsonLd.compact(JsonDocument.of(probeDocument), JsonDocument.of((JsonStructure)theContext))
                .loader(JsonLdDocumentLoader.getInstance().getLoader())
                .compactToRelative(false)
                .get();
        }
    }

    private Entry<String, JsonValue> getFirstNonContextEntry(JsonObject compactedDocument) {
        // Iterate through the entries of the compacted document and return the first entry that is not "@context"
        for (Entry<String, JsonValue> entry : compactedDocument.entrySet()) {
            if (!entry.getKey().equals("@context")) {
                return entry; // Return the first non-context entry
            }
        }
        return null; // If no such entry exists
    }

}
