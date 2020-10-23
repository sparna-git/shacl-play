package fr.sparna.rdf.shacl.shaclplay.validate;

public class ShieldsIoOutput {

	private int schemaVersion = 1;
	private String label;
	private String message;
	private String color;
	private int cacheSeconds = 3600*2;
	

	public int getSchemaVersion() {
		return schemaVersion;
	}
	public void setSchemaVersion(int schemaVersion) {
		this.schemaVersion = schemaVersion;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getCacheSeconds() {
		return cacheSeconds;
	}
	public void setCacheSeconds(int cacheSeconds) {
		this.cacheSeconds = cacheSeconds;
	}
	
}
