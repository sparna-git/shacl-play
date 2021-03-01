package fr.sparna.rdf.shacl.shaclplay.draw;

import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;

public class DrawFormData {

	public static final String KEY = DrawFormData.class.getSimpleName();
	
	protected String errorMessage;
	
	protected ShapesCatalog catalog;
	
	protected String selectedShapesKey;

	/**
	 * Creates a new ValidateFormData instance suitable for displaying the given error message.
	 * @param message
	 * @return
	 */
	public static DrawFormData error(String message) {
		DrawFormData data = new DrawFormData();
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
