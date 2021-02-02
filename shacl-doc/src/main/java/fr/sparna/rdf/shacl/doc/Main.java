package fr.sparna.rdf.shacl.doc;


import java.io.File;
import java.io.FileInputStream;

import org.apache.jena.rdf.model.Model; 
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String shaclFile = args[0];
		Model shaclGraph = ModelFactory.createDefaultModel();

		shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));

	}
}