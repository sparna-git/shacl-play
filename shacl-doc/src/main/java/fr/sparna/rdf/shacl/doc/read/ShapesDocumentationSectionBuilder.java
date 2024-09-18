package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PropertyShape;
import fr.sparna.rdf.shacl.doc.ShapesGraph;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.PropertyShapesGroupDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;

public class ShapesDocumentationSectionBuilder {

	public static ShapesDocumentationSection build(
			NodeShape nodeShape,
			ShapesGraph shapesGraph,
			Model shaclGraph,
			Model owlGraph,
			String lang
	) {
		ShapesDocumentationSection currentSection = new ShapesDocumentationSection();
		
		// URI or blank node ID
		currentSection.setNodeShapeUriOrId(nodeShape.getURIOrId());
		currentSection.setSectionId(nodeShape.getShortFormOrId());
		// if the node shape is itself a class, set its subtitle to the URI
		currentSection.setSubtitleUri(nodeShape.getNodeShape().getURI());
		
		// title : either skos:prefLabel or rdfs:label or the URI short form
		currentSection.setTitle(nodeShape.getDisplayLabel(owlGraph, lang));
		
		// rdfs:comment
		currentSection.setDescription(nodeShape.getDisplayDescription(owlGraph, lang));
		
		// sh:targetSubjectsOf or sh:targetObjectsOf
		if (nodeShape.getShtargetSubjectsOf() != null) {
			currentSection.setTargetSubjectsOf(nodeShape.getShtargetSubjectsOf().getURI());
		}
		
		if (nodeShape.getShtargetObjectsOf() != null) {
			currentSection.setTargetObjectsOf(nodeShape.getShtargetObjectsOf().getURI());
		}
		
		// sh:targetClass
		if(nodeShape.getShTargetClass() != null) {
			
			// Create List<Link>
			List<Link> tClass = nodeShape.getShTargetClass()
									.stream()
									.map(s -> 
										new Link(s.getURI(),
												// label of link is the label if known, otherwise it is the short form
												s.getModel().shortForm(s.getURI())
												)											
											)
									.collect(Collectors.toList());
			
			
			currentSection.setTargetClass(tClass);
			
			/*
			currentSection.setTargetClass(
				new Link(
						nodeShape.getShTargetClass().getURI(),
						// label of link is the label if known, otherwise it is the short form
						nodeShape.getShTargetClass().getModel().shortForm(nodeShape.getShTargetClass().getURI())
				)				
			);
			*/
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
		
		// rdfs:subClassOf if shape is also a class
		currentSection.setSuperClasses(nodeShape.getRdfsSubClassOf().stream()
				.filter(r -> shapesGraph.findNodeShapeByResource(r) != null)
				.map(r -> createLinkFromShape(r, lang))
				.collect(Collectors.toList()));
		
		// shapes targeting a super-class of this shape target
		// --> ontological superclasses
		if(currentSection.getSuperClasses() == null || currentSection.getSuperClasses().size() == 0) {
			currentSection.setSuperClasses(nodeShape.getShTargetClassRdfsSubclassOfInverseOfShTargetClass().stream()
					.filter(r -> shapesGraph.findNodeShapeByResource(r) != null)
					.map(r -> createLinkFromShape(r, lang))
					.collect(Collectors.toList()));
		}
		
		
		// Read the property shapes from this shape and supershapes
		List<PropertyShapesGroupDocumentation> groups = readPropertyGroupsRec(
				nodeShape,
				shapesGraph,
				shaclGraph,
				owlGraph,
				lang
		);			
		
		currentSection.setPropertyGroups(groups);

		return currentSection;
	}
	
	static List<PropertyShapesGroupDocumentation> readPropertyGroupsRec(
			NodeShape nodeShape,
			ShapesGraph shapesGraph,
			Model shaclGraph,
			Model owlGraph,
			String lang
	) {
		List<PropertyShapesGroupDocumentation> groups = new ArrayList<>();
		
		PropertyShapesGroupDocumentation thisGroup = new PropertyShapesGroupDocumentation();
		thisGroup.setTargetClass(new Link(
					"#"+nodeShape.getShortFormOrId(),
					nodeShape.getDisplayLabel(owlGraph, lang)
		));
		List<PropertyShapeDocumentation> properties = new ArrayList<>();
		for (PropertyShape propriete : nodeShape.getProperties()) {
			PropertyShapeDocumentation psd = PropertyShapeDocumentationBuilder.build(propriete, shapesGraph.getAllNodeShapes(), shaclGraph, owlGraph, lang);				
			properties.add(psd);
		}
		thisGroup.setProperties(properties);
		groups.add(thisGroup);
		
		// then recurse up
		List<Resource> superShapes = nodeShape.getSuperShapes();
		for (Resource aSuperShape : superShapes) {
			// find corresponding node shape
			NodeShape superShape = shapesGraph.findNodeShapeByResource(aSuperShape);
			if(superShape != null) {
				groups.addAll(readPropertyGroupsRec(
						superShape,
						shapesGraph,
						shaclGraph,
						owlGraph,
						lang
				));
			}
		}
		
		return groups;
	}
	
	static Link createLinkFromShape(Resource r, String lang) {
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
	 }
	
}
