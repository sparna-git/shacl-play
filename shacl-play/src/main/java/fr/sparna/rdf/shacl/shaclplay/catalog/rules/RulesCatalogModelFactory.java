package fr.sparna.rdf.shacl.shaclplay.catalog.rules;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntryResourceFactory;

public class RulesCatalogModelFactory {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public RulesCatalog fromModel(Model model) {
		RulesCatalog catalog = new RulesCatalog();
		
		model.listSubjectsWithProperty(RDF.type, DCAT.Dataset).forEachRemaining(r -> {
			log.debug("Parsing Catalog entry "+r.getLocalName());
			RulesCatalogEntry entry = new RulesCatalogEntry();
			
			AbstractCatalogEntryResourceFactory reader = new AbstractCatalogEntryResourceFactory();
			reader.populate(entry, r);
			
			catalog.getEntries().add(entry);
		});
		
		return catalog;
	}
	
}
