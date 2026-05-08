package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.rdf.model.Literal;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.MarkdownRenderer;
import fr.sparna.rdf.shacl.doc.NodeShapeDoc;
import fr.sparna.rdf.shacl.doc.PlantUmlSourceGenerator;
import fr.sparna.rdf.shacl.doc.PropertyShapeDoc;
import fr.sparna.rdf.shacl.doc.ShapesGraphDoc;
import fr.sparna.rdf.shacl.doc.UsageDoc;
import fr.sparna.rdf.shacl.doc.UsageOutput;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.PropertyShapesGroupDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationDiagram;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import net.sourceforge.plantuml.board.BNode;

import fr.sparna.rdf.jena.shacl.NodeShape;

public class ShapesDocumentationSectionBuilder {
	
	private PlantUmlSourceGenerator diagramGenerator;

	public ShapesDocumentationSectionBuilder(PlantUmlSourceGenerator diagramGenerator) {
		this.diagramGenerator = diagramGenerator;
	}

	public ShapesDocumentationSection build(
			NodeShapeDoc nodeShape,
			ShapesGraphDoc shapesGraph,
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
			currentSection.setSubtitleUri(new Link(nodeShape.getNodeShape().getURI(), nodeShape.getNodeShape().getURI()));
		} else {
			currentSection.setSubtitleUri(new Link(null, nodeShape.getNodeShape().getURI()));
		}
		
		// title : either skos:prefLabel or rdfs:label or the URI short form
		currentSection.setTitle(nodeShape.getDisplayLabel(owlGraph, lang));
		
		// rdfs:comment
		String renderedMd = MarkdownRenderer.getInstance().renderMarkdown(nodeShape.getDisplayDescription(owlGraph, lang));
		currentSection.setDescription(renderedMd);

		if (readDiagram) {
			// Create one diagram for each section
			List<PlantUmlDiagramOutput> plantUmlDiagrams = this.diagramGenerator.generatePlantUmlDiagramSection(nodeShape.getNodeShape());
			// turn diagrams into output data structure
			plantUmlDiagrams.stream().forEach(d -> currentSection.getSectionDiagrams().add(new ShapesDocumentationDiagram(d)));
		}
		
		// Get type of shape Main or Supportive Entities
		if (!nodeShape.getMainBoolean()) {
			currentSection.setMainToc(isMainEntity(nodeShape));
		} else { 
			currentSection.setMainToc(nodeShape.getMainBoolean());
		}
		
