package fr.sparna.rdf.shacl.shacl2xsd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Main {
	
	 static File mainFolder = new File("C:\\Users\\thoma\\Documents\\02-Projet\\02 Parleiament\\QMS");
	 static File constraintsFile = new File("C:/Users/thoma/Documents/02-Projet/02 Parleiament/QMS/AMS-constraints-mdr.ttl");
		
	//static File mainFolder = new File("/home/thomas/sparna/00-Clients/Cognizone/04-ELM/03-SHACL/ams");
	//static File constraintsFile = new File("/home/thomas/sparna/00-Clients/Cognizone/04-ELM/03-SHACL/AMS-constraints-mdr.ttl");
		
	
	 public static void main(String[] args) throws Exception
     {
		 
		 Model shaclGraph = ModelFactory.createDefaultModel();
		 shaclGraph = InputModelReader.populateModel(shaclGraph, mainFolder);
		 
		 Model constraintsGraph = ModelFactory.createDefaultModel();
		 constraintsGraph = InputModelReader.populateModel(constraintsGraph, constraintsFile);
		 
		 Shacl2XsdConverter convert = new Shacl2XsdConverter("https://data.europa.eu/snb/model#");		 
		 Document ooutputXSD = convert.convert(shaclGraph,constraintsGraph);
		 
		 File outputFile = new File(mainFolder, "output.xsd");
		 writeNode(ooutputXSD, new FileOutputStream(outputFile));
		
		 
     }
     
		private static void writeNode(Node node, OutputStream out) {
			try {
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.transform(new DOMSource(node), new StreamResult(out));
			} catch (TransformerException te) {
				System.out.println("nodeToString Transformer Exception");
			}
		}
	 
}
