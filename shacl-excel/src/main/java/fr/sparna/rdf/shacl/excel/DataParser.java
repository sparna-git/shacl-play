package fr.sparna.rdf.shacl.excel; 

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.excel.model.MainResource;
import fr.sparna.rdf.shacl.excel.model.NodeShape;
import fr.sparna.rdf.shacl.excel.model.PropertyShape;
import fr.sparna.rdf.shacl.excel.model.ShapesGraph;
import fr.sparna.rdf.shacl.excel.model.Sheet;


public class DataParser {

	private static Logger log = LoggerFactory.getLogger(DataParser.class.getName());
	
	protected String lang;
	
	public DataParser(String lang) {
		super();
		this.lang = lang;
	}
	
	public static String guessTemplateLanguage(Model shaclGraphTemplate) {
		// if we find that a single language is used on sh:name and sh:description on property shapes, return it
		Set<String> languages = new HashSet<String>();
		
		ShapesGraph template = new ShapesGraph(shaclGraphTemplate);
		List<NodeShape> nodeShapes = template.getNodeShapes();
		for (NodeShape nodeShape : nodeShapes) {
			List<PropertyShape> pShapes = nodeShape.getPropertyShapes();
			for (PropertyShape pShape : pShapes) {
				languages.addAll(pShape.getNameAndDescriptionLanguages());
			}
		}
		
		if(languages.size() == 1) {
			String lang = languages.iterator().next();
			log.info("Successfully determined template language : "+lang);
			return lang;
		}
		
		// otherwise return null
		return null;
	}

	public List<Sheet> parseData(Model shaclGraphTemplate, Model dataGraph) throws IOException, InvalidFormatException {

		// Wrap template into a data structure that will allow us to read node shapes
		ShapesGraph template = new ShapesGraph(shaclGraphTemplate);

		/*
		 * Read all sheets content based on the node shapes, from the data graph
		 */
		SheetReader msReader = new SheetReader();
		List<Sheet> sheets = msReader.read(template.getNodeShapes(), dataGraph, this.lang);
		
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