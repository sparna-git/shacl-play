package fr.sparna.rdf.shacl.diagram;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WriteDiagrams {
	
	public StringBuffer writeUML(List<PlantUmlDiagram> optDiagrams, 
									   Boolean generateAnchorHyperlink,
									   Boolean avoidArrowsToEmptyBoxes){
		
		//List<StringBuffer> listSourceUML = new ArrayList<StringBuffer>(); 
		
		List<PlantUmlBox> plantBox = optDiagrams.stream().map(p -> p.getDiagrams()).collect(Collectors.toList());
		String noteDiagram = null;
		for (PlantUmlDiagram name : optDiagrams) {
			if(name.getDescription() != null) {
				noteDiagram = name.getDescription();
			}
		}
		
		StringBuffer sourceuml = new StringBuffer();
		sourceuml.append("@startuml\n");
		sourceuml.append("skinparam classFontSize 14"+"\n");
		sourceuml.append("!define LIGHTORANGE\n");
		sourceuml.append("skinparam componentStyle uml2\n");
		sourceuml.append("skinparam wrapMessageWidth 100\n");
		sourceuml.append("skinparam ArrowColor #Maroon\n");
		
		String titleDiagram = "Title "+noteDiagram+"\n"; 
		sourceuml.append(titleDiagram);
		
		PlantUmlRenderer renderer = new PlantUmlRenderer();
		renderer.setGenerateAnchorHyperlink(generateAnchorHyperlink);
		
		// retrieve all package declaration
		Set<String> packages = plantBox.stream().map(b -> b.getPackageName()).collect(Collectors.toSet());
		for(String aPackage : packages ) {
			if(!aPackage.equals("")) {
				sourceuml.append("namespace "+aPackage+" "+"{\n");
			}
			
			for (PlantUmlBox plantUmlBox : plantBox.stream().filter(b -> b.getPackageName().equals(aPackage)).collect(Collectors.toList())) {
					sourceuml.append(renderer.renderNodeShape(plantUmlBox,plantBox,avoidArrowsToEmptyBoxes));
			}
			
			if(!aPackage.equals("")) {
				sourceuml.append("}\n");
			}			
		}
		
		sourceuml.append("hide circle\n");
		sourceuml.append("hide methods\n");
		sourceuml.append("hide empty members\n");
		sourceuml.append("@enduml\n");
		return sourceuml;
	}
}
