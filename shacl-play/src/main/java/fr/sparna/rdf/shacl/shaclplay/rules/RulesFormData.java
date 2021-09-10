package fr.sparna.rdf.shacl.shaclplay.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Service;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;

@Service
public class RulesFormData {

	public static final String KEY = RulesFormData.class.getSimpleName();
	protected String errorMessage;
	protected ShapesCatalog catalog;
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

	public static String getKey() {
		return KEY;
	}
}
