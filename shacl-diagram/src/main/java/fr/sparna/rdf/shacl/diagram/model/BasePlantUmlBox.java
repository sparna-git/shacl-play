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
    public String getBackgroundColorStringBox() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColorStringBox() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Resource> getDepictionBox() {
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
    public List<PlantUmlProperty> getPropertiesBox() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Resource> getRdfsSubClassOf() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Resource> getShNodeBox() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Resource> getTargetClassAsOptional() {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public boolean isTargetingBox(Resource classUri) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setPropertiesBox(List<PlantUmlProperty> propertiesBox) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getLink() {
        // TODO Auto-generated method stub
        return null;
    }

}
