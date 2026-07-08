package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.rdf.model.Literal;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.MarkdownRenderer;
import fr.sparna.rdf.shacl.doc.PlantUmlSourceGenerator;
import fr.sparna.rdf.jena.shacl.ShapesGraph;
import fr.sparna.rdf.shacl.doc.UsageDoc;
import fr.sparna.rdf.shacl.doc.model.Depiction;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.PropertyShapesGroupDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationDiagram;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import fr.sparna.rdf.shacl.doc.model.UsageOutput;
import net.sourceforge.plantuml.board.BNode;

import fr.sparna.rdf.jena.shacl.Shape;
import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.PropertyShape;
import fr.sparna.rdf.jena.shacl.ShOrderComparator;

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
			currentSection.setSubtitleUri(new Link(nodeShape.getShape().getURI(), nodeShape.getShape().getURI()));
		} else {
			currentSection.setSubtitleUri(new Link(null, nodeShape.getShape().getURI()));
		}
		
		// title : either skos:prefLabel or rdfs:label or the URI short form
		currentSection.setTitle(nodeShape.getDisplayLabel(owlGraph, lang));
		
		// rdfs:comment
		String renderedMd = MarkdownRenderer.getInstance().renderMarkdown(nodeShape.getDisplayDescription(owlGraph, lang));
		currentSection.setDescription(renderedMd);

		if (readDiagram) {
			// Create one diagram for each section
			List<PlantUmlDiagramOutput> plantUmlDiagrams = this.diagramGenerator.generatePlantUmlDiagramSection(nodeShape.getShape());
			// turn diagrams into output data structure
			plantUmlDiagrams.stream().forEach(d -> currentSection.getSectionDiagrams().add(new ShapesDocumentationDiagram(d)));
		}
		
		// Get type of shape Main or Supportive Entities
		if (nodeShape.getShaclPlayMain().isPresent()){
			currentSection.setMainToc(nodeShape.getShaclPlayMain().get().getBoolean());
		} else {
			currentSection.setMainToc(isMainEntity(nodeShape));
		}
		
		// Get sh:node as type of shape
		if (nodeShape.getShNodeAsList().size() > 0) {			
			nodeShape.getShNodeAsList().forEach(shNode -> {
				currentSection.setShNode(
					buildShNodeLink(shNode, shapesGraph.getAllNodeShapes(), owlGraph, lang)	
				);
			});
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
		if (nodeShape.getSparqlConstraint().size() > 0) {
			currentSection.setSparqlConstraints(nodeShape.getSparqlConstraint());
		}
		
		// sh:pattern
		currentSection.setPattern(nodeShape.getShPattern().isPresent() ?nodeShape.getShPattern().get().getString():null);
		
		// sh:nodeKind
		Resource nkSection = nodeShape.getShNodeKind().isPresent() ? nodeShape.getShNodeKind().get().asResource() : null;
		currentSection.setNodeKind(PropertyShapeDocumentationBuilder.renderNodeKind(nkSection));
		
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
		
		// foaf:depiction
		currentSection.setDepictions(this.readFoafDepiction(nodeShape));
		
		// Usage
		currentSection.setUsages(findNodeShapeUsage(shapesGraph,nodeShape,shaclGraph,lang));

		
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

	public List<Depiction> readFoafDepiction(NodeShape nodeShape) {
		
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
		
		depictionsResources.sort(new ShOrderComparator());
		
		List<Depiction> depictions = new ArrayList<>();
		for (Resource r : depictionsResources) {
			Depiction aDepiction = new Depiction();
			aDepiction.setSrc(r.getURI());
			aDepiction.setShorder(ShOrderComparator.getShOrderOf(r));
			
			// dcterms:title
			Optional.ofNullable(r.getProperty(DCTerms.title)).map(s -> s.getString()).ifPresent(title -> aDepiction.setTitle(title));
			
			// dcterms:description
			Optional.ofNullable(r.getProperty(DCTerms.description)).map(s -> s.getString()).ifPresent(title -> aDepiction.setDescription(title));
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
		PropertyShapeDocumentationBuilder pBuidler = new PropertyShapeDocumentationBuilder(shapesGraph.getAllNodeShapes(), shaclGraph, owlGraph, lang);	

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

			if (superShape != null && !nodeShape.getShape().equals(superShape.getShape())) {			
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

	public Link buildShNodeLink(Resource shNode, List<NodeShape> allNodeShapes, Model owlGraph, String lang) {
		for(NodeShape aBox : allNodeShapes) {
			// using toString instead of getURI so that it works with anonymous nodeshapes
			if(aBox.getShape().toString().equals(shNode.toString())) {
				return new Link("#"+aBox.getShortFormOrId(), aBox.getDisplayLabel(owlGraph, lang));
			}
		}

		// default link if shape not found
		return buildDefaultLink(shNode);
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

	private boolean isMainEntity(NodeShape ns) {
		if (!ns.getTargetSubjectsOf().isEmpty()) {
			return true;
		} else if (!ns.getTargetObjectsOf().isEmpty()){
			return true;
		} else if (ns.getAllTargetedClasses().size() > 0) {
			return true;
		} else if (ns.getShTargetShSelect() != null) {
			return true;
		} else if (ns.isClassShape()) {
			return true;
		} else {
			return false;
		}
	}
	
	public List<UsageOutput> findNodeShapeUsage (ShapesGraph shapesGraph, NodeShape nodeShape, Model shacModel, String lang) {

		List<Shape> shapes = nodeShape.getUsage();	
		List<UsageDoc> nsUsageAsList = new ArrayList<>();

		// Populate
		for (Shape shape : shapes) {
			if (shape instanceof PropertyShape) {				
				PropertyShape ps = (PropertyShape) shape;
				// Find Node Shape Usage Doc
				PropertyShape psDocUsage = new PropertyShape(ps.getShape());

				// potentially more than 1 NodeShape if the property shape is shared between several NodeShapes
				List<NodeShape> nsUsage = shapesGraph.findNodeShapeByPropertyShape(psDocUsage);
				
				for (NodeShape nodeShape_usageDoc : nsUsage) {
					// do we already have this node shape in the list? If not, add it with the property shape, otherwise just add the property shape to the existing node shape
					boolean nsInList = nsUsageAsList.stream().filter( nsdoc -> nsdoc.getNodeShape().getShape().getURI().equals(nodeShape_usageDoc.getShape().getURI())).findFirst().isPresent();
					if (!nsInList) {
						UsageDoc usDoc = new UsageDoc(new NodeShape(nodeShape_usageDoc.getShape()));
						usDoc.getProperties().add(psDocUsage);
						nsUsageAsList.add(usDoc);
					} else {
						// find the entry corresponding to this node shape
						for (UsageDoc nsResource : nsUsageAsList) {
							if (nsResource.getNodeShape().getShape().getURI().equals(nodeShape_usageDoc.getShape().getURI())) {
								nsResource.getProperties().add(psDocUsage);
							}
						}
					}
				}				
			} else if (shape instanceof NodeShape) {
				NodeShape ns = (NodeShape) shape;
				boolean nsInList = nsUsageAsList.stream().filter( nsdoc -> nsdoc.getNodeShape().getShape().getURI().equals(ns.getShape().getURI())).findFirst().isPresent();
				if (!nsInList) {
					UsageDoc usDoc = new UsageDoc(new NodeShape(ns.getShape()));
					nsUsageAsList.add(usDoc);					
				}
			}
		}

		List<UsageOutput> outputUsage = this.getUsageOutput(nsUsageAsList, shacModel, lang);
		
		return outputUsage;
	}


	private List<UsageOutput> getUsageOutput(List<UsageDoc> nsUsageAsList, Model shacModel, String lang) {

		List<UsageOutput> output = new ArrayList();
		// Sort Usage Doc
		if (nsUsageAsList.size() > 0) {
			// Sort
			nsUsageAsList.sort(((UsageDoc arg0, UsageDoc arg1) -> {
				if (arg0.getNodeShape().getShOrderAsLiteral().isPresent()) {
					if (arg1.getNodeShape().getShOrderAsLiteral().isPresent()) {
						return ((arg0.getNodeShape().getShOrderAsLiteral().get().getDouble() - arg1.getNodeShape().getShOrderAsLiteral().get().getDouble()) > 0)?1:-1;
					} else {
						return -1;
					}
				} else {
					if (arg1.getNodeShape().getShOrderAsLiteral().isPresent()) {
						return 1;
					} else {
						return arg0.getNodeShape().getDisplayLabel(shacModel, lang).compareToIgnoreCase(arg1.getNodeShape().getDisplayLabel(shacModel, lang));
					}
				}
			} ));

			for (UsageDoc usgae_doc : nsUsageAsList) {
			
				//Sort Properties	
				if (usgae_doc.getProperties().size() > 1) {		
					usgae_doc.getProperties()
					.sort((ps1,ps2) -> {
						if(ps1.getShOrder().isPresent()) {
							if(ps2.getShOrder().isPresent()) {
								return (ps1.getShOrderAsLiteral().get().getDouble() - ps2.getShOrderAsLiteral().get().getDouble()) > 0?1:-1;
							} else {
								return -1;
							}
						} else {
							if(ps2.getShOrder() != null) {
								return 1;
							} else {
								// both sh:order are null, try with sh:name
								return ps1.getSortOrderKey(shacModel, lang).compareToIgnoreCase(ps2.getSortOrderKey(shacModel, lang));
							}
						}
					});

				}

				UsageOutput uOutput = new UsageOutput();
				if (usgae_doc.getProperties().size() > 0 ) {
					List<Link> linkUsage = usgae_doc.getProperties()
						.stream()
						.map((ps -> new Link("#"+ps.getShape().getModel().shortForm(ps.getURIOrId()) , ps.getDisplayLabel(shacModel, lang))))
						.collect(Collectors.toList());
				
						uOutput.setNodeshape_name(usgae_doc.getNodeShape().getDisplayLabel(shacModel, lang));
						uOutput.setProperties_usage(linkUsage);
						
				} else {
					uOutput.setNodeshape_link(new Link("#"+usgae_doc.getNodeShape().getShape().getModel().shortForm(usgae_doc.getNodeShape().getURIOrId()), usgae_doc.getNodeShape().getDisplayLabel(shacModel, lang)));			
				} 
				output.add(uOutput);
			}
		}
		return output;
	}
}
