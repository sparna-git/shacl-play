package fr.sparna.rdf.shacl.owl2shacl;

import java.io.IOException;
import java.io.InputStream;
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
import org.apache.jena.rdf.model.ResourceFactory;
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
		Model results = RuleUtil.executeRules(
				input,
				rulesModel,
				null,
				new Slf4jProgressMonitor("Owl2Shacl", log)
		);
		// Every SHACL construct that takes an RDF list needs the same treatment, for the same
		// reason: a SHACL rule cannot CONSTRUCT a list of unknown length, so the rules assert
		// one value per member and the gathering happens here. Left as repeated bare values,
		// a processor cannot honour the constraint - sh:ignoredProperties was disregarded
		// entirely, and an sh:or whose value is not a list is ill-formed (SHACL Sec. 4.6.1).
		for (Property listValued : LIST_VALUED_CONSTRAINTS) {
			results = postProcessLists(results, listValued);
		}
		return results;
	}

	/**
	 * Collects repeated values of a list-valued property into a single RDF list.
	 *
	 * A SHACL rule cannot CONSTRUCT an RDF list of unknown length, so the closed and
	 * semi-closed rulesets assert {@code sh:ignoredProperties} once per property and rely on the
	 * caller to gather them - the rules say as much in a comment. But
	 * <a href="https://www.w3.org/TR/shacl/#ClosedConstraintComponent">SHACL Sec. 4.8.1</a>
	 * requires the value of {@code sh:ignoredProperties} to be a SHACL list, so an implementation
	 * that skips this step emits shapes no processor honours: the ignored properties are
	 * disregarded, and because {@code rdf:type} is the property these rules ignore most often,
	 * every {@code sh:closed} shape then reports a violation for every instance of itself.
	 *
	 * Doing this inside {@code convert} rather than at each call site is what makes the library
	 * and the CLI produce the same shapes as the hosted converter, which has always
	 * post-processed at its own call site.
	 *
	 * Idempotent: a value that is already a list is left alone, so calling it twice is harmless.
	 */
	/**
	 * SHACL constraint components whose value is an RDF list, and which the rulesets
	 * therefore assert one member at a time.
	 *
	 * <p>sh:ignoredProperties takes a list of properties (Sec. 4.8.1); sh:or, sh:and and
	 * sh:xone take a list of shapes (Sec. 4.6). A rule cannot build a list of unknown
	 * length, so each is gathered here after rule execution. The gathering is idempotent -
	 * a value that is already a list is left alone - so a ruleset that somehow produced a
	 * list directly is unaffected.
	 */
	private static final Property[] LIST_VALUED_CONSTRAINTS = {
			SH.ignoredProperties,
			SH.or,
			SH.and,
			// TopBraid's SH vocabulary class has no constant for sh:xone, so it is named
			// here. sh:not is deliberately absent from this list: it takes a single shape,
			// not a list, and gathering it would corrupt it.
			ResourceFactory.createProperty(SH.NS + "xone")
	};

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
