package fr.sparna.rdf.shacl.printer.report;

import static org.jtwig.translate.configuration.TranslateConfigurationBuilder.translateConfiguration;
import static org.jtwig.translate.message.source.cache.CachedMessageSourceFactory.cachedWith;
import static org.jtwig.translate.message.source.cache.PersistentMessageSourceCache.persistentCache;
import static org.jtwig.translate.message.source.factory.PropertiesMessageSourceFactoryBuilder.propertiesMessageSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.translate.TranslateExtension;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class ValidationReportFullWriter implements ValidationReportWriter {

	protected boolean selfContained = true;
	
	public ValidationReportFullWriter() {
		super();
	}

	public ValidationReportFullWriter(boolean selfContained) {
		super();
		this.selfContained = selfContained;
	}	
	
	@Override
	public void write(ValidationReport results, OutputStream out, Locale locale) {		
		PrintableValidationReport printableReport = new PrintableValidationReport(results);

        EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder
                .configuration()
                    .extensions()
                        .add(new TranslateExtension(translateConfiguration()
                        		.withCurrentLocaleSupplier(() -> locale)
                                .withMessageSourceFactory(cachedWith(
                                        persistentCache(),
                                        propertiesMessageSource()
                                                .withLookupClasspath("translations")
                                                .build()
                                ))
                                .build()))
                    .and()
                .build();
		
		
        if(selfContained) {
			JtwigModel model = JtwigModel.newModel();
			model.with("report", printableReport);
			model.with("contentTemplate", "classpath:/views/"+this.getClass().getSimpleName()+".twig");
	
			JtwigTemplate template = JtwigTemplate.classpathTemplate("/views/SimpleReportPage.twig", configuration);
			template.render(model, out);
        } else {
        	JtwigModel model = JtwigModel.newModel();
        	model.with("report", printableReport);
        	JtwigTemplate template = JtwigTemplate.classpathTemplate("/views/"+this.getClass().getSimpleName()+".twig", configuration);
			template.render(model, out);
        }
	}
	
	@Override
	public ValidationReportOutputFormat getFormat() {
		return ValidationReportOutputFormat.HTML_FULL;
	}
	
	public static void main(String... args) throws Exception {
		((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.TRACE);
		
		File resultFile = new File(args[0]);
		File output = new File(ValidationReportFullWriter.class.getSimpleName()+".html");
		ValidationReportFullWriter me = new ValidationReportFullWriter();
		Model m = ModelFactory.createDefaultModel();
		m.read(new FileInputStream(resultFile), RDF.uri, RDFLanguages.filenameToLang(resultFile.getName()).getName());
		me.write(
				new ValidationReport(m, m),
				new FileOutputStream(output),
				Locale.forLanguageTag("fr")
		);
	}

}
