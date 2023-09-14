package fr.sparna.rdf.shacl.owl2shacl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
		
		CLOSED("https://raw.githubusercontent.com/sparna-git/owl2shacl/main/owl2sh-closed.ttl"),
		SEMICLOSED("https://raw.githubusercontent.com/sparna-git/owl2shacl/main/owl2sh-semi-closed.ttl"),
		OPEN("https://raw.githubusercontent.com/sparna-git/owl2shacl/main/owl2sh-open.ttl");
		
		private URL rulesUrl;

		private Owl2ShaclStyle(String url) {
			try {
				this.rulesUrl = new URL(url);
			} catch (MalformedURLException ignore) {
				ignore.printStackTrace();
			}
		}

		public URL getRulesUrl() {
			return rulesUrl;
		}
		
	}
	
	public Model convert(Model input) {
		return this.convert(input, Owl2ShaclStyle.OPEN);
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
		
		try {
			shapesModel.read(
					style.getRulesUrl().openStream(),
					null,
					RDFLanguages.filenameToLang(style.getRulesUrl().toString(), Lang.RDFXML).getName()
			);
		} catch (IOException e) {
			e.printStackTrace();
		}

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
