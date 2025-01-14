package fr.sparna.rdf.shacl.doc;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramGenerator;
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
	
	public PlantUmlDiagramGenerator buildPlantUmlDiagramGenerator() {		
		// draw - without subclasses links
		// set first parameter to true to draw subclassOf links
		PlantUmlDiagramGenerator writer = new PlantUmlDiagramGenerator(
				// includes the subClassOf links
				true,
				// include anchors
				true,
				// avoid arrows to empty boxes
				true,
				//
				hideProperties,
				lang
		);
		
		return writer;
		
	}
	
	public List<PlantUmlDiagramOutput> generatePlantUmlDiagram() {

		PlantUmlDiagramGenerator writer = this.buildPlantUmlDiagramGenerator();
		//String plantUmlString = writer.writeInPlantUml(shapesModel,owlModel);
		List<PlantUmlDiagramOutput> output = writer.generateDiagrams(shapesModel,owlModel);
		
		return output;
	}
	
	public List<PlantUmlDiagramOutput> generatePlantUmlDiagramSection(Resource nodeShape) {
		PlantUmlDiagramGenerator writer = this.buildPlantUmlDiagramGenerator();
		List<PlantUmlDiagramOutput> output = writer.generateDiagramsForSection(shapesModel, owlModel, nodeShape);		
		return output;
	}

}