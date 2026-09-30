package fr.sparna.rdf.vocabularies;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class SD {

    private static final Model M_MODEL = ModelFactory.createDefaultModel();

    public static final String BASE_URI = "http://www.w3.org/ns/sparql-service-description#";

    public final static String PREFIX = "sd";

    public final static Property ENDPOINT = M_MODEL.createProperty(BASE_URI + "endpoint");
    
}