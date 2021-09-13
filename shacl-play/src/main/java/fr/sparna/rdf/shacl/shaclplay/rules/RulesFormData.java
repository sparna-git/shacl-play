package fr.sparna.rdf.shacl.shaclplay.rules;

import org.springframework.stereotype.Service;

import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;

@Service
public class RulesFormData {

	public static final String KEY = RulesFormData.class.getSimpleName();
	protected String errorMessage;
	protected RulesCatalog catalog;
	protected String selectedShapesKey;
	protected String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Creates a new DocFormData instance suitable for displaying the given error
	 * message.
	 * 
	 * @param message
	 * @return
	 */

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public RulesCatalog getCatalog() {
		return catalog;
	}

	public void setCatalog(RulesCatalog catalog) {
		this.catalog = catalog;
	}

	public String getSelectedShapesKey() {
		return selectedShapesKey;
	}

	public void setSelectedShapesKey(String selectedShapesKey) {
		this.selectedShapesKey = selectedShapesKey;
	}

	public static String getKey() {
		return KEY;
	}
}
