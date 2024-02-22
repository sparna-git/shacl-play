package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.shacl.SHACL_PLAY;

public class PlantUmlDiagramReader {

	
	public List<PlantUmlDiagram> readDiagrams(List<PlantUmlBox> boxes, String lang) {
		List<PlantUmlDiagram> diagrams = new ArrayList<>();
		
		// gather a set of all diagram references
		Set<Resource> allDiagramReferences = new HashSet<>();
		for (PlantUmlBox oneBox : boxes) {
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
				for (PlantUmlBox oneBox : boxes) {
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
				
				/*
				 * Attempt to add a box with color to the splitted diagram
				// if there is a reference in this diagram to a shape that have some color information,
				// also include it in the diagram
				List<PlantUmlBox> coloredBoxesToAdd = new ArrayList<>();
				for (PlantUmlBox oneBox : d.getBoxes()) {
					for (PlantUmlProperty oneProp : oneBox.getProperties()) {
						if(oneProp.getShClass().isPresent()) {
							PlantUmlBox targetNodeShape = PlantUmlDiagram.findBoxByTargetClass(oneProp.getShClass().get(), boxes);
							// the target node shape has a color
							// but it is not included in the diagram
							if(
									targetNodeShape != null
									&&
									(targetNodeShape.getColor().isPresent() || targetNodeShape.getBackgroundColor().isPresent())
									&&
									d.findBoxByTargetClass(oneProp.getShClass().get()) == null
									&&
									// make sure it is not added twice
									PlantUmlDiagram.findBoxByTargetClass(oneProp.getShClass().get(), coloredBoxesToAdd) == null
							) {
								// then include it with minimal information
								Model m = ModelFactory.createDefaultModel();
								Resource coloredNodeShape = m.createResource(oneProp.getShClass().get().getURI());
								coloredNodeShape.addProperty(RDF.type, SH.NodeShape);
								coloredNodeShape.addProperty(RDF.type, RDFS.Class);
								targetNodeShape.getColor().ifPresent(c -> coloredNodeShape.addProperty(m.createProperty(SHACL_PLAY.COLOR), c));
								targetNodeShape.getBackgroundColor().ifPresent(c -> coloredNodeShape.addProperty(m.createProperty(SHACL_PLAY.BACKGROUNDCOLOR), c));
								
								PlantUmlBox b = new PlantUmlBox(coloredNodeShape);
								coloredBoxesToAdd.add(b);
							}
						}
					}
				}
				d.getBoxes().addAll(coloredBoxesToAdd);
				*/
				
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
