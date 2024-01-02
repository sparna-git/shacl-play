package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.expr.nodevalue.NodeFunctions;

public class ModelReadingUtils { 

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
	
	public static List<Literal> readLiteral(Resource constraint, Property property) {
		return readLiteralInLang(constraint, property, null);
	}
	
	/**
	 * Reads a Literal in a specific language (possibly null for all languages), and returns all values as a List
	 */
	public static List<Literal> readLiteralInLang(Resource resource, Property property, String lang) {
		if (resource.hasProperty(property)) {
			if(lang != null) {
				// we asked for an explicit language
				if(resource.listProperties(property, lang).toList().size() > 0) {
					return resource.listProperties(property, lang).toList().stream()
							.map(s -> s.getObject().asLiteral())
							.collect(Collectors.toList());
				} else {
					// also look for values with no language
					return resource.listProperties(property, "").toList().stream()
							.map(s -> s.getObject().asLiteral())
							.collect(Collectors.toList());
				}
			} else {
				// we didn't asked for a specific language, search any property value
				return resource.listProperties(property).toList().stream()
						.map(s -> s.getObject().asLiteral())
						.collect(Collectors.toList());
			}
		}
		
		return null;
	}
	
	/**
	 * Reads a Literal in a specific language, and returns all values concatenated as a single String. lang can be null
	 */
	public static String readLiteralInLangAsString(Resource r, Property property, String lang) {
		return ModelRenderingUtils.render(readLiteralInLang(r, property, lang), true);
	}
	
	/**
	 * Reads a Literal, and returns all values concatenated as a single String.
	 */
	public static String readLiteralAsString(Resource r, Property property) {
		return ModelRenderingUtils.render(readLiteral(r, property), true);
	}
	
	/**
	 * Reads all values of the given property, either as Resources or as Literal in a specific language
	 */
	public static List<RDFNode> readObjectAsResourceOrLiteralInLang(Resource r, Property property, String lang) {
		if(r.hasProperty(property)) {
			List<RDFNode> result = new ArrayList<RDFNode>();
			for(RDFNode n : r.listProperties(property).toList().stream().map(s -> s.getObject()).collect(Collectors.toList())) {
				// we keep it either if it is not a Literal or if Literal has no language or the requested language
				if(!n.isLiteral() || n.asLiteral().getLanguage() == null || n.asLiteral().getLanguage().equals("") || n.asLiteral().getLanguage().equals(lang)) {
					result.add(n);
				}
			}
			
			return result;
		}
		
		return null;
	}
	
	/**
	 * Reads all values of the given property, either as Resources or as Literal in a specific language
	 */
	public static List<RDFNode> readObjectAsResourceOrLiteral(Resource r, Property property) {
		if(r.hasProperty(property)) {
			return r.listProperties(property).toList().stream().map(s -> s.getObject()).collect(Collectors.toList());
		}
		
		return null;
	}
	
	/**
	 * Reads all values of the given property as Resources
	 */
	public static List<RDFNode> readObjectAsResource(Resource r, Property property) {
		if(r.hasProperty(property)) {
			return r.listProperties(property).toList().stream().map(s -> s.getObject()).collect(Collectors.toList());
		}
		
		return null;
	}
	
	public static List<RDFNode> asJavaList(Resource resource) {
		return (resource.as(RDFList.class)).asJavaList();
	}
	
}
