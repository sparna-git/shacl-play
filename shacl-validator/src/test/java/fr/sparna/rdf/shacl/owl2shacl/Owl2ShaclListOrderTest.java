package fr.sparna.rdf.shacl.owl2shacl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Assert;
import org.junit.Test;
import org.topbraid.shacl.vocabulary.SH;

/**
 * The gathered RDF list must depend on what the rules asserted, not on the order the model
 * happens to iterate in.
 *
 * <p>These exercise {@link Owl2Shacl#postProcessLists} directly.
 * {@link Owl2ShaclIntegrationTest} covers the same guarantee through the whole conversion.
 *
 * <p>Ordering is imposed rather than preserved because there is nothing to preserve: a SHACL
 * rule cannot CONSTRUCT an RDF list of unknown length, so the rulesets assert one member at a
 * time and the members arrive in the model's own iteration order - for blank nodes, the hash of
 * a randomly generated label, which differs between runs of the same program.
 *
 * <p>Imposing an order is sound for every constraint gathered here, because SHACL says so
 * explicitly. Of {@code sh:and} (Sec. 4.6.2), {@code sh:or} (4.6.3) and {@code sh:xone}
 * (4.6.4) it states that "the order of those shapes does not impact the validation results" -
 * results, not merely conformance, so report content is covered too. {@code
 * sh:ignoredProperties} is a set of properties (Sec. 4.8.1). {@code sh:not} takes a single
 * shape rather than a list and is deliberately not gathered.
 */
public class Owl2ShaclListOrderTest {

	private static final String EX = "http://example.org/";

	/**
	 * Builds a shape with one {@code sh:or} member per name, asserted one at a time exactly as
	 * the rulesets do, added in the order given.
	 */
	private static Model shapeWithAlternatives(String... pathLocalNames) {
		Model model = ModelFactory.createDefaultModel();
		Resource shape = model.createResource(EX + "Shape");
		for (String localName : pathLocalNames) {
			Resource alternative = model.createResource();
			Resource propertyShape = model.createResource();
			propertyShape.addProperty(SH.path, model.createResource(EX + localName));
			propertyShape.addProperty(SH.minCount, model.createTypedLiteral(1));
			alternative.addProperty(SH.property, propertyShape);
			shape.addProperty(SH.or, alternative);
		}
		return model;
	}

	/** The {@code sh:path} local names of the gathered list, in list order. */
	private static List<String> gatheredOrder(Model model) {
		Resource shape = model.getResource(EX + "Shape");
		RDFNode head = model.getProperty(shape, SH.or).getObject();
		Assert.assertTrue("sh:or must be gathered into an RDF list", head.canAs(RDFList.class));

		List<String> order = new ArrayList<>();
		for (RDFNode member : head.as(RDFList.class).asJavaList()) {
			Resource propertyShape = member.asResource().getPropertyResourceValue(SH.property);
			order.add(propertyShape.getPropertyResourceValue(SH.path).getLocalName());
		}
		return order;
	}

	@Test
	public void gathersRepeatedValuesIntoOneList() {
		Model model = shapeWithAlternatives("a", "b", "c");
		Owl2Shacl.postProcessLists(model, SH.or);

		Assert.assertEquals(1, model.listStatements(
				model.getResource(EX + "Shape"), SH.or, (RDFNode) null).toList().size());
		Assert.assertEquals(Arrays.asList("a", "b", "c"), gatheredOrder(model));
	}

	@Test
	public void orderDoesNotDependOnTheOrderMembersWereAsserted() {
		// The same seven alternatives, asserted in two different orders. A ruleset has no
		// control over the order its results land in the model, so as far as the conversion is
		// concerned these are the same input and must produce the same list.
		Model first = shapeWithAlternatives(
				"entityAction", "environmentAction", "infrastructureAction",
				"parameterAction", "setMonitorAction", "trafficAction", "variableAction");
		Model second = shapeWithAlternatives(
				"setMonitorAction", "trafficAction", "variableAction", "environmentAction",
				"parameterAction", "infrastructureAction", "entityAction");

		Owl2Shacl.postProcessLists(first, SH.or);
		Owl2Shacl.postProcessLists(second, SH.or);

		Assert.assertEquals(
				"the gathered order must not depend on the order the members were asserted",
				gatheredOrder(first), gatheredOrder(second));
	}

