package fr.sparna.rdf.shacl.doc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// RENAME : NamespaceSection
public class NamespaceSections {
	// RENAME : prefix
	private String output_prefix;
	// RENAME : namespace
	private String output_namespace;
	
	public static List<NamespaceSections> fromMap(Map<String, String> prefixes) {
		List<NamespaceSections> sections = new ArrayList<>();
		for(Map.Entry<String, String> e : prefixes.entrySet()) {
			NamespaceSections section = new NamespaceSections(e.getKey(), e.getValue());
			sections.add(section);
		}
		return sections;
	}
	
	public NamespaceSections(String output_prefix, String output_namespace) {
		super();
		this.output_prefix = output_prefix;
		this.output_namespace = output_namespace;
	}

	public String getOutput_prefix() {
		return output_prefix;
	}

	public String getOutput_namespace() {
		return output_namespace;
	}

}
