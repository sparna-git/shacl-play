package fr.sparna.rdf.shacl.sparqlgen.construct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrefixDeclaration {
	private String prefix;
	private String namespace;
	
	public static List<PrefixDeclaration> fromMap(Map<String, String> prefixes) {
		List<PrefixDeclaration> sections = new ArrayList<>();
		for(Map.Entry<String, String> e : prefixes.entrySet()) {
			PrefixDeclaration section = new PrefixDeclaration(e.getKey(), e.getValue());
			sections.add(section);
		}
		return sections;
	}
	
	public PrefixDeclaration(String output_prefix, String output_namespace) {
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
