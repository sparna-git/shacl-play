package fr.sparna.rdf.shacl.shaclplay.excel;

import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;

public class ExcelFormData {

	public static final String KEY = ExcelFormData.class.getSimpleName();
	
	protected String errorMessage;
	
	protected ShapesCatalog catalog;

	/**
	 * Creates a new JsonSchemaFormData instance suitable for displaying the given error message.
	 * @param message
	 * @return
	 */
	public static ExcelFormData error(String message) {
		ExcelFormData data = new ExcelFormData();
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

}
