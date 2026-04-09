package fr.sparna.rdf.shacl.doc;

import java.util.List;
import fr.sparna.rdf.shacl.doc.model.Link;

public class UsageOutput {

    private String nodeshape_usage;
    private List<Link> properties_usage;

    public String getNodeshape_usage() {
        return nodeshape_usage;
    }
    public void setNodeshape_usage(String nodeshape_usage) {
        this.nodeshape_usage = nodeshape_usage;
    }
    public List<Link> getProperties_usage() {
        return properties_usage;
    }
    public void setProperties_usage(List<Link> properties_usage) {
        this.properties_usage = properties_usage;
    }
    
}
