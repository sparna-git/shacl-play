package fr.sparna.rdf.shacl.app.excel;

import java.io.FileOutputStream;
import java.security.InvalidParameterException;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.excel.DataParser;
import fr.sparna.rdf.shacl.excel.model.Sheet;
import fr.sparna.rdf.shacl.excel.writeXLS.WriteXLS;


public class Excel implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsExcel a = (ArgumentsExcel)args;
		
		// read input SHACL template file or URL
		Model shaclTemplateGraph = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModel(shaclTemplateGraph, a.getTemplate());
		
		// read input data file
		Model dataGraph = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModel(dataGraph, a.getInput());		
		
		// read dataset template and set of data
		String uniqueLang = DataParser.guessTemplateLanguage(shaclTemplateGraph);
		if(uniqueLang == null && a.getLanguage() == null) {
			throw new InvalidParameterException("No unique language could be guessed from SHACL template. Please specify explicitly a language to use.");
		}
		String lang = (a.getLanguage() != null)?a.getLanguage():uniqueLang;
		DataParser parser = new DataParser(lang);
		List<Sheet> sheets = parser.parseData(shaclTemplateGraph,dataGraph);
		
		// Generate excel
		WriteXLS xlsWriter = new WriteXLS();
		XSSFWorkbook workbook = xlsWriter.generateWorkbook(dataGraph.getNsPrefixMap(),sheets);
		
		// write in file 
		if (!a.getOutput().exists()) {
			a.getOutput().createNewFile();
		}
		
		//Write the workbook in file system
		try(FileOutputStream fileOut = new FileOutputStream(a.getOutput())) {
			workbook.write(fileOut);
	        fileOut.flush();
	        workbook.close();
			log.info("File "+a.getOutput().getPath()+" was written successfully on disk.");
		}
		
	}
	
}
