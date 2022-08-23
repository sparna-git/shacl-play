package fr.sparna.rdf.shacl.doc.write;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;

public interface ShapesDocumentationWriterIfc {

	public enum MODE {
		HTML,
		PDF
	}
	
	public void write(ShapesDocumentation documentation, String outputLang, OutputStream output, MODE mode) throws IOException;
	
}
