package fr.sparna.rdf.shacl.shacl2xsd;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.jena.rdf.model.Model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Shacl2XsdConverter {

	protected String targetNamespace;
	
	public Shacl2XsdConverter(String targetNamespace) {
		super();
		this.targetNamespace = targetNamespace;
	}

	public Document convert(Model shacl) throws Exception {
		Document doc = this.initDocument();
		doConvert(shacl, doc);
		return doc;
	}
	
	protected void doConvert(Model shacl, Document document) throws Exception {
		initRoot(document);
		// here : do actual conversion
	}
	
	private void initRoot(Document doc) {
		Element root = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:schema");
		root.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema#");
		root.setAttribute("xmlns", targetNamespace);
		root.setAttribute("targetNamespace", targetNamespace);
		root.setAttribute("version", "1.0.0");
		root.setAttribute("elementFormDefault", "qualified");
		doc.appendChild(root);
	}
	
	private Document initDocument() {
		Document document = null;
	    DocumentBuilderFactory factory = null;

	    try {
	      factory = DocumentBuilderFactory.newInstance();
	      factory.setNamespaceAware(true);
	      DocumentBuilder builder = factory.newDocumentBuilder();	      
	      document = builder.newDocument();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    
	    return document;
	}
	
}
