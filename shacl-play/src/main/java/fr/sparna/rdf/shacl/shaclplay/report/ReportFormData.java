package fr.sparna.rdf.shacl.shaclplay.report;

import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;

public class ReportFormData {

	public static final String KEY = ReportFormData.class.getSimpleName();
	
	protected String errorMessage;
	
	//protected RulesCatalog catalog;
	protected ShapesCatalog catalog;

	/**
	 * Creates a new JsonLdFormData instance suitable for displaying the given error message.
	 * @param message
	 * @return
	 */
	public static ReportFormData error(String message) {
		ReportFormData data = new ReportFormData();
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
