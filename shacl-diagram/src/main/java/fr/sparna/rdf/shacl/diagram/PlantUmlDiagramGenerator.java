package fr.sparna.rdf.shacl.diagram;

	
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class PlantUmlDiagramGenerator {

	protected boolean includeSubclassLinks = true;
	protected boolean generateAnchorHyperlink = false;
	protected boolean avoidArrowsToEmptyBoxes = true;
	protected boolean hidePropertiesBoxes = false;
	protected String lang;
	
	public PlantUmlDiagramGenerator(
			boolean includeSubclassLinks,
			boolean generateAnchorHyperlink,
			boolean avoidArrowsToEmptyBoxes,
			boolean hidePropertiesBoxes,
			String lang
	) {
		super();
		this.includeSubclassLinks = includeSubclassLinks;
		this.generateAnchorHyperlink = generateAnchorHyperlink;
		this.avoidArrowsToEmptyBoxes = avoidArrowsToEmptyBoxes;
		this.hidePropertiesBoxes = hidePropertiesBoxes;
		this.lang = lang;
	}


	/**
	 * Generate the main diagrams of the documentation for all the NodeShapes in the SHACL graph
	 * @param shaclGraph
	 * @param owlGraph
	 * @return
	 */
	public List<PlantUmlDiagramOutput> generateDiagrams(Model shaclGraph, Model owlGraph) {

		// Get Boxes
		List<Resource> readNodeShapes = this.readNodeShapes(shaclGraph, owlGraph);
		List<PlantUmlBoxIfc> plantUmlBoxes = this.buildBoxes(readNodeShapes);

		// then create the PlanUmlDiagrams
		PlantUmlDiagramReader diagramsReader = new PlantUmlDiagramReader();
		List<PlantUmlDiagram> diagrams = diagramsReader.readDiagrams(plantUmlBoxes, lang);
		
		// Generate Diagram
		List<PlantUmlDiagramOutput> outputDiagram = this.outputDiagrams(diagrams, false);
		
		return outputDiagram;
	}

	/**
	 * Generate the diagrams for a specific section of the documentation, starting from a specific NodeShape
	 * @param shaclGraph
	 * @param owlGraph
	 * @param nodeShape
	 * @return
	 */
	public List<PlantUmlDiagramOutput> generateDiagramsForSection(
		Model shaclGraph,
		Model owlGraph,
		Resource nodeShape
	) {
		
		// Get Boxes
		List<Resource> nodeShapes = this.readNodeShapes(shaclGraph, owlGraph);
		List<PlantUmlBoxIfc> plantUmlBoxes = this.buildBoxes(nodeShapes);
		
		// Generate Diagram
		List<PlantUmlBoxIfc> boxesIncludedInTheDiagram = new ArrayList<>();
		// find the main box
		PlantUmlBoxIfc mainBox = plantUmlBoxes
				.stream()
				.filter(box -> box.getNodeShape().getURI().equals(nodeShape.getURI()))
				.findFirst()
				.get();	
		
		boxesIncludedInTheDiagram.add(mainBox);

		// Add others Resources
		List<PlantUmlBoxIfc> otherResources = this.buildAdditionnalBoxes(plantUmlBoxes, mainBox);
		boxesIncludedInTheDiagram.addAll(otherResources);

		// build a Diagram data structure
		PlantUmlDiagram d = new PlantUmlDiagram();
		// we use the NodeShape as the diagram resource
		d.setResource(nodeShape);
		d.setBoxes(boxesIncludedInTheDiagram);
		
		// if is a one Shape and include properties, print diagram
		boolean createDiagram = false;
		if (boxesIncludedInTheDiagram.size() == 1) {
			int nBox = boxesIncludedInTheDiagram
			.stream()
			.filter(b -> b.getProperties().size() > 0)
			.collect(Collectors.toList())
			.size();
			
			if (nBox > 0) {
				createDiagram = true;
			}		
		} else {
			createDiagram = true;
		}
		
		this.avoidArrowsToEmptyBoxes = false;
		
		List<PlantUmlDiagramOutput> outputDiagram = new ArrayList<>();
		if (createDiagram) {
			outputDiagram = this.outputDiagrams(Collections.singletonList(d), true);
		}
		return outputDiagram;

	}


	/**
	 * Generate the PlantUml output diagrams from the content of the graph specified as a list of PlantUmlBoxIfc
	 * @param plantUmlBoxes
	 * @return
	 */
	private List<PlantUmlDiagramOutput> outputDiagrams(List<PlantUmlDiagram> diagrams, boolean sectionDiagram) {
		
		// and then render each diagram
		PlantUmlRenderer renderer = new PlantUmlRenderer();
		renderer.setGenerateAnchorHyperlink(this.generateAnchorHyperlink);
		renderer.setAvoidArrowsToEmptyBoxes(this.avoidArrowsToEmptyBoxes);
		renderer.setIncludeSubclassLinks(this.includeSubclassLinks);
		renderer.setHideProperties(this.hidePropertiesBoxes);
		renderer.setRenderSectionDiagram(sectionDiagram);
				
		List<PlantUmlDiagramOutput> codePlantUml = diagrams.stream().map(d -> new PlantUmlDiagramOutput(d, renderer)).sorted((o1,o2) -> {
			if(o1.getDiagramOrder() > 0) {
				if(o2.getDiagramOrder() > 0) {
					return ((o1.getDiagramOrder() - o2.getDiagramOrder()) > 0)?1:-1;					
				}else {
					return -1;
				}
			} else {
				if(o2.getDiagramOrder() > 0) {
					return 1;					
				} else {
					// no order, try with title or URI
					return Optional.ofNullable(o1.getDiagramTitle()).orElse(o1.getDiagramUri()).compareTo(Optional.ofNullable(o2.getDiagramTitle()).orElse(o2.getDiagramUri()));
				}
			}
		}).collect(Collectors.toList());
				
		return codePlantUml; //sourceuml.toString();
	}
	
	
	private List<Resource> readNodeShapes(Model shaclGraph, Model owlGraph) {
		
		// read everything typed as NodeShape
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
				
		// also read everything object of an sh:node or sh:qualifiedValueShape, that maybe does not have an explicit rdf:type sh:NodeShape
		List<RDFNode> nodesAndQualifedValueShapesValues = shaclGraph.listStatements(null, SH.node, (RDFNode)null)
				.andThen(shaclGraph.listStatements(null, SH.qualifiedValueShape, (RDFNode)null))
				.toList().stream()
				.map(s -> s.getObject())
				.collect(Collectors.toList());
		
		// add those to our list
		for (RDFNode n : nodesAndQualifedValueShapesValues) {
			if(n.isResource() && !nodeShapes.contains(n)) {
				nodeShapes.add(n.asResource());
			}
		}
		
		return nodeShapes;
	}
	
	private List<PlantUmlBoxIfc> buildBoxes(List<Resource> nodeShapes) {
				
		// 1. Lire toutes les box
		PlantUmlBoxReader nodeShapeReader = new PlantUmlBoxReader();		
		List<PlantUmlBoxIfc> plantUmlBoxes = nodeShapes.stream().map(res -> nodeShapeReader.read(res, nodeShapes)).sorted((b1,b2) -> {
			if(b1.getNodeShape().isAnon()) {
				if(!b2.getNodeShape().isAnon()) {
					return b1.getNodeShape().toString().compareTo(b2.getNodeShape().toString());
				}else {
					return -1;
				}
			}else {
				if(!b2.getNodeShape().isAnon()) {
					return 1;
				} else {
					return b1.getLabel().compareTo(b2.getLabel());
				}
			}
		}).collect(Collectors.toList());
		
		// 2. Une fois qu'on a toute la liste, lire les proprietes
		for (PlantUmlBoxIfc aBox : plantUmlBoxes) {
			aBox.setProperties(nodeShapeReader.readProperties(aBox.getNodeShape()));
		}
		
		return plantUmlBoxes;		
	}
	
	private List<PlantUmlBoxIfc> buildAdditionnalBoxes(List<PlantUmlBoxIfc> allBoxes, PlantUmlBoxIfc box) {
		
		List<PlantUmlBoxIfc> interestingBoxes = box.getProperties()
				.stream()
				.filter(f -> f.getShNode().isPresent() || f.getShClass().isPresent())
				.map( p -> {
					if (p.getShNode().isPresent()) {
						return PlantUmlDiagram.findBoxByResource(p.getShNode().get(), allBoxes);
					}
					
					if (p.getShClass().isPresent()) {
						return PlantUmlDiagram.findBoxByTargetClass(p.getShClass().get(), allBoxes);
					}
					return null;
				})
				.collect(Collectors.toList());
		// Sh:Or
		for (PlantUmlProperty prop : box.getProperties()) {
			if (prop.getShOrShClass() != null) {
				for (Resource aShClass : prop.getShOrShClass()) {
					interestingBoxes.add(PlantUmlDiagram.findBoxByTargetClass(aShClass, allBoxes));
				}
			}
			
			if (prop.getShOrShNode() != null) {
				for (Resource aShNode : prop.getShOrShNode()) {
					interestingBoxes.add(PlantUmlDiagram.findBoxByResource(aShNode, allBoxes));
				}
			}
		}
		
		List<PlantUmlBoxIfc> otherBoxes = interestingBoxes
			.stream()
			.filter(b -> b != null)
			.distinct()
			.map( b -> { 				
				SimplePlantUmlBox newBoxSimple = new SimplePlantUmlBox(b.getNodeShape().getModel().shortForm(b.getNodeShape().getURI()));
				newBoxSimple.setBackgroundColorString(b.getBackgroundColorString());
				newBoxSimple.setColorString(b.getColorString());
				newBoxSimple.setLabel(b.getLabel());
				
				List<Resource> resources = new ArrayList<>();
				newBoxSimple.setDepiction(resources);
				newBoxSimple.setRdfsSubClassOf(resources);
				newBoxSimple.setShNode(resources);
				
				List<PlantUmlProperty> properties = new ArrayList<>();
				newBoxSimple.setProperties(properties);
				
				return newBoxSimple;
				
			})
			.collect(Collectors.toList());
		
		return otherBoxes;
	}

}
