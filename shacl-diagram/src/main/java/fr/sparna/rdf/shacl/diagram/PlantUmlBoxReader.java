package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;;

public class PlantUmlBoxReader {
		
	private ConstraintValueReader valueReader = new ConstraintValueReader();
	
	public PlantUmlBox read(Resource nodeShape) {
		PlantUmlBox box = new PlantUmlBox(nodeShape);
		
		box.setNameshape(nodeShape.getLocalName());
		box.setPackageName(this.readPackageName(nodeShape));
		box.setNametargetclass(this.readNametargetclass(nodeShape));
		
		return box;
	}
	
	public List<PlantUmlProperty> readProperties(Resource nodeShape, List<PlantUmlBox> allBoxes) {
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<PlantUmlProperty> properties = new ArrayList<>();
		PlantUmlPropertyReader propertyReader = new PlantUmlPropertyReader(allBoxes);
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			
			if(object.isResource()) {
				Resource propertyShape = object.asResource();			
				PlantUmlProperty plantvalueproperty = propertyReader.readPlantUmlProperty(propertyShape);
				properties.add(plantvalueproperty);					
			}
		
		}		

		properties.sort((PlantUmlProperty ps1, PlantUmlProperty ps2) -> {
			if(ps1.getValue_order_shacl() != null) {
				if(ps2.getValue_order_shacl() != null) {
					return ps1.getValue_order_shacl() - ps2.getValue_order_shacl();
				} else {
					return -1;
				}
			} else {
				if(ps2.getValue_order_shacl() != null) {
					return 1;
				} else {
					// both sh:order are null, try with sh:path
					return ps1.getValue_path().compareTo(ps2.getValue_path());
				}
			}
		});
		 
		return properties;	
	}
	
	public List<PlantUmlBox> readSuperClasses(Resource nodeShape, List<PlantUmlBox> allBoxes) {
		
		List<Statement> subClassOfStatements = nodeShape.listProperties(RDFS.subClassOf).toList();
		List<PlantUmlBox> superClasses = new ArrayList<>();
		
		for (Statement aSubClassOfStatement : subClassOfStatements) {
			RDFNode object = aSubClassOfStatement.getObject();

			if(object.isURIResource()) {
				
				PlantUmlBox theBox = null;
				for (PlantUmlBox plantUmlBox : allBoxes) {
					if(plantUmlBox.getNodeShape().getURI().equals(object.asResource().getURI())) {
						theBox = plantUmlBox;
						break;
					}
				}
				
				if(theBox != null) {
					superClasses.add(theBox);
				}	
			}
		
		}		

		 
		return superClasses;	
	}	

	public String readNametargetclass(Resource nodeShape) {
		return valueReader.readValueconstraint(nodeShape, SH.targetClass);
	}

	public String readPackageName(Resource nodeShape) {
		String idpackage = "";
		try {
			idpackage = nodeShape.getProperty(nodeShape.getModel().createProperty("https://shacl-play.sparna.fr/ontology#package")).getResource().getLocalName();
		} catch (Exception e) {
			idpackage = "";
		}
		return idpackage;
	}
	
	
	
}