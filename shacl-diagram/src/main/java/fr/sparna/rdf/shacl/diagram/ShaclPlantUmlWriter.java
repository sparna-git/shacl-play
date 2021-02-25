package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclPlantUmlWriter {

	protected boolean includeSubclassLinks = true;
	
	public ShaclPlantUmlWriter(boolean includeSubclassLinks) {
		super();
		this.includeSubclassLinks = includeSubclassLinks;
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

		List<String> sourceuml = new ArrayList<>();
		String Auxpackage = "";
		boolean  close_package=false;
		plantUmlBoxes.sort(Comparator.comparing(PlantUmlBox::getPackageName));
		
		for (PlantUmlBox plantUmlBox : plantUmlBoxes) {
			
			if (plantUmlBox.getPackageName() != "" & !Auxpackage.equals(plantUmlBox.getPackageName())) {
				if (close_package) {
					sourceuml.add("}\n");
				}
				sourceuml.add("namespace "+plantUmlBox.getPackageName()+" "+"{\n");
				Auxpackage = plantUmlBox.getPackageName();
				
				close_package = true;
				sourceuml.add(plantUmlBox.toPlantUml());
			} else {
				sourceuml.add(plantUmlBox.toPlantUml());
			}
			
			PlantUmlRenderer renderer = new PlantUmlRenderer(plantUmlBox.getNameshape());
			for (PlantUmlProperty plantUmlproperty : plantUmlBox.getProperties()) {
				sourceuml.add(renderer.render(plantUmlproperty));
			}
		}
		
		if (close_package) {
			sourceuml.add("}\n");
		}
		
		String source = "@startuml\n";
		source +="!define LIGHTORANGE\n";
		
		//skinparam linetype ortho        // l'instruction cr�er des lignes droits  
		//source +="!includeurl https://raw.githubusercontent.com/Drakemor/RedDress-PlantUML/master/style.puml\n\n";
		
		source += "skinparam componentStyle uml2\n";
		source += "skinparam wrapMessageWidth 100\n";
		source += "skinparam ArrowColor #Maroon\n\n";
		


		for (String code : sourceuml) {
			source += code;
		}
		source += "hide circle\n";
		source += "hide methods\n";
		source += "@enduml\n";


		return source;
	}
	


}
