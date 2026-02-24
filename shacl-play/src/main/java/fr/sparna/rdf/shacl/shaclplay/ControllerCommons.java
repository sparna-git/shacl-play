package fr.sparna.rdf.shacl.shaclplay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.sparna.rdf.xls2rdf.Xls2RdfConverter;
import fr.sparna.rdf.xls2rdf.Xls2RdfConverterFactory;


public class ControllerCommons {
	
	private static Logger log = LoggerFactory.getLogger(ControllerCommons.class.getName());	
	
	public static Model populateModel(Model model, InputStream in, String lang) throws RiotException {
		try {
			// 1. Read as a Dataset with named graphs, to deal with JSON-LD situations
			Dataset d = DatasetFactory.create();
			RDFDataMgr.read(d, in, RDF.getURI(), RDFLanguages.nameToLang(lang));
			// 2. load the UNION of all graphs in our model
			// Note : getUnionModel does not include the default model, this is why we need to add it explicitely
			model.add(d.getUnionModel());
			model.add(d.getDefaultModel());
			return model;
		} finally {
			if(in != null) { try {in.close();} catch(Exception e) {}}
		}
	}
	
	public static Model populateModel(Model model, URL url) throws RiotException, IOException {
		try {
			// uses conneg to determine parser or can guess it from extension
			model.read(url.toString());
		} catch (Exception e) {
			// default to Turtle to be able to parse catalog entries without ttl extension at the end
			log.debug("Simple read() failed based on conneg, will use "+ RDFLanguages.filenameToLang(url.getFile(), Lang.TURTLE)+" RDF language");  
			
			Dataset d = DatasetFactory.create();
			RDFDataMgr.read(
					d,
					url.openConnection().getInputStream(),
					url.toString(),
					RDFLanguages.filenameToLang(url.getFile(), Lang.TURTLE)
			);
			model.add(d.getUnionModel());
			model.add(d.getDefaultModel());
		}
		return model;
	}
	
	public static Model populateModel(Model model, String inlineRdf) throws RiotException {
		
		Lang[] supportedLangs = new Lang[] { Lang.TURTLE, Lang.RDFXML, Lang.NT, Lang.NQUADS, Lang.JSONLD, Lang.TRIG, Lang.TRIX };
		Exception turtleException = null;
		
		boolean parsed = false;
		for (Lang aLang : supportedLangs) {
			try {
				ByteArrayInputStream is = new ByteArrayInputStream(inlineRdf.getBytes());
				model.read(is, null, aLang.getName());
				log.debug("Successfully parsed inline data as "+aLang.getName());
				parsed = true;
				break;
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
	
    public static Model populateModelFromZip(Model model, InputStream in) throws RiotException, IOException {

    	try(ZipInputStream zis = new ZipInputStream(in)) {
	    	ZipEntry entry;
	    	
	    	Dataset d = DatasetFactory.create();
	    	while ((entry = zis.getNextEntry()) != null) {
	    		if(!entry.isDirectory()) {
	    			String lang = FileUtils.guessLang(entry.getName(), "RDF/XML");
	    			log.debug("Processing zip entry : "+ entry.getName()+", guessed lang "+lang);
	    			// read in temporary byte array otherwise model.read closes the stream !
	    			// not available in Java 8
	    			// byte[] buffer = zis.readAllBytes();
	    			byte[] buffer = IOUtils.toByteArray(zis);
	    			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
	    			try {	    				
	    				RDFDataMgr.read(
	    						d,
	    						bais,
	    						RDF.getURI(),
	    						RDFLanguages.filenameToLang(entry.getName(), Lang.RDFXML)
	    				);
						log.debug("Success");
					} catch (Exception e) {
						log.warn("Failed reading zip entry : "+entry.getName()+", error is "+e.getMessage()+", skipping.");
					}
	    			
	    		}            
	        }
	    	
	    	// flatten all named graphs in a single graph
	    	model.add(d.getUnionModel());
			model.add(d.getDefaultModel());
    	}
    	
    	return model;
    }

	public static final void populateModelFromExcel(Model model, InputStream inputStream) throws ControllerModelException {
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
			// Convert RDF4J statement to Jena statement and add to the Jena model
			public void handleStatement(Statement statement) {
				
				// convert subject
				org.apache.jena.rdf.model.Resource subject; 
				if (statement.getSubject() instanceof org.eclipse.rdf4j.model.IRI) {
					subject = model.createResource(statement.getSubject().stringValue());
				} else if (statement.getSubject() instanceof org.eclipse.rdf4j.model.BNode) {
					subject = model.createResource(new AnonId( ((org.eclipse.rdf4j.model.BNode)statement.getSubject()).getID()) );
				} else {
					throw new IllegalArgumentException("Unsupported RDF4J subject type: " + statement.getSubject().getClass().getName());
				}
				
				// convert predicate
				org.apache.jena.rdf.model.Property predicate = model.createProperty(statement.getPredicate().stringValue());
				
				// convert object
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
					object = model.createResource(new AnonId( ((org.eclipse.rdf4j.model.BNode)statement.getObject()).getID()) );
				} else {
					throw new IllegalArgumentException("Unsupported RDF4J object type: " + statement.getObject().getClass().getName());
				}

				model.add(subject, predicate, object);
			}							
		};

		// create an in-memory RDF4J repository
		Repository outputRepository = new SailRepository(new MemoryStore());
		// convert Excel
		Xls2RdfConverter converter = converterFactory.newConverter(outputRepository, null);
		converter.processInputStream(inputStream);
		// export to Jena model
		outputRepository.getConnection().export(jenaModelWriter);
		
	}

	/**
	 * Serialize the RDF Model in the given Lang in the response
	 * @param m
	 * @param format
	 * @param response
	 * @throws IOException
	 */
	public static void serialize(Model m, Lang format, String filename, HttpServletResponse response)
	throws IOException {
		log.debug("Setting response content type to "+format.getContentType().getContentTypeStr());
		response.setContentType(format.getContentType().getContentTypeStr());
		response.setHeader("Content-Disposition", "inline; filename=\""+filename+"."+format.getFileExtensions().get(0)+"\"");
		RDFDataMgr.write(response.getOutputStream(), m, format) ;		
	}
	
	public static void writeJson(Object o, PrintWriter out) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(out, o);
	}

}
