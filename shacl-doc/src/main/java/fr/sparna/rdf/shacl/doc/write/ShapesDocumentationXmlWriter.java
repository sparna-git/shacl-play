package fr.sparna.rdf.shacl.doc.write;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;

/**
 * Prints raw document XML
 * @author thomas
 *
 */
public class ShapesDocumentationXmlWriter implements ShapesDocumentationWriterIfc {
	
	@Override
	public void writeDoc(ShapesDocumentation documentation, String outputLang, OutputStream output, MODE mode)
			throws IOException {
		this.write(documentation, outputLang, output, mode);		
	}
	
	
	
	public void write(ShapesDocumentation documentation, String outputLang, OutputStream output, MODE mode) throws IOException {
		Document xmlDocument;
		XMLStreamWriter xmlStreamWriter;
		try {
			
			// 2. write Documentation structure to XML
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			xmlDocument = f.newDocumentBuilder().newDocument();
			
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			xmlStreamWriter = factory.createXMLStreamWriter(new DOMResult(xmlDocument));
			
			XmlMapper mapper = new XmlMapper();
			ToXmlGenerator xmlGenerator = mapper.getFactory().createGenerator(xmlStreamWriter);
			mapper.writerFor(ShapesDocumentation.class).writeValue(xmlGenerator, documentation);
			
			// Write to output stream
			//printTo(xmlDocument, output);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} 

	}
	
	private static void printTo(Document newDoc, OutputStream out) throws IOException {
		try {
			Source xmlInput = new DOMSource(newDoc);
	        StreamResult xmlOutput = new StreamResult(out);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        //transformerFactory.setAttribute("indent-number", 2);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }

	}

}
