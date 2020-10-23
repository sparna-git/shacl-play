package fr.sparna.rdf.shacl.shaclplay.validate;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.shacl.printer.report.ValidationReport;

public class ShapesDisplayDataFactory {

	public ShapesDisplayData newShapesDisplayData(
			Model dataModel,
			Model shapesModel,
			Model results,
			PermalinkGenerator pGenerator
	) {
		// create a Model with the Union of the results and the shapes
		Model displayModel = shapesModel.union(results);		
		
		String language = "en";
		
		// parse the shapes to data model
		ShapesGraph shapesGraph = new ShapesGraph(shapesModel);
		
		// stores everything in the request/session, and forward to view
		ShapesDisplayData sdd = new ShapesDisplayData(
				displayModel,
				new HTMLRenderer(displayModel, language),
				shapesGraph,
				new ValidationReport(results, displayModel)
		);	
		sdd.setPermalinkGenerator(pGenerator);	
		sdd.setDataModel(dataModel);
		
		return sdd;
	}
	
}
