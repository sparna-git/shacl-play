package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDFS;

import fr.sparna.rdf.shacl.doc.ConstraintValueReader;
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
		currentSection.setSectionId(nodeShape.getShortForm());
		// if the node shape is itself a class, set its subtitle to the URI
		if(nodeShape.isAClass()) {
			currentSection.setSubtitleUri(nodeShape.getNodeShape().getURI());
		}
		
		// title : either rdfs:label or the URI short form
		currentSection.setTitle((nodeShape.getRdfsLabel() != null && !nodeShape.getRdfsLabel().isEmpty()) ? nodeShape.getRdfsLabel() : nodeShape.getShortForm());
		
		// rdfs:comment
		currentSection.setDescription(nodeShape.getRdfsComment());
		
		// sh:targetClass
		if(nodeShape.getShTargetClass() != null) {
			currentSection.setTargetClass(
				new Link(
						nodeShape.getShTargetClass().getURI(),
						// label of link is the short form of the class URI
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
					if(r.hasProperty(RDFS.label, lang)) {
						return new Link(
								"#"+r.getModel().shortForm(r.getURI()),
								ConstraintValueReader.readLiteralInLangAsString(r, RDFS.label, lang)
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
		
		currentSection.setPropertySections(ListPropriete);

		return currentSection;
	}
	
}
