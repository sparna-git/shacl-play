package fr.sparna.rdf.shacl.app.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.excel.DataParser;
import fr.sparna.rdf.shacl.excel.model.Sheet;
import fr.sparna.rdf.shacl.excel.writeXLS.WriteXLS;


public class Excel implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsExcel a = (ArgumentsExcel)args;
		
		// read input file or URL
		Model shaclTemplateGraph = ModelFactory.createDefaultModel();
		shaclTemplateGraph = readModel(shaclTemplateGraph,a.getInputTemplate());
		
		// read ontology file
		Model dataGraph = ModelFactory.createDefaultModel(); 
		dataGraph = readModel(dataGraph,a.getInputSource());
		
		
		//// read dataset Template and set of data
		DataParser parser = new DataParser("en");
		List<Sheet> output_data = parser.parseData(shaclTemplateGraph,dataGraph);
				
		// Get OWL in DataGraph
		List<Resource> ontology = dataGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		
		// Write excel
		WriteXLS write_in_excel = new WriteXLS();
		XSSFWorkbook workbook = write_in_excel.generateWorkbook(dataGraph.getNsPrefixMap(),output_data);
		
		// write in file 
		write_file(workbook, new File(a.getOutput().toString()));
		
	}
	
	public static Model readModel(Model read_model,String inputFile) throws FileNotFoundException {
		
		if(inputFile.startsWith("http")) {
			read_model.read(inputFile, RDF.uri, FileUtils.guessLang(inputFile, "Turtle"));
		} else {
			read_model.read(new FileInputStream(inputFile), RDF.uri, FileUtils.guessLang(inputFile, "RDF/XML"));
		}
		
		return read_model;
	}
	
	public static void write_file(XSSFWorkbook workbook, File outputFile) throws IOException {
			
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		
		try {
			//Write the workbook in file system
			FileOutputStream fileOut = new FileOutputStream(outputFile);
			workbook.write(fileOut);
	        fileOut.flush();
	        workbook.close();
			System.out.println("The "+outputFile.getPath()+" file was written successfully on disk.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
	
}
