package fr.sparna.rdf.shacl.sparqlgen.construct;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.expr.E_Lang;
import org.apache.jena.sparql.expr.E_OneOf;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueString;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;

/**
 * Generic helper methods to assist in SPARQL query generation
 * @author thomas
 *
 */
public final class SparqlQueryHelper {

	
	public static ElementTriplesBlock initElementTriplesBlock(
			Node subject,
			Node predicate,
			Node object
	) {
		ElementTriplesBlock eBlock = new ElementTriplesBlock();		
		eBlock.addTriple(new Triple(subject, predicate, object));
		return eBlock;
	}
	
	/**
	 * Generates a VALUES SPARQL elements from the variable name and a list of IRI Node
	 */
	public static ElementData initElementData(
			Var vInput,
			List<Node> iris
	) {
		ElementData eData = new ElementData();
		
		eData.add(vInput);
		
		iris.forEach(i -> {			
			eData.add(BindingFactory.binding(vInput, i));
		});		
		return eData;
	}
	
	/**
	 * Converts a list of IRIS as string to a list of IRI Node
	 */
	public static List<Node> stringListToNodeList(
			List<String> iris
	) {
		List<Node> nodeList = new ArrayList<Node>();
		iris.forEach(i -> {			
			nodeList.add(NodeFactory.createURI(i));			
		});
		 
		return nodeList;
	}
	
	
	public static ElementFilter initElementFilter(
			ExprVar vInput,
			List<String> strList
			) {
		
		ExprList eList = new ExprList();
		for(String data:strList) {
			eList.add(new NodeValueString(data));					 
		}
		
		Expr ExpLang = new E_Lang(vInput);
		
		Expr OneOf = new E_OneOf(ExpLang, eList);
		
		
		return new ElementFilter(OneOf);
	}
	
	
}
