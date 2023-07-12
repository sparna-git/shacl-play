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
		
		// premier paramètre : template SHACL
		String templateParam = args[0];
		// deuxième paramètre : fichier de data
		String dataParam = args[1];
		
		
		Model shaclTemplateGraph = ModelFactory.createDefaultModel();
		if(templateParam.startsWith("http")) {
			shaclTemplateGraph.read(templateParam, RDF.uri, FileUtils.guessLang(templateParam, "Turtle"));
		} else {
			shaclTemplateGraph.read(new FileInputStream(templateParam), RDF.uri, FileUtils.guessLang(templateParam, "RDF/XML"));
		}		
		
		Model dataGraph = ModelFactory.createDefaultModel();
		if(dataParam.startsWith("http")) {
			dataGraph.read(dataParam, RDF.uri, FileUtils.guessLang(dataParam, "Turtle"));
		} else {
			dataGraph.read(new FileInputStream(dataParam), RDF.uri, FileUtils.guessLang(dataParam, "RDF/XML"));
		}
		
		// read dataset  
		Generator write = new Generator();
		List<InputDataset> output = write.readDocument(shaclTemplateGraph,dataGraph);
		
		
		
	}	
}