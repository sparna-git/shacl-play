package fr.sparna.rdf.shacl.sparqlgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlBatchRunner {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected File outputFile;
	protected boolean generateDebugFiles = true;
	
	public SparqlBatchRunner(File outputFile) {
		super();
		this.outputFile = outputFile;
		this.generateDebugFiles = true;
	}
	
	public void generateSparqlResult(File fDir, String Conn, File shaclFile) throws Exception {
		
		// Create output dir if not exists		
		if(!outputFile.getParentFile().exists()) {
			this.outputFile.getParentFile().mkdirs();
		}
		
		// Read prefix
		Map<String, String> pfm = new HashMap<String, String>();
		if(shaclFile != null) {
			Model shaclGraph = ModelFactory.createDefaultModel();
			shaclGraph.read(new FileInputStream(shaclFile), RDF.uri, FileUtils.guessLang(shaclFile.getName(), "Turtle"));
			pfm = shaclGraph.getNsPrefixMap();
		}
		
		//Result in the Model
		Model modelGeneral = ModelFactory.createDefaultModel();
		for (File sFile : fDir.listFiles()) {
			
			log.debug("Query File : "+sFile.getName()+" "+"Start: "+LocalTime.now());
			
			// Read File
			String qData = new String(Files.readAllBytes(sFile.toPath()));
			
			//Create Query Object
			Query queryFile = QueryFactory.create(qData);
			QueryExecution qExecution = QueryExecutionFactory.sparqlService(Conn, queryFile);
			
			try {
				Model result = qExecution.execConstruct();
				if(!result.isEmpty()) {
					
					modelGeneral.add(result);
					
					int extension = sFile.getName().lastIndexOf('.');
					String nameFile = sFile.getName().substring(0, extension);
					
					//Write result for each query execute
					File qResult = new File(this.outputFile.getParentFile(),nameFile+"_result.ttl");
					RDFDataMgr.write(
							new FileOutputStream(qResult),
							fixPrefixes(result,pfm), 
							Lang.TURTLE);		
					
					log.debug("Execution successful .... "+" "+"End: "+LocalTime.now());
					
					result.close();
				} else {
					log.debug("0 Result ....."+sFile.getName()+" "+"End: "+LocalTime.now());
					
				}
				qExecution.close();
				
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
		
		RDFDataMgr.write(new FileOutputStream(this.outputFile), 
				fixPrefixes(modelGeneral, pfm), 
				Lang.TURTLE) ;

		modelGeneral.close();
	}
	
	public Model fixPrefixes(Model model, Map<String,String> pfm) {
		
		if(pfm != null) {
			for(Map.Entry<String,String> pf:pfm.entrySet()) {
				model.setNsPrefix(pf.getKey(), pf.getValue());					
			}
		}		

		//Prefix fixed
		model.setNsPrefix("xsd","http://www.w3.org/2001/XMLSchema#");

		return model;
	}
		
}
