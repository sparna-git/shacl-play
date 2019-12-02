package fr.sparna.rdf.shacl.printer.report;

import java.io.OutputStream;
import java.util.Locale;

public interface ValidationReportWriter {

	public void write(ValidationReport report, OutputStream out, Locale locale);
	
	public ValidationReportOutputFormat getFormat();
}
