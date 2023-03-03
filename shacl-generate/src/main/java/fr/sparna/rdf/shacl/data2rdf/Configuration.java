package fr.sparna.rdf.shacl.data2rdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

public class Configuration {
  private String shapesNamespace;

  private List<String> ignoredClasses = new ArrayList<>();

  private List<String> ignoredNamespaces = new ArrayList<>();
  
  public Configuration() {
	  // ignore Virtuoso namespace by default
	  this.ignoredNamespaces.add("http://www.openlinksw.com/schemas/virtrdf#");
	  this.ignoredNamespaces.add("http://www.w3.org/ns/sparql-service-description#");
	  // ignore OWL & RDFS namespace by default
	  this.ignoredNamespaces.add("http://www.w3.org/2002/07/owl#");
	  this.ignoredNamespaces.add("http://www.w3.org/2000/01/rdf-schema#");
	  this.ignoredNamespaces.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	  
  }

  public Configuration(String shapesNamespace) {
    this.shapesNamespace = shapesNamespace;
  }

  public String getShapesNamespace() {
    return shapesNamespace;
  }

  public void setShapesNamespace(String shapesNamespace) {
    this.shapesNamespace = shapesNamespace;
  }

  public List<String> getIgnoredClasses() {
    return ListUtils.emptyIfNull(ignoredClasses);
  }

  public void setIgnoredClasses(List<String> ignoredClasses) {
    Objects.requireNonNull(ignoredClasses, "ignoredClasses cannot be null");
    this.ignoredClasses = ignoredClasses;
  }

  public List<String> getIgnoredNamespaces() {
	  return ignoredNamespaces;
  }

  public void setIgnoredNamespaces(List<String> ignoredNamespaces) {
	  Objects.requireNonNull(ignoredClasses, "ignoredNamespaces cannot be null");
	  this.ignoredNamespaces = ignoredNamespaces;
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
