package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;

public class PlantUmlDiagramReader {

	
	public List<PlantUmlDiagram> readDiagrams(List<PlantUmlBoxIfc> boxes, String lang) {
		List<PlantUmlDiagram> diagrams = new ArrayList<>();
		
		// gather a set of all diagram references
		Set<Resource> allDiagramReferences = new HashSet<>();
		for (PlantUmlBoxIfc oneBox : boxes) {
			allDiagramReferences.addAll(oneBox.getDepiction());
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
				for (PlantUmlBoxIfc oneBox : boxes) {
					if(oneBox.getDepiction().contains(aRef)) {
						d.getBoxes().add(oneBox);
					}
				}
				
				// then read an rdfs:label
				d.setTitle(this.readDctTitle(aRef, lang));

				// then read an rdfs:comment
				d.setDescription(this.readDctDescription(aRef, lang));
				
				// and an order
				d.setOrderDiagram(this.readShOrder(aRef));

				// Add others boxes so that their color is apparent in the splitted diagram
				List<PlantUmlBoxIfc> otherResources = PlantUmlDiagramGenerator.addAdditionnalBoxes(d.getBoxes(), boxes);
				d.getBoxes().addAll(otherResources);
				
				diagrams.add(d);
			}
		}
		
		
		return diagrams;
	}
	
	public String readDctTitle(Resource r, String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(r, DCTerms.title, lang);
	}
	
	public String readDctDescription(Resource r, String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(r, DCTerms.description, lang);
	}
	
	public double readShOrder(Resource r) {
		List<Literal> values = ModelReadingUtils.readLiteralInLang(r, SH.order, null);
		if(values != null && values.size() > 0) {
			return values.get(0).asLiteral().getDouble();
		} else {
			return -1;
		}
	}
	
}
