package fr.sparna.rdf.shacl.doc.read;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;

public class ShapesDocumentationModelReader implements ShapesDocumentationReaderIfc {

	@Override
	public ShapesDocumentation readShapesDocumentation(InputStream input, String fileName) {
		Model shaclGraph = ModelFactory.createDefaultModel();

		shaclGraph.read(input, RDF.uri, FileUtils.guessLang(fileName, "RDF/XML"));

		ShapesDocumentation shapesDocumentation = new ShapesDocumentation();
		
		// HERE : READ Model and populate shapesDocumentation
		
		return shapesDocumentation;
	}

	
	
}
