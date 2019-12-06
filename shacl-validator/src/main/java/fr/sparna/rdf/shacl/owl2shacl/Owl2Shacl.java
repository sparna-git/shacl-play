package fr.sparna.rdf.shacl.owl2shacl;

import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.util.FileManager;
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

//		OntDocumentManager mgr = new OntDocumentManager();
//		// set mgr's properties now
//		FileManager.get().addLocatorClassLoader(getClass().getClassLoader());
//		FileManager.get().getLocationMapper().addAltEntry("http://sparna.fr/ontologies/owl2sh", "/fr/sparna/rdf/shacl/rdf2shacl/owl2shacl-common.ttl");
//		
//		mgr.setFileManager(FileManager.get());
//		
//		// now use it
//		OntModelSpec myOntModelSpec = new OntModelSpec( OntModelSpec.OWL_MEM );
//		myOntModelSpec.setDocumentManager( mgr );
//		
//		
//		// read shapes file
//		OntModel shapesModel = ModelFactory.createOntologyModel(myOntModelSpec);	
		
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
