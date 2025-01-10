package fr.sparna.rdf.shacl.diagram;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

public class SimplePlantUmlBox extends BasePlantUmlBox {
    
    private String backgroundColorString;
    private String colorString;
    private Resource nodeShape;

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
    

}
