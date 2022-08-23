package fr.sparna.rdf.shacl.diagram;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Map;

//import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Model; 
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

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
		

		PlantUmlDiagramGenerator writer = new PlantUmlDiagramGenerator(true, false, outExpandDiagram, "en");
		List<PlantUmlDiagramOutput> output = writer.generateDiagrams(shaclGraph,owlGraph);
		
		String outputDirectory ="C:/Temp" ; //args[1];
		
		// determine output filename
		File inputFile = new File(shaclFile);
		String fileName = inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
		
		for (int i = 0; i < output.size(); i++) {
			// output raw string
			outfileuml(output.get(i).getPlantUmlString(), new File( outputDirectory, fileName+'_'+i+".iuml") );
	
			// output in svg
			outfilesvguml(output.get(i).getPlantUmlString(), new File( outputDirectory, fileName+'_'+i+".svg") );
			
			// output in svg
			outfilepnguml(output.get(i).getPlantUmlString(), new File( outputDirectory, fileName+'_'+i+".png")  );
		}			
	}
	
	public static void outfileuml (String uml_code, File myoutputfile) throws UnsupportedEncodingException, IOException {
		
		if (!myoutputfile.exists()) {
			myoutputfile.createNewFile();
		}
		
		FileOutputStream outfile = new FileOutputStream(myoutputfile);
		
		try (Writer w = new OutputStreamWriter(outfile,"UTF-8")){
			w.write(uml_code);
		} catch (FileNotFoundException e1) {
		    e1.printStackTrace();
		}
		
		outfile.close();
	}
	
	public static void outfilesvguml (String source, File myoutputfile) throws IOException {
		
		if (!myoutputfile.exists()) {
			myoutputfile.createNewFile();
		}
		
		FileOutputStream outfile = new FileOutputStream(myoutputfile);
		
		SourceStringReader reader = new SourceStringReader(source);
		//final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write the first image to "os"
		String desc = reader.generateImage(outfile, new FileFormatOption(FileFormat.SVG));
		outfile.close();

		// The XML is stored into svg
		//final String svg = new String(outfile.toByteArray(), Charset.forName("UTF-8"));

	}
	
	public static void outfilepnguml (String source, File myoutputfile) throws IOException {

		if (!myoutputfile.exists()) {
			myoutputfile.createNewFile();
		}

		FileOutputStream outfile = new FileOutputStream(myoutputfile);

		SourceStringReader reader = new SourceStringReader(source);
		//final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write the first image to "os"
		String desc = reader.generateImage(outfile, new FileFormatOption(FileFormat.PNG));
		outfile.close();

		// The XML is stored into svg
		//final String svg = new String(outfile.toByteArray(), Charset.forName("UTF-8"));

	}
}