package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;


import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.topbraid.shacl.vocabulary.SH;

public class PlantUmlBox {
	
	//private Resource nodeShape;
	protected String nameshape;
	List<PlantUmlProperty> shacl_value = new ArrayList<>();
	
	public String getNameshape() {
		return nameshape;
	}


	public void setNameshape(String nameshape) {
		
		this.nameshape = nameshape;
	}

	
	
	public List<PlantUmlProperty> getProperties() {	
		return shacl_value;
	}
	
	
	public void setProperties(Resource nodeShape) {
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<PlantUmlProperty> shacl_value = new ArrayList<>();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			
			if(object.isLiteral()) {
				System.out.println("Problem !");
				}
			
			Resource propertyShape = object.asResource();			
			PlantUmlProperty plantvalueproperty = new PlantUmlProperty(propertyShape);			
			shacl_value.add(plantvalueproperty);		
		
		}		
		this.shacl_value = shacl_value;	
	}	

	public PlantUmlBox(Resource nodeShape) {  
		
		this.setNameshape(nodeShape.getLocalName());
		this.setProperties(nodeShape);		
		
		}	
}