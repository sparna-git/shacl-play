package fr.sparna.rdf.shacl.doc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;


public class OutFilePDF {
	public void generatePDFFromHTML(String filename)
			throws ParserConfigurationException, IOException, DocumentException {
		Document document = new Document(PageSize.LETTER);
		
		// get Instance of the PDFWriter
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("/tmp/outputfile.pdf"));
		//open document
		document.open();
		
		//get the XMLWorkerHelper Instance
		XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
		//convert to PDF
		worker.parseXHtml(writer, document, new FileInputStream(filename));
        
		
		document.close();
		
	}
}