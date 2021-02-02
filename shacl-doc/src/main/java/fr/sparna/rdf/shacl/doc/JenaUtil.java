package fr.sparna.rdf.shacl.doc;


import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.expr.nodevalue.NodeFunctions;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.jenax.util.ImportProperties;

public class JenaUtil {
	
		/**
		 * For a given subject resource and a given collection of (label/comment) properties this finds the most
		 * suitable value of either property for a given list of languages (usually from the current user's preferences).
		 * For example, if the user's languages are [ "en-AU" ] then the function will prefer "mate"@en-AU over
		 * "friend"@en and never return "freund"@de.  The function falls back to literals that have no language
		 * if no better literal has been found.
		 * @param resource  the subject resource
		 * @param langs  the allowed languages
		 * @param properties  the properties to check
		 * @return the best suitable value or null
		 */
		public static Literal getBestStringLiteral(Resource resource, List<String> langs, Iterable<Property> properties) {
			return getBestStringLiteral(resource, langs, properties, (r,p) -> r.listProperties(p));
		}
		
		
		public static Literal getBestStringLiteral(Resource resource, List<String> langs, Iterable<Property> properties, BiFunction<Resource,Property,Iterator<Statement>> getter) {
			Literal label = null;
			int bestLang = -1;
			for(Property predicate : properties) {
				Iterator<Statement> it = getter.apply(resource, predicate);
				while(it.hasNext()) {
					RDFNode object = it.next().getObject();
					if(object.isLiteral()) {
						Literal literal = (Literal)object;
						String lang = literal.getLanguage();
						if(lang.length() == 0 && label == null) {
							label = literal;
						}
						else {
							// 1) Never use a less suitable language
							// 2) Never replace an already existing label (esp: skos:prefLabel) unless new lang is better
							// 3) Fall back to more special languages if no other was found (e.g. use en-GB if only "en" is accepted)
							int startLang = bestLang < 0 ? langs.size() - 1 : (label != null ? bestLang - 1 : bestLang);
							for(int i = startLang; i >= 0; i--) {
								String langi = langs.get(i);
								if(langi.equalsIgnoreCase(lang)) {
									label = literal;
									bestLang = i;
								}
								else if(lang.contains("-") && NodeFunctions.langMatches(lang, langi) && label == null) {
									label = literal;
								}
							}
						}
					}
				}
			}
			return label;
		}


}
