package fr.sparna.rdf.shacl.shaclplay.validate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ControllerCommons {
	
	private static Logger log = LoggerFactory.getLogger(ControllerCommons.class.getName());
	
	public static Model populateModel(Model model, InputStream in, String lang) throws RiotException {
		try {
			model.read(in, RDF.getURI(), lang);
			return model;
		} finally {
			if(in != null) { try {in.close();} catch(Exception e) {}}
		}
	}
	
	public static Model loadModel(InputStream in, String lang) throws RiotException {
		try {
			Model model = ModelFactory.createDefaultModel();
			model.read(in, RDF.getURI(), lang);
			return model;
		} finally {
			if(in != null) { try {in.close();} catch(Exception e) {}}
		}
	}
	
	public static Model loadModel(URL url) throws RiotException, IOException {
		Model model = ModelFactory.createDefaultModel();
		try {
			// uses conneg to determine parser or can guess it from extension
			model.read(url.toString());
		} catch (Exception e) {
			log.debug("Simple read() failed based on conneg, will use "+ RDFLanguages.filenameToLang(url.getFile())+" RDF language");  
			RDFDataMgr.read(
					model,
					url.openConnection().getInputStream(),
					url.toString(),
					RDFLanguages.filenameToLang(url.getFile())
			);
		}
		return model;
	}
	
	public static Model loadModel(String inlineRdf) throws RiotException {
		Model model = ModelFactory.createDefaultModel();
		
		ByteArrayInputStream is = new ByteArrayInputStream(inlineRdf.getBytes());
		
		Lang[] supportedLangs = new Lang[] { Lang.TURTLE, Lang.RDFXML, Lang.NT, Lang.NQUADS, Lang.JSONLD, Lang.TRIG, Lang.TRIX };
		Exception turtleException = null;
		
		boolean parsed = false;
		for (Lang aLang : supportedLangs) {
			try {
				model.read(is, null, aLang.getName());
				log.debug("Successfully parsed inline data as "+aLang.getName());
				parsed = true;
			} catch (Exception e) {
				log.debug("Unable to parse inline data as "+aLang.getName());
				if(aLang == Lang.TURTLE) {
					turtleException = e;
				}
			}
		}
		
		if(!parsed) {
			throw new RiotException("Unable to parse inline text in any of the following formats : "+supportedLangs+", here is the Turtle parsing exception message : "+turtleException.getMessage(), turtleException);
		}
		
		return model;
	}
	
	public static URL getBaseUrl(HttpServletRequest request, String applicationBaseUrlFromConfig) {
		if(applicationBaseUrlFromConfig != null && !applicationBaseUrlFromConfig.equals("")) {			
			try {
				// make sure we add a final '/' if not in the application file
				return new URL(applicationBaseUrlFromConfig+((!applicationBaseUrlFromConfig.endsWith("/")?"/":"")));
			} catch (MalformedURLException ignore) {
				ignore.printStackTrace();
				return null;
			}
		} else {
			// determine base URL from the request
			try {
				// voir http://stackoverflow.com/questions/4931323/whats-the-difference-between-getrequesturi-and-getpathinfo-methods-in-httpservl
				return new URL("http://"+request.getServerName()+((request.getServerPort() != 80)?":"+request.getServerPort():"")+request.getContextPath()+"/");
			} catch (MalformedURLException ignore) {
				ignore.printStackTrace();
				return null;
			}
		}		
	}
	
}
