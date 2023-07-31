package fr.sparna.rdf.shacl.excel; 

import java.io.IOException;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import fr.sparna.rdf.shacl.excel.model.InputDataTemplateReader;
import fr.sparna.rdf.shacl.excel.model.ModelStructure;
import fr.sparna.rdf.shacl.excel.model.ModelStructureReader;
import fr.sparna.rdf.shacl.excel.model.NodeShapeTemplate;


public class Generator {

	public List<ModelStructure> readDocument(Model shaclGraphTemplate, Model dataGraph) throws IOException, InvalidFormatException {

		
		
		// Read Template
		InputDataTemplateReader InputTemplate = new InputDataTemplateReader();
		List<NodeShapeTemplate> source_data_tmp = InputTemplate.readTemplateModel(shaclGraphTemplate);
		
		/*
		 * 
		 * Read all NodeShape and find the dataGraph
		 * 
		 * 
		 */
		ModelStructureReader msReader = new ModelStructureReader();
		List<ModelStructure> data_model = msReader.read(source_data_tmp, dataGraph);
        
		return data_model;
	}
	
	
}