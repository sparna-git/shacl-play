package fr.sparna.rdf.shacl.doc.read;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PropertyShape;
import fr.sparna.rdf.shacl.doc.model.ExpectedValue;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import net.sourceforge.plantuml.board.BNode;


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
		
		// Color
		if (!propertyShape.getColor().isEmpty()) {
			proprieteDoc.setColor(propertyShape.getColor().get().getString());
		}
		
		if (!propertyShape.getBackgroundColor().isEmpty()) {
			proprieteDoc.setBackgroundcolor(propertyShape.getBackgroundColor().get().getString());
		}
		
		if(propertyShape.getShNode() != null) {
			for(NodeShape aBox : allNodeShapes) {
				// using toString instead of getURI so that it works with anonymous nodeshapes
				if(aBox.getNodeShape().toString().equals(propertyShape.getShNode().toString())) {
					Link l = new Link(aBox.getShortFormOrId(), aBox.getDisplayLabel(owlGraph, lang));
					proprieteDoc.getExpectedValue().setLinkNodeShape(l);	
				}
			}
		}
		
		proprieteDoc.getExpectedValue().setExpectedValue(selectExpectedValueAsLink(
				propertyShape.getShClass(),
				propertyShape.getShNode(),
				propertyShape.getShDatatype(),
				propertyShape.getShNodeKind(),
				propertyShape.getShHasValue()
		));

		if(propertyShape.getShIn() != null) {
			List<Link> links = propertyShape.getShIn().stream().map(i -> buildLink(i)).collect(Collectors.toList());
			proprieteDoc.getExpectedValue().setInValues(links);
		}

		if(propertyShape.getShClass() != null) {
			for(NodeShape aNodeShape : allNodeShapes) {
				if(aNodeShape.getShTargetClass() != null && findShClassInShTargetClass(aNodeShape.getShTargetClass(),propertyShape.getShClass().getURI())) {
					Link l = new Link(aNodeShape.getShortFormOrId(), aNodeShape.getDisplayLabel(owlGraph, lang));
					proprieteDoc.getExpectedValue().setLinkNodeShape(l);
					break;
					// checks that the URI of the NodeShape is itself equal to the sh:class
					// add a check to work only with named URI node shapes
				} else if (aNodeShape.getNodeShape().isURIResource() && aNodeShape.getNodeShape().getURI().equals(propertyShape.getShClass().getURI())) {
					Link l = new Link(aNodeShape.getShortFormOrId(), aNodeShape.getDisplayLabel(owlGraph, lang));
					proprieteDoc.getExpectedValue().setLinkNodeShape(l);
					break;
				}
			}					
		}
		
		// Dash:LabelRol
		if (propertyShape.getDashLabelRole() != null) {
			proprieteDoc.setLabelrol(propertyShape.getDashLabelRole().asResource().getURI());
		}
		
		// create a String of comma-separated short forms
		//proprieteDoc.getExpectedValue().setOr(ModelRenderingUtils.render(propertyShape.getShOr(), false));
		proprieteDoc.getExpectedValue().setOr(propertyShape.getShOr());
		
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

	public static Link selectExpectedValueAsLink(
			Resource shClass,
			Resource shNode,
			Resource shDatatype,
			Resource shNodeKind,
			RDFNode shHasValue
	) {
		Link l = null;

		if (shHasValue != null) {
			l = buildLink(shHasValue);
		} else if (shClass != null) {
			l = buildLink(shClass);
		} else if (shNode != null) {
			l = buildLink(shNode);
		} else if (shDatatype != null) {
			if(
				!shDatatype.asResource().getURI().startsWith(XSD.NS)
				&&
				!shDatatype.asResource().getURI().startsWith(RDF.uri)
			) {
				l = buildLink(shDatatype);
			} else {
				// avoid putting a link to well-known datatypes
				l = new Link(null, ModelRenderingUtils.render(shDatatype, true));
			}
		} else if (shNodeKind != null) {
			// avoid putting a link to node kinds
			l = new Link(null, renderNodeKind(shNodeKind));
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

	public static Link buildLink(
			RDFNode node
	) {
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
	
}
