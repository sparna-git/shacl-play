package fr.sparna.rdf.shacl.doc;
import java.util.List;

public class UsageDoc {

    private NodeShapeDoc nodeShape;
    private List<PropertyShapeDoc> properties;
    
    public NodeShapeDoc getNodeShape() {
        return nodeShape;
    }

    public void setNodeShape(NodeShapeDoc nodeShape) {
        this.nodeShape = nodeShape;
    }

    public List<PropertyShapeDoc> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyShapeDoc> properties) {
        this.properties = properties;
    }    
    
}
