package fr.sparna.rdf.shacl.doc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class SVGGenerator {

	public String generateSvgDiagram(String plantUmlString) throws IOException {
		SourceStringReader reader = new SourceStringReader(plantUmlString);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		reader.generateImage(out, new FileFormatOption(FileFormat.SVG));
		out.close();
			
		// The XML is stored into svg
		String svgString = new String(out.toByteArray(), Charset.forName("UTF-8"));
		
		// ensure the characters --> don't appear in the XML comments
		svgString = svgString.replace("\" --> \"", "\" - -> \"");
		
		return svgString;
	}

}