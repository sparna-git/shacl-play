package fr.sparna.rdf.shacl.owl2shacl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.vocabulary.SH;

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
		
		return postProcessLists(results, SH.ignoredProperties);
	}
	
	/**
	 * Collects repeated values of a list-valued property into a single RDF list.
	 * 
	 * A SHACL rule cannot CONSTRUCT an RDF list of unknown length, so the closed and
	 * semi-closed rulesets assert sh:ignoredProperties once per property and rely on the caller
	 * to gather them - owl2sh-closed.ttl says as much in a comment. But SHACL requires the value
	 * of sh:ignoredProperties to be a SHACL list (Sec. 4.8.1), so a caller that skips this step
	 * produces shapes no processor honours: the ignored properties are disregarded, and since
	 * rdf:type is the property these rules ignore most often, every sh:closed shape then reports
	 * a violation for every instance of itself.
	 * 
	 * ConvertController already does this at its own call site, which is why the hosted converter
	 * produces correct shapes where this class does not. Doing it here makes every caller - the
	 * library and the CLI included - agree with it.
	 * 
	 * Idempotent: a value that is already a list is left alone.
	 */
	private static Model postProcessLists(Model model, Property property) {
		for (Resource subject : model.listResourcesWithProperty(property).toList()) {
			List<Statement> statements = model.listStatements(subject, property, (RDFNode) null).toList();
			
			List<RDFNode> listContent = new ArrayList<>();
			for (Statement statement : statements) {
				if (!statement.getObject().canAs(RDFList.class)) {
					listContent.add(statement.getObject());
				}
			}
			
			if (!listContent.isEmpty()) {
				model.remove(statements);
				model.add(subject, property, model.createList(listContent.toArray(new RDFNode[] {})));
			}
		}
		return model;
	}
	
}
