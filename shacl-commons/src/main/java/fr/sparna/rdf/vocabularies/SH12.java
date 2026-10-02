package fr.sparna.rdf.vocabularies;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * This is to remove the dependency to TopBraid SHACL before switching to Jena4 that has its own constant class
 */
public class SH12 {

    public final static String BASE_URI = "http://www.w3.org/ns/shacl#";
    
    public final static String NAME = "SHACL";

    public final static String NS = BASE_URI;

    public final static String PREFIX = "sh";

    public final static Resource shapeGraph = ResourceFactory.createResource(NS + "ShapeGraph");
    
    public final static Property agentInstruction = ResourceFactory.createProperty(NS + "agentInstruction");
   
	
}
