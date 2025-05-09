package fr.sparna.rdf.shacl.app;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.xls2rdf.Xls2RdfConverter;
import fr.sparna.rdf.xls2rdf.Xls2RdfConverterFactory;

public class InputModelReader {

	private static Logger log = LoggerFactory.getLogger(InputModelReader.class.getName());
	
	public static Model populateModelFromFile(Model model, File input) throws MalformedURLException {
		return populateModelFromFile(model, Collections.singletonList(input), null);
	}
	
	public static Model populateModelFromFile(Model model, List<File> inputs) throws MalformedURLException {
		return populateModelFromFile(model, inputs, null);
	}
	
	public static Model populateModelFromFile(Model model, List<File> inputs, Map<String, String> prefixes) throws MalformedURLException {
		return populateModel(model, inputs.stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()), prefixes);
	}
	
	public static Model populateModel(Model model, String input) throws MalformedURLException {
		return populateModel(model, Collections.singletonList(input), null);
	}
	
	public static Model populateModel(Model model, List<String> inputs) throws MalformedURLException {
		return populateModel(model, inputs, null);
	}
	
	
	/**
	 * TODO : this is currently a duplication, find a way to reuse same method from Controller
	 */
	public static Model populateModel(Model model, List<String> inputs, Map<String, String> prefixes) throws MalformedURLException {
		
		for (String anInput : inputs) {
			log.debug("Reading input from : "+anInput);
			
			File inputFile = new File(anInput);
			if(inputFile.exists()) {
				if(inputFile.isDirectory()) {
					for(File f : inputFile.listFiles()) {
						model.add(populateModel(model, f.getAbsolutePath()));
					}
				} else {
					if(RDFLanguages.filenameToLang(inputFile.getName()) != null) {
						try {
							// 1. Read as a Dataset with named graphs, to deal with JSON-LD / nq situations
							Dataset d = DatasetFactory.create();
							RDFDataMgr.read(
								d,
								new FileInputStream(inputFile),
								// so that relative URI references are found in the same directory that the file being read
								inputFile.toPath().toAbsolutePath().getParent().toUri().toString(),
								RDFLanguages.filenameToLang(inputFile.getName())
							);
							// 2. load the UNION of all graphs in our model
							// Note : getUnionModel does not include the default model, this is why we need to add it explicitely
							model.add(d.getUnionModel());
							model.add(d.getDefaultModel());
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
					} else if(inputFile.getName().endsWith("xls") || inputFile.getName().endsWith("xlsx")) {
						try {
							populateModelFromExcel(model, new FileInputStream(inputFile));
						} catch (Exception e) {
							log.error("Failed reading Excel file : "+inputFile.getAbsolutePath()+", error is "+e.getMessage());
							e.printStackTrace();
						}
					} else {
						log.error("Unknown RDF format for file "+inputFile.getAbsolutePath());
					}				
				}
			} else if(anInput.startsWith("http")) {
				RDFDataMgr.read(
						model,
						anInput
				);
			}
		} 	
		
		// ensure prefixes
		if(prefixes != null) {
			prefixes.entrySet().stream().forEach(e -> model.setNsPrefix(e.getKey(), e.getValue()));
		}
		
		return model;
	}

	public static final void populateModelFromExcel(Model model, InputStream inputStream) throws Exception {
		Xls2RdfConverterFactory converterFactory = new Xls2RdfConverterFactory(
			// applyPostProcessings
			false,
			// XL
			false,
			// XL definitions
			false,
			// broader transitive
			false,
			// no fail on reconcile
			false,
			// skip hidden rows and columns
			true						
		);

		AbstractRDFHandler jenaModelWriter = new AbstractRDFHandler() {
			@Override
			public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
				model.setNsPrefix(prefix, uri);
			};

			@Override
			public void handleStatement(Statement statement) {
				// Convert RDF4J statement to Jena statement and add to the Jena model
				org.apache.jena.rdf.model.Resource subject = model.createResource(statement.getSubject().stringValue());
				org.apache.jena.rdf.model.Property predicate = model.createProperty(statement.getPredicate().stringValue());
				org.apache.jena.rdf.model.RDFNode object;
				if (statement.getObject() instanceof org.eclipse.rdf4j.model.Literal) {
					org.eclipse.rdf4j.model.Literal literal = (org.eclipse.rdf4j.model.Literal) statement.getObject();
					if (literal.getLanguage().isPresent()) {
						object = model.createLiteral(literal.getLabel(), literal.getLanguage().get());
					} else if (literal.getDatatype() != null) {
						object = model.createTypedLiteral(literal.getLabel(), literal.getDatatype().stringValue());
					} else {
						object = model.createLiteral(literal.getLabel());
					}
				} else if (statement.getObject() instanceof org.eclipse.rdf4j.model.IRI) {
					object = model.createResource(statement.getObject().stringValue());
				} else if (statement.getObject() instanceof org.eclipse.rdf4j.model.BNode) {
					object = model.createResource(statement.getObject().stringValue());
				} else {
					throw new IllegalArgumentException("Unsupported RDF4J object type: " + statement.getObject().getClass().getName());
				}
				model.add(subject, predicate, object);
			}							
		};

		// create an in-memory RDF4J repository
		org.eclipse.rdf4j.repository.Repository outputRepository = new SailRepository(new MemoryStore());
		// convert Excel
		Xls2RdfConverter converter = converterFactory.newConverter(outputRepository, null);
		converter.processInputStream(inputStream);
		// export to Jena model
		outputRepository.getConnection().export(jenaModelWriter);
		
	}
	
}
