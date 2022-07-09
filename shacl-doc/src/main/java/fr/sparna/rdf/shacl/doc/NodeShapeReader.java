package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.topbraid.shacl.vocabulary.SH;

public class NodeShapeReader {

	private String lang;
	private ConstraintValueReader valueReader = new ConstraintValueReader();

	public NodeShapeReader(String lang) {
		this.lang = lang;
	}
	
	public NodeShape read(Resource nodeShape) {
		
		NodeShape box = new NodeShape(nodeShape);

		box.setShTargetClass(this.readShTargetClass(nodeShape));
		box.setRdfsComment(this.readRdfsComment(nodeShape));
		box.setRdfsLabel(this.readRdfsLabel(nodeShape));
		box.setShPattern(this.readShPattern(nodeShape));
		box.setShNodeKind(this.readSNodeKind(nodeShape));
		box.setShClosed(this.readShClosed(nodeShape));
		box.setShOrder(this.readShOrder(nodeShape));
		box.setSkosExample(this.readSkosExample(nodeShape));
		
		return box;
	}
	
	
	public String readSkosExample(Resource nodeShape) {
		if(nodeShape.hasProperty(SKOS.example)) {
			 return nodeShape.getProperty(SKOS.example).getLiteral().getString();
		} else {
			return null;
		}
	}
	
	public Resource readSNodeKind(Resource nodeShape) {	
		if(nodeShape.hasProperty(SH.nodeKind)) {
			 return nodeShape.getProperty(SH.nodeKind).getResource();
		} else {
			return null;
		}
	}

	public Boolean readShClosed(Resource nodeShape) {
		if(nodeShape.hasProperty(SH.closed)) {
			return Boolean.valueOf(nodeShape.getProperty(SH.closed).getLiteral().getBoolean());
		} else {
			return null;
		}
		
	}

	public Integer readShOrder(Resource nodeShape) {
		Integer value = null;
		if(nodeShape.hasProperty(SH.order)) {
			value = Integer.parseInt(nodeShape.getProperty(SH.order).getLiteral().getString());
		} 
		return value;
	}

	public Literal readShPattern(Resource nodeShape) {
		if(nodeShape.hasProperty(SH.pattern)) {
			 return nodeShape.getProperty(SH.pattern).getLiteral();
		} else {
			return null;
		}
	}

	public String readRdfsLabel(Resource nodeShape) {
		return valueReader.readValueconstraint(nodeShape, RDFS.label, this.lang);
	}

	public String readRdfsComment(Resource nodeShape) {
		return valueReader.readValueconstraint(nodeShape, RDFS.comment, this.lang);
	}

	public Resource readShTargetClass(Resource nodeShape) {
		return nodeShape.getPropertyResourceValue(SH.targetClass);
	}

	public List<PropertyShape> readProperties(Resource nodeShape, List<NodeShape> allBoxes) {
		
		PropertyShapeReader propertyReader = new PropertyShapeReader(this.lang, allBoxes);
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<PropertyShape> propertyShapes = new ArrayList<>();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();

			if (object.isResource()) {
				Resource propertyShape = object.asResource();			
				PropertyShape plantvalueproperty = propertyReader.read(propertyShape);
				propertyShapes.add(plantvalueproperty);	
			}
					
		}
		
		// sort property shapes
		propertyShapes.sort((PropertyShape ps1, PropertyShape ps2) -> {
			if(ps1.getShOrder() != null) {
				if(ps2.getShOrder() != null) {
					return ps1.getShOrder() - ps2.getShOrder();
				} else {
					return -1;
				}
			} else {
				if(ps2.getShOrder() != null) {
					return 1;
				} else {
					// both sh:order are null, try with sh:name
					if(ps1.getShName() != null) {
						if(ps2.getShName() != null) {
							return ps1.getShNameAsString().compareTo(ps2.getShNameAsString());
						} else {
							return -1;
						}
					} else {
						if(ps2.getShName() != null) {
							return 1;
						} else {
							// both sh:name are null, try with sh:path
							return ps1.getShPathAsString().compareToIgnoreCase(ps2.getShPathAsString());
						}
					}
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