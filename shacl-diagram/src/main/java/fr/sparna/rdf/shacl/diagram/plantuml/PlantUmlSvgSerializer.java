package fr.sparna.rdf.shacl.diagram.plantuml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class PlantUmlSvgSerializer {

	private static String SVG_ID_FIX_PATTERN = "(id=\")(\\S*)(\" rx=)";
	private static String SVG_XML_DECLARATION = "<\\?xml version=\"[0-9].[0-9]\" encoding=\"[a-z-]+\" standalone=\"[a-z]+\"\\?>";
	private static String SVG_LENGTHADJUST = "lengthAdjust=\"[^\"]*\"";
	private static String SVG_TEXTLENGTH = "textLength=\"[^\"]*\"";

	public String serializeInSVG(String plantUmlString) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.serializeInSVG(plantUmlString, out);
		return new String(out.toByteArray(), Charset.forName("UTF-8"));
	}

	public void serializeInSVG(String plantUmlString, OutputStream output) throws IOException {
		SourceStringReader reader = new SourceStringReader(plantUmlString);
		// temporary output to be post-processed
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		reader.generateImage(out, new FileFormatOption(FileFormat.SVG));
		out.close();
		
		// get the string back
		String svgString = this.preprocessingSVGCode(new String(out.toByteArray(), Charset.forName("UTF-8")));

		// write post-processed String in the output stream
		output.write(svgString.getBytes("UTF-8"));	
	}

	public String metadataInSVG(String plantUmlString, String titleDiagram) throws IOException {
		//
		SourceStringReader reader = new SourceStringReader(plantUmlString);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		reader.generateImage(out, new FileFormatOption(FileFormat.SVG));

		// temporary output to be post-processed
		ByteArrayOutputStream baous = new ByteArrayOutputStream();
		try {
			this.addElementsInSVG(out,baous,titleDiagram);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
		// Preprocessing
		String svgString = this.preprocessingSVGCode(new String(baous.toByteArray(), Charset.forName("UTF-8")));
		// Output
		return new String(svgString.getBytes("UTF-8"));
	}

	public String preprocessingSVGCode(String svgString) {

		// replace the namespace
		if (svgString.contains("g xmlns=\"\"")) {
			svgString = svgString.replace("g xmlns=\"\"","g");
		}

		// ensure the characters --> don't appear in the XML comments
		svgString = svgString.replace("\" --> \"", "\" - -> \"");
		
		// post-process for Safari
		svgString = this.safariPostProcess(svgString);

		return svgString;

	}

	private String safariPostProcess(String s) {
		// adjust the SVG ids
		s = s.replaceAll(SVG_ID_FIX_PATTERN, "$1uml_$2$3");
		// Replace xml
		s = s.replaceAll(SVG_XML_DECLARATION,"");
		// Replace lengthAdjust
		s = s.replaceAll(SVG_LENGTHADJUST,"");
		// Replace TextLength
		s = s.replaceAll(SVG_TEXTLENGTH,"");

		return s;
	}

	public void addElementsInSVG(ByteArrayOutputStream out, OutputStream output, String titleDiagram ) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		
		ByteArrayInputStream intSVG = new ByteArrayInputStream(out.toByteArray());

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(intSVG);
		
		// Instance
		Element svgElement = doc.getDocumentElement();

		// SVG Configuration
		svgElement.setAttribute("role", "img");
		svgElement.setAttribute("aria-labelledby", "schema-title schema-desc");

		// Create <title> Element
		Element eTitle = doc.createElementNS("http://www.w3.org/2000/svg", "title");
		eTitle.setAttribute("id", "schema-title");
		eTitle.setTextContent("Diagram for shape " + titleDiagram);
		svgElement.appendChild(eTitle);

		// Create <desc> Element
		Element eDesc = doc.createElementNS("http://www.w3.org/2000/svg","desc");
		eDesc.setAttribute("id", "schema-desc");
		eDesc.setTextContent("This diagram shows a UML model centered around the " + titleDiagram + " entity");
		svgElement.appendChild(eDesc);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult(output));
	}


}