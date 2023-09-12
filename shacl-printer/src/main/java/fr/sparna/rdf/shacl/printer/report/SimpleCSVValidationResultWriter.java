package fr.sparna.rdf.shacl.printer.report;

import java.io.OutputStream;
import java.util.Locale;

public class SimpleCSVValidationResultWriter implements ValidationReportWriter {

	
	
	@Override
	public void write(ValidationReport report, OutputStream out, Locale locale) {
		StringBuffer buffer = new StringBuffer();
		
		// CSV header
		buffer.append("Shape,Constraint,Severity,Path,Component,Focus Node,Value,Message"+"\n");
		
		report.getResults().stream().forEach(vr -> {
			// Shape
			buffer.append(RDFRenderer.renderResource(vr.getSourceShape())+",");
			// Severity
			buffer.append(RDFRenderer.renderResource(vr.getSeverity())+",");
			// Property
			buffer.append(RDFRenderer.renderResource(vr.getPath())+",");
			// Component
			buffer.append(RDFRenderer.renderResource(vr.getSourceConstraintComponent())+",");
			// Focus Node
			buffer.append(RDFRenderer.renderRDFNode(vr.getFocusNode())+",");
			// Value
			buffer.append(((vr.getValue() != null)?vr.getValue():"")+",");
			// Message
			buffer.append(vr.getMessage().replaceAll(",", " "));
			// line break
			buffer.append("\n");
		});
		
		try {
			out.write(buffer.toString().getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public ValidationReportOutputFormat getFormat() {
		return ValidationReportOutputFormat.CSV;
	}
	
}
