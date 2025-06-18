package fr.sparna.rdf.shacl.jsonschema.jsonld;

import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.jsonld.loader.HttpLoader;
import com.apicatalog.jsonld.loader.LRUDocumentCache;

public class JsonLdDocumentLoader {
    
    // Register the custom JSON-LD document loader
    private DocumentLoader loader;
    private LRUDocumentCache cacheLoader;

    private static JsonLdDocumentLoader instance = null;

    public static JsonLdDocumentLoader getInstance() {
        if (instance == null) {
            instance = new JsonLdDocumentLoader();
        }
        return instance;
    }

    private JsonLdDocumentLoader() {
        // Private constructor to enforce singleton pattern
        this.loader = HttpLoader.defaultInstance();
        this.cacheLoader = new LRUDocumentCache(loader, 100);
    }

    public DocumentLoader getLoader() {
        return cacheLoader;
    }
}
