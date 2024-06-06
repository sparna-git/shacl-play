package fr.sparna.rdf.shacl.app.draw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramGenerator;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class Draw implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsDraw a = (ArgumentsDraw)args;
		
		// read input file or URL
		Model shapesModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(shapesModel, a.getInput(), null);
		
		// draw
		PlantUmlDiagramGenerator writer = new PlantUmlDiagramGenerator(
				a.isIncludeSubclasses(),
				false,
				true,
				// hide Properties
				false,
				// language
				null
		);
		// TODO : add parameter with OWL file
		List<PlantUmlDiagramOutput> plantUmlStringList = writer.generateDiagrams(shapesModel, ModelFactory.createDefaultModel());
		
		for(File outputFile : a.getOutput()) {
			
			// create output dir if not existing
			File outputDir = outputFile.getParentFile();
			if(outputDir != null && !outputDir.exists()) {
				outputDir.mkdirs();
			}
			
			log.debug("Drawing to "+outputFile.getAbsolutePath());
			
			// TODO : handle multiple diagram output
			String plantUmlString = plantUmlStringList.get(0).getPlantUmlString();

			if(outputFile.getName().endsWith(".iuml")) {
				// output raw string
				write(plantUmlString, outputFile);
			} else if(outputFile.getName().endsWith(".svg")) {				
				FileOutputStream out = new FileOutputStream(outputFile);
				SourceStringReader reader = new SourceStringReader(plantUmlString);
				reader.generateImage(out, new FileFormatOption(FileFormat.SVG));
				out.close();
			} else if(outputFile.getName().endsWith(".png")) {				
				FileOutputStream out = new FileOutputStream(outputFile);
				SourceStringReader reader = new SourceStringReader(plantUmlString);
				reader.generateImage(out, new FileFormatOption(FileFormat.PNG));
				out.close();
			} else if(outputFile.getName().endsWith(".pdf")) {				
				FileOutputStream out = new FileOutputStream(outputFile);
				SourceStringReader reader = new SourceStringReader(plantUmlString);
				reader.generateImage(out, new FileFormatOption(FileFormat.PDF));
				out.close();
			} else {
				// raw string by default
				write(plantUmlString, outputFile);
			}
	
		}		
	}
	
	public void write(String uml_code, File file) throws UnsupportedEncodingException, IOException {
		
		FileOutputStream outfile = new FileOutputStream(file);
		
		try (Writer w = new OutputStreamWriter(outfile,"UTF-8")){
			w.write(uml_code);
		} catch (FileNotFoundException e1) {
		    e1.printStackTrace();
		}
		
		outfile.close();
	}	
}
