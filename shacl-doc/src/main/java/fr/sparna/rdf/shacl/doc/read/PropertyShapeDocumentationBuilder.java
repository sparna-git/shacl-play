package fr.sparna.rdf.shacl.doc.read;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.ShOrReadingUtils;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PropertyShape;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import net.sourceforge.plantuml.board.BNode;


public class PropertyShapeDocumentationBuilder {

	private List<NodeShape> allNodeShapes;
	private Model shaclGraph;
	private Model owlGraph;
	private String lang;

	

	public PropertyShapeDocumentationBuilder(
		List<NodeShape> allNodeShapes,
		Model shaclGraph,
		Model owlGraph,
		String lang
	) {
		this.allNodeShapes = allNodeShapes;
		this.shaclGraph = shaclGraph;
		this.owlGraph = owlGraph;
		this.lang = lang;
	}


	public PropertyShapeDocumentation build(
			PropertyShape propertyShape,
			NodeShape nodeShape) {
		// Start building final structure
		PropertyShapeDocumentation proprieteDoc = new PropertyShapeDocumentation();
		proprieteDoc.setLabel(propertyShape.getDisplayLabel(shaclGraph.union(owlGraph), lang));
		// URI in the documentation
		proprieteDoc.setPropertyUri(buildPathLink(propertyShape));
		// full URI
		proprieteDoc.setPropertyShapeUriOrId(propertyShape.getURIOrId());
		// section ID from concat of node shape ID + short name of the property
		proprieteDoc.setSectionId(nodeShape.getShortFormOrId()+"_"+propertyShape.getShPathAsString());	
		
		// if sh:qualifiedValueShape found in then property else print cardinality
		if (propertyShape.getQualifiedValueShape() != null) {
			proprieteDoc.setCardinalite(renderCardinalities(propertyShape.getShQualifiedMinCount(), propertyShape.getShQualifiedMaxCount()));
		} else {
			proprieteDoc.setCardinalite(renderCardinalities(propertyShape.getShMinCount(), propertyShape.getShMaxCount()));
		}		
		
		proprieteDoc.setDescription(propertyShape.getDisplayDescription(shaclGraph.union(owlGraph), lang));
		
		// Color
		if (!propertyShape.getColor().isEmpty()) {
			proprieteDoc.setColor(propertyShape.getColor().get().getString());
		}
		
		if (!propertyShape.getBackgroundColor().isEmpty()) {
			proprieteDoc.setBackgroundcolor(propertyShape.getBackgroundColor().get().getString());
		}
		
		proprieteDoc.getExpectedValue().setExpectedValue(selectExpectedValueAsLink(
				propertyShape.getShClass(),
				propertyShape.getShNode(),
				propertyShape.getShDatatype(),
				propertyShape.getShNodeKind(),
				propertyShape.getShHasValue(),
				propertyShape.getQualifiedValueShape()
		));

		if(propertyShape.getShIn() != null) {
			List<Link> links = propertyShape.getShIn().stream().map(i -> buildDefaultLink(i)).collect(Collectors.toList());
			proprieteDoc.getExpectedValue().setInValues(links);
		}
		
		// sh:Pattern on property
		if(propertyShape.getShPattern() != null) {
			proprieteDoc.getExpectedValue().setPattern(propertyShape.getShPattern().getLexicalForm());
		}
		
		// dash:LabelRole
		if (propertyShape.isLabelRole()) {
			proprieteDoc.setLabelRole(true);
		}
		
		// sh:Deactivated
		if (propertyShape.isDeactivated()) {
			proprieteDoc.setDeactivated(true);
		}
		
		// read values in sh:or
		RDFList shOrList = propertyShape.getShOr();
		if(shOrList != null) {
			if(ShOrReadingUtils.readShNodeInShOr(shOrList).size() > 0) {
				proprieteDoc.getExpectedValue().setOr(ShOrReadingUtils.readShNodeInShOr(shOrList).stream().map(i -> buildShNodeLink(i)).collect(Collectors.toList()));
			} else if(ShOrReadingUtils.readShClassInShOr(shOrList).size() > 0) {
				proprieteDoc.getExpectedValue().setOr(ShOrReadingUtils.readShClassInShOr(shOrList).stream().map(i -> buildShClassLink(i)).collect(Collectors.toList()));
			} else if(ShOrReadingUtils.readShDatatypeInShOr(shOrList).size() > 0) {
				proprieteDoc.getExpectedValue().setOr(ShOrReadingUtils.readShDatatypeInShOr(shOrList).stream().map(i -> buildShDatatypeLink(i)).collect(Collectors.toList()));
			} else if(ShOrReadingUtils.readShNodeKindInShOr(shOrList).size() > 0) {
				proprieteDoc.getExpectedValue().setOr(ShOrReadingUtils.readShNodeKindInShOr(shOrList).stream().map(i -> buildShNodeKindLink(i)).collect(Collectors.toList()));
			}
		}
		
		return proprieteDoc;
	}
	
	
	public static boolean findShClassInShTargetClass(List<Resource> getTargetClass,String getURIShClass) {
		
		boolean	bResult = false;
		for (Resource r : getTargetClass) {
			if (r.getURI().equals(getURIShClass)){
				bResult = true;
			}	
		}
		return bResult;
	}
	
	
	public static Link buildPathLink(PropertyShape prop) {			
		if(prop.getShPath() != null && prop.getShPath().isURIResource()) {
			return new Link(
					prop.getShPath().getURI(),
					prop.getShPathAsString()
			);			
		} else {
			return new Link(
					null,
					prop.getShPathAsString()
			);
		}
	}
	
