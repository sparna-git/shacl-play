package fr.sparna.rdf.shacl.shacl2xsd;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputModelReader {

	private static Logger log = LoggerFactory.getLogger(InputModelReader.class.getName());
	
	public static Model populateModel(Model model, List<File> inputs) throws MalformedURLException {
		return populateModel(model, inputs, null);
	}
	
	public static Model populateModel(Model model, File input) throws MalformedURLException {
		return populateModel(model, Collections.singletonList(input), null);
	}

	
	public static Model populateModel(Model model, List<File> inputs, Map<String, String> prefixes) throws MalformedURLException {
		
		for (File inputFile : inputs) {
			log.debug("Reading input from file : "+inputFile.getAbsolutePath());
			
			if(inputFile.isDirectory()) {
				for(File f : inputFile.listFiles()) {
					model.add(populateModel(model, Collections.singletonList(f)));
				}
			} else {
				if(RDFLanguages.filenameToLang(inputFile.getName()) != null) {
					try {
						RDFDataMgr.read(
								model,
								new FileInputStream(inputFile),
								// so that relative URI references are found in the same directory that the file being read
								inputFile.toPath().toAbsolutePath().getParent().toUri().toString(),
								RDFLanguages.filenameToLang(inputFile.getName())
						);
					} catch (FileNotFoundException ignore) {
						ignore.printStackTrace();
					}
				} else if(inputFile.getName().endsWith("zip")) {
					try(ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFile))) {
				    	ZipEntry entry;	    	
				    	while ((entry = zis.getNextEntry()) != null) {
				    		if(!entry.isDirectory()) {
				    			log.debug("Reading zip entry : "+entry.getName());  
				    			String lang = FileUtils.guessLang(entry.getName(), "RDF/XML");
				    			// read in temporary byte array otherwise model.read closes the stream !
				    			// not available in Java 8
				    			// byte[] buffer = zis.readAllBytes();
				    			byte[] buffer = IOUtils.toByteArray(zis);
				    			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
				    			try {
				    				model.read(bais, RDF.getURI(), lang);
				    			} catch (Exception ignore) {
									ignore.printStackTrace();
								}
				    		}            
				        }
			    	} catch (Exception ignore) {
						ignore.printStackTrace();
					}
				} else {
					log.error("Unknown RDF format for file "+inputFile.getAbsolutePath());
				}				
			}
		} 	
		
		// ensure prefixes
		if(prefixes != null) {
			prefixes.entrySet().stream().forEach(e -> model.setNsPrefix(e.getKey(), e.getValue()));
		}
		
		return model;
	}
	
}
