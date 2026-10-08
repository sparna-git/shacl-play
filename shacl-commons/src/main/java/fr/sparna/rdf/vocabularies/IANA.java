package fr.sparna.rdf.vocabularies;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

public class IANA {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model M_MODEL = ModelFactory.createDefaultModel();
	
	public static final String NS = "https://www.iana.org/assignments/media-types/";
	
    public static final Resource TEXT_TURTLE = M_MODEL.createResource( NS + "text/turtle" );
	
    public static final Resource APPLICATION_RDFXML = M_MODEL.createResource( NS + "application/rdf+xml" );

    public static final Resource APPLICATION_NTRIPLES = M_MODEL.createResource( NS + "n-triples" );

    public static final Resource APPLICATION_LDJSON = M_MODEL.createResource( NS + "application/ld+json" );

    public static final Resource APPLICATION_ZIP = M_MODEL.createResource( NS + "application/zip" );
}
