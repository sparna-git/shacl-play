package fr.sparna.rdf.shacl.doc.write;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc.MODE;

public class ShapesDocumentationJacksonXsltWriter implements ShapesDocumentationWriterIfc {
	
public enum XSLT {
		
		SHAPES2HTML("doc2html.xsl"),
		DATASET2HTML("dataset2html.xsl");
		
		private String xsltFileName;
		
		private XSLT(String xsltFileName) {
			this.xsltFileName = xsltFileName;
		}

		public String getXsltFileName() {
			return xsltFileName;
		}
		
	}

	
	@Override
	public void writeDoc(ShapesDocumentation documentation, String outputLang, OutputStream output, MODE mode) throws IOException {
		XSLT theXSLT = documentation.isDatasetDocumentation()?XSLT.DATASET2HTML:XSLT.SHAPES2HTML;		
		this.write(documentation, outputLang, output, mode, theXSLT);
	}

	
	private void write(ShapesDocumentation documentation, String outputLang, OutputStream output, MODE mode, XSLT XSLTStyle) throws IOException {
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
			
			// debug XML to console
			System.out.println(printToString(xmlDocument));
						
			// 3. Apply stylesheet to produce XHTML
			Source xmlInput = new DOMSource(xmlDocument);
	        StreamResult xmlOutput = new StreamResult(output);
	        // force Saxon
	        TransformerFactory transformerFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", this.getClass().getClassLoader());
	        transformerFactory.setURIResolver(new ClasspathResourceURIResolver());
	        // Jorge
	        //TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        //TransformerFactory.setAttribute("indent-number", 2);
	        
	        //Source xsltInput = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("doc2html.xsl"));
	        Source xsltInput = new StreamSource(this.getClass().getClassLoader().getResourceAsStream(XSLTStyle.getXsltFileName()));
	        Transformer transformer = transformerFactory.newTransformer(xsltInput); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        
	        // pass in the output language
	        transformer.setParameter("LANG", outputLang);
	        // set the mode PDF/HTML
	        transformer.setParameter("MODE", mode.toString());
	        
	        transformer.transform(xmlInput, xmlOutput);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}
	
	private static String printToString(Document newDoc) throws IOException {
		try {
			Source xmlInput = new DOMSource(newDoc);
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        // transformerFactory.setAttribute("indent-number", 2);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }

	}
	
	class ClasspathResourceURIResolver implements URIResolver {
		@Override
		public Source resolve(String href, String base) throws TransformerException {
			return new StreamSource(ShapesDocumentationJacksonXsltWriter.class.getClassLoader().getResourceAsStream(href));
		}
	}

}
