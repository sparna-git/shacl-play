package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
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

	public String writeInPlantUml(Model shaclGraph) {

		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		// 1. Lire toutes les box
		PlantUmlBoxReader nodeShapeReader = new PlantUmlBoxReader();
		ArrayList<PlantUmlBox> plantUmlBoxes = new ArrayList<>();
		
		for (Resource nodeShape : nodeShapes) {
			PlantUmlBox box = nodeShapeReader.read(nodeShape);
			plantUmlBoxes.add(box);
		} 
		
		// 2. Une fois qu'on a toute la liste, lire les proprietes
		for (PlantUmlBox aBox : plantUmlBoxes) {
			aBox.setProperties(nodeShapeReader.readProperties(aBox.getNodeShape(), plantUmlBoxes));
			if(includeSubclassLinks) {
				aBox.setSuperClasses(nodeShapeReader.readSuperClasses(aBox.getNodeShape(), plantUmlBoxes));
			}
		}

		Set<String> packages = plantUmlBoxes.stream().map(b -> b.getPackageName()).collect(Collectors.toSet());
		
		List<String> sourceuml = new ArrayList<>();
		PlantUmlRenderer renderer = new PlantUmlRenderer();
		renderer.setGenerateAnchorHyperlink(this.generateAnchorHyperlink);
		for(String aPackage : packages ) {
			if(!aPackage.equals("")) {
				sourceuml.add("namespace "+aPackage+" "+"{\n");
			}
		
			for (PlantUmlBox plantUmlBox : plantUmlBoxes.stream().filter(b -> b.getPackageName().equals(aPackage)).collect(Collectors.toList())) {
				sourceuml.add(renderer.renderNodeShape(plantUmlBox));
			}
			if(!aPackage.equals("")) {
				sourceuml.add("}\n");
			}
		}
		
		String source = "@startuml\n";
		source += "skinparam classFontSize 14"+"\n";
		source += "!define LIGHTORANGE\n";
		
		//skinparam linetype ortho        // l'instruction cr�er des lignes droits  
		//source +="!includeurl https://raw.githubusercontent.com/Drakemor/RedDress-PlantUML/master/style.puml\n\n";
		
		source += "skinparam componentStyle uml2\n";
		source += "skinparam wrapMessageWidth 100\n";
		source += "skinparam ArrowColor #Maroon\n\n";

		for (String code : sourceuml) {
			source += code;
		}
		source += "hide circle\n";
		// source += "hide methods\n";
		source += "hide methods\n";
		source += "hide empty members\n";
		source += "@enduml\n";


		return source;
	}

}
