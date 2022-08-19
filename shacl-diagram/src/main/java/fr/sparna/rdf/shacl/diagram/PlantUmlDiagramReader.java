package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;

public class PlantUmlDiagramReader {

	
	public List<PlantUmlDiagram> readDiagrams(List<PlantUmlBox> boxes) {
		List<PlantUmlDiagram> diagrams = new ArrayList<>();
		
		Set<Resource> allDiagramReferences = new HashSet<>();
		for (PlantUmlBox oneBox : boxes) {
			// List<String> references = oneBox.getDiagramReferences().stream().map(r -> r.getURI()).collect(Collectors.toList());
			allDiagramReferences.addAll(oneBox.getDiagramReferences());
		}
		
		if(allDiagramReferences.size() == 0) {
			// no diagram, create a single default one
			PlantUmlDiagram defaultDiagram = new PlantUmlDiagram();
			defaultDiagram.setBoxes(boxes);
			diagrams.add(defaultDiagram);
		} else {
			// some diagrams declared, create one diagram for each
			for (Resource aRef : allDiagramReferences) {
				PlantUmlDiagram d = new PlantUmlDiagram();
				d.setResource(aRef);
				// store all boxes that are included in this diagram
				for (PlantUmlBox oneBox : boxes) {
					if(oneBox.getDiagramReferences().contains(aRef)) {
						d.getBoxes().add(oneBox);
					}
				}
				diagrams.add(d);
			}
		}
		
		
		return diagrams;
	}
	
}
