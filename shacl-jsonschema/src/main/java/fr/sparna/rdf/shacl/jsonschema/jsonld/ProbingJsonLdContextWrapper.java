package fr.sparna.rdf.shacl.jsonschema.jsonld;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.jsonld.loader.HttpLoader;
import com.apicatalog.jsonld.loader.LRUDocumentCache;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

public class ProbingJsonLdContextWrapper implements JsonLdContextWrapper {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());


    // Register the custom JSON-LD document loader
    static DocumentLoader LOADER = HttpLoader.defaultInstance();
    static DocumentLoader CACHE_LOADER = new LRUDocumentCache(LOADER, 100);


    private final JsonValue context;

    public ProbingJsonLdContextWrapper(JsonValue context) {
        this.context = context;
    }

    @Override
    public String readTermForProperty(
        String propertyUri,
        boolean isIriProperty,
        String datatype,
        String language
    ) throws JsonLdException {
        log.trace("Probing JSON-LD context for property URI: {}", propertyUri);
        try {
            JsonObject probeDocument = prepareProbePropertyDocument(propertyUri, isIriProperty, datatype, language);
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
    public String readTermFromValue(String uri) throws JsonLdException {
        log.trace("Probing JSON-LD context for value URI: {}", uri);
        try {
            JsonObject probeDocument = prepareProbeValueDocument(uri);
            //debug the input and output            
            System.out.println(probeDocument.toString());
            log.trace("Probe document: {}", probeDocument);
            JsonObject compactedDocument = doCompact(probeDocument, probeDocument.get("@context"));
            Entry<String, JsonValue> firstEntry = getFirstNonContextEntry(compactedDocument);
            if (firstEntry != null) {
                if(firstEntry.getValue().getValueType() == JsonValue.ValueType.STRING) {
                    return firstEntry.getValue().toString(); // Return the value as a string
                } else {
                    return uri;
                } 
            } else {
                return uri;
            }
        } catch (JsonLdError e) {
            throw new JsonLdException("Error compacting JSON-LD document for value URI: " + uri, e);
        }
    }

    @Override
    public boolean requiresArray(
        String propertyUri,
        boolean isIriProperty,
        String datatype,
        String language
    ) throws JsonLdException {
        log.trace("Checking if property URI requires an array: {}", propertyUri);
        try {
            JsonObject probeDocument = prepareProbePropertyDocument(propertyUri, isIriProperty, datatype, language);
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

    private JsonObject prepareProbePropertyDocument(
        String propertyUri,
        boolean isIriProperty,
        String datatype,
        String language
    ) {
        // Prepare a JSON-LD document that probes the context for the given property URI
        // This method should create a document that includes the property URI and is ready for compaction
        
        JsonValue value = JsonValue.NULL; // Default value is null
        if(isIriProperty) {
            value = Json.createObjectBuilder()
                .add("@id", propertyUri) // Use @id for IRI properties
                .build();
        } else if (language != null) {
            value = Json.createObjectBuilder()
                .add("@language", language) // Use @language for language-tagged properties
                .build();
        } else if (datatype != null) {
            value = Json.createObjectBuilder()
                .add("@type", datatype) // Use @type for datatype properties
                .build();
        } 

        // create an empty JSON object
        JsonObject probeDocument = Json.createObjectBuilder()
            .add(propertyUri, value) // Add the property URI with a null value
            // and the context
            .add("@context", context)
            .build();

        return probeDocument;
    }

    private JsonObject prepareProbeValueDocument(
        String valueUri
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
                .add("@type", "@vocab")
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

    private JsonObject doCompact(JsonObject probeDocument, JsonValue theContext) throws JsonLdError {
        // Use the JSON-LD library to compact the probe document with the context
        if(theContext.getValueType() == JsonValue.ValueType.STRING) {
            return com.apicatalog.jsonld.JsonLd.compact(JsonDocument.of(probeDocument), theContext.toString())
                .loader(ProbingJsonLdContextWrapper.CACHE_LOADER)
                .compactToRelative(false)
                .get();
        } else {
            return com.apicatalog.jsonld.JsonLd.compact(JsonDocument.of(probeDocument), JsonDocument.of((JsonStructure)theContext))
                .loader(ProbingJsonLdContextWrapper.CACHE_LOADER)
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