	@Test
	public void orderIsTheSameOnEveryRun() {
		// Repeated because the defect was intermittent: it followed the hash of a random label,
		// so one pair of runs could agree by chance.
		List<String> expected = null;
		for (int run = 0; run < 50; run++) {
			Model model = shapeWithAlternatives("one", "two", "three", "four", "five", "six");
			Owl2Shacl.postProcessLists(model, SH.or);
			List<String> order = gatheredOrder(model);
			if (expected == null) {
				expected = order;
			} else {
				Assert.assertEquals("run " + run + " produced a different order", expected, order);
			}
		}
	}

	@Test
	public void orderFollowsContentRatherThanLabel() {
		// The members are blank nodes, so their labels carry no information; the only stable
		// key is what they contain.
		Model model = shapeWithAlternatives("charlie", "alpha", "bravo");
		Owl2Shacl.postProcessLists(model, SH.or);

		Assert.assertEquals(Arrays.asList("alpha", "bravo", "charlie"), gatheredOrder(model));
	}

	@Test
	public void membersDifferingOnlyDeeperInTheirContentStillOrder() {
		// Two members whose sh:path agrees and which differ only in a nested value: the key has
		// to walk the whole subtree, not just the first level, or these two compare equal and
		// their relative order falls back to iteration order.
		//
		// Built in both insertion orders, because with two members a key that cannot tell them
		// apart still agrees with the expected order half the time.
		for (int run = 0; run < 20; run++) {
			Model model = ModelFactory.createDefaultModel();
			Resource shape = model.createResource(EX + "Shape");
			for (int max : (run % 2 == 0 ? new int[] { 9, 2 } : new int[] { 2, 9 })) {
				Resource alternative = model.createResource();
				Resource propertyShape = model.createResource();
				propertyShape.addProperty(SH.path, model.createResource(EX + "same"));
				propertyShape.addProperty(SH.maxCount, model.createTypedLiteral(max));
				alternative.addProperty(SH.property, propertyShape);
				shape.addProperty(SH.or, alternative);
			}

			Owl2Shacl.postProcessLists(model, SH.or);

			List<String> maxima = new ArrayList<>();
			for (RDFNode member : model.getProperty(shape, SH.or).getObject()
					.as(RDFList.class).asJavaList()) {
				maxima.add(member.asResource().getPropertyResourceValue(SH.property)
						.getProperty(SH.maxCount).getObject().asLiteral().getLexicalForm());
			}
			Assert.assertEquals("run " + run, Arrays.asList("2", "9"), maxima);
		}
	}

	@Test
	public void iriMembersAreOrderedToo() {
		// sh:ignoredProperties takes IRIs rather than anonymous shapes; the same guarantee holds.
		Model model = ModelFactory.createDefaultModel();
		Resource shape = model.createResource(EX + "Shape");
		for (String localName : new String[] { "zulu", "alpha", "mike" }) {
			shape.addProperty(SH.ignoredProperties, model.createResource(EX + localName));
		}

		Owl2Shacl.postProcessLists(model, SH.ignoredProperties);

		List<String> order = new ArrayList<>();
		RDFNode head = model.getProperty(shape, SH.ignoredProperties).getObject();
		for (RDFNode member : head.as(RDFList.class).asJavaList()) {
			order.add(member.asResource().getLocalName());
		}
		Assert.assertEquals(Arrays.asList("alpha", "mike", "zulu"), order);
	}

	@Test
	public void literalMembersAreOrdered() {
		// Both insertion orders, for the same reason as above.
		for (int run = 0; run < 20; run++) {
			Model model = ModelFactory.createDefaultModel();
			Resource shape = model.createResource(EX + "Shape");
			for (String value : (run % 2 == 0 ? new String[] { "b", "a" } : new String[] { "a", "b" })) {
				shape.addProperty(SH.or, ResourceFactory.createPlainLiteral(value));
			}

			Owl2Shacl.postProcessLists(model, SH.or);

			List<RDFNode> members = model.getProperty(shape, SH.or).getObject()
					.as(RDFList.class).asJavaList();
			Assert.assertEquals("run " + run, "a", members.get(0).asLiteral().getLexicalForm());
			Assert.assertEquals("run " + run, "b", members.get(1).asLiteral().getLexicalForm());
		}
	}

