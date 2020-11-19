package fr.sparna.rdf.shacl.shaclplay.catalog;

import java.util.List;

public interface Catalog<Entry> {

	public List<Entry> getEntries();
	
	public Entry getCatalogEntryById(String id);
}
