package fr.sparna.rdf.shacl.doc.read;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;

public interface ShapesDocumentationReaderIfc {

	public ShapesDocumentation readShapesDocumentation(
			Model shapesModel,
			Model owlModel,
		    String lang
    );
	
}
