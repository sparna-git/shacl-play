package fr.sparna.rdf.shacl.sparqlgen.shaclmodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

public class PropertyShapeReader {

	protected List<NodeShape> allNodeShapes;
	
	
	public PropertyShapeReader(List<NodeShape> allNodeShapes) {
		super();
		this.allNodeShapes = allNodeShapes;
	}

	public PropertyShape read(Resource constraint) {

		PropertyShape shProperty = new PropertyShape(constraint);

		shProperty.setPath(this.readPath(constraint));
		shProperty.setHasValue(this.readHasValue(constraint));
		shProperty.setIn(this.readIn(constraint));
		shProperty.setNode(this.readNode(constraint));
		shProperty.setLanguageIn(readLanguageIn(constraint));
		shProperty.setOrNode(this.readOrNode(constraint));
		
		return shProperty;
	}
	
	
	protected Resource readPath(Resource constraint) {		
		Resource r = ModelHelper.readAsResource(constraint, SH.path);
		return r;
	}
	
	protected RDFNode readHasValue(Resource constraint) {		
		RDFNode values = ModelHelper.readAsResource(constraint, SH.hasValue);
		return values;
	}
	
	
	protected NodeShape readNode(Resource constraint) {	
		Resource value = ModelHelper.readAsResource(constraint, SH.node);
		NodeShape nShapeResource = null; 
		if(value != null) {
			//
			for (NodeShape nShape : allNodeShapes) {
				if(nShape.getNodeShapeResource().equals(value)) {
					nShapeResource = nShape;
					break;
				}
			}							
		}
		if(nShapeResource != null) {
			return nShapeResource;
		}else {
			return null;
		}	
	}
	
	
	protected List<RDFNode> readIn(Resource constraint) {
		List<RDFNode> values = ModelHelper.readAsRDFList(constraint, SH.in);
		if(values != null) {
			List<RDFNode> result = new ArrayList<>();
			for (RDFNode node : values) {
				if(!node.isURIResource()) {
					throw new RuntimeException("Property "+SH.languageIn+" on subject "+constraint+" contains a non-literal value");
				}
				result.add(node);
			}
			return result;
		} else {
			return null;
		}
	}
	
	
	

	protected List<String> readLanguageIn(Resource constraint) {
		List<RDFNode> values = ModelHelper.readAsRDFList(constraint, SH.languageIn);
		if(values != null) {
			List<String> result = new ArrayList<>();
			for (RDFNode node : values) {
				if(!node.isLiteral() ) {
					throw new RuntimeException("Property "+SH.languageIn+" on subject "+constraint+" contains a non-literal value");
				}
				result.add(node.asLiteral().getLexicalForm());
			}
			return result;
		} else {
			return null;
		}
	}
	
	protected List<NodeShape> readOrNode(Resource constraint) {
		List<RDFNode> orContent = ModelHelper.readAsRDFList(constraint, SH.or);
		if(orContent != null) {
			List<NodeShape> result = new ArrayList<>();
			for(RDFNode node : orContent) {
				if(node.isResource()) {
					if(node.asResource().hasProperty(SH.node)) {
						Resource nodeShapeReference = ModelHelper.readAsResource(node.asResource(), SH.node);
						for (NodeShape nodeShape : this.allNodeShapes) {
							if(nodeShape.getNodeShapeResource().getURI().equals(nodeShapeReference.getURI())) {
								result.add(nodeShape);
								break;
							}
						}
					}
				}
			}
			return result;
		} else {
			return null;
		}
	}

}
