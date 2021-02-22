package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.topbraid.shacl.vocabulary.SH;;

public class PlantUmlBox {
	
	private Resource nodeShape;
	protected String nameshape;
	List<PlantUmlProperty> shacl_value = new ArrayList<>();
	protected String nametargetclass; 
	protected String packageName;
	
	

	public PlantUmlBox(Resource nodeShape) {  
	    this.nodeShape = nodeShape;
		this.setNameshape(nodeShape.getLocalName());
		this.setPackageName(nodeShape);
		this.setNametargetclass(nodeShape);	
		
	}
	
	public String getNameshape() {
		return nameshape;
	}

	public void setNameshape(String nameshape) {
		
		this.nameshape = nameshape;
	}	
	
	public List<PlantUmlProperty> getProperties() {	
		return shacl_value;
	}
	
	
	public void readProperties(Resource nodeShape, List<PlantUmlBox> allBoxes) {
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<PlantUmlProperty> shacl_value = new ArrayList<>();
		PlantUmlPropertyReader propertyReader = new PlantUmlPropertyReader(allBoxes);
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			
			if(object.isLiteral()) {
				System.out.println("Problem !");
				}
			
			Resource propertyShape = object.asResource();			
			PlantUmlProperty plantvalueproperty = propertyReader.readPlantUmlProperty(propertyShape);
			shacl_value.add(plantvalueproperty);		
		
		}		

		shacl_value.sort((PlantUmlProperty ps1, PlantUmlProperty ps2) -> {
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
		 
		this.shacl_value = shacl_value;	
	}	
	
		
	public String getNametargetclass() {
		return nametargetclass;
	}

	public void setNametargetclass(Resource nodeShape) {
		ConstraintValueReader constargetclass = new ConstraintValueReader();
		this.nametargetclass = constargetclass.readValueconstraint(nodeShape, SH.targetClass);
	}
	
	public String getQualifiedName() {
		
		return packageName+"."+this.nameshape;
	}

	public Resource getNodeShape() {
		return nodeShape;
	}

	public void setNodeShape(Resource nodeShape) {
		
		
		this.nodeShape = nodeShape;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(Resource nodeShape) {
		String idpackage = "";
		try {
			idpackage = nodeShape.getProperty(nodeShape.getModel().createProperty("https://shacl-play.sparna.fr/ontology#package")).getResource().getLocalName();
		} catch (Exception e) {
			idpackage = "";
		}
		this.packageName = idpackage;
	}
	
	
	
}