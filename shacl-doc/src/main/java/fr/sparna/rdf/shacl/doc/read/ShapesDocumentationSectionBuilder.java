package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDFS;

import fr.sparna.rdf.shacl.doc.ModelReadingUtils;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PropertyShape;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;

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
		currentSection.setSectionId(nodeShape.getShortFormOrId());
		// if the node shape is itself a class, set its subtitle to the URI
		if(nodeShape.isAClass()) {
			currentSection.setSubtitleUri(nodeShape.getNodeShape().getURI());
		}
		
		// title : either skos:prefLabel or rdfs:label or the URI short form
		currentSection.setTitle(nodeShape.getDisplayLabel(owlGraph, lang));
		
		// rdfs:comment
		currentSection.setDescription(nodeShape.getDisplayDescription(owlGraph, lang));
		
		// sh:targetClass
		if(nodeShape.getShTargetClass() != null) {
			currentSection.setTargetClass(
				new Link(
						nodeShape.getShTargetClass().getURI(),
						// label of link is the label if known, otherwise it is the short form
						nodeShape.getShTargetClass().getModel().shortForm(nodeShape.getShTargetClass().getURI())
				)				
			);
		}
		
		// sparql target
		if(nodeShape.getShTargetShSelect() != null) {
			currentSection.setSparqlTarget(nodeShape.getShTargetShSelect().getString());
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
		currentSection.setSkosExample((nodeShape.getSkosExample() != null)?nodeShape.getSkosExample().toString():null);
		
		// rdfs:subClassOf
		currentSection.setSuperClasses(nodeShape.getRdfsSubClassOf().stream()
				.filter(r -> allNodeShapes.stream().anyMatch(ns -> ns.getNodeShape().toString().equals(r.toString())))
				.map(r -> {
					// use the label if present, otherwise use the short form
					String label = ModelReadingUtils.readLiteralInLangAsString(r, RDFS.label, lang);
					if(label != null) {
						return new Link(
								"#"+r.getModel().shortForm(r.getURI()),
								label
						);
					} else {
						return new Link(
								"#"+r.getModel().shortForm(r.getURI()),
								r.getModel().shortForm(r.getURI())
						);
					}
		}).collect(Collectors.toList()));
		
		// Read the property shapes
		List<PropertyShapeDocumentation> ListPropriete = new ArrayList<>();
		for (PropertyShape propriete : nodeShape.getProperties()) {
			PropertyShapeDocumentation psd = PropertyShapeDocumentationBuilder.build(propriete, allNodeShapes, shaclGraph, owlGraph, lang);				
			ListPropriete.add(psd);
		}
		
		// this items 
		currentSection.setColor("");
		currentSection.setMessageSeverities(null);
		
		currentSection.setPropertySections(ListPropriete);

		return currentSection;
	}
	
}
