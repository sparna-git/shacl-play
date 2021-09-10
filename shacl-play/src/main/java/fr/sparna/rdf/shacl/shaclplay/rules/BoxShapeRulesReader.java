package fr.sparna.rdf.shacl.shaclplay.rules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class BoxShapeRulesReader {
	
	protected List<BoxShape> allBoxes;
	public BoxShapeRulesReader(List<BoxShape> allBoxes) {
		super();
		this.allBoxes = allBoxes;
	}
	
	// Inicio de la recoleccion de informacion.
	public List<BoxShapeRules> readRulesProperties(Resource nodeShape,List<BoxShape> allBoxes, List<Resource> Shape) {
		
		List<BoxShapeRules> boxRulesProperties = new ArrayList<>();		
		for(Resource rRules : Shape) {
			BoxShapeRules rulesProperties = new BoxShapeRules();
			rulesProperties.setShSparqlRuleName(rRules.getModel().shortForm(rRules.getURI()));
			rulesProperties.setRdfsLabel(this.readShRulesLabel(rRules));
			rulesProperties.setRdfsComments(this.readShRulesComment(rRules));
			rulesProperties.setShPrefixes(this.readShRulesPrefixes(rRules));
			rulesProperties.setShOrder(this.readShRulesOrder(rRules));
			rulesProperties.setShConstruct(this.readShRulesConstruct(rRules));
			boxRulesProperties.add(rulesProperties);
		}		
		boxRulesProperties.sort(Comparator.comparing(BoxShapeRules::getShSparqlRuleName));
		return boxRulesProperties;
		 
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
			value = nodeShapeRules.getProperty(RDFS.label).getString();
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
