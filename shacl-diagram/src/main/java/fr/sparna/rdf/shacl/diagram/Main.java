package fr.sparna.rdf.shacl.diagram;

import java.io.FileInputStream;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String shaclFile = args[0];
		
		Model shaclGraph = ModelFactory.createDefaultModel();
		shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));
		
		System.out.println("SHACL Graph contains "+shaclGraph.size()+" triples");
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
		for (Resource nodeShape : nodeShapes) {
			System.out.println("Found a NodeShape : "+nodeShape.getURI());
		}
		
	}

}
