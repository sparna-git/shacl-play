package fr.sparna.rdf.shacl.diagram.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class SimplePlantUmlBox extends BasePlantUmlBox {
    
    private String backgroundColorStringBox;
    private String colorStringBox;
    private Resource nodeShape;
    private List<Resource> shNodeBox = new ArrayList<>();
    private String label;
    private List<Resource> depictionBox = new ArrayList<>();
    protected List<PlantUmlProperty> propertiesBox = new ArrayList<>();
    private List<Resource> rdfsSubClassOf = new ArrayList<>();
	private String link;

	private Optional<Resource> targetClass;
    
    public SimplePlantUmlBox(Resource r) {
        this.nodeShape = r;
		// init the link
		this.link = "#" + ((r.isURIResource())?this.nodeShape.getModel().shortForm(this.nodeShape.getURI()):r.getId().getLabelString());    
    }

	public String getBackgroundColorStringBox() {
		return backgroundColorStringBox;
	}



	public void setBackgroundColorStringBox(String backgroundColorStringBox) {
		this.backgroundColorStringBox = backgroundColorStringBox;
	}



	public String getColorStringBox() {
		return colorStringBox;
	}



	public void setColorStringBox(String colorStringBox) {
		this.colorStringBox = colorStringBox;
	}



	public Resource getNodeShape() {
        return nodeShape;
    }

	public String getLabel() {
		return label;
	}

	public List<Resource> getDepictionBox() {
		return this.depictionBox;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setDepictionBox(List<Resource> depictionBox) {
		this.depictionBox = depictionBox;
	}

	public List<PlantUmlProperty> getPropertiesBox() {
		return propertiesBox;
	}

	public void setPropertiesBox(List<PlantUmlProperty> propertiesBox) {
		this.propertiesBox = propertiesBox;
	}

	public List<Resource> getRdfsSubClassOf() {
		return rdfsSubClassOf;
	}

	public void setRdfsSubClassOf(List<Resource> rdfsSubClassOf) {
		this.rdfsSubClassOf = rdfsSubClassOf;
	}

	public List<Resource> getShNodeBox() {
		return shNodeBox;
	}

	public void setShNodeBox(List<Resource> shNodeBox) {
		this.shNodeBox = shNodeBox;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
    public Optional<Resource> getTargetClassAsOptional() {
        return this.targetClass;
    }

	public void setTargetClass(Optional<Resource> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
    public boolean isTargetingBox(Resource classUri) {
		boolean hasShTargetClass = this.getTargetClassAsOptional().filter(c -> c.equals(classUri)).isPresent();		
		boolean isItselfTheClass = 
		this.nodeShape.hasProperty(RDF.type, RDFS.Class)
		&&
		this.nodeShape.hasProperty(RDF.type, SH.NodeShape)
		&&
		this.nodeShape.equals(classUri);
		
		return hasShTargetClass || isItselfTheClass;
    }

}
