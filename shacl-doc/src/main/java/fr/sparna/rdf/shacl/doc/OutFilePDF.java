package fr.sparna.rdf.shacl.doc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;


public class OutFilePDF {
	public void generatePDFFromHTML(String filename) throws FileNotFoundException, IOException {
		try (OutputStream os = new FileOutputStream("/tmp/out.pdf")) 
		{
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withUri("file:///tmp/output.html");
            builder.toStream(os);
            builder.run();
        }
    }	
}