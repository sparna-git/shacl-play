package fr.sparna.rdf.shacl.excel;

import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.excel.ConstraintValueReader;
import fr.sparna.rdf.shacl.excel.model.PropertyShapeTemplate;

public class ShapesTemplateReader {
	
	protected ConstraintValueReader constraintValueReader = new ConstraintValueReader();	
	
	public PropertyShapeTemplate read (Resource constraint) {
	
		PropertyShapeTemplate tmp = new PropertyShapeTemplate();
		
		tmp.setSh_path(this.readShPath(constraint));
		tmp.setSh_name(this.readShName(constraint));
		tmp.setSh_description(this.readShDescription(constraint));
		tmp.setSh_order(this.readShOrder(constraint));
		tmp.setDatatype(this.readShDatatype(constraint));
		tmp.setSh_UniqueLang(this.readShUniqueLang(constraint));
			
		return tmp;
	}
	
	
	public String readShPath(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint, SH.path);
	}
	
	public String readShName(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint,SH.name);
	}
	
	public String readShDescription(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint,SH.description);
	}
	
	public Integer readShOrder(Resource constraint) {
		return Integer.valueOf(constraintValueReader.readValueconstraint(constraint,SH.order));
	}
	
	public String readShDatatype(Resource constraint) {
		return constraintValueReader.readValueconstraint(constraint,SH.datatype);
	}
	
	public String readShUniqueLang(Resource constraint) {
		String value = null;
		if (
				constraint.hasProperty(SH.uniqueLang)
				&&
				constraintValueReader.readValueconstraint(constraint, SH.uniqueLang) != null
				&&
				!constraintValueReader.readValueconstraint(constraint, SH.uniqueLang).equals("")
		) {			
			value = "uniqueLang";
		}
	    return value;
	}
}
