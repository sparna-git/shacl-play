package fr.sparna.rdf.shacl.excel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.excel.PropertyShapeTemplateReader;

public class InputDataTemplateReader {

	public List<NodeShapeTemplate> readTemplateModel(Model shaclGraphTemplate) {
		
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
		List<NodeShapeTemplate> nodeShapeTemplateList = new ArrayList<>();		
		PropertyShapeTemplateReader propertyShapeTemplateReader = new PropertyShapeTemplateReader();
		for (Resource ns : nodeShapes) {
			NodeShapeTemplate nodeShapeTemplate = new NodeShapeTemplate(ns);
			
			if (ns.hasProperty(SH.order)) {
				nodeShapeTemplate.setSHOrder(ns.getProperty(SH.order).getInt());
			}
			
			/** Analyse and record the various kinds of shapes an targets */
			if (ns.hasProperty(SH.targetClass)) {
				nodeShapeTemplate.setSHTargetClass(ns.getProperty(SH.targetClass).getResource());
			}
			
			if (ns.hasProperty(SH.targetSubjectsOf)) {
				nodeShapeTemplate.setSHTargetSubjectsOf(ns.getProperty(SH.targetSubjectsOf).getResource());
			}
			
			if (ns.hasProperty(SH.targetObjectsOf)) {
				nodeShapeTemplate.setSHTargetObjectOf(ns.getProperty(SH.targetObjectsOf).getResource());
			}
			
			
			
			List<PropertyShapeTemplate> propertyShapeTeamplates = new ArrayList<>();
			List<Statement> shPropertyStatements = ns.listProperties(SH.property).toList();
			for (Statement lproperty : shPropertyStatements) {
				propertyShapeTeamplates.add(propertyShapeTemplateReader.read(lproperty.getObject().asResource()));
			}

			List<PropertyShapeTemplate> data_for_columns = propertyShapeTeamplates
					.stream()
					.sorted((a,b) -> {
						if (b.getSh_order().toString() != null) {
							if (a.getSh_order().toString() != null) {
								return a.getSh_order().compareTo(b.getSh_order());
							} else {
								return -1;								
							}
						} else {
							if (a.getSh_order().toString() == null) {
								return 1;
							} else {
								return a.getSh_name().compareTo(b.getSh_name());
							}
						}
					})
					.collect(Collectors.toList());
			nodeShapeTemplate.setShapesTemplate(data_for_columns);
			
			
			
			nodeShapeTemplateList.add(nodeShapeTemplate);
		}
		
		List<NodeShapeTemplate> source_data_tmp = nodeShapeTemplateList.stream().sorted((a,b) -> {
			if (a.getSHOrder() != null) {
				if (b.getSHOrder() != null) {
					return a.getSHOrder().compareTo(b.getSHOrder());
				} else {
					return -1;
				}
			} else {
				if (b.getSHOrder() != null) {
					return 1;
				} else {
					return a.getNodeShape().getURI().compareTo(b.getNodeShape().getURI());
				}
			}
		})
		.collect(Collectors.toList());
		
		
		return source_data_tmp;
	}


}
