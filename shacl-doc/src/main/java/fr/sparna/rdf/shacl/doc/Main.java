package fr.sparna.rdf.shacl.doc;

import java.io.FileInputStream;


import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationReaderIfc;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationTestReader;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String shaclFile = args[0];
		String shaclFileOWL = args[1];
		
		// 1. read input SHACL
	    //ShapesDocumentationReaderIfc reader = new ShapesDocumentationTestReader();
		// uncomment for read SHACL parsing
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader();
		ShapesDocumentation doc = reader.readShapesDocumentation(new FileInputStream(shaclFile), new FileInputStream(shaclFileOWL), shaclFile);
		
		
		// 2. write Documentation structure to XML
		ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
		writer.write(doc, System.out);
		
	}
	

	
}