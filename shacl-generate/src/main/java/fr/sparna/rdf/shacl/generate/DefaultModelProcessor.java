package fr.sparna.rdf.shacl.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

public class DefaultModelProcessor implements ModelProcessorIfc {

	private List<String> excludedClasses = new ArrayList<String>();
	private List<String> excludedNamespaces = new ArrayList<String>();
	
	private List<String> includedClasses = new ArrayList<String>();
	private List<String> includedNamespaces = new ArrayList<String>();
	
	public DefaultModelProcessor() {
		// ignore Virtuoso namespace by default
		this.excludedNamespaces.add("http://www.openlinksw.com/schemas/virtrdf#");
		this.excludedNamespaces.add("http://www.w3.org/ns/sparql-service-description#");
		// ignore OWL & RDFS namespace by default
		this.excludedNamespaces.add("http://www.w3.org/2002/07/owl#");
		this.excludedNamespaces.add("http://www.w3.org/2000/01/rdf-schema#");
		this.excludedNamespaces.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		this.excludedNamespaces.add("http://www.w3.org/2001/XMLSchema#");
	}

	@Override
	public List<String> getExcludedClasses() {
		return excludedClasses;
	}

	public List<String> getExcludedNamespaces() {
		return excludedNamespaces;
	}

	public void setExcludedNamespaces(List<String> ignoredNamespaces) {
		this.excludedNamespaces = ignoredNamespaces;
	}

	public void setExcludedClasses(List<String> ignoredClasses) {
		this.excludedClasses = ignoredClasses;
	}

	public List<String> getIncludedClasses() {
		return includedClasses;
	}

	public void setIncludedClasses(List<String> includedClasses) {
		this.includedClasses = includedClasses;
	}

	public List<String> getIncludedNamespaces() {
		return includedNamespaces;
	}

	public void setIncludedNamespaces(List<String> includedNamespaces) {
		this.includedNamespaces = includedNamespaces;
	}

	public boolean isIgnoredType(String typeUri) {		
		if(
				// if there is some explicitely included namespace,  type is excluded if it does not belong to this list
				(
					CollectionUtils.isNotEmpty(this.getIncludedNamespaces())
					&&
					!this.getIncludedNamespaces().stream().anyMatch(ns -> typeUri.startsWith(ns))
				)
				||
				// if there is some explicitely excluded namespace, make sure the namespace is in this list
				(
					CollectionUtils.isNotEmpty(this.getExcludedNamespaces())
					&&
					this.getExcludedNamespaces().stream().anyMatch(ns -> typeUri.startsWith(ns))
				)
		) {
				return true;
		}
		if(
				(
					CollectionUtils.isNotEmpty(this.getIncludedClasses())
					&&
					!this.getIncludedClasses().contains(typeUri)
				)
				||
				(
					CollectionUtils.isNotEmpty(this.getExcludedClasses())
					&&
					this.getExcludedClasses().contains(typeUri)
				)
				
		) {
			return true;
		}

		return false;
	}
}
