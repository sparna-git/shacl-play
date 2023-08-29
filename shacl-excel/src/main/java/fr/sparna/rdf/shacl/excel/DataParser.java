package fr.sparna.rdf.shacl.excel; 

import java.io.IOException;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import fr.sparna.rdf.shacl.excel.model.Sheet;
//import fr.sparna.rdf.xls2rdf.postprocess.QBPostProcessor.QB;
import fr.sparna.rdf.shacl.excel.model.MainResource;
import fr.sparna.rdf.shacl.excel.model.NodeShape;


public class DataParser {

	protected String lang;
	
	public DataParser(String lang) {
		super();
		this.lang = lang;
	}

	public List<Sheet> parseData(Model shaclGraphTemplate, Model dataGraph) throws IOException, InvalidFormatException {

		// Read node shapes
		TemplateReader templateReader = new TemplateReader();
		List<NodeShape> nodeShapes = templateReader.readTemplateModel(shaclGraphTemplate);
		
		/*
		 * Read all sheets content based on the node shapes, from the data graph
		 */
		SheetReader msReader = new SheetReader();
		List<Sheet> sheets = msReader.read(nodeShapes, dataGraph, this.lang);
		
		/*
		 * Add main resource in first sheet
		 */
		Resource mainResource = getMainResource(dataGraph);
		// if found, compute our header
		if(mainResource != null) {
			MainResource mr = new MainResource(mainResource);
			sheets.get(0).setHeaderValues(mr.getHeaderValues());
			// and all sheets get that URI as their B1 URI
			sheets.stream().forEach(s -> s.setB1Uri(mainResource.getURI()));
		} else {
			// otherwise each sheets gets its targetClass or NodeShape URI as B1 URI
			sheets.stream().forEach(s -> {
				if(s.getInputNodeShape().getSHTargetClass() != null) {
					s.setB1Uri(s.getInputNodeShape().getSHTargetClass().getURI());
				} else {
					s.setB1Uri(s.getInputNodeShape().getNodeShape().getURI());
				}
			});
		}
		
        
		return sheets;
	}
	
	/**
	 * Retrieves the "main resource" from the graph, that is the resource that will be placed in the header
	 * @param dataGraph
	 * @return
	 */
	private Resource getMainResource(Model dataGraph) {
		// if there is a single owl:Ontology, take it
		List<Resource> ontologies = dataGraph.listSubjectsWithProperty(RDF.type, OWL.Ontology).toList();
		if(ontologies.size() == 1) {
			// rewrap into the dataGraph so that the Resource belongs to it
			return dataGraph.createResource(ontologies.get(0).getURI());
		}
		// otherwise if there is a single dcat:Dataset, take it
		List<Resource> datasets = dataGraph.listSubjectsWithProperty(RDF.type, DCAT.Dataset).toList();
		if(datasets.size() == 1) {
			return dataGraph.createResource(datasets.get(0).getURI());
		}
		// otherwise if there is a single skos:ConceptScheme, take it
		List<Resource> conceptSchemes = dataGraph.listSubjectsWithProperty(RDF.type, SKOS.ConceptScheme).toList();
		if(conceptSchemes.size() == 1) {
			return dataGraph.createResource(conceptSchemes.get(0).getURI());
		}
		// otherwise return null
		return null;
	}
	
	
}