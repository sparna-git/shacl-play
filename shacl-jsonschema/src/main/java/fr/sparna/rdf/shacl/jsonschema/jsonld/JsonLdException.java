package fr.sparna.rdf.shacl.jsonschema.jsonld;

public class JsonLdException extends Exception {

    private static final long serialVersionUID = 1L;

    public JsonLdException(String message) {
        super(message);
    }

    public JsonLdException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonLdException(Throwable cause) {
        super(cause);
    }

}
