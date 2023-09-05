package fr.sparna.rdf.shacl.generate;

import java.util.List;

public interface ModelProcessorIfc {
	
	public List<String> getIgnoredClasses();
	
	public List<String> getIgnoredNamespaces();
	
	public boolean isIgnoredType(String typeUri);
	
}
