package fr.sparna.rdf.shacl.excel; 

import java.io.IOException;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import fr.sparna.rdf.shacl.excel.model.Sheet;
import fr.sparna.rdf.shacl.excel.model.NodeShapeTemplate;


public class DataParser {

	public List<Sheet> parseData(Model shaclGraphTemplate, Model dataGraph) throws IOException, InvalidFormatException {

		// Read Template
		TemplateReader templateReader = new TemplateReader();
		List<NodeShapeTemplate> source_data_tmp = templateReader.readTemplateModel(shaclGraphTemplate);
		
		/*
		 * Read all NodeShape and find the dataGraph
		 */
		SheetReader msReader = new SheetReader();
		List<Sheet> data_model = msReader.read(source_data_tmp, dataGraph);
        
		return data_model;
	}
	
	
}