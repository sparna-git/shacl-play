package fr.sparna.rdf.shacl.doc;
import java.util.List;

import fr.sparna.rdf.shacl.doc.model.Link;

public class UsageDoc {

    private NodeShape nodeShape;
    private List<PropertyShape> properties;
    
    public UsageDoc(NodeShape nodeShape) {
		this.nodeShape = nodeShape;
	}

    public NodeShape getNodeShape() {
        return nodeShape;
    }

    public void setNodeShape(NodeShape nodeShape) {
        this.nodeShape = nodeShape;
    }

    public List<PropertyShape> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyShape> properties) {
        this.properties = properties;
    }    
    
}
