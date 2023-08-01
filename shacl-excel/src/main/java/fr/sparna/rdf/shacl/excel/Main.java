package fr.sparna.rdf.shacl.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
		Generator write = new Generator();
		List<ModelStructure> output_data = write.readDocument(shaclTemplateGraph,dataGraph);
				
		// Get OWL in DataGraph
		List<Resource> ontology = dataGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		
		// Write excel
		WriteXLS write_xsl = new WriteXLS();
		XSSFWorkbook workbook = write_xsl.processWorkBook(dataGraph.getNsPrefixMap(),ontology, output_data);
		
		
		// write in file 
		File strFile = new File(dataParam);
		String outputDirectory ="C://Temp//" ;
		String filename_xsl = outputDirectory+getBaseName(strFile.getName())+".xlsx";
		
		
		try {
			//Write the workbook in file system
			FileOutputStream fileOut = new FileOutputStream(filename_xsl);
			workbook.write(fileOut);
	        fileOut.flush();
	        workbook.close();
			System.out.println("The "+filename_xsl+" file was written successfully on disk.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
	}	
	
	public static String getBaseName(String fileName) {
		
		 int index = fileName.lastIndexOf('.');
		    if (index == -1) {
		        return fileName;
		    } else {
		        return fileName.substring(0, index);
		    }
	}
	
}