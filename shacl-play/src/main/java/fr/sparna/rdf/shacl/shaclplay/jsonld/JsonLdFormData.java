package fr.sparna.rdf.shacl.shaclplay.jsonld;

//import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;

public class JsonLdFormData {

	public static final String KEY = JsonLdFormData.class.getSimpleName();
	
	protected String errorMessage;
	
	//protected RulesCatalog catalog;
	protected ShapesCatalog catalog;
	
	protected String selectedShapesKey;

	/**
	 * Creates a new ConvertFormData instance suitable for displaying the given error message.
	 * @param message
	 * @return
	 */
	public static JsonLdFormData error(String message) {
		JsonLdFormData data = new JsonLdFormData();
		data.setErrorMessage(message);
		return data;
	}	
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/*
	public RulesCatalog getCatalog() {
		return catalog;
	}

	public void setCatalog(RulesCatalog catalog) {
		this.catalog = catalog;
	}
	*/

	public String getSelectedShapesKey() {
		return selectedShapesKey;
	}

	public void setSelectedShapesKey(String selectedShapesKey) {
		this.selectedShapesKey = selectedShapesKey;
	}
	
	public ShapesCatalog getCatalog() {
		return catalog;
	}

	public void setCatalog(ShapesCatalog catalog) {
		this.catalog = catalog;
	}

}
