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

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationReaderIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc.MODE;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXmlWriter;

public class Doc implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsDoc a = (ArgumentsDoc)args;
		
		// read input file or URL
		Model shapesModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(shapesModel, a.getInput(), null);
		
		// read ontology file
		Model owlModel = ModelFactory.createDefaultModel(); 
		if(a.getOntologies() != null) {
			InputModelReader.populateModelFromFile(owlModel, a.getOntologies(), null);
		}
		
		// create output dir if not existing
		File outputDir = a.getOutput().getParentFile();
		if(outputDir != null && !outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		String name_img = null;
		if(a.getImgLogo() != null) {			
			if(new File(a.getImgLogo()).exists()) {
				File fileImg = new File(a.getImgLogo()); 
				File fileOut = new File(a.getOutput().toString());
				name_img = fileImg.getName();
				// copy imagen file in the output directory
				Path sourceImg = FileSystems.getDefault().getPath(a.getImgLogo().toString());
				Path outputDirImg = FileSystems.getDefault().getPath(fileOut.getParentFile().getPath()+"\\"+name_img);
				Files.copy(sourceImg, outputDirImg, StandardCopyOption.REPLACE_EXISTING);					
			} else {
				// not an existing file, take it as a URL
				name_img = a.getImgLogo();
			}
		}
		
		// generate doc
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader(a.getDiagramShacl(), name_img, a.getHidePropertiesShacl());
		ShapesDocumentation doc = reader.readShapesDocumentation(
				shapesModel,
				owlModel,
				a.getLanguage()
		);
		
		
		FileOutputStream out = new FileOutputStream(a.getOutput());
		if(a.getPdf()) {
			
			// 1. write Documentation structure to XML
			ShapesDocumentationWriterIfc writerHTML = new ShapesDocumentationJacksonXsltWriter();
			ByteArrayOutputStream htmlBytes = new ByteArrayOutputStream();
			writerHTML.writeDoc(doc,a.getLanguage(), htmlBytes, MODE.PDF);
			
			//read file html
			String htmlCode = new String(htmlBytes.toByteArray(),"UTF-8");
			
			// Convert
			PdfRendererBuilder _builder = new PdfRendererBuilder();			 
			_builder.useFastMode();
			
			_builder.withHtmlContent(htmlCode,"http://shacl-play.sparna.fr/play");			
			
			try (OutputStream os = new FileOutputStream(a.getOutput())) {
				_builder.toStream(os);
				_builder.testMode(false);
				_builder.run();
			}			
		} else {
			
			if(a.getOutput().getName().endsWith(".xml")) {
				// 2. write Documentation structure to XML
				ShapesDocumentationWriterIfc writer = new ShapesDocumentationXmlWriter();
				writer.writeDoc(doc, a.getLanguage(), out, MODE.HTML);
			} else {
				// 2. write Documentation structure to HTML
				ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
				writer.writeDoc(doc, a.getLanguage(), out, MODE.HTML);
			}
			out.close();
		}	
	}

}
