package fr.sparna.rdf.shacl.generate;

import java.util.List;

public interface ModelProcessorIfc {
	
	public List<String> getExcludedClasses();
	
	public List<String> getExcludedNamespaces();
	
	public boolean isIgnoredType(String typeUri);
	
}
