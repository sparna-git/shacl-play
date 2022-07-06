package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclBoxReader {

	private String lang;
	private ConstraintValueReader valueReader = new ConstraintValueReader();

	public ShaclBoxReader(String lang) {
		this.lang = lang;
	}
	
	public ShaclBox read(Resource nodeShape) {
		
		ShaclBox box = new ShaclBox(nodeShape);

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
		String value = null;	
		try {
			value = nodeShape.getProperty(nodeShape.getModel().createProperty("http://www.w3.org/2004/02/skos/core#example")).getLiteral().getString();
		} catch (Exception e) {
			value = null;
		}		
		return value;
	}
	
	public String readSNodeKind(Resource nodeShape) {	
		String value = valueReader.readValueconstraint(nodeShape, SH.nodeKind, null);
		if(value != null) {
			value = nodeShape.getModel().shortForm(value);
		} 
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.nodeKind).toList();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();

			if (object.isResource()) {
				Resource propertyShape = object.asResource();			
				String plantvalueproperty = propertyShape.getURI();
				
			}
					
		}
		
		/*
		Resource nkBlankNode = nodeShape.getPropertyResourceValue(SH.nodeKind);
		if(SH.BlankNode.equals(nkBlankNode)) {
			Graph idNB = nkBlankNode.getModel().getGraph().;			
		}
		*/
		
		return value;
	}

	public Boolean readShClosed(Resource nodeShape) {	
		return Boolean.parseBoolean(valueReader.readValueconstraint(nodeShape, SH.closed, null));
	}

	public Integer readShOrder(Resource nodeShape) {
		Integer value = null;
		if(nodeShape.hasProperty(SH.order)) {
			value = Integer.parseInt(nodeShape.getProperty(SH.order).getLiteral().getString());
		} 
		return value;
	}

	public String readShPattern(Resource nodeShape) {
		return valueReader.readValueconstraint(nodeShape, SH.pattern, null);
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

	public List<ShaclProperty> readProperties(Resource nodeShape, List<ShaclBox> allBoxes) {
		
		ShaclPropertyReader propertyReader = new ShaclPropertyReader(this.lang, allBoxes);
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<ShaclProperty> propertyShapes = new ArrayList<>();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();

			if (object.isResource()) {
				Resource propertyShape = object.asResource();			
				ShaclProperty plantvalueproperty = propertyReader.read(propertyShape);
				propertyShapes.add(plantvalueproperty);	
			}
					
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