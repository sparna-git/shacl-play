package fr.sparna.rdf.shacl.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

public class DefaultModelProcessor implements ModelProcessorIfc {

	private List<String> ignoredClasses = new ArrayList<String>();
	private List<String> ignoredNamespaces = new ArrayList<String>();
	
	
	
	public DefaultModelProcessor() {
		// ignore Virtuoso namespace by default
		this.ignoredNamespaces.add("http://www.openlinksw.com/schemas/virtrdf#");
		this.ignoredNamespaces.add("http://www.w3.org/ns/sparql-service-description#");
		// ignore OWL & RDFS namespace by default
		this.ignoredNamespaces.add("http://www.w3.org/2002/07/owl#");
		this.ignoredNamespaces.add("http://www.w3.org/2000/01/rdf-schema#");
		this.ignoredNamespaces.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		this.ignoredNamespaces.add("http://www.w3.org/2001/XMLSchema#");
	}

	@Override
	public String getTypeTranslation(Set<String> classes) {
		return null;
	}

	@Override
	public List<String> getIgnoredClasses() {
		return ignoredClasses;
	}

	public List<String> getIgnoredNamespaces() {
		return ignoredNamespaces;
	}

	public void setIgnoredNamespaces(List<String> ignoredNamespaces) {
		this.ignoredNamespaces = ignoredNamespaces;
	}

	public void setIgnoredClasses(List<String> ignoredClasses) {
		this.ignoredClasses = ignoredClasses;
	}

	public boolean isIgnoredType(String typeUri) {
		if(CollectionUtils.isNotEmpty(this.getIgnoredNamespaces())) {
			if(this.getIgnoredNamespaces().stream().anyMatch(ns -> typeUri.startsWith(ns))) {
				return true;
			}
		}
		if(CollectionUtils.isNotEmpty(this.getIgnoredClasses())) {
			return this.getIgnoredClasses().contains(typeUri);
		}

		return false;
	}
}
