package fr.sparna.rdf.shacl.generate.providers;

import java.util.List;

import org.apache.jena.rdf.model.Resource;

public interface TargetDefinitionIfc {
    
    public List<Resource> getTargetNode();

    public List<Resource> getTargetClass();

    public List<Resource> getTargetSubjectsOf();

    public List<Resource> getTargetObjectsOf();

    public List<String> getSparqlTarget();

    public boolean isEmptyTarget();

}
