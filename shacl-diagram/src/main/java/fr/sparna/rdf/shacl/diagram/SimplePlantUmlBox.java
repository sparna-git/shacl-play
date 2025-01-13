package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

public class SimplePlantUmlBox extends BasePlantUmlBox {
    
    private String backgroundColorString;
    private String colorString;
    private Resource nodeShape;
    private String label;
    private List<Resource> depiction;
    protected List<PlantUmlProperty> properties = new ArrayList<>();	
    
    public SimplePlantUmlBox(String uri) {
        this.nodeShape = ModelFactory.createDefaultModel().createResource(uri).asResource();
    }
    
    public String getBackgroundColorString() {
        return backgroundColorString;
    }
    public void setBackgroundColorString(String backgroundColorString) {
        this.backgroundColorString = backgroundColorString;        
    }
    public String getColorString() {
        return colorString;
    }
    public void setColorString(String colorString) {
        this.colorString = colorString;
    }
    public Resource getNodeShape() {
        return nodeShape;
    }

	public String getLabel() {
		return label;
	}

	public List<Resource> getDepiction() {
		List<Resource> s = new ArrayList<>();
		return s;
	}

	public List<PlantUmlProperty> getProperties() {
		return properties;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setDepiction(List<Resource> depiction) {
		this.depiction = depiction;
	}

	public void setProperties(List<PlantUmlProperty> properties) {
		this.properties = properties;
	}
    
    
    
    
}
