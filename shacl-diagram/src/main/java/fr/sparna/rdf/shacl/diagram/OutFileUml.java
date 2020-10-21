package fr.sparna.rdf.shacl.diagram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class OutFileUml {
	
	public void outfileuml (String uml_code, String nameFile) throws UnsupportedEncodingException, IOException {
		
		
		File myoutputfile = new File("C:/Temp/"+nameFile); 
		
		if (!myoutputfile.exists()) {
			myoutputfile.createNewFile();
		}
		
		FileOutputStream outfile = new FileOutputStream(myoutputfile);
		
		try (Writer w = new OutputStreamWriter(outfile,"UTF-8")){
			w.write(uml_code);
		} 
		catch (FileNotFoundException e1) {
		    e1.printStackTrace();
		}
		
		outfile.close();
	}	
}
