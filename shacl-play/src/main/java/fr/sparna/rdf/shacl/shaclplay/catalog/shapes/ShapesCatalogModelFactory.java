package fr.sparna.rdf.shacl.shaclplay.catalog.shapes;

import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry.Distribution;
import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntryResourceFactory;

public class ShapesCatalogModelFactory {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public ShapesCatalog fromModel(Model model) {
		ShapesCatalog catalog = new ShapesCatalog();
		
		model.listSubjectsWithProperty(RDF.type, DCAT.Dataset).forEachRemaining(r -> {
			ShapesCatalogEntry entry = new ShapesCatalogEntry();
			
			AbstractCatalogEntryResourceFactory reader = new AbstractCatalogEntryResourceFactory();
			reader.populate(entry, r);
			
			// turtle download URL
			entry.setExcelDownloadUrl(readExcelDownloadUrl(entry));
			
			catalog.getEntries().add(entry);
		});
		
		return catalog;
	}

	
	protected URL readExcelDownloadUrl(AbstractCatalogEntry entry) {
		final String EXCEL_MEDIA_TYPE = "https://www.iana.org/assignments/media-types/application/vnd.ms-excel";
		
		Distribution d = entry.findDistributionByMediaType(EXCEL_MEDIA_TYPE);
		
		if(d == null) {
			return null;
		} else {
			return d.getDownloadUrl();
		}
	}	
}
