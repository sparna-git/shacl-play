package fr.sparna.rdf.shacl.shaclplay.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

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
			List<Resource> resourceTarget =  GraphModel.listResourcesWithProperty(RDF.type, SH.SPARQLTarget).toList();
			shape.setTarget(TargetReader.readTargetProperties(shape.getNodeShape(), BoxShapeAll, resourceTarget));
			
			/*
			 * Lectura de la informacion de SparqlRules 
			 * */
			List<Resource> resourceRules = GraphModel.listResourcesWithProperty(RDF.type, SH.SPARQLRule).toList();
			
			BoxShapeRulesReader RulesPropertiesReader = new BoxShapeRulesReader(BoxShapeAll);
			shape.setRules(RulesPropertiesReader.readRulesProperties(shape.getNodeShape(), BoxShapeAll, resourceRules));
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
			value = nodeShape.asResource().getModel().shortForm(nodeShape.getURI());
		}else {
			value = nodeShape.toString();
		}		
		return value;
	}	

}
