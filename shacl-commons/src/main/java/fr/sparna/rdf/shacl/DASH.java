package fr.sparna.rdf.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class DASH {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model M_MODEL = ModelFactory.createDefaultModel();
	
	public static final String NS = "http://datashapes.org/dash#";
	
	/** <p>The annotation property that shall be set.</p> */
    public static final Property propertyRole = M_MODEL.createProperty( NS + "propertyRole" );
	
	/** <p>The annotation property that shall be set.</p> */
    public static final Resource LabelRole = M_MODEL.createProperty( NS + "LabelRole" );
	
    /** <p>The annotation property that shall be set.</p> */
    public static final Resource FailureResult = M_MODEL.createResource( NS + "FailureResult" );
	
}
