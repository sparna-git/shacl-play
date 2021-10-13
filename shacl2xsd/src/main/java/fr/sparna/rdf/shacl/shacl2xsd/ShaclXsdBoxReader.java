package fr.sparna.rdf.shacl.shacl2xsd;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;


public class ShaclXsdBoxReader {
		
	public ShaclXsdBox read(Resource nodeShape, List<Resource> allNodeShapes) {
		ShaclXsdBox box = new ShaclXsdBox(nodeShape);
		
		box.setLabel(this.readLabel(nodeShape, allNodeShapes));
		box.setPackageName(this.readPackageName(nodeShape));
		box.setNametargetclass(this.readNametargetclass(nodeShape));
		box.setUseReference(this.readReference(nodeShape));
		return box;
	}
	
	public Boolean readReference(Resource nodeShape) {
		Boolean value = false;
		if(nodeShape.hasProperty(nodeShape.getModel().createProperty("http://shacl-play.sparna.fr/ontology#xsdUseReferences"))) {
			try {
				value = nodeShape.getProperty(nodeShape.getModel().createProperty("http://shacl-play.sparna.fr/ontology#xsdUseReferences")).getBoolean();				
			} catch (Exception e) {
				value = false;
			}
		}
		return value;
	}
	
	
	
	public String readLabel(Resource nodeShape, List<Resource> allNodeShapes) {
		// strip out hyphens
		String value = null;
		if(nodeShape.isURIResource()) {
			value = nodeShape.asResource().getModel().shortForm(nodeShape.getURI());
		}else {
			value = nodeShape.toString();
		}
		
		return value;
	}
	
	public List<ShaclXsdProperty> readProperties(Resource nodeShape, List<ShaclXsdBox> allBoxes,Model owlGraph) {
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<ShaclXsdProperty> properties = new ArrayList<>();
		ShaclXsdPropertyReader propertyReader = new ShaclXsdPropertyReader(allBoxes);
		
		
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			
			if(object.isResource()) {
				Resource propertyShape = object.asResource();			
				ShaclXsdProperty plantvalueproperty = propertyReader.readPlantUmlProperty(propertyShape, owlGraph);
				properties.add(plantvalueproperty);					
			}
		
		}		

		properties.sort((ShaclXsdProperty ps1, ShaclXsdProperty ps2) -> {
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
	public List<ShaclXsdBox> readSuperClasses(Resource nodeShape, List<ShaclXsdBox> allBoxes, Model owlGraph) {
		
		List<Statement> subClassOfStatements = nodeShape.listProperties(RDFS.subClassOf).toList();
		List<ShaclXsdBox> superClasses = new ArrayList<>();
		
		for (Statement aSubClassOfStatement : subClassOfStatements) {
			RDFNode object = aSubClassOfStatement.getObject();

			// exclude restrictions
			if(object.isURIResource()) {
				
				ShaclXsdBox theBox = null;
				for (ShaclXsdBox plantUmlBox : allBoxes) {
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
		String value= null;
		if(nodeShape.hasProperty(SH.targetClass)) {
			value = nodeShape.getModel().shortForm(nodeShape.getProperty(SH.targetClass).getResource().getURI());
		}
		return value;
		//return valueReader.readValueconstraint(nodeShape, SH.targetClass);
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
	
	public List<String> readPrefixes(Resource nodeShape) {
		ShaclPrefixReader reader = new ShaclPrefixReader();
		List<String> prefixes = new ArrayList<>();
		
		// read prefixes on node shape
		prefixes.addAll(reader.readPrefixes(nodeShape));
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			if(object.isResource()) {
				Resource propertyShape = object.asResource();
				prefixes.addAll(reader.readPrefixes(propertyShape));
			}
		}
		return prefixes;
	}
	
	
}