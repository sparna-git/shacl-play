package fr.sparna.rdf.shacl.shaclplay.catalog.shapes;

import java.util.ArrayList;
import java.util.List;

import fr.sparna.rdf.shacl.shaclplay.catalog.Catalog;

public class ShapesCatalog implements Catalog<ShapesCatalogEntry> {

	protected List<ShapesCatalogEntry> entries;

	public ShapesCatalog() {
		this.entries = new ArrayList<ShapesCatalogEntry>();
	}

	public List<ShapesCatalogEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<ShapesCatalogEntry> entries) {
		this.entries = entries;
	}
	
	public ShapesCatalogEntry getCatalogEntryById(String id) {
		return this.entries.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}
	
}
