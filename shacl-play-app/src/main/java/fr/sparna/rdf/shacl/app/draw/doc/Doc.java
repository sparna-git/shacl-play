package fr.sparna.rdf.shacl.app.draw.doc;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationReaderIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXmlWriter;

public class Doc implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsDoc a = (ArgumentsDoc)args;
		
		// read input file or URL
		Model shapesModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModel(shapesModel, a.getInput(), null);
		
		// generate doc
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader();
		ShapesDocumentation doc = reader.readShapesDocumentation(new FileInputStream(a.getInput().get(0)), a.getInput().get(0).getName());
		
		// 2. write Documentation structure to XML
		
		FileOutputStream out = new FileOutputStream(a.getOutput());
		if(a.getOutput().getName().endsWith(".xml")) {
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationXmlWriter();
			writer.write(doc, out);
		} else {
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
			writer.write(doc, out);
		}
		out.close();
	
	}
	
}
