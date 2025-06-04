package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

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

	private Optional<Resource> targetClass;
    
    public SimplePlantUmlBox(Resource r) {
        this.nodeShape = r;
		// init the link
		this.link = "#" + ((r.isURIResource())?this.nodeShape.getModel().shortForm(this.nodeShape.getURI()):r.getId().getLabelString());    
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

	@Override
    public Optional<Resource> getTargetClass() {
        return this.targetClass;
    }

	public void setTargetClass(Optional<Resource> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
    public boolean isTargeting(Resource classUri) {
		boolean hasShTargetClass = this.getTargetClass().filter(c -> c.equals(classUri)).isPresent();		
		boolean isItselfTheClass = 
		this.nodeShape.hasProperty(RDF.type, RDFS.Class)
		&&
		this.nodeShape.hasProperty(RDF.type, SH.NodeShape)
		&&
		this.nodeShape.equals(classUri);
		
		return hasShTargetClass || isItselfTheClass;
    }

}
