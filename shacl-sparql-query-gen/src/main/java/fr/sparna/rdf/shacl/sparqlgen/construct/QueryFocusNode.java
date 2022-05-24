package fr.sparna.rdf.shacl.sparqlgen.construct;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.shared.PrefixMapping;

public class QueryFocusNode {
	
	public Query queryfocusnode(String shSelect) {
		
		Query qShaclSelect = QueryFactory.create(shSelect);
		return qShaclSelect;
	}
	
	
	public PrefixMapping getPrefixMapping(Query oQuery) {
		PrefixMapping pm = oQuery.getPrefixMapping();
		return pm;
	}

}
