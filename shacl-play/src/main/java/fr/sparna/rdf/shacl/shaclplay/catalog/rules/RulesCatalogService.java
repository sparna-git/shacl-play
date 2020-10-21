package fr.sparna.rdf.shacl.shaclplay.catalog.rules;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RulesCatalogService {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public RulesCatalog getRulesCatalog() {
		Model catalogModel = ModelFactory.createDefaultModel();
		catalogModel.read("https://raw.githubusercontent.com/sparna-git/SHACL-Catalog/master/rules-catalog.ttl", null, Lang.TURTLE.getName());
		
		return new RulesCatalogModelFactory().fromModel(catalogModel);
	}
}
