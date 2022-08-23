package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.shacl.doc.read.PropertyShapeDocumentationBuilder;

public class ConstraintValueReader { 

	
	/**
	 * Reads a Literal in a specific language, and returns all values as a List
	 */
	public static List<Literal> readLiteralInLang(Resource constraint, Property property, String lang) {
		if (constraint.hasProperty(property)) {
			if(lang != null) {
				// we asked for an explicit language
				if(constraint.listProperties(property, lang).toList().size() > 0) {
					return constraint.listProperties(property, lang).toList().stream()
							.map(s -> s.getObject().asLiteral())
							.collect(Collectors.toList());
				} else {
					// also look for values with no language
					return constraint.listProperties(property, "").toList().stream()
							.map(s -> s.getObject().asLiteral())
							.collect(Collectors.toList());
				}
			} else {
				// we didn't asked for a specific language, search any property value
				return constraint.listProperties(property).toList().stream()
						.map(s -> s.getObject().asLiteral())
						.collect(Collectors.toList());
			}
		}
		
		return null;
	}
	
	/**
	 * Reads a Literal in a specific language, and returns all values concatenated as a single String
	 */
	public static String readLiteralInLangAsString(Resource r, Property property, String lang) {
		return PropertyShapeDocumentationBuilder.render(readLiteralInLang(r, property, lang), true);
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
	 * Reads all values of the given property as Resources
	 */
	public static List<RDFNode> readObjectAsResource(Resource r, Property property) {
		if(r.hasProperty(property)) {
			return r.listProperties(property).toList().stream().map(s -> s.getObject()).collect(Collectors.toList());
		}
		
		return null;
	}
	
}
