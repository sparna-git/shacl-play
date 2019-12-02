package fr.sparna.rdf.shacl.shaclplay.catalog;

public class CatalogData {

	public static final String KEY = CatalogData.class.getSimpleName();
	
	protected ShapesCatalog catalog;

	public ShapesCatalog getCatalog() {
		return catalog;
	}

	public void setCatalog(ShapesCatalog catalog) {
		this.catalog = catalog;
	}
	
}
