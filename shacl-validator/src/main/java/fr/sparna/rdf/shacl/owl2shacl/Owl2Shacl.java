package fr.sparna.rdf.shacl.owl2shacl;

import java.io.IOException;
import java.io.InputStream;
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

	/**
	 * Converts using one of the predefined conversion styles, whose rules are fetched from
	 * the owl2shacl repository.
	 */
	public Model convert(Model input, Owl2ShaclStyle style) {
		try {
			return this.convert(input, style.getRulesUrl());
		} catch (IOException e) {
			// Previously the read error was printed and conversion continued against an
			// empty rules graph, yielding an empty result that looks like a successful
			// conversion of an ontology with nothing to say. Failing is the honest outcome.
			throw new RuntimeException("Cannot read the conversion rules for style " + style, e);
		}
	}

	/**
	 * Converts using a rules graph read from an arbitrary location: a local file, or a URL
	 * of your choosing. This makes a conversion reproducible - the predefined styles read
	 * from a branch that moves - and allows rule changes to be tried before they are
	 * published.
	 *
	 * @param input     the OWL ontology to convert
	 * @param rulesUrl  location of the conversion rules, in any RDF syntax Jena can read;
	 *                  the syntax is derived from the file extension, defaulting to RDF/XML
	 * @throws IOException if the rules cannot be read. A conversion with no rules would
	 *                     silently produce an empty result, which is worse than failing.
	 */
	public Model convert(Model input, URL rulesUrl) throws IOException {
		OntModel rulesModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		try (java.io.InputStream in = rulesUrl.openStream()) {
			rulesModel.read(
					in,
					null,
					RDFLanguages.filenameToLang(rulesUrl.toString(), Lang.RDFXML).getName()
			);
		} catch (IOException e) {
			throw new IOException("Cannot read the conversion rules from " + rulesUrl, e);
		}
		return this.convert(input, rulesModel);
	}

	/**
	 * Converts using a rules graph the caller has already read.
	 */
	public Model convert(Model input, Model rulesModel) {
		// do the actual rule execution
		return RuleUtil.executeRules(
				input,
				rulesModel,
				null,
				new Slf4jProgressMonitor("Owl2Shacl", log)
		);
	}

}
