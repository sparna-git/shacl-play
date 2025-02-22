package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class SimplePlantUmlBox extends BasePlantUmlBox {
    
    private String backgroundColorString;
    private String colorString;
    private Resource nodeShape;
    private List<Resource> shNode = new ArrayList<>();
    private String label;
    private List<Resource> depiction = new ArrayList<>();
    protected List<PlantUmlProperty> properties = new ArrayList<>();
    private List<Resource> rdfsSubClassOf = new ArrayList<>();
	private String link;
    
    public SimplePlantUmlBox(Resource r) {
        this.nodeShape = r;
		// init the link
		this.link = "#" + this.nodeShape.getModel().shortForm(this.nodeShape.getURI());    
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
		return this.depiction;
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

	public List<Resource> getRdfsSubClassOf() {
		return rdfsSubClassOf;
	}

	public void setRdfsSubClassOf(List<Resource> rdfsSubClassOf) {
		this.rdfsSubClassOf = rdfsSubClassOf;
	}

	public List<Resource> getShNode() {
		return shNode;
	}

	public void setShNode(List<Resource> shNode) {
		this.shNode = shNode;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
