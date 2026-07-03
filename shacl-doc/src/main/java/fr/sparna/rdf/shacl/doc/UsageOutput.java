package fr.sparna.rdf.shacl.doc;

import java.util.List;
import fr.sparna.rdf.shacl.doc.model.Link;

public class UsageOutput {

    private String nodeshape_name;
    private Link nodeshape_link;
    private List<Link> properties_usage;

    
    
    public List<Link> getProperties_usage() {
        return properties_usage;
    }
    public void setProperties_usage(List<Link> properties_usage) {
        this.properties_usage = properties_usage;
    }
    public String getNodeshape_name() {
        return nodeshape_name;
    }
    public void setNodeshape_name(String nodeshape_name) {
        this.nodeshape_name = nodeshape_name;
    }
    public Link getNodeshape_link() {
        return nodeshape_link;
    }
    public void setNodeshape_link(Link nodeshape_link) {
        this.nodeshape_link = nodeshape_link;
    }
    
    
    
}
