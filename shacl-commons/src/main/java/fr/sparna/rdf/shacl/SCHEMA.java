package fr.sparna.rdf.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class SCHEMA {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model M_MODEL = ModelFactory.createDefaultModel();
	
	public static final String NS = "http://schema.org/";

	
	/** <p>The annotation property for schema:name.</p> */
    public static final Property name = M_MODEL.createProperty( NS + "name" );

    	/** <p>The annotation property for schema:description.</p> */
    public static final Property description = M_MODEL.createProperty( NS + "description" );

}
