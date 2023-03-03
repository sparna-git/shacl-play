package fr.sparna.rdf.shacl.shaclplay.rules;

import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShape;
import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShapeTarget;

public class BoxShapeTargetReader {
	
	protected List<BoxShape> allBoxes;
	public BoxShapeTargetReader(List<BoxShape> allBoxes) {
		super();
		this.allBoxes = allBoxes;
	}

	// Inicio de la recoleccion de informacion.
	
	public BoxShapeTarget readTargetProperties(Resource nodeShape) {	
		
		BoxShapeTarget target = new BoxShapeTarget();
		target.setShPrefix(this.readPrefixes(nodeShape));
		target.setShSelect(this.readTargetSelect(nodeShape));
		
		return target;
		
	}

	public String readPrefixes(Resource nodeShapeTarget) {
		String value = null;
		if(nodeShapeTarget.hasProperty(SH.prefixes)) {			
			value = nodeShapeTarget.getProperty(SH.prefixes).getResource().getURI();
		}		
		return value;
	}
	
	public String readTargetSelect(Resource nodeShapeTarget) {
		String value = null;
		if(nodeShapeTarget.hasProperty(SH.select)) {
			value = nodeShapeTarget.getProperty(SH.select).getString();
		}		
		return value;
	}
	

}
