package fr.sparna.rdf.shacl.shaclplay.catalog;

import java.util.ArrayList;
import java.util.List;

public class ShapesCatalog {

	protected List<CatalogEntry> entries;

	public ShapesCatalog() {
		this.entries = new ArrayList<CatalogEntry>();
	}

	public List<CatalogEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<CatalogEntry> entries) {
		this.entries = entries;
	}
	
	public CatalogEntry getCatalogEntryById(String id) {
		return this.entries.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}
	
}
