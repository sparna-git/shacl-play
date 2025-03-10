package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.topbraid.shacl.vocabulary.SH;

public class NodeShapeReader {

	private String lang;

	public NodeShapeReader(String lang) {
		this.lang = lang;
	}
	

	public List<PropertyShape> readProperties(Resource nodeShape, List<NodeShape> allBoxes, Model owlModel) {
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<PropertyShape> propertyShapes = new ArrayList<>();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();

			if (object.isResource()) {
				Resource propertyShape = object.asResource();			
				PropertyShape plantvalueproperty = new PropertyShape(propertyShape);
				propertyShapes.add(plantvalueproperty);	
			}					
		}
		
		// sort property shapes
		propertyShapes.sort((PropertyShape ps1, PropertyShape ps2) -> {
			if(ps1.getShOrder() != null) {
				if(ps2.getShOrder() != null) {
					return (ps1.getShOrder() - ps2.getShOrder()) > 0?1:-1;
				} else {
					return -1;
				}
			} else {
				if(ps2.getShOrder() != null) {
					return 1;
				} else {
					// both sh:order are null, try with sh:name
					return ps1.getSortOrderKey(owlModel, lang).compareToIgnoreCase(ps2.getSortOrderKey(owlModel, lang));
				}
			}
		});
		
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