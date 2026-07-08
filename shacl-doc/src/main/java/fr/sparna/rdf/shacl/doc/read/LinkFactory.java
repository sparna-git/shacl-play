package fr.sparna.rdf.shacl.doc.read;

import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;


import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.PropertyPath;
import fr.sparna.rdf.jena.shacl.ShapesGraph;
import fr.sparna.rdf.shacl.doc.model.Link;
import net.sourceforge.plantuml.board.BNode;

public class LinkFactory {
   
    public static Link buildDefaultLink(RDFNode node) {
		Link l = new Link();

		if (node instanceof Literal) {
			Literal lt = node.asLiteral();
			l.setLabel(lt.getLexicalForm());
		} else if (node instanceof Resource) {
			l.setHref(node.asResource().getURI());
			l.setLabel(ModelRenderingUtils.render(node, true));
		} else if (node instanceof BNode) {
			l.setLabel(ModelRenderingUtils.render(node, true));
		}		
		return l;
	}

    /**
     * Builds a link for a sh:node or other shape reference, such as rdfs:subClassOf between ShapesClasses
     */
    public static Link buildShNodeOrOtherShapeReferenceLink(Resource shNode, ShapesGraph shapesGraph, Model owlGraph, String lang) {
		NodeShape nodeShape = shapesGraph.findNodeShapeByResource(shNode);
		if(nodeShape != null) {
			return new Link("#"+nodeShape.getShortFormOrId(), nodeShape.getDisplayLabel(owlGraph, lang));
		} else {
			// default link if shape not found
			return LinkFactory.buildDefaultLink(shNode);
		}	
	}


	public static Link buildShClassLink(Resource shClass, ShapesGraph shapesGraph, Model owlGraph, String lang) {
        List<NodeShape> nodeShapes = shapesGraph.findNodeShapeByTargetClass(shClass);

		if(nodeShapes != null && !nodeShapes.isEmpty()) {
			return new Link("#"+nodeShapes.get(0).getShortFormOrId(), nodeShapes.get(0).getDisplayLabel(owlGraph, lang));
		} else {
			// default link if class not found
			return LinkFactory.buildDefaultLink(shClass);
		}
	}

	public static Link buildShDatatypeLink(Resource shDatatype) {
		if(
			!shDatatype.asResource().getURI().startsWith(XSD.NS)
			&&
			!shDatatype.asResource().getURI().startsWith(RDF.uri)
		) {
			return LinkFactory.buildDefaultLink(shDatatype);
		} else {
			// avoid putting a link to well-known datatypes
			return new Link(null, ModelRenderingUtils.render(shDatatype, true));
		}
	}

	public static Link buildShNodeKindLink(Resource shNodeKind) {
		// avoid putting a link to node kinds
		return new Link(null, renderNodeKind(shNodeKind));
	}

    public static Link buildPropertyPathLink(PropertyPath path) {			
		if(path != null && path.getResource().isURIResource()) {
			return new Link(
					path.getResource().getURI(),
					path.renderSparqlPropertyPath()
			);			
		} else {
			return new Link(
					null,
					path.renderSparqlPropertyPath()
			);
		}
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
