package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.excel.model.NodeShape;

public class TemplateReader {

	public List<NodeShape> readTemplateModel(Model shaclGraphTemplate) {
		
		// read graph for the building the recovery all the head columns
		List<Resource> nodeShapes = shaclGraphTemplate.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		// also read everything object of an sh:node or sh:qualifiedValueShape, that
		// maybe does not have an explicit rdf:type sh:NodeShape
		List<RDFNode> nodesAndQualifedValueShapesValues = shaclGraphTemplate.listStatements(null, SH.node, (RDFNode) null)
						.andThen(shaclGraphTemplate.listStatements(null, SH.qualifiedValueShape, (RDFNode) null)).toList().stream()
						.map(s -> s.getObject()).collect(Collectors.toList());

		// add those to our list
		for (RDFNode n : nodesAndQualifedValueShapesValues) {
			if (n.isResource() && !nodeShapes.contains(n)) {
				nodeShapes.add(n.asResource());
			}
		}
				
		// Header Class 
		List<NodeShape> nodeShapeTemplateList = new ArrayList<>();		
		for (Resource ns : nodeShapes) {
			NodeShape nodeShapeTemplate = new NodeShape(ns);			
			nodeShapeTemplateList.add(nodeShapeTemplate);
		}
		
		
		return nodeShapeTemplateList;
	}


}
