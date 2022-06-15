package fr.sparna.rdf.shacl.shaclplay.sparql;

import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;

public class SparqlFormData {

	public static final String KEY = SparqlFormData .class.getSimpleName();
	
	protected String errorMessage;
	
	protected ShapesCatalog catalog;
	
	protected String selectedShapesKey;

	/**
	 * Creates a new DocFormData instance suitable for displaying the given error message.
	 * @param message
	 * @return
	 */
	public static SparqlFormData  error(String message) {
		SparqlFormData  data = new SparqlFormData ();
		data.setErrorMessage(message);
		return data;
	}	
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public ShapesCatalog getCatalog() {
		return catalog;
	}

	public void setCatalog(ShapesCatalog catalog) {
		this.catalog = catalog;
	}

	public String getSelectedShapesKey() {
		return selectedShapesKey;
	}

	public void setSelectedShapesKey(String selectedShapesKey) {
		this.selectedShapesKey = selectedShapesKey;
	}

}
