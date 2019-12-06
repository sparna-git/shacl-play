package fr.sparna.rdf.shacl.owl2shacl;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.rules.RuleUtil;

import fr.sparna.rdf.shacl.validator.Slf4jProgressMonitor;

public class Owl2Shacl {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public static enum Owl2ShaclStyle {
		
		CLOSED("owl2shacl-closed.ttl"),
		SIMPLE("owl2shacl-simple.ttl");
		
		private String resourcePath;

		private Owl2ShaclStyle(String resourcePath) {
			this.resourcePath = resourcePath;
		}

		public String getResourcePath() {
			return resourcePath;
		}
		
	}
	
	public Model convert(Model input) {
		return this.convert(input, Owl2ShaclStyle.SIMPLE);
	}
	
	public Model convert(Model input, Owl2ShaclStyle style) {
		
		// read shapes file
		OntModel shapesModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);		
		shapesModel.read(
				this.getClass().getResourceAsStream(style.getResourcePath()),
				null,
				RDFLanguages.filenameToLang(style.getResourcePath(), Lang.RDFXML).getName()
		);

		// do the actual rule execution
		Model results = RuleUtil.executeRules(
				input,
				shapesModel,
				null,
				new Slf4jProgressMonitor("Owl2Shacl", log)
		);
		
		return results;
	}
	
	
	
}
