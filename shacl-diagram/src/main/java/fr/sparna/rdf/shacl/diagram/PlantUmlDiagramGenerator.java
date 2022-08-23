package fr.sparna.rdf.shacl.diagram;

	
import java.util.ArrayList;
import java.util.List;
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
	protected String lang;
	
	public PlantUmlDiagramGenerator(
			boolean includeSubclassLinks,
			boolean generateAnchorHyperlink,
			boolean avoidArrowsToEmptyBoxes,
			String lang
	) {
		super();
		this.includeSubclassLinks = includeSubclassLinks;
		this.generateAnchorHyperlink = generateAnchorHyperlink;
		this.avoidArrowsToEmptyBoxes = avoidArrowsToEmptyBoxes;
		this.lang = lang;
	}

	public List<PlantUmlDiagramOutput> generateDiagrams(Model shaclGraph, Model owlGraph) {

		// read everything typed as NodeShape
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
		
		// also read everything object of an sh:node or sh:qualifiedValueShape, that maybe does not have an explicit rdf:type sh:NodeShape
		List<RDFNode> nodesAndQualifedValueShapesValues = shaclGraph.listStatements(null, SH.node, (RDFNode)null)
				.andThen(shaclGraph.listStatements(null, SH.qualifiedValueShape, (RDFNode)null))
				.toList().stream()
				.map(
					s -> s.getObject()
						)
				.collect(Collectors.toList());
		
		// add those to our list
		for (RDFNode n : nodesAndQualifedValueShapesValues) {
			if(n.isResource() && !nodeShapes.contains(n)) {
				nodeShapes.add(n.asResource());
			}
		}
		
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
		
		
		// 2. Une fois qu'on a toute la liste, lire les proprietes
		for (PlantUmlBox aBox : plantUmlBoxes) {
			aBox.setProperties(nodeShapeReader.readProperties(aBox.getNodeShape(), plantUmlBoxes,owlGraph));
			if(includeSubclassLinks) {
				aBox.setSuperClasses(nodeShapeReader.readSuperClasses(aBox.getNodeShape(), plantUmlBoxes,owlGraph));
			}
		}
		
		// then create the PlanUmlDiagrams
		PlantUmlDiagramReader diagramsReader = new PlantUmlDiagramReader();
		List<PlantUmlDiagram> diagrams = diagramsReader.readDiagrams(plantUmlBoxes, lang);
		
		// and then render each diagram
		PlantUmlRenderer renderer = new PlantUmlRenderer();
		renderer.setGenerateAnchorHyperlink(this.generateAnchorHyperlink);
		renderer.setAvoidArrowsToEmptyBoxes(avoidArrowsToEmptyBoxes);
		
		List<PlantUmlDiagramOutput> codePlantUml = diagrams.stream().map(d -> new PlantUmlDiagramOutput(d, renderer)).collect(Collectors.toList());
		
		return codePlantUml; //sourceuml.toString();
		

	}
}
