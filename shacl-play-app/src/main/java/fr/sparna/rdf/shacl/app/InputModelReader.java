package fr.sparna.rdf.shacl.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputModelReader {

	private static Logger log = LoggerFactory.getLogger(InputModelReader.class.getName());
	
	public static Model populateModel(Model model, List<File> inputs) throws MalformedURLException {
		return populateModel(model, inputs, null);
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
