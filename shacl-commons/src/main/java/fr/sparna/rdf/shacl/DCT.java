package fr.sparna.rdf.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class DCT {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model M_MODEL = ModelFactory.createDefaultModel();
	
	public static final String NS = "http://purl.org/dc/terms/";
	
	/** <p>The annotation property that shall be set.</p> */
    public static final Property Description = M_MODEL.createProperty( NS + "description" );
	
	
}
