package fr.sparna.rdf.shacl.shaclplay.doc;

import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class DocFormData {

	public static final String KEY = DocFormData.class.getSimpleName();
	
	private String errorMessage;
	
	private ShapesCatalog catalog;
	
	private String selectedShapesKey;

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
