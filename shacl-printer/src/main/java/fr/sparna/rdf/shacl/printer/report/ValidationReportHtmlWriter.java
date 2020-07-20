package fr.sparna.rdf.shacl.printer.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ValidationReportHtmlWriter implements ValidationReportWriter {

	protected boolean selfContained = true;
	
	public ValidationReportHtmlWriter() {
		super();
	}

	public ValidationReportHtmlWriter(boolean selfContained) {
		super();
		this.selfContained = selfContained;
	}	
	
	@Override
	public void write(ValidationReport results, OutputStream out, Locale locale) throws IOException {		
		PrintableValidationReport printableReport = new PrintableValidationReport(results);
		
		try {
	        if(selfContained) {        	
	        	Template template = FreemarkerConfiguration.getConfiguration().getTemplate("SimpleReportPage.ftlh");
				Map<String, Object> model = new HashMap<>();
				model.put("report", printableReport);
				model.put("contentTemplate", this.getClass().getSimpleName()+".ftlh");
				// pour les traductions
				// model.put("msg", new MessageResolverMethod(messageSource, locale));
				template.process(model, new OutputStreamWriter(out));
	        } else {        	
	        	Template template = FreemarkerConfiguration.getConfiguration().getTemplate(this.getClass().getSimpleName()+".ftlh");
				Map<String, Object> model = new HashMap<>();
				model.put("report", printableReport);
				template.process(model, new OutputStreamWriter(out));
	        }
		} catch (TemplateException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	@Override
	public ValidationReportOutputFormat getFormat() {
		return ValidationReportOutputFormat.HTML;
	}
	
	public static void main(String... args) throws Exception {
		((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.TRACE);
		
		File resultFile = new File(args[0]);
		File output = new File(ValidationReportHtmlWriter.class.getSimpleName()+".html");
		ValidationReportHtmlWriter me = new ValidationReportHtmlWriter();
		Model m = ModelFactory.createDefaultModel();
		m.read(new FileInputStream(resultFile), RDF.uri, RDFLanguages.filenameToLang(resultFile.getName()).getName());
		me.write(
				new ValidationReport(m, m),
				new FileOutputStream(output),
				Locale.forLanguageTag("fr")
		);
	}

}
