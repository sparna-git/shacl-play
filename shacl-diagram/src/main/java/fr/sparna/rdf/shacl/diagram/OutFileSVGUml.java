package fr.sparna.rdf.shacl.diagram;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.svg.SvgGraphics;

public class OutFileSVGUml {
	
	public void outfilesvguml (String source) throws IOException {
		
		File myoutputfile = new File( "C:/Temp/outputsvg.svg");
		
		if (!myoutputfile.exists()) {
			myoutputfile.createNewFile();
		}
		
		FileOutputStream outfile = new FileOutputStream(myoutputfile);
		
		SourceStringReader reader = new SourceStringReader(source);
		//final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write the first image to "os"
		String desc = reader.generateImage(outfile, new FileFormatOption(FileFormat.SVG));
		outfile.close();

		// The XML is stored into svg
		//final String svg = new String(outfile.toByteArray(), Charset.forName("UTF-8"));

	}

}
