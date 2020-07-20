package fr.sparna.rdf.shacl.printer.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ValidationReportRawDatatableWriter implements ValidationReportWriter {


	@Override
	public void write(ValidationReport results, OutputStream out, Locale locale) throws IOException {		
		List<PrintableSHResult> displayResults = results.getResults().stream().map(r -> new PrintableSHResult(r)).collect(Collectors.toList());
		
		try {
			Template template = FreemarkerConfiguration.getConfiguration().getTemplate("DatatableView.ftlh");
			Map<String, Object> model = new HashMap<>();
			model.put("data", displayResults);
			model.put("contentTemplate", this.getClass().getSimpleName()+".ftlh");
			template.process(model, new OutputStreamWriter(out));
		} catch (TemplateException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	@Override
	public ValidationReportOutputFormat getFormat() {
		return ValidationReportOutputFormat.HTML_RAW;
	}

	public static void main(String... args) throws Exception {
		File resultFile = new File(args[0]);
		File output = new File(ValidationReportRawDatatableWriter.class.getSimpleName()+".html");
		ValidationReportRawDatatableWriter me = new ValidationReportRawDatatableWriter();
		Model m = ModelFactory.createDefaultModel();
		m.read(new FileInputStream(resultFile), RDF.uri, RDFLanguages.filenameToLang(resultFile.getName()).getName());
		me.write(
				new ValidationReport(m, m),
				new FileOutputStream(output),
				Locale.forLanguageTag("fr")
		);
	}

}
