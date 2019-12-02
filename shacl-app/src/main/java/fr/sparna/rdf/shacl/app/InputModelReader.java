package fr.sparna.rdf.shacl.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputModelReader {

	private static Logger log = LoggerFactory.getLogger(InputModelReader.class.getName());
	
	public static Model readInputModel(List<File> inputs) throws MalformedURLException {
		Model inputModel = ModelFactory.createDefaultModel();
		
		for (File inputFile : inputs) {
			log.debug("Reading input from file : "+inputFile.getAbsolutePath());
			
			if(inputFile.isDirectory()) {
				for(File f : inputFile.listFiles()) {
					inputModel.add(readInputModel(Collections.singletonList(f)));
				}
			} else {
				if(RDFLanguages.filenameToLang(inputFile.getName()) != null) {
					try {
						RDFDataMgr.read(inputModel, new FileInputStream(inputFile), RDF.getURI(), RDFLanguages.filenameToLang(inputFile.getName()));
					} catch (FileNotFoundException ignore) {
						ignore.printStackTrace();
					}
				} else {
					log.error("Unknown RDF format for file "+inputFile.getAbsolutePath());
				}				
			}
		} 	
		
		return inputModel;
	}
	
	
	
}
