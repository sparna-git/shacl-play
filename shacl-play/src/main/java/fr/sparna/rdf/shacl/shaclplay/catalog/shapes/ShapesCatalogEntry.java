package fr.sparna.rdf.shacl.shaclplay.catalog.shapes;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.List;

import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;

public class ShapesCatalogEntry extends AbstractCatalogEntry {

	protected URL excelDownloadUrl;

	public URL getExcelDownloadUrl() {
		return excelDownloadUrl;
	}
	public void setExcelDownloadUrl(URL excelDownloadUrl) {
		this.excelDownloadUrl = excelDownloadUrl;
	}
	
}
