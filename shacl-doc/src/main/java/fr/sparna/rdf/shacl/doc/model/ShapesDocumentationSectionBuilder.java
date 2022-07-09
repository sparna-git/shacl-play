package fr.sparna.rdf.shacl.doc.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PropertyShape;

public class ShapesDocumentationSectionBuilder {

	public static ShapesDocumentationSection build(
			NodeShape nodeShape,
			List<NodeShape> allNodeShapes,
			Model shaclGraph,
			Model owlGraph,
			String lang
	) {
		ShapesDocumentationSection currentSection = new ShapesDocumentationSection();
		
		// URI
		currentSection.setUri(nodeShape.getShortForm());
		
		// title : either rdfs:label or the URI short form
		currentSection.setTitle(nodeShape.getRdfsLabel() != null ? nodeShape.getRdfsLabel() : nodeShape.getShortForm());
		
		// rdfs:comment
		currentSection.setDescription(nodeShape.getRdfsComment());
		
		// sh:targetClass
		if(nodeShape.getShTargetClass() != null) {
			currentSection.setTargetClassLabel(nodeShape.getShTargetClass().getModel().shortForm(nodeShape.getShTargetClass().getURI()));
			currentSection.setTargetClassUri(nodeShape.getShTargetClass().getURI());
		}
		
		// sh:pattern
		currentSection.setPattern(nodeShape.getShPattern() != null?nodeShape.getShPattern().getString():null);
		
		// sh:nodeKind
		currentSection.setNodeKind(PropertyShapeDocumentationBuilder.renderNodeKind(nodeShape.getShNodeKind()));
		
		// sh:closed
		if(nodeShape.getShClosed() != null) {
			currentSection.setClosed(nodeShape.getShClosed());
		}
		
		// skos:example
		currentSection.setSkosExample(nodeShape.getSkosExample());
		
		
		// Read the property shapes
		List<PropertyShapeDocumentation> ListPropriete = new ArrayList<>();
		for (PropertyShape propriete : nodeShape.getProperties()) {
			PropertyShapeDocumentation psd = PropertyShapeDocumentationBuilder.build(propriete, allNodeShapes, shaclGraph, owlGraph, lang);				
			ListPropriete.add(psd);
		}
		
		currentSection.setPropertySections(ListPropriete);

		return currentSection;
	}
	
}
