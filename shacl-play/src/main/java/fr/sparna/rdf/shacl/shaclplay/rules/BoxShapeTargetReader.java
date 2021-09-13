package fr.sparna.rdf.shacl.shaclplay.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.diagram.PlantUmlBox;
import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShape;
import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShapeTarget;

public class BoxShapeTargetReader {
	
	protected List<BoxShape> allBoxes;
	public BoxShapeTargetReader(List<BoxShape> allBoxes) {
		super();
		this.allBoxes = allBoxes;
	}

	// Inicio de la recoleccion de informacion.
	
	public List<BoxShapeTarget> readTargetProperties(Resource nodeShape,List<BoxShape> allBoxes, List<Resource> Shape) {	
				
		List<BoxShapeTarget> aTarget = new ArrayList<>();		
		for(Resource rTarget : Shape) {
			BoxShapeTarget target = new BoxShapeTarget();
			target.setShPrefix(this.readPrefixes(rTarget));
			target.setShSelect(this.readTargetSelect(rTarget));
			aTarget.add(target);
		}
		return aTarget;
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
