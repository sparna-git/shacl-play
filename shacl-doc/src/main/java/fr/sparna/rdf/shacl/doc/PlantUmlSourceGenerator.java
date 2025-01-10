package fr.sparna.rdf.shacl.doc;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramGenerator;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramGeneratorSections;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;

public class PlantUmlSourceGenerator {

	Model shapesModel;
	Model owlModel;
	boolean hideProperties;
	String lang;
	
	public PlantUmlSourceGenerator(Model shapesModel,Model owlModel,boolean hideProperties,String lang) {
		
		super();
		this.shapesModel = shapesModel;				
		this.owlModel  = owlModel;
		this.hideProperties = hideProperties; 
		this.lang = lang;
	}	
	
	public PlantUmlDiagramGeneratorSections writeDraw() {
		
		// draw - without subclasses links
		// set first parameter to true to draw subclassOf links
		//PlantUmlDiagramGenerator writer = new PlantUmlDiagramGenerator(
		PlantUmlDiagramGeneratorSections writer = new PlantUmlDiagramGeneratorSections(
				// includes the subClassOf links
				true,
				// include anchors
				true,
				// avoid arrows to empty boxes
				true,
				//
				hideProperties,
				lang);
		Model finalModel = ModelFactory.createDefaultModel();
		finalModel.add(shapesModel);
		if(owlModel != null) {
			finalModel.add(owlModel);
		}
		
		return writer;
		
	}
	
	public List<PlantUmlDiagramOutput> generatePlantUmlDiagram(
			
	) {

		PlantUmlDiagramGeneratorSections writer = this.writeDraw();
		//String plantUmlString = writer.writeInPlantUml(shapesModel,owlModel);
		List<PlantUmlDiagramOutput> output = writer.generateDiagrams(shapesModel,owlModel);
		
		return output;
	}
	
	public List<PlantUmlDiagramOutput> generatePlantUmlDiagramSection() {

		PlantUmlDiagramGeneratorSections writer = this.writeDraw();
		
		//String plantUmlString = writer.writeInPlantUml(shapesModel,owlModel);
		List<PlantUmlDiagramOutput> output = writer.generateDiagramsSection(shapesModel, owlModel);
		
		return output;
	}

}