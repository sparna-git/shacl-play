package fr.sparna.rdf.shacl.generate.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class ResourceTargetDefinition implements TargetDefinitionIfc {

    private Resource nodeShape;


    public ResourceTargetDefinition(Resource nodeShape) {
        this.nodeShape = nodeShape;
    }

    @Override
    public List<Resource> getTargetNode() {
        StmtIterator it = nodeShape.listProperties(SH.targetNode);
        return it.toList().stream().map(st -> st.getObject().asResource()).collect(Collectors.toList());
    }

    @Override
    public List<Resource> getTargetClass() {
        StmtIterator it = nodeShape.listProperties(SH.targetClass);
        List<Resource> result = it.toList().stream().map(st -> st.getObject().asResource()).collect(Collectors.toList());
        // implicit targetClass
        if(nodeShape.hasProperty(RDF.type, RDFS.Class)) {
			result.add(nodeShape);
		}

        return result;
    }

    @Override
    public List<Resource> getTargetSubjectsOf() {
        StmtIterator it = nodeShape.listProperties(SH.targetSubjectsOf);
        return it.toList().stream().map(st -> st.getObject().asResource()).collect(Collectors.toList());
    }

    @Override
    public List<Resource> getTargetObjectsOf() {
        StmtIterator it = nodeShape.listProperties(SH.targetObjectsOf);
        return it.toList().stream().map(st -> st.getObject().asResource()).collect(Collectors.toList());
    }

    @Override
    public List<String> getSparqlTarget() {
        StmtIterator it = nodeShape.listProperties(SH.target);
        List<String> result  = new ArrayList<String>();
        while(it.hasNext()) {
			Resource shTargetValue = it.next().getObject().asResource();
			if(shTargetValue.hasProperty(SH.select)) {
                result.add(shTargetValue.getProperty(SH.select).getObject().asLiteral().getLexicalForm());
			}
		}

        return result;
    }

    @Override
    public boolean isEmptyTarget() {
        return 
        getTargetNode().isEmpty()
        &&
        getTargetClass().isEmpty()
        &&
        getTargetSubjectsOf().isEmpty()
        &&
        getTargetObjectsOf().isEmpty()
        &&
        getSparqlTarget().isEmpty()
        ;
    }

}
