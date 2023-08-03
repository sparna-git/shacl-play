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

import fr.sparna.rdf.shacl.excel.model.ModelStructure;
import fr.sparna.rdf.shacl.excel.writeXLS.WriteXLS;

public class Main {

	public static void main(String[] args) throws Exception {
		
		// premier paramètre : template SHACL
		String templateParam = args[0];
		// deuxième paramètre : fichier de data
		String dataParam = args[1];
		
		
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
		Generator model_data_source = new Generator();
		List<ModelStructure> output_data = model_data_source.readDocument(shaclTemplateGraph,dataGraph);
				
		// Get OWL in DataGraph
		List<Resource> ontology = dataGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		
		// Write excel
		WriteXLS write_in_excel = new WriteXLS();
		XSSFWorkbook workbook = write_in_excel.processWorkBook(dataGraph.getNsPrefixMap(),ontology, output_data);
		
		
		// write in file 
		write_file(new File(dataParam), workbook);
		
		
	}	
	
	public static String getBaseName(String fileName) {
		
		 int index = fileName.lastIndexOf('.');
		    if (index == -1) {
		        return fileName;
		    } else {
		        return fileName.substring(0, index);
		    }
	}
	
	public static void write_file(File myoutputfile, XSSFWorkbook workbook) throws IOException {
		
		if (!myoutputfile.exists()) {
			myoutputfile.createNewFile();
		}
		
		try {
			//Write the workbook in file system
			String outputDirectory ="C://Temp//" ;
			String filename = outputDirectory+getBaseName(myoutputfile.getName())+".xlsx";
			FileOutputStream fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
	        fileOut.flush();
	        workbook.close();
			System.out.println("The "+filename+" file was written successfully on disk.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
}