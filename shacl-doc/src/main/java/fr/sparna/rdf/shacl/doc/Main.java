package fr.sparna.rdf.shacl.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

import fr.sparna.rdf.shacl.diagram.OutFileUml;
import fr.sparna.rdf.shacl.diagram.ShaclPlantUmlWriter;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationReaderIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXmlWriter;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String shaclFile = args[0];
		String shaclFileOWL = args[1];
		String outputLang = args[2];
		boolean outDiagram = Boolean.parseBoolean(args[3]);
		boolean outPdf = Boolean.parseBoolean(args[4]);
		boolean outExpandDiagram = Boolean.parseBoolean(args[5]);
		String imgLogo = args[6];
		
		Model shaclGraph = ModelFactory.createDefaultModel();
		if(shaclFile.startsWith("http")) {
			shaclGraph.read(shaclFile, RDF.uri, FileUtils.guessLang(shaclFile, "Turtle"));
		} else {
			shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));
		}
		
		Model owlGraph = ModelFactory.createDefaultModel();
		if(shaclFileOWL != null) {
			if(shaclFileOWL.startsWith("http")) {
				owlGraph.read(shaclFileOWL, RDF.uri, FileUtils.guessLang(shaclFileOWL, "Turtle"));
			} else {
				owlGraph.read(new FileInputStream(shaclFileOWL), RDF.uri, FileUtils.guessLang(shaclFileOWL, "RDF/XML"));
			}			
		}
		
		// 1. read input SHACL
	    //ShapesDocumentationReaderIfc reader = new ShapesDocumentationTestReader();
		// uncomment for read SHACL parsing
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader(outDiagram,imgLogo);
		ShapesDocumentation doc = reader.readShapesDocumentation(shaclGraph, owlGraph, outputLang, shaclFile, outExpandDiagram);
		
		// 2. write Documentation structure to XML
		ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
		//OutFilePDF writerPDF = new OutFilePDF();
		ShapesDocumentationXmlWriter writerXml = new ShapesDocumentationXmlWriter();
		writer.write(doc, outputLang, System.out,"");
		writer.write(doc, outputLang, new FileOutputStream(new File("/tmp/output.html")),"");
		
		writerXml.write(doc, outputLang, System.out,"");
		writerXml.write(doc, outputLang, new FileOutputStream(new File("/tmp/output.xml")),"");
		
		
	}
	

	
}