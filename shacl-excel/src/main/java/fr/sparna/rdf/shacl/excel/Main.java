package fr.sparna.rdf.shacl.excel;

import java.io.File;
import java.io.FileInputStream;
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

import fr.sparna.rdf.shacl.excel.model.Sheet;
import fr.sparna.rdf.shacl.excel.writeXLS.WriteXLS;

public class Main {

	public static void main(String[] args) throws Exception {
		
		// premier paramètre : template SHACL
		String templateParam = args[0];
		// deuxième paramètre : fichier de data
		String dataParam = args[1];
		
		// troisieme parametre : fichier d'output
		String outputPath = args[2];
		
		
		Model shaclTemplateGraph = ModelFactory.createDefaultModel();
		if(templateParam.startsWith("http")) {
			shaclTemplateGraph.read(templateParam, RDF.uri, FileUtils.guessLang(templateParam, "Turtle"));
		} else {
			shaclTemplateGraph.read(new FileInputStream(templateParam), RDF.uri, FileUtils.guessLang(templateParam, "RDF/XML"));
		}		
		
		Model dataGraph = ModelFactory.createDefaultModel();
		if(dataParam.startsWith("http")) {
			dataGraph.read(dataParam, RDF.uri, FileUtils.guessLang(dataParam, "Turtle"));
		} else {
			dataGraph.read(new FileInputStream(dataParam), RDF.uri, FileUtils.guessLang(dataParam, "RDF/XML"));
		}
		
		// read dataset Template and set of data
		DataParser parser = new DataParser("en");
		List<Sheet> output_data = parser.parseData(shaclTemplateGraph,dataGraph);
		
		// Write excel
		WriteXLS write_in_excel = new WriteXLS();
		XSSFWorkbook workbook = write_in_excel.generateWorkbook(dataGraph.getNsPrefixMap(),output_data);
		
		
		// write in file 
		write_file(workbook, new File(outputPath));
		
		
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