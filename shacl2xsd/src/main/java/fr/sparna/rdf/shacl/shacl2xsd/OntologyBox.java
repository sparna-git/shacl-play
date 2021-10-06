package fr.sparna.rdf.shacl.shacl2xsd;

import java.util.List;
import java.util.Map;

public class OntologyBox {
	
	
	protected List<OntologyImports> OntoImports;
	protected List<OntologyClass> OntoClass;
	protected List<OntologyObjectProperty> OntoOP;
	
	public List<OntologyImports> getOntoImports() {
		return OntoImports;
	}
	public void setOntoImports(List<OntologyImports> ontoImports) {
		OntoImports = ontoImports;
	}
	public List<OntologyClass> getOntoClass() {
		return OntoClass;
	}
	public void setOntoClass(List<OntologyClass> ontoClass) {
		OntoClass = ontoClass;
	}
	public List<OntologyObjectProperty> getOntoOP() {
		return OntoOP;
	}
	public void setOntoOP(List<OntologyObjectProperty> ontoOP) {
		OntoOP = ontoOP;
	}
	
	
	
	
	
	

}
