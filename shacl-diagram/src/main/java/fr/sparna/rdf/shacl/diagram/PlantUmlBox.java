package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.listeners.NullListener;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.Metadata;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

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
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			
			if(object.isLiteral()) {
				System.out.println("Problem !");
				}
			
			Resource propertyShape = object.asResource();			
			PlantUmlProperty plantvalueproperty = new PlantUmlProperty(propertyShape, allBoxes);			
			shacl_value.add(plantvalueproperty);		
		
		}		
		
		//Comparator<PlantUmlProperty> ashacl_value = Comparator.comparing(PlantUmlProperty::getValue_order_shacl)
			//	.thenComparing(PlantUmlProperty::getValue_path);
		 
		
		 
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