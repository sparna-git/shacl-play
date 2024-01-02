package fr.sparna.rdf.shacl.doc.read;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PropertyShape;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;


public class PropertyShapeDocumentationBuilder {

	public static PropertyShapeDocumentation build(
			PropertyShape propertyShape,
			List<NodeShape> allNodeShapes,
			Model shaclGraph,
			Model owlGraph,
			String lang) {
		// Start building final structure
		PropertyShapeDocumentation proprieteDoc = new PropertyShapeDocumentation();
		proprieteDoc.setLabel(propertyShape.getDisplayLabel(shaclGraph.union(owlGraph), lang));
		// URI in the documentation
		proprieteDoc.setPropertyUri(buildPathLink(propertyShape));
		// full URI
		proprieteDoc.setPropertyShapeUriOrId(propertyShape.getURIOrId());		
		
		proprieteDoc.setCardinalite(renderCardinalities(propertyShape.getShMinCount(), propertyShape.getShMaxCount()));
		proprieteDoc.setDescription(propertyShape.getDisplayDescription(shaclGraph.union(owlGraph), lang));
		
		if(propertyShape.getShNode() != null) {
			for(NodeShape aBox : allNodeShapes) {
				// using toString instead of getURI so that it works with anonymous nodeshapes
				if(aBox.getNodeShape().toString().equals(propertyShape.getShNode().toString())) {
					proprieteDoc.getExpectedValue().setLinkNodeShapeUri(aBox.getShortFormOrId());
					proprieteDoc.getExpectedValue().setLinkNodeShape(aBox.getDisplayLabel(owlGraph, lang));	
				}
			}
		}
		
		proprieteDoc.getExpectedValue().setExpectedValueLabel(selectExpectedValueLabel(
				propertyShape.getShClass(),
				propertyShape.getShNode(),
				propertyShape.getShDatatype(),
				propertyShape.getShNodeKind(),
				propertyShape.getShHasValue()
		));
		proprieteDoc.setExpectedValueAdditionnalInfoIn(ModelRenderingUtils.render(propertyShape.getShIn(), false));
		
		if(propertyShape.getShClass() != null) {
			for(NodeShape aNodeShape : allNodeShapes) {
				if(aNodeShape.getShTargetClass() != null && aNodeShape.getShTargetClass().getURI().equals(propertyShape.getShClass().getURI())) {
					proprieteDoc.getExpectedValue().setLinkNodeShapeUri(aNodeShape.getShortFormOrId()); //aName.getShortForm().getLocalName()
					proprieteDoc.getExpectedValue().setLinkNodeShape(aNodeShape.getDisplayLabel(owlGraph, lang));
					break;
					// checks that the URI of the NodeShape is itself equal to the sh:class
					// add a check to work only with named URI node shapes
				} else if (aNodeShape.getNodeShape().isURIResource() && aNodeShape.getNodeShape().getURI().equals(propertyShape.getShClass().getURI())) {
					proprieteDoc.getExpectedValue().setLinkNodeShapeUri(aNodeShape.getShortFormOrId()); //aName.getShortForm().getLocalName()
					proprieteDoc.getExpectedValue().setLinkNodeShape(aNodeShape.getDisplayLabel(owlGraph, lang));
					break;
				}
			}					
		}
		
		// create a String of comma-separated short forms
		proprieteDoc.getExpectedValue().setOr(ModelRenderingUtils.render(propertyShape.getShOr(), false));
		
		return proprieteDoc;
	}
	
	public static Link buildPathLink(PropertyShape prop) {			
		if(prop.getShPath().isURIResource()) {
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
	
	public static String selectExpectedValueLabel(
			Resource shClass,
			Resource shNode,
			Resource shDatatype,
			Resource shNodeKind,
			RDFNode shHasValue
	) {
		String value = null;

		if (shHasValue != null) {
			value = ModelRenderingUtils.render(shHasValue, false);
		} else if (shClass != null) {
			value = ModelRenderingUtils.render(shClass, false);
		} else if (shNode != null) {
			value = ModelRenderingUtils.render(shNode, false);
		} else if (shDatatype != null) {
			value = ModelRenderingUtils.render(shDatatype, false);
		} else if (shNodeKind != null) {
			value = renderNodeKind(shNodeKind);
		}
		
		return value;
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
	
}
