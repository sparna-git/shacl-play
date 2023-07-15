package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;;

public class PlantUmlBoxReader {
		
	private ConstraintValueReader valueReader = new ConstraintValueReader();
	
	public PlantUmlBox read(Resource nodeShape, List<Resource> allNodeShapes) {
		PlantUmlBox box = new PlantUmlBox(nodeShape);
		
		box.setLabel(this.readLabel(nodeShape));
		box.setPackageName(this.readPackageName(nodeShape));
		box.setNametargetclass(this.readNametargetclass(nodeShape));
		box.setColorClass(this.readColor(nodeShape));
		box.setDiagramReferences(this.readDiagramReferences(nodeShape, allNodeShapes));
		return box;
	}
	
	
	public List<Resource> readDiagramReferences(Resource nodeShape, List<Resource> allNodeShapes) {
		List<Resource> result = new ArrayList<>();
		
		if(nodeShape.getProperty(FOAF.depiction) != null) {			
			for(Statement s : nodeShape.listProperties(FOAF.depiction).toList()) {
				result.add(s.getObject().asResource());
			}
		}
		
		return result;
	}
	
	public String readColor(Resource nodeShape) {	
		String value = null;
		try {
			if(nodeShape.hasProperty(nodeShape.getModel().createProperty(SHACL_PLAY.COLOR))) {
				value= nodeShape.getProperty(nodeShape.getModel().createProperty(SHACL_PLAY.COLOR)).getLiteral().getString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}	
	
	public String readLabel(Resource nodeShape) {
		if(nodeShape.isURIResource()) {
			return nodeShape.asResource().getModel().shortForm(nodeShape.getURI());
		} else {
			return nodeShape.toString();
		}
	}
	
	public List<PlantUmlProperty> readProperties(Resource nodeShape, List<PlantUmlBox> allBoxes,Model owlGraph) {
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<PlantUmlProperty> properties = new ArrayList<>();
		PlantUmlPropertyReader propertyReader = new PlantUmlPropertyReader(allBoxes);
		
		
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			
			if(object.isResource()) {
				Resource propertyShape = object.asResource();			
				PlantUmlProperty plantvalueproperty = propertyReader.readPlantUmlProperty(propertyShape, owlGraph);
				properties.add(plantvalueproperty);					
			}
		
		}		

		properties.sort((PlantUmlProperty ps1, PlantUmlProperty ps2) -> {
			if(ps1.getValue_order_shacl() != null) {
				if(ps2.getValue_order_shacl() != null) {
					return (int) (ps1.getValue_order_shacl() - ps2.getValue_order_shacl());
				} else {
					return -1;
				}
			} else {
				if(ps2.getValue_order_shacl() != null) {
					return 1;
				} else {
					// both sh:order are null, try with sh:path
					if(ps1.getValue_path() != null && ps2.getValue_path() != null) {						
						return ps1.getValue_path().compareTo(ps2.getValue_path());
					} else {
						return ps1.getPropertyShape().toString().compareTo(ps2.getPropertyShape().toString());
					}
				}
			}
		});
		 
		return properties;	
	}

	// read the superClasses declared on the NodeShape
	// implies that the NodeShape is itself a Class
	public List<PlantUmlBox> readSuperClasses(Resource nodeShape, List<PlantUmlBox> allBoxes, Model owlGraph) {
		
		List<Statement> subClassOfStatements = nodeShape.listProperties(RDFS.subClassOf).toList();
		List<PlantUmlBox> superClasses = new ArrayList<>();
		
		for (Statement aSubClassOfStatement : subClassOfStatements) {
			RDFNode object = aSubClassOfStatement.getObject();

			// exclude restrictions
			if(object.isURIResource()) {
				
				PlantUmlBox theBox = null;
				for (PlantUmlBox plantUmlBox : allBoxes) {
					if(!plantUmlBox.getNodeShape().isAnon()) {
						if(plantUmlBox.getNodeShape().getURI().equals(object.asResource().getURI())) {
							theBox = plantUmlBox;
							break;
						}
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
			idpackage = nodeShape.getProperty(nodeShape.getModel().createProperty(SHACL_PLAY.PACKAGE)).getResource().getLocalName();
		} catch (Exception e) {
			idpackage = "";
		}
		return idpackage;
	}
	
	
	
}