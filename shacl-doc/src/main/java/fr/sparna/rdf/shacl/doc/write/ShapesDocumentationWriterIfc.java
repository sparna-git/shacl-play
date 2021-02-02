package fr.sparna.rdf.shacl.doc.write;

import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.Document;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;

public interface ShapesDocumentationWriterIfc {

	public void write(ShapesDocumentation documentation, OutputStream output) throws IOException;
	
}
