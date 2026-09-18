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

import fr.sparna.cli.watch.WatchFile;
import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.diagram.DrawFormat;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramGenerator;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.diagram.serialize.DiagramSerializer;

public class Draw implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private ArgumentsDraw a;
	
	@Override
	public void execute(Object args) throws Exception {
		this.a = (ArgumentsDraw)args;
		
		if (this.a.getWatch()) {
			this.generateDraw();
			System.out.println("Watching "+a.getInput().get(0).getAbsolutePath()+" for changes and writing output to "+a.getOutput());
			// WatchService 
			WatchFile wf = new WatchFile(a.getInput().get(0), () -> {
				try {
					this.generateDraw();
				} catch (Exception e) {
					log.error("Error regenerating on file change", e);
					e.printStackTrace();
				}
			});
			wf.runWatchFile();
		} else {
			this.generateDraw();
		}	
	}

	public void generateDraw() throws Exception {

		// read input file or URL
		Model shapesModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(shapesModel, this.a.getInput(), null);
		
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

			DiagramSerializer diagramSerializer = new DiagramSerializer();
			byte[] output = diagramSerializer.doOutputDiagram(plantUmlStringList, outputFile.getName(), DrawFormat.fromFileName(outputFile.getName()));

			// write output to file
			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				fos.write(output);
			} catch (IOException e) {
				log.error("Error writing output file", e);
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
