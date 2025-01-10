package fr.sparna.rdf.shacl.shaclplay.jsonSchema;

import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;

public class JsonSchemaFormData {

	public static final String KEY = JsonSchemaFormData.class.getSimpleName();
	
	protected String errorMessage;
	
	protected ShapesCatalog catalog;

	/**
	 * Creates a new JsonSchemaFormData instance suitable for displaying the given error message.
	 * @param message
	 * @return
	 */
	public static JsonSchemaFormData error(String message) {
		JsonSchemaFormData data = new JsonSchemaFormData();
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
