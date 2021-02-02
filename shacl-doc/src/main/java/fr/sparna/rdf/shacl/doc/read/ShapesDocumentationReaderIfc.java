package fr.sparna.rdf.shacl.doc.read;

import java.io.InputStream;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;

public interface ShapesDocumentationReaderIfc {

	public ShapesDocumentation readShapesDocumentation(InputStream input, String fileName);
	
}
