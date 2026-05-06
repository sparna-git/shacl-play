package fr.sparna.rdf.shacl.diagram.plantuml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUmlSvgSerializer {

	private static String SVG_ID_FIX_PATTERN = "(id=\")(\\S*)(\" rx=)";
	private static String SVG_XML_DECLARATION = "<\\?xml version=\"[0-9].[0-9]\" encoding=\"[a-z-]+\" standalone=\"[a-z]+\"\\?>";
	private static String SVG_LENGTHADJUST = "lengthAdjust=\"[^\"]*\"";
	private static String SVG_TEXTLENGTH = "textLength=\"[^\"]*\"";

	public void serializeInSVG(String plantUmlString, OutputStream output) throws IOException {
		SourceStringReader reader = new SourceStringReader(plantUmlString);
		// temporary output to be post-processed
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		reader.generateImage(out, new FileFormatOption(FileFormat.SVG));
		out.close();
			
		// get the string back
		String svgString = new String(out.toByteArray(), Charset.forName("UTF-8"));
		
		// replace the namespace
		if (svgString.contains("g xmlns=\"\"")) {
			svgString = svgString.replace("g xmlns=\"\"","g");
		}
		
		// ensure the characters --> don't appear in the XML comments
		svgString = svgString.replace("\" --> \"", "\" - -> \"");
		
		// post-process for Safari
		svgString = this.safariPostProcess(svgString);

		// write post-processed String in the output stream
		output.write(svgString.getBytes("UTF-8"));	
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

}