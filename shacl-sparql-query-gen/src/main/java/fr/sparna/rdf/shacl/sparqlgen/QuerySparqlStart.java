package fr.sparna.rdf.shacl.sparqlgen;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.Template;

import fr.sparna.rdf.shacl.sparqlgen.construct.QueryFocusNode;

public class QuerySparqlStart {

	final BasicPattern bp = new BasicPattern();
	final BasicPattern bp2 = new BasicPattern();
	final ElementTriplesBlock eTripleOptionalWhereClause = new ElementTriplesBlock();
	final List<ElementTriplesBlock> ListOptional = new ArrayList<>();

	// Variable
	final Var vThat = Var.alloc("that");
	final Var vThis = Var.alloc("this");
	
	
	public void querySparqlStart() {

		System.out.println("Requ�te 1 : Requ�te pour une NodeShape de 'point de d�part'");

		String sh_Select_1 = "PREFIX eli: <http://data.europa.eu/eli/ontology#>\r\n"
				+ "PREFIX resource-type: <http://publications.europa.eu/resource/authority/resource-type/>\r\n"
				+ "SELECT ?this\r\n" + "WHERE {\r\n" + "  ?this eli:work_type ?type .\r\n" + "  VALUES ?type {\r\n"
				+ "    # all narrower terms of QUEST_EP\r\n" + "    resource-type:INTERPELL_G\r\n"
				+ "    resource-type:INTERPELL_K\r\n" + "    resource-type:QUEST_ORAL\r\n"
				+ "    resource-type:QUEST_PRIORITY\r\n" + "    resource-type:QUEST_TIME\r\n"
				+ "    resource-type:QUEST_WRITTEN\r\n" + "  }\r\n" + "}";

		String sh_Select_2 = "PREFIX eli: <http://data.europa.eu/eli/ontology#>\r\n"
				+ "PREFIX resource-type: <http://publications.europa.eu/resource/authority/resource-type/>\r\n"
				+ "SELECT (?this AS ?that)\r\n"
				+ "        WHERE {\r\n"
				+ "          ?this eli:work_type ?type .\r\n"
				+ "          VALUES ?type {\r\n"
				+ "            # all narrower terms of QUEST_EP\r\n"
				+ "            resource-type:INTERPELL_G\r\n"
				+ "            resource-type:INTERPELL_K\r\n"
				+ "            resource-type:QUEST_ORAL\r\n"
				+ "            resource-type:QUEST_PRIORITY\r\n"
				+ "            resource-type:QUEST_TIME\r\n"
				+ "            resource-type:QUEST_WRITTEN\r\n"
				+ "          }\r\n"
				+ "        }";

		// Query Constructing
		Query queryConstruct = QueryFactory.create();
		ElementGroup WhereClause = new ElementGroup();

		// Get and fetch the properties for the config in Optional condition
		ElementTriplesBlock eTripleOptionalWhereClause = new ElementTriplesBlock();
		eTripleOptionalWhereClause.addTriple(new Triple(NodeFactory.createVariable("this"),
				NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
				NodeFactory.createVariable("rdf_type")));

		ElementOptional optional = new ElementOptional(eTripleOptionalWhereClause);
		WhereClause.addElement(optional);

		// property WorkType
		ElementTriplesBlock eTripleOptionalWhereClause1 = new ElementTriplesBlock();
		eTripleOptionalWhereClause1.addTriple(new Triple(NodeFactory.createVariable("this"),
				NodeFactory.createURI("http://data.europa.eu/eli/ontology#work_type"),
				NodeFactory.createVariable("work_type")));
		ElementOptional optional1 = new ElementOptional(eTripleOptionalWhereClause1);
		ElementGroup eGroup = new ElementGroup();
		// eGroup.addElementFilter(new Ele);
		WhereClause.addElement(optional1);
		// property date document
		ElementTriplesBlock eTripleOptionalWhereClause2 = new ElementTriplesBlock();
		eTripleOptionalWhereClause2.addTriple(new Triple(NodeFactory.createVariable("this"),
				NodeFactory.createURI("http://data.europa.eu/eli/ontology#date_document"),
				NodeFactory.createVariable("date_document")));
		ElementOptional optional2 = new ElementOptional(eTripleOptionalWhereClause2);
		WhereClause.addElement(optional2);
		// property date document
		ElementTriplesBlock eTripleOptionalWhereClause3 = new ElementTriplesBlock();
		eTripleOptionalWhereClause3.addTriple(new Triple(NodeFactory.createVariable("this"),
				NodeFactory.createURI("http://purl.org/dc/terms/title"), NodeFactory.createVariable("title")));
		ElementOptional optional3 = new ElementOptional(eTripleOptionalWhereClause3);
		WhereClause.addElement(optional3);

		// Construct
		//BasicPattern bp = new BasicPattern();
		bp.add(new Triple(NodeFactory.createVariable("this"),
				NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
				NodeFactory.createVariable("rdf_type")));

		bp.add(new Triple(NodeFactory.createVariable("this"),
				NodeFactory.createURI("http://data.europa.eu/eli/ontology#work_type"),
				NodeFactory.createVariable("work_type")));

		bp.add(new Triple(NodeFactory.createVariable("this"),
				NodeFactory.createURI("http://data.europa.eu/eli/ontology#date_document"),
				NodeFactory.createVariable("date_document")));

		bp.add(new Triple(NodeFactory.createVariable("this"), NodeFactory.createURI("http://purl.org/dc/terms/title"),
				NodeFactory.createVariable("title")));

		queryConstruct.setConstructTemplate(new Template(bp));
		queryConstruct.setQueryConstructType();

		// SubQuery
		Query queryShSelect;
		try {

			// Query in sh:Select
			queryShSelect = QueryFactory.create(sh_Select_1); // This should fail with the

			// Get Prefixes
			PrefixMapping pm = queryShSelect.getPrefixMapping();

			// Set the prefix to Query Construct
			queryConstruct.getPrefixMapping().setNsPrefixes(queryShSelect.getPrefixMapping().getNsPrefixMap());

			// Remove the prefix
			pm.clearNsPrefixMap();
			queryShSelect.setPrefixMapping(pm);

			// Add subQuery in the Query Construct
			WhereClause.addElement(new ElementSubQuery(queryShSelect));
		} // default SPARQL parser.
		catch (QueryParseException e) { // Hence, this exception
			System.out.println(e); // is expected.
		}

		queryConstruct.setQueryPattern(WhereClause);
		System.out.println(queryConstruct.toString());

		/*
		 * ----------------------------------------------------------------------------
		 * ----------------------------------------------------------------------------
		 * ----------------------------------------------------------------------------
		 */
		
		System.out.println("## Requ�te 2 : Requ�te pour suivre un chemin depuis la NodeShape de \"point de d�part\"");

		
		// Query the focus node
		Query qSelectNode = QueryFactory.create(sh_Select_2);
		
		// Construct Query Select
		Query nQuery = new Query();
		ElementGroup WhereClause_nQuery = new ElementGroup();
		
		// Query output Triples
		Query nQueryOutput = new Query();
		ElementGroup WhereClause_Output = new ElementGroup();

		
		Var vrdf_type = Var.alloc("rdf_type");
		Var vskos_prefLabel = Var.alloc("skos_prefLabel");
		
		nQueryOutput.setPrefix("eli","http://data.europa.eu/eli/ontology#");
		nQueryOutput.setPrefix("resource-type","http://publications.europa.eu/resource/authority/resource-type/");
		nQueryOutput.setPrefix("eli-dl","http://data.europa.eu/eli/eli-draft-legislation-ontology#");
		
		
		BasicPattern bpContruct = new BasicPattern();
		bpContruct.add(new Triple(
				vThis,
				NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
				vrdf_type
				));
		bpContruct.add(new Triple(
				vThis,
				NodeFactory.createURI("http://www.w3.org/2004/02/skos/core#prefLabel"),
				vskos_prefLabel
				));		
		nQueryOutput.setConstructTemplate(new Template(bpContruct));
		nQueryOutput.setQueryConstructType();
		
		
		List<ElementOptional> eOptional = new ArrayList<>();
		ElementTriplesBlock eOptionTriple = new ElementTriplesBlock();
		eOptionTriple.addTriple(new Triple(
				vThis,
				NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
				vrdf_type)
				);
		ElementOptional eop = new ElementOptional(eOptionTriple);
		WhereClause_Output.addElement(eop);
		
		ElementTriplesBlock eOptionTriple1 = new ElementTriplesBlock();
		eOptionTriple1.addTriple(new Triple(
				vThis,
				NodeFactory.createURI("http://www.w3.org/2004/02/skos/core#prefLabel"),
				vskos_prefLabel
				)
				);
		ElementOptional eop1 = new ElementOptional(eOptionTriple1);
		WhereClause_Output.addElement(eop1);
		
		
		//nQuery.getProject().add(vThat);
		nQuery.getProject().add(vThis);
		
		ElementTriplesBlock eSelect = new ElementTriplesBlock();
		eSelect.addTriple(new Triple(
				vThat,
				NodeFactory.createURI("http://data.europa.eu/eli/eli-draft-legislation-ontology#parliamentary_term"),
				vThis
				));
		WhereClause_nQuery.addElement(eSelect);
		
		//
		// Get Prefixes
		PrefixMapping pm = qSelectNode.getPrefixMapping();
		// Set the prefix to Query Construct
		nQuery.getPrefixMapping().setNsPrefixes(qSelectNode.getPrefixMapping().getNsPrefixMap());

		// Remove the prefix
		pm.clearNsPrefixMap();
		qSelectNode.setPrefixMapping(pm);
		
		
		WhereClause_nQuery.addElement(new ElementSubQuery(qSelectNode));
		nQuery.setQueryPattern(WhereClause_nQuery);
		nQuery.setQuerySelectType();
		nQuery.setDistinct(true);
		
		PrefixMapping pmQuery =  nQuery.getPrefixMapping();
		pmQuery.clearNsPrefixMap();
		nQuery.setPrefixMapping(pmQuery);
		
		WhereClause_Output.addElement(new ElementSubQuery(nQuery));
		nQueryOutput.setQueryPattern(WhereClause_Output);
		
		
		System.out.println(nQueryOutput);
		
		
		/*
		 * ----------------------------------------------------------------------------
		 * ----------------------------------------------------------------------------
		 * ----------------------------------------------------------------------------
		 */
		
		System.out.println("## Requ�te 3 : Requ�te pour suivre un chemin depuis la NodeShape de \"point de d�part\", � 2 niveaux");
		
		String qryFn = "PREFIX eli: <http://data.europa.eu/eli/ontology#>\r\n"
				+ "PREFIX resource-type: <http://publications.europa.eu/resource/authority/resource-type/>\r\n"
				+"SELECT (?this AS ?that)\r\n"
				+ "        WHERE {\r\n"
				+ "          ?this eli:work_type ?type .\r\n"
				+ "          VALUES ?type {\r\n"
				+ "            # all narrower terms of QUEST_EP\r\n"
				+ "            resource-type:INTERPELL_G\r\n"
				+ "            resource-type:INTERPELL_K\r\n"
				+ "            resource-type:QUEST_ORAL\r\n"
				+ "            resource-type:QUEST_PRIORITY\r\n"
				+ "            resource-type:QUEST_TIME\r\n"
				+ "            resource-type:QUEST_WRITTEN\r\n"
				+ "          }\r\n"
				+ "        }";
		
		
		// Etape 1. Query focus node
		
		
		QueryFocusNode qfn = new QueryFocusNode();
		Query focus = qfn.queryfocusnode(qryFn);
		focus.setPrefixMapping(qfn.getPrefixMapping(focus));
		
		//# follow a property that has a sh:node constraint
		Query qNodeconstraint = new Query();
		ElementGroup WhereNodeconstraint = new ElementGroup(); // Where
		
		Var v_eli_is_realized_by = Var.alloc("eli_is_realized_by");
		Var v_eli_is_embodied_by = Var.alloc("eli_is_embodied_by");
		
		qNodeconstraint.setQuerySelectType(); // Select
		qNodeconstraint.setDistinct(true); // Distinc
		qNodeconstraint.getProject().add(vThis);
		
		ElementTriplesBlock eCondition1 = new ElementTriplesBlock();
		eCondition1.addTriple(new Triple(
				vThat,
				NodeFactory.createURI("http://data.europa.eu/eli/ontology#is_realized_by"),
				v_eli_is_realized_by
				));
		WhereNodeconstraint.addElement(eCondition1); //add condition
		ElementTriplesBlock eCondition2 = new ElementTriplesBlock();
		eCondition2.addTriple(new Triple(
				v_eli_is_realized_by,
				v_eli_is_embodied_by,
				vThis
				));
		WhereNodeconstraint.addElement(eCondition2); //add condition
		WhereNodeconstraint.addElement(new ElementSubQuery(focus));	// Add query focus node	
		qNodeconstraint.setQueryPattern(WhereNodeconstraint); //joint the Where Clause in the query
		
		//# Construct outpur triples
		Query qOutput = new Query();
		ElementGroup WhereOutput = new ElementGroup();
		BasicPattern bp3 = new BasicPattern();
		bp3.add(new Triple(
				vThis,
				NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
				vrdf_type
				));
		
		bp3.add(new Triple(
				vThis,
				NodeFactory.createURI("http://www.w3.org/2004/02/skos/core#prefLabel"),
				vskos_prefLabel
				));
		qOutput.setConstructTemplate(new Template(bp3));
		qOutput.setQueryConstructType();
		WhereOutput.addElement(new ElementSubQuery(qNodeconstraint));
		
		qOutput.setQueryPattern(WhereOutput);	
		
		System.out.println(qOutput);
	}
}
