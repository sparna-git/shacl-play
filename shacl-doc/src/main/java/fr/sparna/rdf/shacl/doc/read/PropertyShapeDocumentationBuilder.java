package fr.sparna.rdf.shacl.doc.read;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.ShOrReadingUtils;
import fr.sparna.rdf.shacl.doc.NodeShapeDoc;
import fr.sparna.rdf.shacl.doc.PropertyShapeDoc;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import net.sourceforge.plantuml.board.BNode;


public class PropertyShapeDocumentationBuilder {

	private List<NodeShapeDoc> allNodeShapes;
	private Model shaclGraph;
	private Model owlGraph;
	private String lang;

	

	public PropertyShapeDocumentationBuilder(
		List<NodeShapeDoc> allNodeShapes,
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
			PropertyShapeDoc propertyShape,
			NodeShapeDoc nodeShape) {
		// Start building final structure
		PropertyShapeDocumentation proprieteDoc = new PropertyShapeDocumentation();
		proprieteDoc.setLabel(propertyShape.getDisplayLabel(shaclGraph.union(owlGraph), lang));
		
		// URI in the documentation
		proprieteDoc.setPropertyUri(buildPathLink(propertyShape));
		// full URI
		proprieteDoc.setPropertyShapeUriOrId(propertyShape.getPropertyShape().getModel().shortForm(propertyShape.getURIOrId()));
		// section ID from concat of node shape ID + short name of the property
		//proprieteDoc.setSectionId(nodeShape.getShortFormOrId()+"_"+propertyShape.getShPathAsString());	
		proprieteDoc.setSectionId(nodeShape.getShortFormOrId()+"_"+propertyShape.getShPathAsString());	
		
		// if sh:qualifiedValueShape found in then property else print cardinality
		if (propertyShape.getShQualifiedValueShape().isPresent()) {
			Integer qlfMin = propertyShape.getShQualifiedMinCount().isPresent() ? propertyShape.getShQualifiedMinCount().get() : null;
			Integer qlfMax = propertyShape.getShQualifiedMaxCount().isPresent() ? propertyShape.getShQualifiedMaxCount().get() : null;
			proprieteDoc.setCardinalite(renderCardinalities(qlfMin, qlfMax));
		} else {
			Integer minCount = propertyShape.getShMinCount().isPresent() ? propertyShape.getShMinCount().get() : null;
			Integer maxCount = propertyShape.getShMaxCount().isPresent() ? propertyShape.getShMaxCount().get() : null;
			proprieteDoc.setCardinalite(renderCardinalities(minCount, maxCount));
		}		
		
		proprieteDoc.setDescription(propertyShape.getDisplayDescription(shaclGraph.union(owlGraph), lang));
		
		// colors
		if (!propertyShape.getShaclPlayColor().isEmpty()) {
			proprieteDoc.setColor(propertyShape.getShaclPlayColor().get().getString());
		}		
		if (!propertyShape.getShaclPlayBackgroundColor().isEmpty()) {
			proprieteDoc.setBackgroundcolor(propertyShape.getShaclPlayBackgroundColor().get().getString());
		}
		
		proprieteDoc.getExpectedValue().setExpectedValue(selectExpectedValueAsLink(
				propertyShape.getShClass().isPresent() ? propertyShape.getShClass().get().asResource() : null ,
				propertyShape.getShNode().isPresent() ? propertyShape.getShNode().get().asResource() : null ,
				propertyShape.getShDatatype().isPresent() ? propertyShape.getShDatatype().get().asResource() : null,
				propertyShape.getShNodeKind().isPresent() ? propertyShape.getShNodeKind().get().asResource() : null,
				propertyShape.getShHasValue().isPresent() ? propertyShape.getShHasValue().get() : null,
				propertyShape.getShQualifiedValueShape().isPresent() ? propertyShape.getShQualifiedValueShape().get().asResource() : null
		));

		if(propertyShape.getShIn() != null) {
			List<Link> links = propertyShape.getShIn().stream().map(i -> buildDefaultLink(i)).collect(Collectors.toList());
			proprieteDoc.getExpectedValue().setInValues(links);
		}
		// sh:hasValue is taken as the main value above in selectExpectedValueAsLink()
		// it is placed in the "inValues" slot only if there is a sh:node or sh:class
		if(propertyShape.getShHasValue().isPresent() && (propertyShape.getShClass().isPresent() || propertyShape.getShNode().isPresent())) {
			proprieteDoc.getExpectedValue().setInValues(Collections.singletonList(buildDefaultLink(propertyShape.getShHasValue().get())));
		}
		
		// sh:pattern on property
		if(propertyShape.getShPattern().isPresent()) {
			proprieteDoc.getExpectedValue().setPattern(propertyShape.getShPattern().get().getLexicalForm());
		}
		
		// dash:LabelRole
		if (propertyShape.isLabelRole()) {
			proprieteDoc.setLabelRole(true);
		}
		
		// sh:deactivated
		if (propertyShape.isDeactivated()) {
			proprieteDoc.setDeactivated(true);
		}
		
		// skos:example
		proprieteDoc.setExamples(
			propertyShape.getSkosExample().stream().map(example -> example.toString()).collect(Collectors.joining("; "))
		);
		
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
		
	public static Link buildPathLink(PropertyShapeDoc prop) {			
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
		} else if (shNode != null) {
			return this.buildShNodeLink(shNode);
		} else if (shClass != null) {
			return this.buildShClassLink(shClass);
		} else if (shDatatype != null) {
			return this.buildShDatatypeLink(shDatatype);
		} else if (shNodeKind != null) {
			return this.buildShNodeKindLink(shNodeKind);
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
		for(NodeShapeDoc aBox : allNodeShapes) {
			// using toString instead of getURI so that it works with anonymous nodeshapes
			if(aBox.getNodeShape().toString().equals(shNode.toString())) {
				return new Link("#"+aBox.getShortFormOrId(), aBox.getDisplayLabel(owlGraph, lang));
			}
		}

		// default link if shape not found
		return buildDefaultLink(shNode);
	}

	public Link buildShClassLink(Resource shClass) {
		for(NodeShapeDoc aNodeShape : allNodeShapes) {
			if(aNodeShape.getAllTargetedClasses() != null && findShClassInShTargetClass(aNodeShape.getAllTargetedClasses(),shClass.getURI())) {
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
		
		PropertyShapeDoc qualifiedvaludShapeObject = new PropertyShapeDoc(shQualifiedValueShape);

		return selectExpectedValueAsLink(
			qualifiedvaludShapeObject.getShClass().isPresent() ? qualifiedvaludShapeObject.getShClass().get().asResource() : null,
			qualifiedvaludShapeObject.getShNode().isPresent() ? qualifiedvaludShapeObject.getShNode().get().asResource() : null,
			qualifiedvaludShapeObject.getShDatatype().isPresent() ? qualifiedvaludShapeObject.getShDatatype().get().asResource() : null,
			qualifiedvaludShapeObject.getShNodeKind().isPresent() ? qualifiedvaludShapeObject.getShNodeKind().get().asResource() : null,
			qualifiedvaludShapeObject.getShHasValue().isPresent() ? qualifiedvaludShapeObject.getShHasValue().get().asResource() : null,
			// null for qualifiedValueShape
			null
		);
	}
}
