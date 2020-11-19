package fr.sparna.rdf.shacl.shaclplay.catalog.rules;

import java.util.ArrayList;
import java.util.List;

import fr.sparna.rdf.shacl.shaclplay.catalog.Catalog;

public class RulesCatalog implements Catalog<RulesCatalogEntry> {

	protected List<RulesCatalogEntry> entries;

	public RulesCatalog() {
		this.entries = new ArrayList<RulesCatalogEntry>();
	}

	public List<RulesCatalogEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<RulesCatalogEntry> entries) {
		this.entries = entries;
	}
	
	public RulesCatalogEntry getCatalogEntryById(String id) {
		return this.entries.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}
	
}
