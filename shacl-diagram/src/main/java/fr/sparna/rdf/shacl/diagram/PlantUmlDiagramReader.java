package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class PlantUmlDiagramReader {

	
	public List<PlantUmlDiagram> readDiagrams(List<PlantUmlBox> boxes, String lang) {
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
				
				// then read an rdfs:label
				d.setTitle(this.readDctTitle(aRef, lang));

				// then read an rdfs:commebt
				d.setDescription(this.readDctDescription(aRef, lang));
				
				// and an order
				d.setOrderDiagram(this.readShOrder(aRef));
				
				diagrams.add(d);
			}
		}
		
		
		return diagrams;
	}
	
	public String readDctTitle(Resource r, String lang) {
		return ConstraintValueReader.readLiteralInLangAsString(r, DCTerms.title, lang);
	}
	
	public String readDctDescription(Resource r, String lang) {
		return ConstraintValueReader.readLiteralInLangAsString(r, DCTerms.description, lang);
	}
	
	public int readShOrder(Resource r) {
		List<Literal> values = ConstraintValueReader.readLiteralInLang(r, SH.order, null);
		if(values != null && values.size() > 0) {
			return values.get(0).asLiteral().getInt();
		} else {
			return -1;
		}
	}
	
}
