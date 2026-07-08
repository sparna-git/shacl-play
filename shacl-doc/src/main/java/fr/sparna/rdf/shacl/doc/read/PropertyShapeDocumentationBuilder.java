package fr.sparna.rdf.shacl.doc.read;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.PropertyShape;
import fr.sparna.rdf.jena.shacl.ShOrReadingUtils;
import fr.sparna.rdf.jena.shacl.ShapesGraph;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;


public class PropertyShapeDocumentationBuilder {

	private ShapesGraph shapesGraph;
	private Model shaclGraph;
	private Model owlGraph;
	private String lang;

	

	public PropertyShapeDocumentationBuilder(
		ShapesGraph shapesGraph,
		Model shaclGraph,
		Model owlGraph,
		String lang
	) {
		this.shapesGraph = shapesGraph;
		this.shaclGraph = shaclGraph;
		this.owlGraph = owlGraph;
		this.lang = lang;
	}

	public static String buildPropertyShapeSectionId(NodeShape nodeShape, PropertyShape propertyShape) {
		String pathString = propertyShape.getPropertyPath().renderSparqlPropertyPath();
		return nodeShape.getShortFormOrId()+"_"+pathString;
		// return propertyShape.getShape().getModel().shortForm(propertyShape.getURIOrId());
	}


	public PropertyShapeDocumentation build(
			PropertyShape propertyShape,
			NodeShape nodeShape
	) {
		// Start building final structure
		PropertyShapeDocumentation proprieteDoc = new PropertyShapeDocumentation();
		proprieteDoc.setLabel(propertyShape.getDisplayLabel(shaclGraph.union(owlGraph), lang));
		
		// URI in the documentation
		proprieteDoc.setPropertyUri(LinkFactory.buildPropertyPathLink(propertyShape.getPropertyPath()));
		// full URI
		proprieteDoc.setPropertyShapeUriOrId(propertyShape.getShape().getModel().shortForm(propertyShape.getURIOrId()));
		// section ID from concat of node shape ID + short name of the property
		// this is to easily write anchor references to this section	
		proprieteDoc.setSectionId(buildPropertyShapeSectionId(nodeShape, propertyShape));
		
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
			List<Link> links = propertyShape.getShIn().stream().map(i -> LinkFactory.buildDefaultLink(i)).collect(Collectors.toList());
			proprieteDoc.getExpectedValue().setInValues(links);
		}
		// sh:hasValue is taken as the main value above in selectExpectedValueAsLink()
		// it is placed in the "inValues" slot only if there is a sh:node or sh:class
		if(propertyShape.getShHasValue().isPresent() && (propertyShape.getShClass().isPresent() || propertyShape.getShNode().isPresent())) {
			proprieteDoc.getExpectedValue().setInValues(Collections.singletonList(LinkFactory.buildDefaultLink(propertyShape.getShHasValue().get())));
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
		String exampleString = propertyShape.getSkosExample().stream().map(example -> example.toString()).collect(Collectors.joining("; "));
		proprieteDoc.setExamples(exampleString.equals("")?null:exampleString);
		
		// read values in sh:or
		RDFList shOrList = propertyShape.getShOr();
		if(shOrList != null) {
			if(ShOrReadingUtils.readShNodeInShOr(shOrList).size() > 0) {
				proprieteDoc.getExpectedValue().setOr(ShOrReadingUtils.readShNodeInShOr(shOrList).stream().map(i -> LinkFactory.buildShNodeOrOtherShapeReferenceLink(i, shapesGraph, owlGraph, lang)).collect(Collectors.toList()));
			} else if(ShOrReadingUtils.readShClassInShOr(shOrList).size() > 0) {
				proprieteDoc.getExpectedValue().setOr(ShOrReadingUtils.readShClassInShOr(shOrList).stream().map(i -> LinkFactory.buildShClassLink(i, shapesGraph, owlGraph, lang)).collect(Collectors.toList()));
			} else if(ShOrReadingUtils.readShDatatypeInShOr(shOrList).size() > 0) {
				proprieteDoc.getExpectedValue().setOr(ShOrReadingUtils.readShDatatypeInShOr(shOrList).stream().map(i -> LinkFactory.buildShDatatypeLink(i)).collect(Collectors.toList()));
			} else if(ShOrReadingUtils.readShNodeKindInShOr(shOrList).size() > 0) {
				proprieteDoc.getExpectedValue().setOr(ShOrReadingUtils.readShNodeKindInShOr(shOrList).stream().map(i -> LinkFactory.buildShNodeKindLink(i)).collect(Collectors.toList()));
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
			return LinkFactory.buildDefaultLink(shHasValue);
		} else if (shNode != null) {
			return LinkFactory.buildShNodeOrOtherShapeReferenceLink(shNode, shapesGraph, owlGraph, lang);
		} else if (shClass != null) {
			return LinkFactory.buildShClassLink(shClass, shapesGraph, owlGraph, lang);
		} else if (shDatatype != null) {
			return LinkFactory.buildShDatatypeLink(shDatatype);
		} else if (shNodeKind != null) {
			return LinkFactory.buildShNodeKindLink(shNodeKind);
		}
		
		return l;
	}
	


	
	public Link buildShQualifiedValueShape(Resource shQualifiedValueShape) {
		
		PropertyShape qualifiedvaludShapeObject = new PropertyShape(shQualifiedValueShape);

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