	public static String renderCardinalities(Integer min, Integer max) {
		String minCount = "0";
		String maxCount = "*";
		
		if (min != null) {
			minCount = min.toString();
		}
		if (max != null) {
			maxCount = max.toString();
		}
		
		return minCount + ".." + maxCount;
	}

	public Link selectExpectedValueAsLink(
			Resource shClass,
			Resource shNode,
			Resource shDatatype,
			Resource shNodeKind,
			RDFNode shHasValue,
			Resource shQualifiedValueShape
	) {
		Link l = null;

		if (shQualifiedValueShape != null) {
			l = this.buildShQualifiedValueShape(shQualifiedValueShape);			
		} else if (shHasValue != null && shNode == null && shClass == null) {
			return buildDefaultLink(shHasValue);
		// sh:node has precedence over sh:class
		} else if (shNode != null) {
			return this.buildShNodeLink(shNode);
			//l = this.buildShNodeLink(shNode);
		} else if (shClass != null) {
			return this.buildShClassLink(shClass);
			//l = this.buildShClassLink(shClass);
		} else if (shDatatype != null) {
			return this.buildShDatatypeLink(shDatatype);
			//l = this.buildShDatatypeLink(shDatatype);
		} else if (shNodeKind != null) {
			return this.buildShNodeKindLink(shNodeKind);
			//l = this.buildShNodeKindLink(shNodeKind);
		}
		
		return l;
	}
	
	public static String renderNodeKind(Resource nodeKind) {
		if(nodeKind == null) {
			return null;
		}
		
		String rendered = ModelRenderingUtils.render(nodeKind, false);
		if (rendered.startsWith("sh:")) {
			return rendered.split(":")[1];	
		} else {
			return rendered;
		}
	}

	public Link buildShNodeLink(Resource shNode) {
		for(NodeShape aBox : allNodeShapes) {
			// using toString instead of getURI so that it works with anonymous nodeshapes
			if(aBox.getNodeShape().toString().equals(shNode.toString())) {
				return new Link("#"+aBox.getShortFormOrId(), aBox.getDisplayLabel(owlGraph, lang));
			}
		}

		// default link if shape not found
		return buildDefaultLink(shNode);
	}

	public Link buildShClassLink(Resource shClass) {
		for(NodeShape aNodeShape : allNodeShapes) {
			if(aNodeShape.getShTargetClass() != null && findShClassInShTargetClass(aNodeShape.getShTargetClass(),shClass.getURI())) {
				return new Link("#"+aNodeShape.getShortFormOrId(), aNodeShape.getDisplayLabel(owlGraph, lang));
				// checks that the URI of the NodeShape is itself equal to the sh:class
				// add a check to work only with named URI node shapes
			} else if (aNodeShape.getNodeShape().isURIResource() && aNodeShape.getNodeShape().getURI().equals(shClass.getURI())) {
				return new Link("#"+aNodeShape.getShortFormOrId(), aNodeShape.getDisplayLabel(owlGraph, lang));
			}
		}

		// default link if class not found
		return buildDefaultLink(shClass);
	}

	public Link buildShDatatypeLink(Resource shDatatype) {
		if(
				!shDatatype.asResource().getURI().startsWith(XSD.NS)
				&&
				!shDatatype.asResource().getURI().startsWith(RDF.uri)
			) {
				return buildDefaultLink(shDatatype);
			} else {
				// avoid putting a link to well-known datatypes
				return new Link(null, ModelRenderingUtils.render(shDatatype, true));
			}
	}

	public Link buildShNodeKindLink(Resource shNodeKind) {
		// avoid putting a link to node kinds
		return new Link(null, renderNodeKind(shNodeKind));
	}

	public Link buildDefaultLink(RDFNode node) {
		Link l = new Link();

		if (node instanceof Literal) {
			Literal lt = node.asLiteral();
			l.setLabel(lt.getLexicalForm());
			l.setLang(lt.getLanguage());
			l.setDatatype(ModelRenderingUtils.render(node.getModel().createResource(lt.getDatatypeURI()), false) );
		} else if (node instanceof Resource) {
			l.setHref(node.asResource().getURI());
			l.setLabel(ModelRenderingUtils.render(node, true));
		} else if (node instanceof BNode) {
			l.setLabel(ModelRenderingUtils.render(node, true));
		}		
		return l;
	}
	
	public Link buildShQualifiedValueShape(Resource shQualifiedValueShape) {
		
		PropertyShape qualifiedvaludShapeObject = new PropertyShape(shQualifiedValueShape);

		return selectExpectedValueAsLink(
			qualifiedvaludShapeObject.getShClass(),
			qualifiedvaludShapeObject.getShNode(),
			qualifiedvaludShapeObject.getShDatatype(),
			qualifiedvaludShapeObject.getShNodeKind(),
			qualifiedvaludShapeObject.getShHasValue(),
			// null for qualifiedValueShape
			null
		);
	}
}
