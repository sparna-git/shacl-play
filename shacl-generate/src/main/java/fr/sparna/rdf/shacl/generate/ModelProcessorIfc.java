package fr.sparna.rdf.shacl.generate;

import java.util.List;
import java.util.Set;

public interface ModelProcessorIfc {

	public String getTypeTranslation(Set<String> classes);
	
	public List<String> getIgnoredClasses();
	
	public List<String> getIgnoredNamespaces();
	
	public boolean isIgnoredType(String typeUri);
	
}
