package fr.sparna.rdf.shacl.shaclplay.doc;

import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;

public class DocFormData {

	public static final String KEY = DocFormData.class.getSimpleName();
	
	protected String errorMessage;
	
	protected ShapesCatalog catalog;
	
	protected String selectedShapesKey;

	/**
	 * Creates a new DocFormData instance suitable for displaying the given error message.
	 * @param message
	 * @return
	 */
	public static DocFormData error(String message) {
		DocFormData data = new DocFormData();
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
