package fr.sparna.rdf.shacl.app.doc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import fr.sparna.rdf.WatchService.watchFile;
import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationReaderIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc.MODE;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXmlWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXsltRespecWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXsltShaclPlayWriter;

public class Doc implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private ArgumentsDoc a;
	
	@Override
	public void execute(Object args) throws Exception {
		this.a = (ArgumentsDoc)args;
	
		if (this.a.getWatch()) {
			this.generateDoc();
			System.out.println("The file :" + this.a.getOutput() + " was generated.");
			//
			watchFile wf = new watchFile(a.getInput().get(0), () -> {
				try {
					this.generateDoc();
				} catch (Exception e) {
					log.error("Error regenerating documentation on file change", e);
				}
			});
			wf.runWatchFile();
		} else {
			this.generateDoc();
		}	
	}

	public void generateDoc() throws Exception {

		// read input file or URL
		Model shapesModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(shapesModel, this.a.getInput(), null);
		
		// read ontology file
		Model owlModel = ModelFactory.createDefaultModel(); 
		if(this.a.getOntologies() != null) {
			InputModelReader.populateModelFromFile(owlModel, this.a.getOntologies(), null);
		}
		
		// create output dir if not existing
		File outputDir = this.a.getOutput().getParentFile();
		if(outputDir != null && !outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		String name_img = null;
		if(this.a.getImgLogo() != null) {			
			if(new File(this.a.getImgLogo()).exists()) {
				File fileImg = new File(this.a.getImgLogo()); 
				File fileOut = new File(this.a.getOutput().toString());
				name_img = fileImg.getName();
				// copy imagen file in the output directory
				Path sourceImg = FileSystems.getDefault().getPath(this.a.getImgLogo().toString());
				Path outputDirImg = FileSystems.getDefault().getPath(fileOut.getParentFile().getPath()+"\\"+name_img);
				Files.copy(sourceImg, outputDirImg, StandardCopyOption.REPLACE_EXISTING);					
			} else {
				// not an existing file, take it as a URL
				name_img = this.a.getImgLogo();
			}
		}
		
		ShapesDocumentationReaderIfc reader = ShapesDocumentationModelReader.buildShapesDocumentationModelReader(
			shapesModel,
			owlModel,
			this.a.getLanguage(),
			this.a.getDiagramShacl(),
			name_img,
			this.a.getHidePropertiesShacl(),
			!this.a.getNoSectionDiagrams(),
			!this.a.getNoUnusedNodeShapeFiltering()
		);
		
		ShapesDocumentation doc = reader.readShapesDocumentation();
		
		
		FileOutputStream out = new FileOutputStream(this.a.getOutput());
		if(this.a.isPdfOutput()) {			
			// 1. write Documentation structure to XML
			ShapesDocumentationWriterIfc writerHTML = new ShapesDocumentationXsltShaclPlayWriter(MODE.PDF);
			ByteArrayOutputStream htmlBytes = new ByteArrayOutputStream();
			writerHTML.writeDoc(
				doc,
				this.a.getLanguage(),
				htmlBytes
			);
			
			//read file html
			String htmlCode = new String(htmlBytes.toByteArray(),"UTF-8");
			
			// Convert
			PdfRendererBuilder _builder = new PdfRendererBuilder();			 
			_builder.useFastMode();
			
			_builder.withHtmlContent(htmlCode,"https://shacl-play.sparna.fr/play");			
			
			try (OutputStream os = new FileOutputStream(this.a.getOutput())) {
				_builder.toStream(os);
				_builder.testMode(false);
				_builder.run();
			}			
		} else if(this.a.isXmlOutput()) {
			// 2. write Documentation structure to XML
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationXmlWriter();
			writer.writeDoc(doc, this.a.getLanguage(), out);
		} else {

			if (this.a.getOldversion()) {
				ShapesDocumentationWriterIfc writer = new ShapesDocumentationXsltShaclPlayWriter(MODE.HTML);
				writer.writeDoc(doc, this.a.getLanguage(), out);
			} else {
				// 2. write Documentation structure to HTML
				ShapesDocumentationWriterIfc writer = new ShapesDocumentationXsltRespecWriter(MODE.HTML); //ShapesDocumentationXsltShaclPlayWriter(MODE.HTML);
				writer.writeDoc(doc, this.a.getLanguage(), out);
			}
		}	
		out.flush();
		out.close();

	}

}
