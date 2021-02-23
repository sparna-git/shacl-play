package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclBoxReader {

	private String lang;

	public ShaclBoxReader(String lang) {
		this.lang = lang;
	}
	
	public ShaclBox read(Resource nodeShape) {
		ShaclBox box = new ShaclBox();
		
		box.setNodeShape(nodeShape);
		box.setNodeShapeBox(nodeShape);
		box.setNameshape(nodeShape.getLocalName());
		box.setNametargetclass(this.readNametargetclass(nodeShape));
		box.setRdfsComment(this.readRdfsComment(nodeShape));
		box.setRdfslabel(this.readRdfslabel(nodeShape));
		box.setShpatternNodeShape(this.readShpatternNodeShape(nodeShape));
		box.setShnodeKind(this.readShnodeKind(nodeShape));
		box.setShClose(this.readShClose(nodeShape));
		box.setShOrder(this.readShOrder(nodeShape));		
		return box;
	}
	
	public String readShnodeKind(Resource nodeShape) {
		ConstraintValueReader constarget = new ConstraintValueReader();
		return nodeShape.getModel().shortForm(constarget.readValueconstraint(nodeShape, SH.nodeKind, null));
	}

	public Boolean readShClose(Resource nodeShape) {
		ConstraintValueReader constarget = new ConstraintValueReader();		
		return Boolean.parseBoolean(constarget.readValueconstraint(nodeShape, SH.closed, null));
	}

	public Integer readShOrder(Resource nodeShape) {
		Integer value = null;
		if(nodeShape.hasProperty(SH.order)) {
			value = Integer.parseInt(nodeShape.getProperty(SH.order).getLiteral().getString());
		} 
		return value;
	}

	public String readShpatternNodeShape(Resource nodeShape) {
		ConstraintValueReader constraintValueReader = new ConstraintValueReader();
		return constraintValueReader.readValueconstraint(nodeShape, SH.pattern, null);
	}

	public String readRdfslabel(Resource nodeShape) {
		ConstraintValueReader constraintValue = new ConstraintValueReader();
		return constraintValue.readValueconstraint(nodeShape, RDFS.label, this.lang);
	}

	public String readRdfsComment(Resource nodeShape) {
		ConstraintValueReader constraintValue = new ConstraintValueReader();
		return constraintValue.readValueconstraint(nodeShape, RDFS.comment, this.lang);
	}

	public String readNametargetclass(Resource nodeShape) {
		ConstraintValueReader constargetclass = new ConstraintValueReader();
		return constargetclass.readValueconstraint(nodeShape, SH.targetClass, null);
	}

	public List<ShaclProperty> readProperties(Resource nodeShape, List<ShaclBox> allBoxes) {
		
		ShaclPropertyReader propertyReader = new ShaclPropertyReader(this.lang, allBoxes);
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<ShaclProperty> propertyShapes = new ArrayList<>();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();

			if (object.isLiteral()) {
				System.out.println("Problem !");
			}

			Resource propertyShape = object.asResource();			
			ShaclProperty plantvalueproperty = propertyReader.read(propertyShape);
			propertyShapes.add(plantvalueproperty);			
		}
		
		// sort property shapes
		propertyShapes.sort((ShaclProperty ps1, ShaclProperty ps2) -> {
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
					if(ps1.getName() != null) {
						if(ps2.getName() != null) {
							return ps1.getName().compareTo(ps2.getName());
						} else {
							return -1;
						}
					} else {
						if(ps2.getName() != null) {
							return 1;
						} else {
							// both sh:name are null, try with sh:path
							return ps1.getPath().compareToIgnoreCase(ps2.getPath());
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