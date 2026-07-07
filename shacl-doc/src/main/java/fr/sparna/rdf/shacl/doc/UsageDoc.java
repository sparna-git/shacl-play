package fr.sparna.rdf.shacl.doc;
import java.util.List;

import fr.sparna.rdf.jena.shacl.PropertyShape;

public class UsageDoc {

    private NodeShapeDoc nodeShape;
    private List<PropertyShape> properties;

    public UsageDoc(NodeShapeDoc nodeShape) {
        this.nodeShape = nodeShape;
        this.properties = new java.util.ArrayList<PropertyShape>();
    }
    
    public NodeShapeDoc getNodeShape() {
        return nodeShape;
    }

    public void setNodeShape(NodeShapeDoc nodeShape) {
        this.nodeShape = nodeShape;
    }

    public List<PropertyShape> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyShape> properties) {
        this.properties = properties;
    }    
    
}
