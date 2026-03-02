package fr.sparna.rdf.shacl.doc.write;

import java.io.IOException;
import java.io.OutputStream;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;

public interface ShapesDocumentationWriterIfc {

	public enum MODE {
		HTML,
		PDF,
		XML,
		HTML_RESPEC
	}
	
	public void writeDoc(ShapesDocumentation documentation, String outputLang, OutputStream output) throws IOException;
	
}
