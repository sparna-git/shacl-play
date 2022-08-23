package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
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
		
		NodeShape ns = new NodeShape(nodeShape);

		ns.setShTargetClass(this.readShTargetClass(nodeShape));
		ns.setRdfsComment(this.readRdfsComment(nodeShape));
		ns.setRdfsLabel(this.readRdfsLabel(nodeShape));
		ns.setShPattern(this.readShPattern(nodeShape));
		ns.setShNodeKind(this.readSNodeKind(nodeShape));
		ns.setShClosed(this.readShClosed(nodeShape));
		ns.setShOrder(this.readShOrder(nodeShape));
		ns.setSkosExample(this.readSkosExample(nodeShape));
		
		ns.setRdfsSubClassOf(this.readRdfsSubClassOf(nodeShape));
		
		return ns;
	}
	
	
	public RDFNode readSkosExample(Resource nodeShape) {
		return Optional.ofNullable(nodeShape.getProperty(SKOS.example)).map(s -> s.getObject()).orElse(null);
	}
	
	public Resource readSNodeKind(Resource nodeShape) {	
		return Optional.ofNullable(nodeShape.getProperty(SH.nodeKind)).map(s -> s.getResource()).orElse(null);
	}

	public Boolean readShClosed(Resource nodeShape) {
		return Optional.ofNullable(nodeShape.getProperty(SH.closed)).map(s -> Boolean.valueOf(s.getBoolean())).orElse(null);
	}

	public Integer readShOrder(Resource nodeShape) {
		return Optional.ofNullable(nodeShape.getProperty(SH.order)).map(s -> Integer.parseInt(s.getString())).orElse(null);
	}

	public Literal readShPattern(Resource nodeShape) {
		return Optional.ofNullable(nodeShape.getProperty(SH.pattern)).map(s -> s.getLiteral()).orElse(null);
	}

	public String readRdfsLabel(Resource nodeShape) {
		return ConstraintValueReader.readLiteralInLangAsString(nodeShape, RDFS.label, this.lang);
	}

	public String readRdfsComment(Resource nodeShape) {
		return ConstraintValueReader.readLiteralInLangAsString(nodeShape, RDFS.comment, this.lang);
	}

	public Resource readShTargetClass(Resource nodeShape) {
		return Optional.ofNullable(nodeShape.getProperty(SH.targetClass)).map(s -> s.getResource()).orElse(null);
	}
	
	public List<Resource> readRdfsSubClassOf(Resource nodeShape) {
		return nodeShape.listProperties(RDFS.subClassOf).toList().stream()
				.map(s -> s.getResource())
				.filter(r -> { return r.isURIResource() && !r.getURI().equals(OWL.Thing.getURI()); })
				.collect(Collectors.toList());
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