	@Test
	public void anAlreadyGatheredListIsLeftAlone() {
		// Idempotence: the method is applied once per list-valued constraint, and a ruleset that
		// somehow produced a list directly must not have it re-wrapped.
		Model model = ModelFactory.createDefaultModel();
		Resource shape = model.createResource(EX + "Shape");
		Resource kept = model.createResource(EX + "kept");
		shape.addProperty(SH.or, model.createList(new RDFNode[] { kept }));

		Owl2Shacl.postProcessLists(model, SH.or);
		Owl2Shacl.postProcessLists(model, SH.or);

		RDFNode head = model.getProperty(shape, SH.or).getObject();
		Assert.assertTrue(head.canAs(RDFList.class));
		Assert.assertEquals(1, head.as(RDFList.class).size());
		Assert.assertEquals(kept, head.as(RDFList.class).asJavaList().get(0));
	}

	@Test
	public void aShapeThatRefersToItselfTerminates() {
		// Recursive shapes are legal and do occur; the ordering key must not follow the cycle
		// forever. Without the guard this overflows the stack rather than failing an assertion.
		Model model = ModelFactory.createDefaultModel();
		Resource shape = model.createResource(EX + "Shape");
		Resource recursive = model.createResource();
		recursive.addProperty(SH.node, recursive);
		shape.addProperty(SH.or, recursive);
		shape.addProperty(SH.or, model.createResource(EX + "plain"));

		Owl2Shacl.postProcessLists(model, SH.or);

		RDFNode head = model.getProperty(shape, SH.or).getObject();
		Assert.assertTrue(head.canAs(RDFList.class));
		Assert.assertEquals(2, head.as(RDFList.class).size());
	}

	@Test
	public void twoShapesAreGatheredIndependently() {
		Model model = ModelFactory.createDefaultModel();
		Resource first = model.createResource(EX + "First");
		Resource second = model.createResource(EX + "Second");
		first.addProperty(SH.or, model.createResource(EX + "b"));
		first.addProperty(SH.or, model.createResource(EX + "a"));
		second.addProperty(SH.or, model.createResource(EX + "d"));
		second.addProperty(SH.or, model.createResource(EX + "c"));

		Owl2Shacl.postProcessLists(model, SH.or);

		Assert.assertEquals(Arrays.asList("a", "b"), localNames(model, first));
		Assert.assertEquals(Arrays.asList("c", "d"), localNames(model, second));
	}

	@Test
	public void twoDifferentlyWiredCyclesAreOrderedDeterministically() {
		// A back-reference rendered as a bare marker loses which ancestor it points at, so
		// "child points at itself" and "child points at its parent" would tie - and a tie falls
		// back to the model's iteration order, which is what this change removes.
		//
		//   A: root -p-> m ,  m -q-> root ,  m -r-> m
		//   B: root -p-> n ,  n -q-> n    ,  n -r-> root
		assertOrderIsStableAcrossInsertionOrders(
				(model, shape) -> {
					Resource root = model.createResource();
					Resource child = model.createResource();
					root.addProperty(model.createProperty(EX + "p"), child);
					child.addProperty(model.createProperty(EX + "q"), root);
					child.addProperty(model.createProperty(EX + "r"), child);
					return root;
				},
				(model, shape) -> {
					Resource root = model.createResource();
					Resource child = model.createResource();
					root.addProperty(model.createProperty(EX + "p"), child);
					child.addProperty(model.createProperty(EX + "q"), child);
					child.addProperty(model.createProperty(EX + "r"), root);
					return root;
				});
	}

