package fr.sparna.rdf.shacl;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * This is to remove the dependency to TopBraid SHACL before switching to Jena4 that has its own constant class
 */
public class SH {

    public final static String BASE_URI = "http://www.w3.org/ns/shacl#";
    
    public final static String NAME = "SHACL";

    public final static String NS = BASE_URI;

    public final static String PREFIX = "sh";

    public final static Resource NodeShape = ResourceFactory.createResource(NS + "NodeShape");
    
    public final static Resource ValidationResult = ResourceFactory.createResource(NS + "ValidationResult");
    
    
    
    public final static Property focusNode = ResourceFactory.createProperty(NS + "focusNode");
    
    public final static Property alternativePath = ResourceFactory.createProperty(NS + "alternativePath");
    
    public final static Property inversePath = ResourceFactory.createProperty(NS + "inversePath");
    
    public final static Property resultSeverity = ResourceFactory.createProperty(NS + "resultSeverity");
    
    public final static Property conforms = ResourceFactory.createProperty(NS + "conforms");
    
    public final static Property resultPath = ResourceFactory.createProperty(NS + "resultPath");
    
    public final static Property resultMessage = ResourceFactory.createProperty(NS + "resultMessage");
    
    public final static Property sourceConstraint = ResourceFactory.createProperty(NS + "sourceConstraint");
    
    public final static Property sourceConstraintComponent = ResourceFactory.createProperty(NS + "sourceConstraintComponent");
    
    public final static Property sourceShape = ResourceFactory.createProperty(NS + "sourceShape");
    
    public final static Property value = ResourceFactory.createProperty(NS + "value");
    
    
    
    public final static Resource Violation = ResourceFactory.createResource(NS + "Violation");

    public final static Resource Warning = ResourceFactory.createResource(NS + "Warning");
    
    public final static Resource Info = ResourceFactory.createResource(NS + "Info");
    

	
}
