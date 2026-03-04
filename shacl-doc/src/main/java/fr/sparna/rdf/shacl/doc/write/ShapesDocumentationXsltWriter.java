package fr.sparna.rdf.shacl.doc.write;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;

public class ShapesDocumentationXsltWriter implements ShapesDocumentationWriterIfc {

	private ShapesDocumentationWriterIfc.MODE mode;
	private String docXsltFilename;
	private String datasetXsltFilename;

	public ShapesDocumentationXsltWriter(
		ShapesDocumentationWriterIfc.MODE mode,
		String docXsltFilename,
		String datasetXsltFilename
	) {
		this.mode = mode;
		this.docXsltFilename = docXsltFilename;
		this.datasetXsltFilename = datasetXsltFilename;
	}

	
	@Override
	public void writeDoc(ShapesDocumentation documentation, String outputLang, OutputStream output) throws IOException {
		String xsltFileName = documentation.isDatasetDocumentation()?this.datasetXsltFilename:this.docXsltFilename;

		this.write(documentation, outputLang, output, xsltFileName);
	}

	
	private void write(ShapesDocumentation documentation, String outputLang, OutputStream output, String xsltFilename) throws IOException {
		Document xmlDocument;
		XMLStreamWriter xmlStreamWriter;
		
		try {
			
			// 1. write Documentation structure to XML
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			xmlDocument = f.newDocumentBuilder().newDocument();
			
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			xmlStreamWriter = factory.createXMLStreamWriter(new DOMResult(xmlDocument));
			
			XmlMapper mapper = new XmlMapper();
			ToXmlGenerator xmlGenerator = mapper.getFactory().createGenerator(xmlStreamWriter);
			mapper.writerFor(ShapesDocumentation.class).writeValue(xmlGenerator, documentation);
						
			// 2. Apply stylesheet to produce XHTML
			Source xmlInput = new DOMSource(xmlDocument);
	        StreamResult xmlOutput = new StreamResult(output);
	        // force Saxon
	        TransformerFactory transformerFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", this.getClass().getClassLoader());
	        transformerFactory.setURIResolver(new ClasspathResourceURIResolver());
	        
	        Source xsltInput = new StreamSource(this.getClass().getClassLoader().getResourceAsStream(xsltFilename));
	        Transformer transformer = transformerFactory.newTransformer(xsltInput); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        
	        // pass in the output language
	        transformer.setParameter("LANG", outputLang);
	        // set the mode PDF/HTML
	        transformer.setParameter("MODE", this.mode.toString());
	        
	        transformer.transform(xmlInput, xmlOutput);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class ClasspathResourceURIResolver implements URIResolver {
		@Override
		public Source resolve(String href, String base) throws TransformerException {
			return new StreamSource(ShapesDocumentationXsltWriter.class.getClassLoader().getResourceAsStream(href));
		}
	}

}
