package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class PlantUmlBox {
	
	//private Resource nodeShape;
	protected String nameshape;
	List<PlantUmlProperty> shacl_value = new ArrayList<>();
	protected String nametargetclass; 
	protected String rdfs_class;
	
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
		
		//shacl_value.sort(Comparator.comparing(PlantUmlProperty::getValue_order_shacl));
		shacl_value.sort(Comparator.comparing(PlantUmlProperty::getValue_path));
		this.shacl_value = shacl_value;	
	}	

	
	public String getNametargetclass() {
		return nametargetclass;
	}

	public void setNametargetclass(Resource nodeShape) {
		ConstraintValueReader constargetclass = new ConstraintValueReader();
		this.nametargetclass = constargetclass.readValueconstraint(nodeShape, SH.targetClass);
	}
	
	public String getRdfs_class() {
		return rdfs_class;
	}

	public void setRdfs_class(Resource nodeShape) {
	//	boolean result = nodeShape.hasProperty(RDF.type, RDFS.Class);
		
		//Resource list = nodeShape.getModel().getRDFNode(RDFS.Class);
	  //  RDFList rdfList = list.as(RDFList.class);
	   // ExtendedIterator<RDFNode> items = rdfList.iterator();		
			
		this.rdfs_class = rdfs_class;
	}

	public PlantUmlBox(Resource nodeShape) {  
	    
		this.setNameshape(nodeShape.getLocalName());
		this.setNametargetclass(nodeShape);
		//this.setRdfs_class(nodeShape);
		this.setProperties(nodeShape);		
		
		}	
}