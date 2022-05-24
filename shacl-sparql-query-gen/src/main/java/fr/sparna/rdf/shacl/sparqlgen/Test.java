package fr.sparna.rdf.shacl.sparqlgen;

import java.io.FileNotFoundException;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.Template;

public class Test {

	final BasicPattern bp = new BasicPattern();
	final ElementTriplesBlock eTripleWhereClause = new ElementTriplesBlock();

	public void test() {
		System.out.println("Testing...");

		String sparqlQuery = "SELECT ?this WHERE { ?this a ?x }";
		Query query = QueryFactory.create(sparqlQuery);
		Query newQuery = QueryFactory.create();
		// BasicPattern bp = new BasicPattern();
		bp.add(new Triple(NodeFactory.createVariable("this"), NodeFactory.createURI("https://exemple.fr/myProperty"),
				NodeFactory.createVariable("thatIs")));
		Template t = new Template(bp);
		newQuery.setConstructTemplate(t);
		newQuery.setQueryConstructType();
		ElementGroup whereClause = new ElementGroup();
		whereClause.addElement(new ElementSubQuery(query));

		ElementTriplesBlock tripleBlock = new ElementTriplesBlock();
		tripleBlock.addTriple(new Triple(NodeFactory.createVariable("test"),
				NodeFactory.createURI("https://exemple.fr/toto"), NodeFactory.createVariable("test")));
		ElementOptional optional = new ElementOptional(tripleBlock);
		whereClause.addElement(optional);
		newQuery.setQueryPattern(whereClause);

		// System.out.println(query.toString());
		// System.out.println("New query :");
		// System.out.println(newQuery.toString());
	}

	public static void main(String[] args) throws FileNotFoundException {
		Test me = new Test();
		// me.test();

	}
}
