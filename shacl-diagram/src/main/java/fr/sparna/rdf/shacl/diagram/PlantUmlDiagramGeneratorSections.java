package fr.sparna.rdf.shacl.diagram;

	
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.compress.harmony.unpack200.bytecode.forms.ThisFieldRefForm;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.diagram.PlantUmlBox;


public class PlantUmlDiagramGeneratorSections {

	protected boolean includeSubclassLinks = true;
	protected boolean generateAnchorHyperlink = false;
	protected boolean avoidArrowsToEmptyBoxes = true;
	protected boolean hidePropertiesBoxes = false;
	protected String lang;
	
	public PlantUmlDiagramGeneratorSections(
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
	
	
	public List<Resource> readNodeShapes(Model shaclGraph, Model owlGraph) {
		
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
	
	public List<PlantUmlBoxIfc> buildBoxes(List<Resource> nodeShapes) {
				
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
	
	public List<PlantUmlBoxIfc> buildProperties(List<PlantUmlBoxIfc> plantUmlBoxes, List<Resource> nodeShapes) {
		
		PlantUmlBoxReader nodeShapeReader = new PlantUmlBoxReader();
		// 2. Une fois qu'on a toute la liste, lire les proprietes
		for (PlantUmlBoxIfc aBox : plantUmlBoxes) {
			aBox.setProperties(nodeShapeReader.readProperties(aBox.getNodeShape()));
		}
		return plantUmlBoxes;
	}
	
	public List<PlantUmlBoxIfc> getBoxes(List<Resource> nodeShapes,PlantUmlBoxIfc Box) {
		
		
		List<Resource> nodes = Box.getProperties()
				.stream()
				.filter(f -> f.getShNode().isPresent() || f.getShClass().isPresent())
				.map( box -> {
					if (box.getShNode().isPresent()) {
						return nodeShapes
								.stream()
								.filter(ns -> ns.getURI().equals(box.getShNode().get().getURI()))
								.collect(Collectors.toList())
								.get(0);
					}
					
					if (box.getShClass().isPresent()) {
						return nodeShapes
								.stream()
								.filter(getResource -> getResource.getURI().equals(box.getShClass().get().getURI()))
								.collect(Collectors.toList())
								.get(0);
					}					
					return null;
				})
				.collect(Collectors.toList());
		
		
		List<PlantUmlBoxIfc> otherBoxes = nodes
			.stream()
			.map( nodeShapeBox -> { 
				//SimplePlantUmlBox s = new SimplePlantUmlBox(nodeShapeBox.getURI()); 
				PlantUmlBoxReader nodeShapeReader = new PlantUmlBoxReader();
				PlantUmlBoxIfc plantUmlBoxes = nodeShapeReader.read(nodeShapeBox, nodes);
				
				SimplePlantUmlBox newBoxSimple = new SimplePlantUmlBox(nodeShapeBox.getURI());
				newBoxSimple.setBackgroundColorString(plantUmlBoxes.getBackgroundColorString());
				newBoxSimple.setColorString(plantUmlBoxes.getColorString());
				newBoxSimple.setLabel(plantUmlBoxes.getLabel());
				
				List<Resource> resources = new ArrayList<>();
				newBoxSimple.setDepiction(resources);
				
				List<PlantUmlProperty> properties = new ArrayList<>();
				newBoxSimple.setProperties(properties);
				
				return newBoxSimple;
				
			})
			.collect(Collectors.toList());
		
		return otherBoxes;
	}
	
	public List<PlantUmlDiagramOutput> generateDiagramsForSection(
		Model shaclGraph,
		Model owlGraph,
		Resource nodeShape
	) {
		
		// Get Boxes
		List<Resource> nodeShapes = this.readNodeShapes(shaclGraph, owlGraph);
		List<PlantUmlBoxIfc> allBoxes = this.buildBoxes(nodeShapes);
		
		// Generate Diagram
		List<PlantUmlBoxIfc> boxesIncludedInTheDiagram = new ArrayList<>();
		// find the main box
		PlantUmlBoxIfc mainBox = allBoxes
				.stream()
				.filter(box -> box.getNodeShape().getURI().equals(nodeShape.getURI()))
				.findFirst()
				.get();	
		
		boxesIncludedInTheDiagram.add(mainBox);

		// Add others Resources
		List<PlantUmlBoxIfc> otherResources = this.getBoxes(nodeShapes, mainBox);
		//		
		boxesIncludedInTheDiagram.addAll(otherResources);
		// Generate diagram
		List<PlantUmlDiagramOutput> outputDiagram = this.outputDiagrams(boxesIncludedInTheDiagram);

		return outputDiagram;
		
		
		// for (PlantUmlBox box : plantUmlBoxes) {
		// 	List<PlantUmlBox> keepSection = new ArrayList<>();
		// 	keepSection.add(box);
		// 	List<PlantUmlDiagramOutput> outputDiagram = this.outputDiagrams(keepSection);
		// 	outputDiagrams.addAll(outputDiagram); 
		// }		
		// return outputDiagrams;	
	}

	public List<PlantUmlDiagramOutput> generateDiagrams(Model shaclGraph, Model owlGraph) {

		// Get Boxes
		List<Resource> readNodeShapes = this.readNodeShapes(shaclGraph, owlGraph);
		List<PlantUmlBoxIfc> plantUmlBoxes = this.buildBoxes(readNodeShapes);
		//List<PlantUmlBoxIfc> plantUmlBoxes = this.buildProperties(Boxes, readNodeShapes);		
		
		// Generate Diagram
		List<PlantUmlDiagramOutput> outputDiagram = this.outputDiagrams(plantUmlBoxes);
		
		return outputDiagram;

	}
		
	public List<PlantUmlDiagramOutput> outputDiagrams(List<PlantUmlBoxIfc> plantUmlBoxes) {
		
		// then create the PlanUmlDiagrams
		PlantUmlDiagramReader diagramsReader = new PlantUmlDiagramReader();
		List<PlantUmlDiagram> diagrams = diagramsReader.readDiagrams(plantUmlBoxes, lang);
		
		// and then render each diagram
		PlantUmlRenderer renderer = new PlantUmlRenderer();
		renderer.setGenerateAnchorHyperlink(this.generateAnchorHyperlink);
		renderer.setAvoidArrowsToEmptyBoxes(this.avoidArrowsToEmptyBoxes);
		renderer.setIncludeSubclassLinks(this.includeSubclassLinks);
		renderer.setHideProperties(this.hidePropertiesBoxes);
				
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

}
