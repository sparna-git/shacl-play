package fr.sparna.rdf.shacl.excel;

import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.excel.model.PropertyShapeTemplate;

public class PropertyShapeReader {
	
	protected ConstraintValueReader constraintValueReader = new ConstraintValueReader();	
	
	public PropertyShapeTemplate read (Resource constraint) {
	
		PropertyShapeTemplate tmp = new PropertyShapeTemplate(constraint);
		
		tmp.setSh_path(this.readShPath(constraint));
		tmp.setSh_name(this.readShName(constraint));
		tmp.setSh_description(this.readShDescription(constraint));
		tmp.setSh_order(this.readShOrder(constraint));
		tmp.setDatatype(this.readShDatatype(constraint));
			
		return tmp;
	}
	
	
	public Resource readShPath(Resource constraint) {
		return constraint.getProperty(SH.path).getResource();
	}
	
	public String readShName(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint,SH.name);
	}
	
	public String readShDescription(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint,SH.description);
	}
	
	public Double readShOrder(Resource constraint) {
		return Double.valueOf(constraintValueReader.readValueconstraint(constraint,SH.order));
	}
	
	public String readShDatatype(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint,SH.datatype);
	}
}
