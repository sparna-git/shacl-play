package fr.sparna.rdf.shacl.doc.read;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;

public interface DatasetDocumentationReaderIfc {

	public ShapesDocumentation readDatasetDocumentation(
			Model dataset,
			Model owlModel,
		    String lang
	);
	
	public ShapesDocumentation generateDatasetDocumentation(
			Model owlModel,
			Model statisticsModel,
			Model shapesModel,
			String lang
	);
	
}
