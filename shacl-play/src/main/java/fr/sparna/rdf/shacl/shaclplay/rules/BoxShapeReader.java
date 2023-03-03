package fr.sparna.rdf.shacl.shaclplay.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShape;
import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShapeRules;
import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShapeTarget;

public class BoxShapeReader {
	
	
	public List<BoxShape> read(Model GraphModel) {
		
		
		List<Resource> nodeShapes =  GraphModel.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
		List<BoxShape> BoxShapeAll = nodeShapes.stream().map(res -> readShape(res, nodeShapes)).collect(Collectors.toList());
		List<BoxShape> boxShape = new ArrayList<>();
		
		/*
		 * Leemos el node e integramos las propriedades de SparqlTarget y SparqlRules 
		 */
		// Lectura de los datos de SparqlTarget
		BoxShapeTargetReader TargetReader = new BoxShapeTargetReader(BoxShapeAll); 
		for(BoxShape shape : BoxShapeAll) {
			
			/* id for Target SPARQL */
			Resource IdTarget = shape.getNodeShape().getProperty(SH.target).getResource();
			
			/*
			 * Get all target node
			 */
			List<Resource> resourceTarget =  GraphModel.listResourcesWithProperty(RDF.type, SH.SPARQLTarget).toList();
			List<BoxShapeTarget> aTarget= new ArrayList<>();
			for (Resource nodeTarget : resourceTarget) {
				if (nodeTarget.equals(IdTarget)) {
					aTarget.add(TargetReader.readTargetProperties(nodeTarget));
				}
			}
			
			if (aTarget.size() > 0) {
				shape.setTarget(aTarget);
			}

			/*
			 * Lectura de la informacion de SparqlRules 
			 * */
			BoxShapeRulesReader_code RulesPropertiesReaderCode = new BoxShapeRulesReader_code();
			List<BoxShapeRules> aRules= new ArrayList<>();
			if (shape.getNodeShape().getProperty(SH.rule) != null) {
				for (Statement s : shape.getNodeShape().listProperties(SH.rule).toList()) {
					aRules.add(RulesPropertiesReaderCode.readRulesProperties(s.getObject().asResource()));
				}
			}
			
			if (aRules.size() > 0) {
				shape.setRules(aRules);
			}
			
			boxShape.add(shape);
		}	
		return boxShape;
	}
	
	public BoxShape readShape(Resource constraint, List<Resource> allNodeShapes) {
		BoxShape nodeShape = new BoxShape(constraint);
		nodeShape.setLabel(this.readLabel(constraint, allNodeShapes));	
		
		return nodeShape;
	}
	
	public String readLabel(Resource nodeShape, List<Resource> allNodeShapes) {
		String value = null;
		if(nodeShape.isURIResource()) {
			value = nodeShape.asResource().getModel().shortForm(nodeShape.asResource().getLocalName());
		}else {
			value = nodeShape.toString();
		}		
		return value;
	}	

}
