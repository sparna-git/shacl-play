package fr.sparna.rdf.shacl.owl2shacl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;
import org.topbraid.shacl.vocabulary.SH;

/**
 * The same ontology must convert to the same graph every time.
 *
 * <p>{@link Owl2ShaclListOrderTest} checks the gathering step in isolation. This drives the
 * whole of {@link Owl2Shacl#convert(Model, Model)} - rule execution included - because that is
 * the level at which the defect was visible and the level at which consumers experience it: a
 * conversion that is not reproducible cannot be checksummed, diffed or pinned, and every
 * regeneration produces spurious churn that hides real changes.
 *
 * <p>The fixture asserts one {@code sh:or} member at a time, which is the contract every
 * list-valued SHACL constraint relies on - a SHACL rule cannot CONSTRUCT an RDF list of unknown
 * length - and creates its members as blank nodes, which is what made the gathering order
 * follow the hash of a freshly generated random label.
 */
public class Owl2ShaclIntegrationTest {

	private static final String EX = "http://example.org/";
	private static final int RUNS = 10;

	private static Model load(String resource) {
		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = Owl2ShaclIntegrationTest.class.getClassLoader()
				.getResourceAsStream(resource)) {
			Assert.assertNotNull("missing test resource " + resource, in);
			RDFDataMgr.read(model, in, Lang.TURTLE);
		} catch (java.io.IOException e) {
			throw new IllegalStateException("cannot read " + resource, e);
		}
		return model;
	}

	private static Model convertOnce() {
		// Fresh models per run: reusing them would let one run's blank node identities leak
		// into the next and mask exactly the effect under test.
		return new Owl2Shacl().convert(
				load("Owl2ShaclListOrderData.ttl"),
				load("Owl2ShaclListOrderRules.ttl"));
	}

	/** The {@code sh:path} local names of a gathered {@code sh:or}, in list order. */
	private static List<String> alternativeOrder(Model model) {
		Resource shape = model.getResource(EX + "Shape");
		Assert.assertTrue("the conversion produced no sh:or for ex:Shape",
				model.contains(shape, SH.or));

		RDFNode head = model.getProperty(shape, SH.or).getObject();
		Assert.assertTrue(
				"sh:or must be an RDF list - a bare value is ill-formed (SHACL Sec. 4.6.3)",
				head.canAs(RDFList.class));

		List<String> order = new ArrayList<>();
		for (RDFNode member : head.as(RDFList.class).asJavaList()) {
			order.add(member.asResource().getPropertyResourceValue(SH.property)
					.getPropertyResourceValue(SH.path).getLocalName());
		}
		return order;
	}

	@Test
	public void theRulesReallyDoAssertOneMemberAtATime() {
		// Guards the fixture itself. If the rule ever produced a list directly, every other
		// test here would pass while testing nothing.
		Model raw = ModelFactory.createDefaultModel();
		raw.add(org.topbraid.shacl.rules.RuleUtil.executeRules(
				load("Owl2ShaclListOrderData.ttl"),
				load("Owl2ShaclListOrderRules.ttl"),
				null, null));

		Resource shape = raw.getResource(EX + "Shape");
		Assert.assertEquals("the fixture must assert one sh:or value per alternative",
				7, raw.listStatements(shape, SH.or, (RDFNode) null).toList().size());
	}

	@Test
	public void conversionProducesTheSameListOrderEveryRun() {
		List<String> expected = null;
		for (int run = 0; run < RUNS; run++) {
			List<String> order = alternativeOrder(convertOnce());
			Assert.assertEquals("the conversion dropped or duplicated a member", 7, order.size());
			if (expected == null) {
				expected = order;
			} else {
				Assert.assertEquals(
						"run " + run + " ordered the sh:or members differently; the same "
								+ "ontology must convert to the same graph every time",
						expected, order);
			}
		}
	}

	@Test
	public void conversionProducesAnIsomorphicGraphEveryRun() {
		// The strongest statement, and the one that failed before: two runs differing only in
		// list order are NOT isomorphic, because an RDF list is an rdf:first/rdf:rest chain and
		// a different order is a different set of triples. No RDF canonicalisation can absorb
		// that, which is why it has to be fixed here.
		Model reference = convertOnce();
		for (int run = 1; run < RUNS; run++) {
			Model other = convertOnce();
			Assert.assertTrue(
					"run " + run + " produced a graph that is not isomorphic to run 0",
					reference.isIsomorphicWith(other));
		}
	}

	@Test
	public void conversionProducesTheSameListSequenceEveryRun() {
		// A rendering of the *sequence*, not of the triple set. Conflating blank node labels
		// and sorting - the obvious way to make output comparable across runs - would erase
		// exactly what is under test: an RDF list's cells are blank nodes, so permuting the
		// members permutes the cells onto themselves and the sorted triple set is unchanged.
		String reference = null;
		for (int run = 0; run < RUNS; run++) {
			String rendering = String.join(">", alternativeOrder(convertOnce()));
			if (reference == null) {
				reference = rendering;
			} else {
				Assert.assertEquals("run " + run + " produced a different sequence",
						reference, rendering);
			}
		}
	}

	@Test
	public void ignoredPropertiesAreGatheredAndOrderedToo() {
		Model converted = convertOnce();
		Resource closed = converted.getResource(EX + "Closed");
		RDFNode head = converted.getProperty(closed, SH.ignoredProperties).getObject();
		Assert.assertTrue(
				"sh:ignoredProperties must be a list (SHACL Sec. 4.8.1)",
				head.canAs(RDFList.class));

		List<String> order = new ArrayList<>();
		for (RDFNode member : head.as(RDFList.class).asJavaList()) {
			order.add(member.asResource().getLocalName());
		}
		Assert.assertEquals(Arrays.asList("alpha", "mike", "zulu"), order);
	}

	/**
	 * Proof that a label-conflating rendering would not have caught this, kept as a test so the
	 * temptation is documented rather than rediscovered. Reversing the gathered list leaves the
	 * sorted, label-erased triple set identical, while the graphs are genuinely different.
	 */
	@Test
	public void aLabelConflatingRenderingWouldNotDetectAReordering() {
		Model converted = convertOnce();
		Model reversed = ModelFactory.createDefaultModel().add(converted);
		Resource shape = reversed.getResource(EX + "Shape");
		RDFList original = reversed.getProperty(shape, SH.or).getObject().as(RDFList.class);
		List<RDFNode> members = new ArrayList<>(original.asJavaList());
		java.util.Collections.reverse(members);
		reversed.remove(reversed.listStatements(shape, SH.or, (RDFNode) null));
		original.removeList();
		reversed.add(shape, SH.or, reversed.createList(members.toArray(new RDFNode[] {})));

		Assert.assertNotEquals("the fixture must have more than one member to reverse",
				alternativeOrder(converted), alternativeOrder(reversed));
		Assert.assertFalse("reversing a list yields a different graph",
				converted.isIsomorphicWith(reversed));
		Assert.assertEquals(
				"a rendering that erases blank node labels and sorts cannot see list order - "
						+ "which is why the reproducibility tests here compare the sequence",
				renderWithLabelsErased(converted), renderWithLabelsErased(reversed));
	}

	/**
	 * Deliberately blind to list order; see
	 * {@link #aLabelConflatingRenderingWouldNotDetectAReordering()}. Not used to assert
	 * reproducibility.
	 */
	private static String renderWithLabelsErased(Model model) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		RDFDataMgr.write(out, model, Lang.NTRIPLES);
		String[] lines = new String(out.toByteArray(), StandardCharsets.UTF_8).split("\n");
		List<String> normalised = new ArrayList<>();
		for (String line : lines) {
			if (!line.trim().isEmpty()) {
				normalised.add(line.replaceAll("_:[A-Za-z0-9]+", "_:b"));
			}
		}
		java.util.Collections.sort(normalised);
		return String.join("\n", normalised);
	}
}
