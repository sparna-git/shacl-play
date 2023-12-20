package fr.sparna.rdf.shacl.doc.write;

import java.io.IOException;
import java.io.OutputStream;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;

public interface ShapesDocumentationWriterIfc {

	public enum MODE {
		HTML,
		PDF,
		XML
	}
	
	public void writeShapesDoc(ShapesDocumentation documentation, String outputLang, OutputStream output, MODE mode) throws IOException;
	
	public void writeDatasetDoc(ShapesDocumentation documentation, String outputLang, OutputStream output, MODE mode) throws IOException;
	
}
