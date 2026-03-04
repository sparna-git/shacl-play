package fr.sparna.rdf.shacl.diagram.model;

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;

public class BasePlantUmlBox implements PlantUmlBoxIfc {

    @Override
    public int countShNodeOrShClassReferencesTo(String id, PlantUmlDiagram diagram) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getBackgroundColorString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColorString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Resource> getDepiction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLabel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Resource getNodeShape() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPlantUmlQuotedBoxName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PlantUmlProperty> getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Resource> getRdfsSubClassOf() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Resource> getShNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Resource> getTargetClass() {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public boolean isTargeting(Resource classUri) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setProperties(List<PlantUmlProperty> properties) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getLink() {
        // TODO Auto-generated method stub
        return null;
    }

}
