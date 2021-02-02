package fr.sparna.rdf.shacl.doc;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.nio.file.attribute.UserPrincipalNotFoundException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String shaclFile = args[0];
		Model shaclGraph = ModelFactory.createDefaultModel();

		shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));

		ShaclWriter writer = new ShaclWriter();
		String output = writer.writeInsShaclxml(shaclGraph);
		
		String outputDirectory ="C:/Temp" ; //args[1];
		
		// determine output filename
		File inputFile = new File(shaclFile);
		String fileName = inputFile.getName();
		
	
		// output raw string
	    OutFileDocument outfile = new OutFileDocument(new File(outputDirectory));
		outfile.outfileuml(output, fileName.substring(0, fileName.lastIndexOf('.'))+".xml");
		
	}
}