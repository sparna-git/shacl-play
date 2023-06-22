package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.topbraid.shacl.vocabulary.SH;

public class Prefixes {

	public List<NamespaceSection> prefixes(List<Resource> nodeShapes, Model shaclGraph) {

		HashSet<String> gatheredPrefixes = new HashSet<>();
		for (Resource ns : nodeShapes) {
			List<String> prefixes = readPrefixes(ns);
			gatheredPrefixes.addAll(prefixes);
		}

		//Map<String, String> necessaryPrefixes = ShaclPrefixReader.gatherNecessaryPrefixes(shaclGraph.getNsPrefixMap(),gatheredPrefixes);
		List<NamespaceSection> namespaceSections = NamespaceSection.fromMap(shaclGraph.getNsPrefixMap());
		List<NamespaceSection> sortNameSpacesectionPrefix = namespaceSections.stream().sorted((s1, s2) -> {
			if (s1.getprefix() != null) {
				if (s2.getprefix() != null) {
					return s1.getprefix().toString().compareTo(s2.getprefix().toString());
				} else {
					return -1;
				}
			} else {
				if (s2.getprefix() != null) {
					return 1;
				} else {
					return s1.getprefix().compareTo(s2.getprefix());
				}
			}
		}).collect(Collectors.toList());

		return sortNameSpacesectionPrefix;
	}

	public List<String> readPrefixes(Resource nodeShape) {
		ShaclPrefixReader reader = new ShaclPrefixReader();
		List<String> prefixes = new ArrayList<>();

		// read prefixes on node shape
		prefixes.addAll(reader.readPrefixes(nodeShape));

		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			if (object.isResource()) {
				Resource propertyShape = object.asResource();
				prefixes.addAll(reader.readPrefixes(propertyShape));
			}
		}
		return prefixes;
	}
}