package fr.sparna.rdf.shacl.doc.model;

import fr.sparna.rdf.jena.shacl.SparqlConstraint;

public class ConstraintEntry {
   
    private String description;
    private String select;

	public ConstraintEntry(SparqlConstraint sparqlConstraint, String lang) {
		this.description = sparqlConstraint.getDescription(lang);
		this.select = sparqlConstraint.getSelect();
	}
	
	public ConstraintEntry() {
	}
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSelect() {
        return select;
    }
    public void setSelect(String select) {
        this.select = select;
    }

    



}