package fr.sparna.rdf.shacl.printer.report;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerConfiguration {

	private static Configuration configuration;
	
	public static Configuration getConfiguration() {
		if(configuration == null) {
			// Create your Configuration instance, and specify if up to what FreeMarker
			// version (here 2.3.29) do you want to apply the fixes that are not 100%
			// backward-compatible. See the Configuration JavaDoc for details.
			configuration = new Configuration(Configuration.VERSION_2_3_29);

			configuration.setTemplateLoader(new ClassTemplateLoader(FreemarkerConfiguration.class, "/templates"));

			// From here we will set the settings recommended for new projects. These
			// aren't the defaults for backward compatibilty.

			// Set the preferred charset template files are stored in. UTF-8 is
			// a good choice in most applications:
			configuration.setDefaultEncoding("UTF-8");

			// Sets how errors will appear.
			// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

			// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
			configuration.setLogTemplateExceptions(false);

			// Wrap unchecked exceptions thrown during template processing into TemplateException-s:
			configuration.setWrapUncheckedExceptions(true);

			// Do not fall back to higher scopes when reading a null loop variable:
			configuration.setFallbackOnNullLoopVariable(false);
		}
		
		return configuration;
	}
	
}
