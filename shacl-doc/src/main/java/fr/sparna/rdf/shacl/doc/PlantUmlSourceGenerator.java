package fr.sparna.rdf.shacl.doc;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramGenerator;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;

public class PlantUmlSourceGenerator {

	public List<PlantUmlDiagramOutput> generatePlantUmlDiagram(
			Model shapesModel,
			Model owlModel,
			boolean subclasssOf,
			boolean Classlink,
			boolean avoidArrowsToEmptyBoxes
	) {

		// draw - without subclasses links
		// set first parameter to true to draw subclassOf links
		PlantUmlDiagramGenerator writer = new PlantUmlDiagramGenerator(subclasssOf, Classlink, avoidArrowsToEmptyBoxes);
		Model finalModel = ModelFactory.createDefaultModel();
		finalModel.add(shapesModel);
		if(owlModel != null) {
			finalModel.add(owlModel);
		}
		
		//String plantUmlString = writer.writeInPlantUml(shapesModel,owlModel);
		List<PlantUmlDiagramOutput> output = writer.generateDiagrams(shapesModel,owlModel);
		
		return output;
	}

}