	@Test
	public void aSharedChildAndTwoIdenticalChildrenAreOrderedDeterministically() {
		// A tree serialisation renders these identically - "p and q both lead to a node saying
		// v=1" - though one reaches a single blank node and the other reaches two.
		//
		//   A: root -p-> s , root -q-> s  , s  -v-> "1"
		//   B: root -p-> s1, root -q-> s2 , s1 -v-> "1" , s2 -v-> "1"
		assertOrderIsStableAcrossInsertionOrders(
				(model, shape) -> {
					Resource shared = model.createResource()
							.addProperty(model.createProperty(EX + "v"), "1");
					Resource root = model.createResource();
					root.addProperty(model.createProperty(EX + "p"), shared);
					root.addProperty(model.createProperty(EX + "q"), shared);
					return root;
				},
				(model, shape) -> {
					Resource root = model.createResource();
					root.addProperty(model.createProperty(EX + "p"), model.createResource()
							.addProperty(model.createProperty(EX + "v"), "1"));
					root.addProperty(model.createProperty(EX + "q"), model.createResource()
							.addProperty(model.createProperty(EX + "v"), "1"));
					return root;
				});
	}

	/** Builds one {@code sh:or} member into the given model and returns it. */
	private interface MemberBuilder {
		Resource build(Model model, Resource shape);
	}

	/**
	 * Two members that a weaker key would tie must still be ordered the same way every time.
	 *
	 * <p>Builds the pair in both insertion orders, repeatedly, and requires every gathered list
	 * to agree. If the two tie, {@link List#sort} is stable and the members come out in
	 * insertion order - so the two orders disagree and this fails. Comparing keys directly would
	 * mean exposing them; this tests the property the keys exist for.
	 */
	private static void assertOrderIsStableAcrossInsertionOrders(
			MemberBuilder first, MemberBuilder second) {
		String reference = null;
		for (int run = 0; run < 20; run++) {
			boolean firstFirst = run % 2 == 0;
			Model model = ModelFactory.createDefaultModel();
			Resource shape = model.createResource(EX + "Shape");
			Resource a = firstFirst ? first.build(model, shape) : second.build(model, shape);
			Resource b = firstFirst ? second.build(model, shape) : first.build(model, shape);
			shape.addProperty(SH.or, a);
			shape.addProperty(SH.or, b);

			Owl2Shacl.postProcessLists(model, SH.or);
			RDFList gathered = model.getProperty(shape, SH.or).getObject().as(RDFList.class);
			Assert.assertEquals("both members must survive gathering", 2, gathered.size());

			// Which member came first, identified by its shape rather than its label.
			List<RDFNode> members = gathered.asJavaList();
			String rendering = fingerprint(members.get(0)) + "|" + fingerprint(members.get(1));
			if (reference == null) {
				reference = rendering;
			} else {
				Assert.assertEquals(
						"run " + run + " ordered two members that a weaker key would tie "
								+ "differently; the ordering has fallen back to insertion order",
						reference, rendering);
			}
		}
	}

	/** A label-independent rendering of a member, for identifying which one it is. */
	private static String fingerprint(RDFNode node) {
		if (!node.isAnon()) {
			return node.toString();
		}
		Set<Resource> reachable = new java.util.HashSet<>();
		collect(node.asResource(), reachable);
		return render(node.asResource(), 0) + "#" + reachable.size();
	}

	private static void collect(Resource start, Set<Resource> seen) {
		for (Statement statement : start.listProperties().toList()) {
			if (statement.getObject().isAnon() && seen.add(statement.getObject().asResource())) {
				collect(statement.getObject().asResource(), seen);
			}
		}
	}

	private static String render(Resource node, int depth) {
		if (depth > 5) {
			return "...";
		}
		List<String> parts = new ArrayList<>();
		for (Statement statement : node.listProperties().toList()) {
			RDFNode object = statement.getObject();
			parts.add(statement.getPredicate().getLocalName() + "="
					+ (object.isAnon() ? render(object.asResource(), depth + 1) : object.toString()));
		}
		java.util.Collections.sort(parts);
		return "{" + String.join(",", parts) + "}";
	}

	private static List<String> localNames(Model model, Resource subject) {
		List<String> names = new ArrayList<>();
		for (RDFNode member : model.getProperty(subject, SH.or).getObject()
				.as(RDFList.class).asJavaList()) {
			names.add(member.asResource().getLocalName());
		}
		return names;
	}
}
