package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NamespaceSection {
	private String prefix;
	private String namespace;
	
	public static List<NamespaceSection> fromMap(Map<String, String> prefixes) {
		List<NamespaceSection> sections = new ArrayList<>();
		for(Map.Entry<String, String> e : prefixes.entrySet()) {
			NamespaceSection section = new NamespaceSection(e.getKey(), e.getValue());
			sections.add(section);
		}
		return sections;
	}
	
	public NamespaceSection(String output_prefix, String output_namespace) {
		super();
		this.prefix = output_prefix;
		this.namespace = output_namespace;
	}

	public String getprefix() {
		return prefix;
	}

	public String getnamespace() {
		return namespace;
	}

}