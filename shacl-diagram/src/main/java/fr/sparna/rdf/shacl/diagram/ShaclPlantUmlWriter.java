package fr.sparna.rdf.shacl.diagram;

	
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclPlantUmlWriter {

	protected boolean includeSubclassLinks = true;
	protected boolean generateAnchorHyperlink = false;
	
	public ShaclPlantUmlWriter(boolean includeSubclassLinks, boolean generateAnchorHyperlink) {
		super();
		this.includeSubclassLinks = includeSubclassLinks;
		this.generateAnchorHyperlink = generateAnchorHyperlink;
	}

	public String writeInPlantUml(Model shaclGraph,Model owlGraph, Boolean outExpandDiagram) {

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
		
		List<PlantUmlBox> plantUmlBoxes = nodeShapes.stream().map(res -> nodeShapeReader.read(res)).sorted((b1,b2) -> {
			if (b1.getNametargetclass() != null) {
				if(b2.getNametargetclass() != null) {
					return b2.getNametargetclass().compareTo(b1.getNametargetclass());
				} else {
					return -1;
				}				
			} else {
				if(b2.getNametargetclass() != null) {
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
		
		StringBuffer sourceuml = new StringBuffer();
		sourceuml.append("@startuml\n");
		sourceuml.append("skinparam classFontSize 14"+"\n");
		sourceuml.append("!define LIGHTORANGE\n");
		
		//skinparam linetype ortho        // l'instruction cr�er des lignes droits  
		//source +="!includeurl https://raw.githubusercontent.com/Drakemor/RedDress-PlantUML/master/style.puml\n\n";
		
		sourceuml.append("skinparam componentStyle uml2\n");
		sourceuml.append("skinparam wrapMessageWidth 100\n");
		sourceuml.append("skinparam ArrowColor #Maroon\n");
		sourceuml.append("skinparam dpi 80 \n\n");

		PlantUmlRenderer renderer = new PlantUmlRenderer();
		renderer.setGenerateAnchorHyperlink(this.generateAnchorHyperlink);
		// retrieve all package declaration
		Set<String> packages = plantUmlBoxes.stream().map(b -> b.getPackageName()).collect(Collectors.toSet());
		for(String aPackage : packages ) {
			if(!aPackage.equals("")) {
				sourceuml.append("namespace "+aPackage+" "+"{\n");
			}
			
			for (PlantUmlBox plantUmlBox : plantUmlBoxes.stream().filter(b -> b.getPackageName().equals(aPackage)).collect(Collectors.toList())) {
				sourceuml.append(renderer.renderNodeShape(plantUmlBox,plantUmlBoxes,outExpandDiagram));
			}
			
			if(!aPackage.equals("")) {
				sourceuml.append("}\n");
			}			
		}
		
		sourceuml.append("hide circle\n");
		// source += "hide methods\n";
		sourceuml.append("hide methods\n");
		sourceuml.append("hide empty members\n");
		sourceuml.append("@enduml\n");


		return sourceuml.toString();
	}

}
