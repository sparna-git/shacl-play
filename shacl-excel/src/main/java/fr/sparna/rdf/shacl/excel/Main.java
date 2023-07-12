package fr.sparna.rdf.shacl.excel;

import java.io.FileInputStream;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

import fr.sparna.rdf.shacl.excel.model.InputDataset;

public class Main {

	public static void main(String[] args) throws Exception {
		
		
		String shaclFileTmp = args[0];
		String shaclDataSet = args[1];
		
		
		Model shaclGraphTemplate = ModelFactory.createDefaultModel();
		Model shaclGraphDataSet = ModelFactory.createDefaultModel();
		
		if(shaclDataSet.startsWith("http")) {
			shaclGraphTemplate.read(shaclFileTmp, RDF.uri, FileUtils.guessLang(shaclFileTmp, "Turtle"));
		} else {
			shaclGraphTemplate.read(new FileInputStream(shaclFileTmp), RDF.uri, FileUtils.guessLang(shaclFileTmp, "RDF/XML"));
		}
		
		
		
		if(shaclDataSet.startsWith("http")) {
			shaclGraphDataSet.read(shaclDataSet, RDF.uri, FileUtils.guessLang(shaclDataSet, "Turtle"));
		} else {
			shaclGraphDataSet.read(new FileInputStream(shaclDataSet), RDF.uri, FileUtils.guessLang(shaclDataSet, "RDF/XML"));
		}
		
		
		// read Template of build 
		
		
		
		// read dataset  
		Generator write = new Generator();
		List<InputDataset> output = write.readDocument(shaclGraphTemplate,shaclGraphDataSet);
		
		
		
	}	
}