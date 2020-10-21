package fr.sparna.rdf.shacl.diagram;


import java.io.File;
import java.io.FileInputStream;

//import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Model;   /* Creation et manipulation de RDF */
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

public class Main {

	public static void main(String[] args) throws Exception {

		String shaclFile = args[0];
		Model shaclGraph = ModelFactory.createDefaultModel();

		shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile, "RDF/XML"));

		ShaclPlantUmlWriter writer = new ShaclPlantUmlWriter();
		String output = writer.writeInPlantUml(shaclGraph);
		
		String outputDirectory = args[1];
		
		// determine output filename
		File inputFile = new File(shaclFile);
		String fileName = inputFile.getName();
		
		// output raw string
		OutFileUml outfile = new OutFileUml(new File(outputDirectory));
		outfile.outfileuml(output, fileName.substring(0, fileName.lastIndexOf('.'))+".iuml");

		// output in svg
		OutFileSVGUml fileplantuml = new OutFileSVGUml(new File(outputDirectory));
		fileplantuml.outfilesvguml(output, fileName.substring(0, fileName.lastIndexOf('.'))+".svg");

	}
}