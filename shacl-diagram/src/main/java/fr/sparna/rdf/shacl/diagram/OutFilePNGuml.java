package fr.sparna.rdf.shacl.diagram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class OutFilePNGuml {
	
	protected File outputDirectory;
	
	public OutFilePNGuml(File outputDirectory) {
		super();
		this.outputDirectory = outputDirectory;
	}
	
public void outfilepnguml (String source, String fileName) throws IOException {
		
		File myoutputfile = new File( this.outputDirectory, fileName);
		
		if (!myoutputfile.exists()) {
			myoutputfile.createNewFile();
		}
		
		FileOutputStream outfile = new FileOutputStream(myoutputfile);
		
		SourceStringReader reader = new SourceStringReader(source);
		//final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write the first image to "os"
		String desc = reader.generateImage(outfile, new FileFormatOption(FileFormat.PNG));
		outfile.close();

		// The XML is stored into svg
		//final String svg = new String(outfile.toByteArray(), Charset.forName("UTF-8"));

	}

}
