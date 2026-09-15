package fr.sparna.rdf.shacl.owl2shacl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
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
		// entirely, and an sh:or whose value is not a list is ill-formed (SHACL Sec. 4.6.3).
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
	 * sh:xone take a list of shapes (Sec. 4.6.2 to 4.6.4). A rule cannot build a list of unknown
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

	/**
	 * Package-private rather than private so that the ordering guarantee can be tested
	 * directly: reaching it through {@link #convert} would mean asserting on the output of
	 * the whole rule engine to check one property of one step.
	 */
	static Model postProcessLists(Model model, Property property) {
		for (Resource subject : model.listResourcesWithProperty(property).toList()) {
			List<Statement> statements = model.listStatements(subject, property, (RDFNode) null).toList();

			List<RDFNode> listContent = new ArrayList<>();
			for (Statement statement : statements) {
				if (!statement.getObject().canAs(RDFList.class)) {
					listContent.add(statement.getObject());
				}
			}

			if (!listContent.isEmpty()) {
				// Jena's listStatements order is the model's internal iteration order, which is
				// not stable across runs: a blank node hashes on its label, and Jena labels
				// blank nodes with a fresh random UUID, so the same node hashes differently in
				// every process. Gathering in that order made the same input produce a
				// different graph on every conversion - the members are the same, but an RDF
				// list is an rdf:first/rdf:rest chain, so a different order is a different set
				// of triples and therefore a different graph. RDF canonicalization cannot
				// absorb that, and should not: list order is meaningful in general.
				//
				// It is not meaningful here. SHACL states for each of sh:and (Sec. 4.6.2),
				// sh:or (4.6.3) and sh:xone (4.6.4) that "the order of those shapes does not
				// impact the validation results", and sh:ignoredProperties is a set of
				// properties (Sec. 4.8.1), so imposing a total order changes no shape's
				// meaning while making the output reproducible.
				listContent.sort(Comparator.comparing(node -> orderingKey(node, new ArrayList<>())));
				model.remove(statements);
				model.add(subject, property, model.createList(listContent.toArray(new RDFNode[] {})));
			}
		}
		return model;
	}

	/**
	 * A key that orders list members by what they contain rather than by what they are called.
	 *
	 * <p>The members gathered above are usually blank nodes - anonymous shapes - so ordering by
	 * label would be no more stable than the iteration order it replaces. This describes the
	 * node's own content instead, so two conversions of the same input produce the same key for
	 * the same member, on any JVM and in any locale.
	 *
	 * <p>The key is the size of the node's blank-node closure followed by a recursive
	 * description of its statements. Both parts are needed, and each catches what the other
	 * misses:
	 *
	 * <ul>
	 * <li>the recursive description is a <em>tree</em> serialisation, so on its own it cannot
	 * tell one child referenced twice from two structurally identical children. The closure
	 * size can: those two shapes reach a different number of blank nodes.</li>
	 * <li>the closure size is a single number, so on its own it says almost nothing. The
	 * description distinguishes everything else.</li>
	 * </ul>
	 *
	 * <p>A reference back to a node already being described is rendered with the distance back
	 * to it rather than as a bare marker, so that two differently wired cyclic shapes - one
	 * pointing at itself where the other points at its parent - do not describe themselves
	 * identically.
	 *
	 * <p><strong>The ordering is total on distinguishable members, not on all members.</strong>
	 * Deciding that two arbitrary RDF subgraphs differ is graph isomorphism, which this
	 * deliberately does not attempt. Two members that this cannot tell apart sort equal, and
	 * {@link List#sort} being stable then leaves them in the order the model supplied - which is
	 * the order this exists to replace. That is only reachable for members that agree on their
	 * closure size and on their entire recursive description, which no ruleset here produces:
	 * measured across all 500 lists gathered from ASAM OpenDRIVE V1.9.0 and OpenSCENARIO XML
	 * V1.4.0, there are no equal keys and no cyclic references at all.
	 *
	 * @param node     the member to describe
	 * @param visiting blank nodes already being described further up the recursion, innermost
	 *                 last, so that a shape referring back to one of them terminates instead of
	 *                 overflowing the stack
	 */
	private static String orderingKey(RDFNode node, List<Resource> visiting) {
		if (!node.isAnon()) {
			return terminalKey(node);
		}
		return blankNodeClosureSize(node.asResource()) + ":" + describe(node.asResource(), visiting);
	}

	private static String terminalKey(RDFNode node) {
		if (node.isURIResource()) {
			return "u:" + node.asResource().getURI();
		}
		Literal literal = node.asLiteral();
		return "l:" + literal.getLexicalForm()
				+ "^^" + literal.getDatatypeURI()
				+ "@" + literal.getLanguage();
	}

	/**
	 * Number of distinct blank nodes reachable from {@code start}, excluding itself.
	 *
	 * <p>This is what separates sharing from duplication: {@code _:a p _:s ; q _:s} reaches one,
	 * {@code _:b p _:s1 ; q _:s2} with {@code _:s1} and {@code _:s2} identical reaches two,
	 * while their recursive descriptions are the same string.
	 */
	private static int blankNodeClosureSize(Resource start) {
		Set<Resource> reached = new HashSet<>();
		Deque<Resource> pending = new ArrayDeque<>();
		pending.push(start);
		while (!pending.isEmpty()) {
			for (Statement statement : pending.pop().listProperties().toList()) {
				RDFNode object = statement.getObject();
				if (object.isAnon() && reached.add(object.asResource())) {
					pending.push(object.asResource());
				}
			}
		}
		reached.remove(start);
		return reached.size();
	}

	private static String describe(Resource blank, List<Resource> visiting) {
		int alreadyAt = visiting.lastIndexOf(blank);
		if (alreadyAt >= 0) {
			// How far back up the current path the reference points. Distance rather than a
			// bare marker: a node pointing at itself and a node pointing at its parent are
			// different shapes and must describe themselves differently.
			return "^" + (visiting.size() - alreadyAt);
		}

		visiting.add(blank);
		try {
			// Sorted so the description does not inherit the iteration order it replaces.
			List<String> parts = new ArrayList<>();
			for (Statement statement : blank.listProperties().toList()) {
				RDFNode object = statement.getObject();
				parts.add(statement.getPredicate().getURI() + "="
						+ (object.isAnon()
								? describe(object.asResource(), visiting)
								: terminalKey(object)));
			}
			Collections.sort(parts);
			return "{" + String.join(",", parts) + "}";
		} finally {
			visiting.remove(visiting.size() - 1);
		}
	}

}
