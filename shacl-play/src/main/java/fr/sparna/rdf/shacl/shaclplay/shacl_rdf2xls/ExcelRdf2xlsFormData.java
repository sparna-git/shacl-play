package fr.sparna.rdf.shacl.shaclplay.shacl_rdf2xls;

import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;

public class ExcelRdf2xlsFormData {

	public static final String KEY = ExcelRdf2xlsFormData.class.getSimpleName();
	
	protected String errorMessage;
	
	protected ShapesCatalog catalog;

	/**
	 * Creates a new JsonSchemaFormData instance suitable for displaying the given error message.
	 * @param message
	 * @return
	 */
	public static ExcelRdf2xlsFormData error(String message) {
		ExcelRdf2xlsFormData data = new ExcelRdf2xlsFormData();
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
