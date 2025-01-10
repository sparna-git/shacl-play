package fr.sparna.rdf.shacl.diagram;

	
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;
import fr.sparna.tools.RandomColor;

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
	
	
	public List<Resource> readModel(Model shaclGraph, Model owlGraph) {
		
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
	
	public List<PlantUmlBox> buildBoxes(List<Resource> nodeShapes) {
				
		// 1. Lire toutes les box
		PlantUmlBoxReader nodeShapeReader = new PlantUmlBoxReader();		
		List<PlantUmlBox> plantUmlBoxes = nodeShapes.stream().map(res -> nodeShapeReader.read(res, nodeShapes)).sorted((b1,b2) -> {
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
		
		
		return plantUmlBoxes;		
	}
	
	public List<PlantUmlBox> buildProperties(List<PlantUmlBox> plantUmlBoxes, List<Resource> nodeShapes) {
		
		PlantUmlBoxReader nodeShapeReader = new PlantUmlBoxReader();
		// 2. Une fois qu'on a toute la liste, lire les proprietes
		for (PlantUmlBox aBox : plantUmlBoxes) {
			aBox.setProperties(nodeShapeReader.readProperties(aBox.getNodeShape()));
		}
		return plantUmlBoxes;
	}
	
	public List<PlantUmlBox> setColorInProperties(List<PlantUmlBox> plantUmlBoxes) {
		
		PlantUmlBoxReader nodeShapeReader = new PlantUmlBoxReader();
		// 2. Une fois qu'on a toute la liste, lire les proprietes
		for (PlantUmlBox aBox : plantUmlBoxes) {		
			
			List<PlantUmlProperty> properties = nodeShapeReader.readProperties(aBox.getNodeShape());
			
			for (PlantUmlProperty p : properties) {
				if (p.getShNode().isPresent()) {					
					if (!p.getColor().isPresent()) {
						
						// find elemento in all plantUmlBoxes
						String bgc_color = plantUmlBoxes
								.stream()
								.filter(f -> f.getNodeShape().getURI().equals(p.getShNode().get().getURI()))
								.map(m -> m.getBackgroundColor().isPresent() ? m.getBackgroundColor().get().toString() : "" )
								.collect(Collectors.toList())
								.get(0)
								.toString();
						
						// find elemento in all plantUmlBoxes
						String police_color = plantUmlBoxes
								.stream()
								.filter(f -> f.getNodeShape().getURI().equals(p.getShNode().get().getURI()))
								.map(m -> m.getColor().isPresent() ? m.getColor().get().toString() : "" )
								.collect(Collectors.toList())
								.get(0)
								.toString();
						
						if (bgc_color != "") {							
							
							// find element in all plantUmlBoxes
							/*
							String bgc_random_color = plantUmlBoxes
									.stream()
									.filter(f -> f.getNodeShape().getURI().equals(p.getShNode().get().getURI()))
									.map(m -> m.getBackgroundColorRandom() )
									.collect(Collectors.toList())
									.get(0)
									.toString();
							*/
							// Set new Color in property
							 
							p.setBackgroundColor(bgc_color);
						} 
						
						if (police_color != "") {
							p.setTxtColor(police_color);
						}
						//else {
						//	p.setBackgroundColor(bgc_color);
						//}
						
					}
				}
			}
			aBox.setProperties(properties);
		}
		return plantUmlBoxes;
		
	}
	
	public List<PlantUmlDiagramOutput> generateDiagramsSection(Model shaclGraph, Model owlGraph) {
		
		// Get Boxes
		List<Resource> readNodeShapes = this.readModel(shaclGraph, owlGraph);
		//
		List<PlantUmlBox> AllBoxes = this.buildBoxes(readNodeShapes);
		// Add Color random in Box
		//List<PlantUmlBox> Boxes = this.setColorInShape(AllBoxes);
		// Add Color in properties
		List<PlantUmlBox> plantUmlBoxes = this.setColorInProperties(AllBoxes);
		
		// Generate Diagram
		List<PlantUmlDiagramOutput> outputDiagrams = new ArrayList<>();
		for (PlantUmlBox box : plantUmlBoxes) {
			List<PlantUmlBox> keepSection = new ArrayList<>();
			keepSection.add(box);
			List<PlantUmlDiagramOutput> outputDiagram = this.outputDiagrams(keepSection);
			outputDiagrams.addAll(outputDiagram); 
		}		
		return outputDiagrams;	
	}

	public List<PlantUmlDiagramOutput> generateDiagrams(Model shaclGraph, Model owlGraph) {

		// Get Boxes
		List<Resource> readNodeShapes = this.readModel(shaclGraph, owlGraph);
		List<PlantUmlBox> Boxes = this.buildBoxes(readNodeShapes);
		List<PlantUmlBox> plantUmlBoxes = this.buildProperties(Boxes, readNodeShapes);
		
		
		// Generate Diagram
		List<PlantUmlDiagramOutput> outputDiagram = this.outputDiagrams(plantUmlBoxes);
		
		return outputDiagram;

	}
		
	public List<PlantUmlDiagramOutput> outputDiagrams(List<PlantUmlBox> plantUmlBoxes) {
		
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
		
		System.out.println("Test.....");
				
		return codePlantUml; //sourceuml.toString();
	}

}
