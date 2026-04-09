package fr.sparna.rdf.shacl.doc.read;

import java.beans.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;
import org.apache.jena.rdf.model.Literal;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.MarkdownRenderer;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PlantUmlSourceGenerator;
import fr.sparna.rdf.shacl.doc.PropertyShape;
import fr.sparna.rdf.shacl.doc.ShapesGraph;
import fr.sparna.rdf.shacl.doc.UsageDoc;
import fr.sparna.rdf.shacl.doc.UsageOutput;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.PropertyShapesGroupDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationDiagram;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import net.sourceforge.plantuml.board.BNode;

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
		if(nodeShape.isAClass()) {
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
		if (nodeShape.getShaclPlayMain() == null) {
			currentSection.setMainToc(isMainEntity(nodeShape));
		} else { 
			currentSection.setMainToc(nodeShape.getShaclPlayMain());
		}
		
		// Get sh:node as type of shape
		if (nodeShape.getShNode() != null) {			
			nodeShape.getShNode().forEach(shNode -> {
				currentSection.setShNode(
					buildShNodeLink(shNode, shapesGraph.getAllNodeShapes(), owlGraph, lang)	
				);
			});
		}


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
		currentSection.setPattern(nodeShape.getShPattern() != null?nodeShape.getShPattern().getString():null);
		
		// sh:nodeKind
		currentSection.setNodeKind(PropertyShapeDocumentationBuilder.renderNodeKind(nodeShape.getShNodeKind()));
		
		// sh:closed
		if(nodeShape.getShClosed() != null) {
			currentSection.setClosed(nodeShape.getShClosed());
		}
		
		// SH.hasValue
		//currentSection.set
		
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
		
		// foaf:depiction
		if (nodeShape.getFoafDepiction().size() > 0) {
			currentSection.setDepictions(nodeShape.getFoafDepiction());
		}
		
		// Usage
		currentSection.setUsages(findNodeShapeInOtherProperties(shaclGraph, shapesGraph.getAllNodeShapes() ,nodeShape, owlGraph, lang));

		
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

	public Link buildShNodeLink(Resource shNode, List<NodeShape> allNodeShapes, Model owlGraph, String lang) {
		for(NodeShape aBox : allNodeShapes) {
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

	private boolean isMainEntity(NodeShape ns) {
		if (ns.getShtargetSubjectsOf() != null) {
			return true;
		} else if (ns.getShtargetObjectsOf() != null){
			return true;
		} else if (ns.getShTargetClass().size() > 0) {
			return true;
		} else if (ns.getShTargetShSelect() != null) {
			return true;
		} else if (ns.isAClass()) {
			return true;
		} else {
			return false;
		}
	}

	
	public List<UsageOutput> findNodeShapeInOtherProperties (Model shaclGraph,List<NodeShape> allNodeShapes, NodeShape nodeShape, Model owlGraph, String lang) {
		
		// id of nodeshape is the same as the id of one of the node shapes in the model
		String nodeShapeId = nodeShape.getShortFormOrId();

		// Save in Map
		List<UsageDoc> usageDocumentatoin = new ArrayList<>();
		// find node shape in the all properties and return the list of properties where it is found
		shaclGraph
			.listStatements()
			// get all properties for each node shape
			.forEach( e -> {
				if (e.getObject().isResource()) {
					if (e.getObject().asResource().getURI() != null ) {
						if (e.getObject().asResource().getModel().shortForm(e.getObject().asResource().getURI()).equals(nodeShapeId)) {
							
							// Convert to propertyShape
							PropertyShape property_usage = new PropertyShape(e.getSubject().asResource());
							// The nodeShape 
							if (!property_usage.getResource().isAnon()) {
							
								// find the node shape
								//NodeShape ns_usage;
								for (NodeShape ns : allNodeShapes) {								
									boolean isInNodeShape = ns.getProperties()
															.stream()
															.filter(fp -> fp.getURIOrId().equals(property_usage.getURIOrId()))
															.findFirst()
															.isPresent();
									if (isInNodeShape) {
										// collector
										boolean bInList = usageDocumentatoin.stream().filter( nsu -> nsu.getNodeShape().getShortFormOrId().equals(ns.getShortFormOrId())).findFirst().isPresent();
										if (!bInList) {
											UsageDoc ud = new UsageDoc(ns);
											ud.setNodeShape(ns);
											List<PropertyShape> ps = new ArrayList<>();
											ps.add(property_usage);
											ud.setProperties(ps);

											usageDocumentatoin.add(ud);

										} else {
											// get ns 
											int id = 0;
											for (UsageDoc uDoc : usageDocumentatoin ) {
												if (uDoc.getNodeShape().getShortFormOrId().equals(ns.getShortFormOrId())) {
													List<PropertyShape> updatePS = uDoc.getProperties();
													updatePS.add(property_usage);
													uDoc.setProperties(updatePS);

													usageDocumentatoin.set(id, uDoc);
												}
												id++;
											}
										}
										break;
									}
								}
							}
						}
					}
				}
		});

		// sort for nodeshape
		usageDocumentatoin.sort((ns1,ns2) -> {
			if (ns1.getNodeShape().getShOrder() != null) {
				if (ns2.getNodeShape().getShOrder() != null) {
					return (ns1.getNodeShape().getShOrder()) - (ns2.getNodeShape().getShOrder()) > 0?1:-1;
				} else {
					return -1;
				}
			} else {
				if (ns2.getNodeShape().getShOrder() != null) {
					return 1;
				} else {
					return ns1.getNodeShape().getDisplayLabel(owlGraph, lang).compareTo(ns2.getNodeShape().getDisplayLabel(owlGraph, lang));
				}
			}
		});
		
		// ort for each properties
		List<UsageOutput> showusage = new ArrayList<>();
		for (UsageDoc usgae_doc : usageDocumentatoin) {
			
			//Sort Properties			
			usgae_doc.getProperties()
				.sort((ps1,ps2) -> {
					if(ps1.getShOrder() != null) {
						if(ps2.getShOrder() != null) {
							return (ps1.getShOrder() - ps2.getShOrder()) > 0?1:-1;
						} else {
							return -1;
						}
					} else {
						if(ps2.getShOrder() != null) {
							return 1;
						} else {
							// both sh:order are null, try with sh:name
							return ps1.getSortOrderKey(owlGraph, lang).compareToIgnoreCase(ps2.getSortOrderKey(owlGraph, lang));
						}
					}
				});

			if (usgae_doc.getProperties().size() > 0 ) {
				List<Link> linkUsage = usgae_doc.getProperties()
					.stream()
					.map((ps -> new Link("#"+ps.getResource().getModel().shortForm(ps.getURIOrId()) , ps.getDisplayLabel(owlGraph, lang))))
					.collect(Collectors.toList());
			
					UsageOutput uOutput = new UsageOutput();
					uOutput.setNodeshape_usage(usgae_doc.getNodeShape().getDisplayLabel(owlGraph, lang));
					uOutput.setProperties_usage(linkUsage);

					showusage.add(uOutput);
			}
		}

		return showusage;
	}


}
