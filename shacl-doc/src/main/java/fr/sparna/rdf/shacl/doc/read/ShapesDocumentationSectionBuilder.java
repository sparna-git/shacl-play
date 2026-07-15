package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.MarkdownRenderer;
import fr.sparna.rdf.shacl.doc.PlantUmlSourceGenerator;
import fr.sparna.rdf.jena.shacl.ShapesGraph;
import fr.sparna.rdf.shacl.doc.UsageDoc;
import fr.sparna.rdf.shacl.doc.model.ConstraintEntry;
import fr.sparna.rdf.shacl.doc.model.Depiction;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.PropertyShapesGroupDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationDiagram;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import fr.sparna.rdf.shacl.doc.model.Usage;

import fr.sparna.rdf.jena.shacl.Shape;
import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.PropertyShape;

public class ShapesDocumentationSectionBuilder {
	
	private PlantUmlSourceGenerator diagramGenerator;

	public ShapesDocumentationSectionBuilder(PlantUmlSourceGenerator diagramGenerator) {
		this.diagramGenerator = diagramGenerator;
	}

	public ShapesDocumentationSection build(
			NodeShape nodeShape,
			ShapesGraph shapesGraph,
			Model shaclGraph,
			Model owlGraph,
			String lang,
			boolean readDiagram
	) {
		ShapesDocumentationSection currentSection = new ShapesDocumentationSection();
		
		// URI or blank node ID
		currentSection.setNodeShapeUriOrId(nodeShape.getURIOrId());
		currentSection.setSectionId(nodeShape.getShortFormOrId());
		// if the node shape is itself a class, set its subtitle to the URI
		if(nodeShape.isClassShape()) {
			currentSection.setSubtitleUri(new Link(nodeShape.getResource().getURI(), nodeShape.getResource().getURI()));
		} else {
			currentSection.setSubtitleUri(new Link(null, nodeShape.getResource().getURI()));
		}
		
		// title : either skos:prefLabel or rdfs:label or the URI short form
		currentSection.setTitle(nodeShape.getDisplayLabel(owlGraph, lang));
		
		// rdfs:comment
		String renderedMd = MarkdownRenderer.getInstance().renderMarkdown(nodeShape.getDisplayDescription(owlGraph, lang));
		currentSection.setDescription(renderedMd);

		if (readDiagram) {
			// Create one diagram for each section
			List<PlantUmlDiagramOutput> plantUmlDiagrams = this.diagramGenerator.generatePlantUmlDiagramSection(nodeShape.getResource());
			// turn diagrams into output data structure
			plantUmlDiagrams.stream().forEach(d -> currentSection.getSectionDiagrams().add(new ShapesDocumentationDiagram(d)));
		}
		
		// Get type of shape Main or Supportive Entities
		if (nodeShape.getShaclPlayMain().isPresent()){
			currentSection.setMainToc(nodeShape.getShaclPlayMain().get().getBoolean());
		} else {
			currentSection.setMainToc(nodeShape.hasTarget());
		}
		
		// Get sh:node as type of shape
		List<Resource> shNodes = nodeShape.getShNodeAsList();
		if (shNodes.size() > 0) {
			currentSection.setShNodes(shNodes.stream()
					.map(shNode -> LinkFactory.buildShNodeOrOtherShapeReferenceLink(shNode, shapesGraph, owlGraph, lang))
					.collect(Collectors.toList()));
		}


		// sh:targetSubjectsOf or sh:targetObjectsOf
		if (!nodeShape.getTargetSubjectsOf().isEmpty()) {
			currentSection.setTargetSubjectsOf(nodeShape.getTargetSubjectsOf().get(0).getURI());
		}		
		if (!nodeShape.getTargetObjectsOf().isEmpty()) {
			currentSection.setTargetObjectsOf(nodeShape.getTargetObjectsOf().get(0).getURI());
		}
		
		// sh:targetClass
		if(nodeShape.getAllTargetedClasses().size() > 0) {
			
			// Create List<Link>
			List<Link> tClass = nodeShape.getAllTargetedClasses()
									.stream()
									.map(s -> 
										new Link(s.getURI(),
												// label of link is the label if known, otherwise it is the short form
												s.getModel().shortForm(s.getURI())
												)											
											)
									.collect(Collectors.toList());
			
			currentSection.setTargetClass(tClass);
		}
		
		// sparql target
		if(nodeShape.getShTargetShSelect() != null) {
			currentSection.setSparqlTarget(nodeShape.getShTargetShSelect().getString());
		}
		
		// SPARQL CONSTRAINT
		if (nodeShape.getShSparql().size() > 0) {
			currentSection.setConstraintEntries(nodeShape.getShSparql().stream()
					.map(sc -> new ConstraintEntry(sc, lang))
					.collect(Collectors.toList()));
		}
		
		// sh:pattern
		currentSection.setPattern(nodeShape.getShPattern().isPresent() ?nodeShape.getShPattern().get().getString():null);
		
		// sh:nodeKind
		Resource nkSection = nodeShape.getShNodeKind().isPresent() ? nodeShape.getShNodeKind().get().asResource() : null;
		currentSection.setNodeKind(LinkFactory.renderNodeKind(nkSection));
		
		// sh:closed
		if(nodeShape.getShClosed().isPresent()) {
			currentSection.setClosed(nodeShape.getShClosed().get().getBoolean());
		}
		
		// skos:example
		String exampleString = nodeShape.getSkosExample().stream().map(example -> example.toString()).collect(Collectors.joining("; "));
		currentSection.setSkosExample(exampleString.equals("")?null:exampleString);
		
		// rdfs:subClassOf if shape is also a class
		currentSection.setSuperClasses(nodeShape.getRdfsSubClassOf().stream()
				.filter(r -> shapesGraph.findNodeShapeByResource(r) != null)
				.map(r -> LinkFactory.buildShNodeOrOtherShapeReferenceLink(r, shapesGraph, owlGraph, lang))
				.collect(Collectors.toList()));
		
		// shapes targeting a super-class of this shape target
		// --> ontological superclasses
		if(currentSection.getSuperClasses() == null || currentSection.getSuperClasses().size() == 0) {
			currentSection.setSuperClasses(nodeShape.getShTargetClassRdfsSubclassOfInverseOfShTargetClass().stream()
					.filter(r -> shapesGraph.findNodeShapeByResource(r) != null)
					.map(r -> LinkFactory.buildShNodeOrOtherShapeReferenceLink(r, shapesGraph, owlGraph, lang))
					.collect(Collectors.toList()));
		}
		
		// foaf:depiction
		currentSection.setDepictions(this.readFoafDepiction(nodeShape, lang));
		
		// Usage
		currentSection.setUsages(findNodeShapeUsage(shapesGraph,nodeShape,shaclGraph,owlGraph,lang));

		
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

	public List<Depiction> readFoafDepiction(NodeShape nodeShape, String lang) {
		
		//List<Statement> depic = nodeShape.listProperties(FOAF.depiction).toList();
		List<Resource> depictionsResources = new ArrayList<>();

		for (Resource aDepiction : nodeShape.getDepiction()) {
			if(
				aDepiction.getURI() != null
				&&
				(
					aDepiction.getURI().contains(".jpg")
					||
					aDepiction.getURI().contains(".png")
				)
			)
			depictionsResources.add(aDepiction);					
		}
		
		depictionsResources.sort(new Shape.ShOrderComparator());
		
		List<Depiction> depictions = new ArrayList<>();
		for (Resource r : depictionsResources) {
			Depiction aDepiction = new Depiction();
			aDepiction.setSrc(r.getURI());
			aDepiction.setShorder(Shape.ShOrderComparator.getShOrderOf(r));
			
			// dcterms:title
			aDepiction.setTitle(ModelReadingUtils.readLiteralInLangAsString(r, DCTerms.title, lang));
			
			// dcterms:description
			aDepiction.setDescription(ModelReadingUtils.readLiteralInLangAsString(r, DCTerms.description, lang));
			depictions.add(aDepiction);
		}
		
		return depictions;	
	}
	
	static List<PropertyShapesGroupDocumentation> readPropertyGroupsRec(
			NodeShape nodeShape,
			ShapesGraph shapesGraph,
			Model shaclGraph,
			Model owlGraph,
			String lang
	) {
		List<PropertyShapesGroupDocumentation> groups = new ArrayList<>();
		
		// add a group for this current node shape
		PropertyShapesGroupDocumentation thisGroup = new PropertyShapesGroupDocumentation();
		thisGroup.setTargetClass(new Link("#"+nodeShape.getShortFormOrId(),nodeShape.getDisplayLabel(owlGraph, lang)));

		List<PropertyShapeDocumentation> properties = new ArrayList<>();
		PropertyShapeDocumentationBuilder pBuidler = new PropertyShapeDocumentationBuilder(shapesGraph, shaclGraph, owlGraph, lang);	

		for (PropertyShape aPropertyShape : nodeShape.getProperties()) {
			PropertyShapeDocumentation psd = pBuidler.build(
				aPropertyShape,
				nodeShape);				
			properties.add(psd);
		}
		thisGroup.setProperties(properties);
		groups.add(thisGroup);
		
		// then recurse up
		List<Resource> superShapes = nodeShape.getSuperShapes();

		for (Resource aSuperShape : superShapes) {
			// find corresponding node shape
			NodeShape superShape = shapesGraph.findNodeShapeByResource(aSuperShape) != null ? shapesGraph.findNodeShapeByResource(aSuperShape) : null ;

			if (superShape != null && !nodeShape.getResource().equals(superShape.getResource())) {			
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
	

	
	public List<Usage> findNodeShapeUsage (ShapesGraph shapesGraph, NodeShape nodeShape, Model shacModel, Model owlModel, String lang) {

		List<Shape> incomingShapes = nodeShape.getUsage();	
		List<UsageDoc> nsUsageAsList = new ArrayList<>();

		// Populate
		for (Shape shape : incomingShapes) {
			if (shape instanceof PropertyShape) {				
				PropertyShape ps = (PropertyShape) shape;

				// potentially more than 1 NodeShape if the property shape is shared between several NodeShapes
				List<NodeShape> nsUsage = shapesGraph.findNodeShapeByPropertyShape(ps);
				
				for (NodeShape aNodeShapeWithThisPropertyShape : nsUsage) {
					// do we already have this node shape in the list? If not, add it with the property shape, otherwise just add the property shape to the existing node shape
					boolean nsInList = nsUsageAsList.stream().filter( nsdoc -> nsdoc.getNodeShape().getResource().equals(aNodeShapeWithThisPropertyShape.getResource())).findFirst().isPresent();
					if (!nsInList) {
						UsageDoc usDoc = new UsageDoc(aNodeShapeWithThisPropertyShape);
						usDoc.getProperties().add(ps);
						nsUsageAsList.add(usDoc);
					} else {
						// find the entry corresponding to this node shape
						for (UsageDoc nsResource : nsUsageAsList) {
							if (nsResource.getNodeShape().getResource().equals(aNodeShapeWithThisPropertyShape.getResource())) {
								nsResource.getProperties().add(ps);
							}
						}
					}
				}				
			} else if (shape instanceof NodeShape) {
				NodeShape ns = (NodeShape) shape;
				boolean nsInList = nsUsageAsList.stream().filter( nsdoc -> nsdoc.getNodeShape().getResource().equals(ns.getResource())).findFirst().isPresent();
				if (!nsInList) {
					UsageDoc usDoc = new UsageDoc(new NodeShape(ns.getResource()));
					nsUsageAsList.add(usDoc);					
				}
			}
		}

		List<Usage> outputUsage = this.getUsageOutput(nsUsageAsList, shacModel, owlModel, lang);
		
		return outputUsage;
	}


	private List<Usage> getUsageOutput(List<UsageDoc> nsUsageAsList, Model shacModel, Model owlModel, String lang) {

		List<Usage> output = new ArrayList<>();
		// Sort Usage Doc
		if (nsUsageAsList.size() > 0) {
			Shape.ShapeDisplayLabelComparator comparator = new Shape.ShapeDisplayLabelComparator(owlModel, lang);
			// Sort
			nsUsageAsList.sort(((UsageDoc arg0, UsageDoc arg1) -> { return comparator.compare(arg0.getNodeShape(), arg1.getNodeShape()); } ));

			for (UsageDoc usgae_doc : nsUsageAsList) {			
				//Sort Properties		
				usgae_doc.getProperties().sort(new PropertyShape.PropertyShapeComparator());

				Usage uOutput = new Usage();
				if (usgae_doc.getProperties().size() > 0 ) {
					List<Link> linkUsage = usgae_doc.getProperties()
						.stream()
						.map((ps -> new Link(
							"#"+PropertyShapeDocumentationBuilder.buildPropertyShapeSectionId(usgae_doc.getNodeShape(), ps),
							// we need to avoid an empty label here otherwise ReSpec complains, so we default to the short form of the property shape if no label is found
							ps.getDisplayLabel(shacModel, lang).equals("")?ps.getResource().getModel().shortForm(ps.getShPath().getURI()):ps.getDisplayLabel(shacModel, lang)
						))).collect(Collectors.toList());
				
						uOutput.setNodeshape_name(usgae_doc.getNodeShape().getDisplayLabel(shacModel, lang));
						uOutput.setProperties_usage(linkUsage);						
				} else {
					uOutput.setNodeshape_link(new Link("#"+usgae_doc.getNodeShape().getResource().getModel().shortForm(usgae_doc.getNodeShape().getURIOrId()), usgae_doc.getNodeShape().getDisplayLabel(shacModel, lang)));			
				} 
				output.add(uOutput);
			}
		}
		return output;
	}
}
