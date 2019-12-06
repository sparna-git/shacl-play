package fr.sparna.rdf.shacl.printer.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class ValidationReportRawDatatableWriter implements ValidationReportWriter {


	@Override
	public void write(ValidationReport results, OutputStream out, Locale locale) {		
		List<PrintableSHResult> displayResults = results.getResults().stream().map(r -> new PrintableSHResult(r)).collect(Collectors.toList());
		
		JtwigModel model = JtwigModel.newModel();
		model.with("data", displayResults);
		model.with("contentTemplate", "classpath:/views/"+this.getClass().getSimpleName()+".twig");

		JtwigTemplate template = JtwigTemplate.classpathTemplate("/views/DatatableView.twig");
		template.render(model, out);
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
