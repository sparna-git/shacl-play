package fr.sparna.rdf.shacl.shaclplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


/**
 * Everything that needs to be loaded at an application-wide level
 * 
 * @author Thomas Francart
 */
public class ApplicationData {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected String buildVersion;	
	protected String buildTimestamp;
	
	@Value("${shaclplay.largeInput.threshold:100000}")
	private int largeInputThreshold;
	
	@Value("${shaclplay.baseUrl:#{null}}")
	private String applicationBaseUrl;
	
	@Value("${shaclplay.validation.maxInputSize:500000}")
	private int validationMaxInputSize;
	
	@Value("${shaclplay.validation.maxInputSizeWithInference:50000}")
	private int validationMaxInputSizeWithInference;
	
	public ApplicationData() {
		super();	
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public String getBuildTimestamp() {
		return buildTimestamp;
	}

	public void setBuildTimestamp(String buildTimestamp) {
		this.buildTimestamp = buildTimestamp;
	}
	
	public String getApplicationBaseUrl() {
		return applicationBaseUrl;
	}

	public void setApplicationBaseUrl(String applicationBaseUrl) {
		this.applicationBaseUrl = applicationBaseUrl;
	}
	
	public int getLargeInputThreshold() {
		return largeInputThreshold;
	}

	public void setLargeInputThreshold(int largeInputThreshold) {
		this.largeInputThreshold = largeInputThreshold;
	}

	public int getValidationMaxInputSize() {
		return validationMaxInputSize;
	}

	public void setValidationMaxInputSize(int validationMaxInputSize) {
		this.validationMaxInputSize = validationMaxInputSize;
	}

	public int getValidationMaxInputSizeWithInference() {
		return validationMaxInputSizeWithInference;
	}

	public void setValidationMaxInputSizeWithInference(int validationMaxInputSizeWithInference) {
		this.validationMaxInputSizeWithInference = validationMaxInputSizeWithInference;
	}
	
}
