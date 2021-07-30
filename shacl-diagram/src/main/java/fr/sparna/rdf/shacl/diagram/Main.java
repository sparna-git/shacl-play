package fr.sparna.rdf.shacl.diagram;


import java.io.File;
import java.io.FileInputStream;
import java.nio.file.attribute.UserPrincipalNotFoundException;

//import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Model; 
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String shaclFile = args[0];
		String shaclFileOWL = args[1];
		boolean outExpandDiagram = Boolean.parseBoolean(args[2]);
		
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
		

		ShaclPlantUmlWriter writer = new ShaclPlantUmlWriter(true, false, outExpandDiagram);
		String output = writer.writeInPlantUml(shaclGraph,owlGraph);
		
		String outputDirectory ="C:/Temp" ; //args[1];
		
		// determine output filename
		File inputFile = new File(shaclFile);
		String fileName = inputFile.getName();
		
		// output raw string
		OutFileUml outfile = new OutFileUml(new File(outputDirectory));
		outfile.outfileuml(output, fileName.substring(0, fileName.lastIndexOf('.'))+".iuml");

		// output in svg
		OutFileSVGUml fileplantuml = new OutFileSVGUml(new File(outputDirectory));
		fileplantuml.outfilesvguml(output, fileName.substring(0, fileName.lastIndexOf('.'))+".svg");
		
		// output in svg
		OutFilePNGuml fileplantumlpng = new OutFilePNGuml(new File(outputDirectory));
		fileplantumlpng.outfilepnguml(output, fileName.substring(0, fileName.lastIndexOf('.'))+".png");
		
		

	}
}