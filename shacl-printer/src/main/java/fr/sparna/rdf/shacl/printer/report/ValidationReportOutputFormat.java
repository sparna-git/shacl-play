package fr.sparna.rdf.shacl.printer.report;

import java.util.Arrays;
import java.util.List;

public enum ValidationReportOutputFormat {

	RDF(Arrays.asList(new String[] { "ttl", "rdf", "jsonld" })),
	CSV(Arrays.asList(new String[] { "csv" })),
	HTML_SUMMARY(Arrays.asList(new String[] { "summary.html" })),
	HTML_FULL(Arrays.asList(new String[] { "full.html" })),
	HTML(Arrays.asList(new String[] { "html" }));
	
	protected List<String> fileExtensions;
	
	private ValidationReportOutputFormat(List<String> fileExtensions) {
		this.fileExtensions = fileExtensions;
	}

	public List<String> getFileExtensions() {
		return fileExtensions;
	}
	
	public static ValidationReportOutputFormat forFileName(String filename) {
		for (ValidationReportOutputFormat f : ValidationReportOutputFormat.values()) {
			for (String extension : f.getFileExtensions()) {
				if(filename.endsWith(extension)) {
					return f;
				}
			}
		}
		
		return null;
	}
	
}
