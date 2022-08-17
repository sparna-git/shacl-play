package fr.sparna.rdf.shacl.app.doc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.doc.PlantUmlSourceGenerator;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationReaderIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXmlWriter;
import net.sourceforge.plantuml.code.TranscoderUtil;

public class Doc implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsDoc a = (ArgumentsDoc)args;
		
		//Call imagen
		
		
		// read input file or URL
		Model shapesModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModel(shapesModel, a.getInput(), null);
		
		// read ontology file
		Model owlModel = ModelFactory.createDefaultModel(); 
		if(a.getOntologies() != null) {
			InputModelReader.populateModel(owlModel, a.getOntologies(), null);
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
				try {
					Files.copy(sourceImg, outputDirImg, StandardCopyOption.REPLACE_EXISTING);
					
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e);
				}
			}else {
				name_img = a.getImgLogo();
			}
		}
		
		if(a.getPdf()) {
			a.setDiagramShacl(false);
		}
		
		// generate doc
		// true to read diagram
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader(a.getDiagramShacl(),name_img);
		ShapesDocumentation doc = reader.readShapesDocumentation(
				shapesModel,
				owlModel,
				a.getLanguage(),
				a.getInput().get(0).getName(),
				// avoid arrows to empty boxes
				true
		);
		
		
		FileOutputStream out = new FileOutputStream(a.getOutput());
		List<String> urlPngDiagram = new ArrayList<String>();
		if(a.getPdf()) {
			
			System.out.println("Creation pdf file");
			
			// 1. write Documentation structure to HTML
			// Option pour créer le diagramme	 	
			PlantUmlSourceGenerator sourceGenerator = new PlantUmlSourceGenerator();
			try {
				// Read source Uml
				List<String> plantUmlSourceCode = sourceGenerator.generatePlantUmlDiagram(shapesModel, ModelFactory.createDefaultModel(),false,false,false);
				// if source uml is true generate png file
				if(!plantUmlSourceCode.isEmpty()) {
					for (String sourceCode : plantUmlSourceCode) {
						// Write the first image to "png"
						urlPngDiagram.add("http://www.plantuml.com/plantuml/png/"+TranscoderUtil.getDefaultTranscoder().encode(sourceCode));
					}
				}
			} catch (IOException e) {
			}
			
			ShapesDocumentationWriterIfc writerHTML = new ShapesDocumentationJacksonXsltWriter();
			ByteArrayOutputStream htmlBytes = new ByteArrayOutputStream();
			writerHTML.write(doc, a.getLanguage(), htmlBytes,urlPngDiagram);
			
			//read file html
			if(!a.getOutput().exists()) {
				System.out.println("pdf file exist");
			 
			}
			//read file html
			String htmlCode = new String(htmlBytes.toByteArray());
			htmltopdfFile(htmlCode,a.getOutput());
			
			
		}else {
			
			if(a.getOutput().getName().endsWith(".xml")) {
				// 2. write Documentation structure to XML
				ShapesDocumentationWriterIfc writer = new ShapesDocumentationXmlWriter();
				writer.write(doc, a.getLanguage(), out,urlPngDiagram);
			} else {
				// 2. write Documentation structure to HTML
				ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
				writer.write(doc, a.getLanguage(), out,urlPngDiagram);
			}
			out.close();
		}	
	}
	
	
	public String readFileHTML(String string) throws IOException {
		FileReader fr=new FileReader(string);
		BufferedReader br= new BufferedReader(fr);
		StringBuilder contentHTML=new StringBuilder(1024);
		String s;
		while((s=br.readLine())!=null)
		    {
			contentHTML.append(s);
		    } 
		br.close();
		
		return contentHTML.toString();		
	}
	
	
	public void htmltopdfFile(String codeHtml,File outFile) throws IOException {
		System.out.println("Pdf path "+outFile);
		
		try (OutputStream os = new FileOutputStream(outFile)) {
		   System.out.println("building construct");
		   PdfRendererBuilder _builder = new PdfRendererBuilder();
		   _builder.useFastMode();
		   _builder.withHtmlContent(codeHtml, "/");
		   
		   _builder.toStream(os);
		  
		   _builder.run();
		   
		   //os.close(); 
		   
		   System.out.println("pdf file created ...");
		}
		 
		
	}
}
