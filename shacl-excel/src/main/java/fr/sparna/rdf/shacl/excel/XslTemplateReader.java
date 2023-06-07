package fr.sparna.rdf.shacl.excel;

import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.excel.ConstraintValueReader;

public class XslTemplateReader {
	
	protected ConstraintValueReader constraintValueReader = new ConstraintValueReader();	
	
	public XslTemplate read (Resource constraint) {
	
		XslTemplate tmp = new XslTemplate();
		
		tmp.setSh_path(this.readShPath(constraint));
		tmp.setSh_name(this.readShName(constraint));
		tmp.setSh_description(this.readShDescription(constraint));
		tmp.setSh_order(this.readShOrder(constraint));
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
}
