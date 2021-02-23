package fr.sparna.rdf.shacl.doc;

import java.io.FileInputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationReaderIfc;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationTestReader;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXmlWriter;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String shaclFile = args[0];
		String shaclFileOWL = args[1];
		String outputLang = args[2];
		
		Model shaclGraph = ModelFactory.createDefaultModel();
		shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));
		
		Model owlGraph = ModelFactory.createDefaultModel();
		if(shaclFileOWL != null) {
			owlGraph.read(new FileInputStream(shaclFileOWL), RDF.uri, FileUtils.guessLang(shaclFileOWL, "RDF/XML"));
		}
		
		// 1. read input SHACL
	    //ShapesDocumentationReaderIfc reader = new ShapesDocumentationTestReader();
		// uncomment for read SHACL parsing
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader();
		ShapesDocumentation doc = reader.readShapesDocumentation(shaclGraph, owlGraph, outputLang, shaclFile);
		
		
		// 2. write Documentation structure to XML
		ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
		//ShapesDocumentationXmlWriter writer = new ShapesDocumentationXmlWriter();
		writer.write(doc, outputLang, System.out);
		
	}
	

	
}