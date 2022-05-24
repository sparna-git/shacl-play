package fr.sparna.rdf.shacl.sparqlgen.shaclmodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.topbraid.shacl.vocabulary.SH;

public class NodeShapeReader {
	
	public NodeShape read(Resource nodeShape) {
		
		NodeShape box = new NodeShape(nodeShape);
		
		box.setTargetClass(this.readTargetClass(nodeShape));
		box.setTargetSelect(this.readSelectQuery(nodeShape));
		
		return box;
	}
		
	public Resource readTargetClass(Resource nodeShape) {
		return ModelHelper.readAsURIResource(nodeShape, SH.targetClass);
	}
	
	public Query readSelectQuery(Resource nodeShape) {
		if(nodeShape.hasProperty(SH.target)) {
			Resource target = ModelHelper.readAsResource(nodeShape, SH.target);			
			Literal select = ModelHelper.readAsLiteral(target, SH.select);
			if(select != null) {
				return QueryFactory.create(select.getLexicalForm());
			}
		}
		return null;
	}
	
	
	public List<PropertyShape> readPropertyShape(Resource nodeShape, List<NodeShape> allNodeShapes){
		
		PropertyShapeReader propertyShapeReader = new PropertyShapeReader(allNodeShapes);
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<PropertyShape> propertyShapes = new ArrayList<>();
		
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();

			if (object.isResource()) {
				Resource propertyShape = object.asResource();			
				PropertyShape plantvalueproperty = propertyShapeReader.read(propertyShape);
				propertyShapes.add(plantvalueproperty);	
			}
					
		}
		
		
		return propertyShapes;
	}
	
	public List<String> readPrefixes(Resource nodeShape) {
		ShaclPrefixReader reader = new ShaclPrefixReader();
		List<String> prefixes = new ArrayList<>();
		
		// read prefixes on node shape
		prefixes.addAll(reader.readPrefixes(nodeShape));
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			if(object.isResource()) {
				Resource propertyShape = object.asResource();
				prefixes.addAll(reader.readPrefixes(propertyShape));
			}
		}
		return prefixes;
	}

}
