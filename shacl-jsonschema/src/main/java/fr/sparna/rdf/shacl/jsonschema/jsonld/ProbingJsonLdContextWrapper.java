package fr.sparna.rdf.shacl.jsonschema.jsonld;
import java.util.Map.Entry;

import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.model.RgxGenCharsDefinition;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

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
            System.out.println("  "+probeDocument.toString());
            JsonObject compactedDocument = doCompact(probeDocument, this.context);
            System.out.println("  "+compactedDocument.toString());
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
    public String readTermFromValue(String uri) throws JsonLdException {
        log.trace("Probing JSON-LD context for value URI: {}", uri);
        try {
            JsonObject probeDocument = prepareProbeValueDocument(uri, true);
            //debug the input and output            
            log.trace("Probe document: {}", probeDocument);
            JsonObject compactedDocument = doCompact(probeDocument, probeDocument.get("@context"));
            Entry<String, JsonValue> firstEntry = getFirstNonContextEntry(compactedDocument);

            String finalResult = uri;

            if (firstEntry != null) {
                if(firstEntry.getValue().getValueType() == JsonValue.ValueType.STRING) {
                    // returns the string value
                    finalResult = firstEntry.getValue().toString().replaceAll("^\"|\"$", ""); // Remove quotes
                }  
            }

            if(finalResult.equals(uri)) {
                // second try with an "@id" to test if the URI can be simplified with a @base declaration

                JsonObject probeDocument2 = prepareProbeValueDocument(uri, false);
                //debug the input and output            
                log.trace("Probe document 2: {}", probeDocument2);
                JsonObject compactedDocument2 = doCompact(probeDocument2, probeDocument2.get("@context"));
                Entry<String, JsonValue> firstEntry2 = getFirstNonContextEntry(compactedDocument2);

                if (firstEntry2 != null) {
                    if(firstEntry2.getValue().getValueType() == JsonValue.ValueType.STRING) {
                        // returns the string value
                        finalResult = firstEntry2.getValue().toString().replaceAll("^\"|\"$", ""); // Remove quotes
                    }  
                }
            }

            return finalResult;


        } catch (JsonLdError e) {
            throw new JsonLdException("Error compacting JSON-LD document for value URI: " + uri, e);
        }
    }

    @Override
    public String simplifyPattern(String regexPattern) throws JsonLdException {
        log.trace("Probing JSON-LD context for regex: {}", regexPattern);
        // System.out.println("Probing JSON-LD context for regex: "+regexPattern);
        try {
            String testValue = generateMatchingString(regexPattern);
            JsonObject probeDocument = prepareProbeRegex(testValue);
            //debug the input and output            
            // System.out.println(probeDocument.toString());
            log.trace("Probe document: {}", probeDocument);
            JsonObject compactedDocument = doCompact(probeDocument, probeDocument.get("@context"));
            // System.out.println(compactedDocument.toString());
            Entry<String, JsonValue> firstEntry = getFirstNonContextEntry(compactedDocument);
            if (firstEntry != null) {
                // read the @id of the JsonValue, which should be a string
                if(firstEntry.getValue().getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject valueObject = firstEntry.getValue().asJsonObject();
                    // read the first entry of the object, which should be the @id
                    JsonValue firstValue = valueObject.entrySet().iterator().next().getValue();
                    String returnedValue = firstValue.getValueType() == JsonValue.ValueType.STRING ? firstValue.toString().replaceAll("^\"|\"$", "") : null;
                    if(returnedValue != null) {
                        System.out.println("Returned value: " + returnedValue);
                        if(returnedValue.length() < testValue.length() && testValue.endsWith(returnedValue)) {
                            // now we now that some simplification was applied in the context
                            // most probably due to @base

                            // determine the piece of the string that was removed
                            String removed = testValue.substring(0, testValue.length() - returnedValue.length());
                            // and return the simplified regex pattern
                            String simplifiedPattern = regexPattern.replaceAll(removed, "");

                            return simplifiedPattern;
                        }
                    }
                }
            }
        } catch (JsonLdError e) {
            throw new JsonLdException("Error compacting JSON-LD document for probing regex: " + regexPattern, e);
        }
        return regexPattern;
    }



    @Override
    public boolean requiresArray(
        String propertyUri,
        boolean isIriProperty,
        String datatype,
        String language
    ) throws JsonLdException {
        // preventing call with getURI() on a blank node
        if(propertyUri == null) {
            return false;
        }

        log.trace("Checking if property URI requires an array: {}", propertyUri);
        try {
            JsonObject probeDocument = prepareProbePropertyDocument(propertyUri, isIriProperty, false, datatype, language);
            //debug the input and output            
            log.trace("Probe document: {}", probeDocument);
            JsonObject compactedDocument = doCompact(probeDocument, this.context);
            Entry<String, JsonValue> firstEntry = getFirstNonContextEntry(compactedDocument);
            if (firstEntry != null) {
                return firstEntry.getValue().getValueType() == JsonValue.ValueType.ARRAY; // Check if the value is an array
            } else {
                return false;
            }
        } catch (JsonLdError e) {
            throw new JsonLdException("Error compacting JSON-LD document for property URI: " + propertyUri, e);
        }
    }

    @Override
    public boolean requiresContainerLanguage(
        String propertyUri
    ) throws JsonLdException {
        log.trace("Checking if property URI requires a language container: {}", propertyUri);
        try {
            JsonObject probeDocument = prepareProbePropertyDocument(propertyUri, false, false, RDF.langString.getURI(), null);

            log.trace("Probe document: {}", probeDocument);
            JsonObject compactedDocument = doCompact(probeDocument, this.context);
            Entry<String, JsonValue> firstEntry = getFirstNonContextEntry(compactedDocument);
            if (firstEntry != null) {
                // if the first entry is an object, and if it contains a single key that has 2 letters, then it is a language container
                if(firstEntry.getValue().getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject valueObject = firstEntry.getValue().asJsonObject();
                    if(valueObject.size() == 1) {
                        String key = valueObject.keySet().iterator().next();
                        if(key.length() == 2 && key.matches("[a-zA-Z]{2}")) {
                            return true; // It is a language container
                        }   
                    }
                }
            }

            return false;
        } catch (JsonLdError e) {
            throw new JsonLdException("Error compacting JSON-LD document for property URI: " + propertyUri, e);
        }
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

        if(isInverse) {
            // create an empty JSON object
            probeDocument = Json.createObjectBuilder()
                .add("@reverse", Json.createObjectBuilder().add(propertyUri, value).build())
                // and the context
                .add("@context", context)
                .build();
        } else {
            // create an empty JSON object
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
        JsonObject probeDocument = Json.createObjectBuilder()
            .add("shaclplay", value) // Add the property URI with a null value
            // and the context
            .add("@context", completeContext)
            .build();

        return probeDocument;
    }

    private JsonObject prepareProbeRegex(
        String testValue
    ) {
        // 2. Use this matching String as a value of an @id property in the JSON-LD document
        JsonValue value = Json.createObjectBuilder()
            .add("@id", testValue) // Use @id for IRI properties
            .build();

        // 3. create an empty JSON object with the value and the context
        String propertyUri = "https://shacl-play.sparna.fr/regex";

        // create an empty JSON object
        JsonObject probeDocument = Json.createObjectBuilder()
            .add(propertyUri, value) // Add the property URI to the document
            // and the context
            .add("@context", context)
            .build();

        return probeDocument;
    }

    public static String generateMatchingString(String regex) {
        RgxGenProperties properties = new RgxGenProperties();
        // when matching a dot, always use the special \u0000 character for replacement
        // this is because EP uses regexes like "^https://data.europarl.europa.eu/eli/dl/doc/[A-Za-z0-9\-_]+/[a-z][a-z]$"
        // that directly contains a dot
        RgxGenOption.DOT_MATCHES_ONLY.setInProperties(properties, RgxGenCharsDefinition.of("\u0000"));
        // when generating any number of values, generate 3 characters
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(properties, 3);

        RgxGen rgxGen = RgxGen.parse(properties, regex);
        String output = rgxGen.generate();

        // replace the value inserted for dots back with a dot
        return output.replaceAll("\u0000", ".").replaceAll(" ", "_");
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