		// Get sh:node as type of shape
		if (nodeShape.getShNodeAsList().size() > 0) {			
			nodeShape.getShNodeAsList().forEach(shNode -> {
				currentSection.setShNode(
					buildShNodeLink(shNode, shapesGraph.getAllNodeShapesDoc(), owlGraph, lang)	
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
		
		// sparql Constraint
		if(nodeShape.getShSparqlDCTDescription() != null) {
			currentSection.setDescriptionSparql(nodeShape.getShSparqlDCTDescription().getString());
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
		
		// SH.hasValue
		//currentSection.set
		
		// skos:example
		currentSection.setSkosExample(
			nodeShape.getSkosExample().stream().map(example -> example.toString()).collect(Collectors.joining("; "))
		);
		
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
		if (nodeShape.getFoafDepiction().size() > 0) {
			currentSection.setDepictions(nodeShape.getFoafDepiction());
		}
		
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
	
	static List<PropertyShapesGroupDocumentation> readPropertyGroupsRec(
			NodeShapeDoc nodeShape,
			ShapesGraphDoc shapesGraph,
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
		PropertyShapeDocumentationBuilder pBuidler = new PropertyShapeDocumentationBuilder(shapesGraph.getAllNodeShapesDoc(), shaclGraph, owlGraph, lang);	
		for (PropertyShapeDoc aPropertyShape : nodeShape.getPropertiesDoc()) {
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
			NodeShapeDoc superShape = shapesGraph.findNodeShapeByResource(aSuperShape) != null ? new NodeShapeDoc(shapesGraph.findNodeShapeByResource(aSuperShape).getNodeShape()) : null ;
			if (superShape != null && !aSuperShape.equals(superShape.getNodeShape())) {			
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

	public Link buildShNodeLink(Resource shNode, List<NodeShapeDoc> allNodeShapes, Model owlGraph, String lang) {
		for(NodeShapeDoc aBox : allNodeShapes) {
			// using toString instead of getURI so that it works with anonymous nodeshapes
			if(aBox.getNodeShape().toString().equals(shNode.toString())) {
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

	private boolean isMainEntity(NodeShapeDoc ns) {
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
	
	public List<UsageOutput> findNodeShapeUsage (ShapesGraphDoc shapesGraph,NodeShapeDoc nodeShape, Model shacModel, String lang) {
		
		/* 
		List<UsageDoc> nsUsageAsList = new ArrayList<>();
		// Get all properties from the nodeshape
		List<Resource> propertiesUsage = nodeShape.getPropertiesUsage();
		if (propertiesUsage.size() > 0) {
			for (Resource r : propertiesUsage) {
				if (!r.isAnon() && r.isResource()) {
					List<Resource> nResourceFound = shapesGraph.findNodeShapeByProperty(r);
					//System.out.println("Number of Resouces: " + nResourceFound.size());
					for(Resource rFound : nResourceFound) {
						NodeShapeDoc nsUsage = new NodeShapeDoc(rFound);
						
						PropertyShapeDoc psDocUsage = new PropertyShapeDoc(r);
						boolean nsExist = nsUsageAsList.stream().filter( nsList -> nsList.getNodeShape().getNodeShape().getURI().equals(nsUsage.getNodeShape().getURI())).findFirst().isPresent();
						if (!nsExist) {
							UsageDoc usDoc = new UsageDoc();
							usDoc.setNodeShape(nsUsage);
							List<PropertyShapeDoc> psList = new ArrayList<>();
							psList.add(psDocUsage);
							usDoc.setProperties(psList);
							nsUsageAsList.add(usDoc);
						} else {
							Integer nCount = 0;
							for (UsageDoc nsResource : nsUsageAsList) {
								if (nsResource.getNodeShape().getNodeShape().getURI().equals(nsUsage.getNodeShape().getURI())) {
									List<PropertyShapeDoc> psList = nsResource.getProperties();
									boolean nsExistProperty = psList.stream().filter( pp -> pp.getPropertyShape().getURI().equals(psDocUsage.getPropertyShape().getURI()) ).findFirst().isPresent();
									if (!nsExistProperty) {
										List<PropertyShapeDoc> p = nsResource.getProperties();
										p.add(psDocUsage);
										nsResource.setProperties(p);
										nsUsageAsList.set(nCount, nsResource);
									}
								}
								nCount++;
							}							
						}

					}
				}
			}
		}
			*/
		List<UsageDoc> nsUsageAsList = nodeShape.getUsage();
		List<UsageOutput> showusage = new ArrayList<>();
		if (nsUsageAsList.size() > 0) {
			// Short
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

			// ort for each properties
			for (UsageDoc usgae_doc : nsUsageAsList) {
			
				//Sort Properties			
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

				if (usgae_doc.getProperties().size() > 0 ) {
					List<Link> linkUsage = usgae_doc.getProperties()
						.stream()
						.map((ps -> new Link("#"+ps.getPropertyShape().getModel().shortForm(ps.getURIOrId()) , ps.getDisplayLabel(shacModel, lang))))
						.collect(Collectors.toList());
				
						UsageOutput uOutput = new UsageOutput();
						uOutput.setNodeshape_usage(usgae_doc.getNodeShape().getDisplayLabel(shacModel, lang));
						uOutput.setProperties_usage(linkUsage);

						showusage.add(uOutput);
				}
			}
		}
				
		return showusage;
	}

	
}
