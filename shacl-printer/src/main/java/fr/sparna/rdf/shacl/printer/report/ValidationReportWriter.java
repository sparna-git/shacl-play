package fr.sparna.rdf.shacl.printer.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public interface ValidationReportWriter {

	public void write(ValidationReport report, OutputStream out, Locale locale) throws IOException;
	
	public ValidationReportOutputFormat getFormat();
}
