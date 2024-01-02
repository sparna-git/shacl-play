package fr.sparna.rdf.shacl.shaclplay.generate;

public class GenerateFormData {

	public static final String KEY = GenerateFormData.class.getSimpleName();
	
	protected String errorMessage;

	/**
	 * Creates a new GenerateFormData instance suitable for displaying the given error message.
	 * @param message
	 * @return
	 */
	public static GenerateFormData error(String message) {
		GenerateFormData data = new GenerateFormData();
		data.setErrorMessage(message);
		return data;
	}	
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
