package fr.sparna.rdf.shacl.shaclplay.rules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShape;
import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShapeRules;

public class BoxShapeRulesReader_code {
	
	// Inicio de la recoleccion de informacion.
	public BoxShapeRules readRulesProperties(Resource nodeShape) {
		BoxShapeRules rulesProperties = new BoxShapeRules();
		rulesProperties.setShSparqlRuleName(nodeShape.getModel().shortForm(nodeShape.asResource().getLocalName()));
		rulesProperties.setRdfsLabel(this.readShRulesLabel(nodeShape));
		rulesProperties.setRdfsComments(this.readShRulesComment(nodeShape));
		rulesProperties.setShPrefixes(this.readShRulesPrefixes(nodeShape));
		rulesProperties.setShOrder(this.readShRulesOrder(nodeShape));
		rulesProperties.setShConstruct(this.readShRulesConstruct(nodeShape));
		
		return rulesProperties;
		 
	}
	
	public String readShRulesPrefixes(Resource nodeShapeRules) {
		String value = null;
		if(nodeShapeRules.hasProperty(SH.prefixes)) {
			value = nodeShapeRules.getProperty(SH.prefixes).getResource().toString();
		}
		return value;
	}
	
	public String readShRulesLabel(Resource nodeShapeRules) {
		String value = null;
		if(nodeShapeRules.hasProperty(RDFS.label)) {
			
			value = nodeShapeRules.getProperty(RDFS.label).getModel().shortForm(nodeShapeRules.getProperty(RDFS.label).getString()); 
			
			//nodeShapeRules.getProperty(RDFS.label).getString();
		}
		return value;
	}
	
	public String readShRulesComment(Resource nodeShapeRules) {
		String value = null;
		if(nodeShapeRules.hasProperty(RDFS.comment)) {
			value = nodeShapeRules.getProperty(RDFS.comment).getString();
		}
		return value;
	}
	
	public String readShRulesOrder(Resource nodeShapeRules) {
		String value = null;
		if(nodeShapeRules.hasProperty(SH.order)) {
			value = nodeShapeRules.getProperty(SH.order).getString();
		}
		return value;
	}
	
	public String readShRulesConstruct(Resource nodeShapeRules) {
		String value = null;
		if(nodeShapeRules.hasProperty(SH.construct)) {
			value = nodeShapeRules.getProperty(SH.construct).getString();
		}
		return value;
	}

}
