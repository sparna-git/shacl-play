package fr.sparna.rdf.shacl.doc;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.PageSizeUnits;
import com.openhtmltopdf.pdfboxout.PdfBoxRenderer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.render.Box;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;

public class OutFilePDF {

	public void outfilepdf(String file) {

		try {
		      // Source HTML file
		      String inputHTML = "C:\\tmp\\output.html";
		      // Generated PDF file name
		      String outputPdf = "C:\\tmp\\output_doc.pdf";
		      htmlToPdf(inputHTML, outputPdf);
			} catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		    }
		  }
			
		 private static Document html5ParseDocument(String inputHTML) throws IOException{
		    org.jsoup.nodes.Document doc;
			System.out.println("parsing ...");
		    doc = Jsoup.parse(new File(inputHTML), "UTF-8");
		    System.out.println("parsing done ..." + doc);
		    return new W3CDom().fromJsoup(doc);
		  }
		
		 
		  		 
		  private static void htmlToPdf(String inputHTML, String outputPdf) throws IOException {
		    Document doc = html5ParseDocument(inputHTML);
		    String baseUri = FileSystems.getDefault()
		              .getPath("C:/", "tmp/")
		              .toUri()
		              .toString();
		    OutputStream os = new FileOutputStream(outputPdf);
		    PdfRendererBuilder _builder = new PdfRendererBuilder();
		    _builder.useFastMode();
		    _builder.useDefaultPageSize(310, 397, PageSizeUnits.MM);
		    _builder.withFile(new File(outputPdf));
		    _builder.toStream(os);
		    
		    _builder.withW3cDocument(doc, baseUri);
		    _builder.run();
		    System.out.println("PDF generation completed");
		    os.close();
		  }		  
}
