package fr.sparna.rdf.shacl.doc.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(Include.NON_NULL)
public class Targets {

    private List<Link> targetClass;
    private String targetSubjectsOf;
	private String targetObjectsOf;
    private String sparqlTarget;
    private int numberOfTargets;
    
    public List<Link> getTargetClass() {
        return targetClass;
    }
    public void setTargetClass(List<Link> targetClass) {
        this.targetClass = targetClass;
    }
    public String getTargetSubjectsOf() {
        return targetSubjectsOf;
    }
    public void setTargetSubjectsOf(String targetSubjectsOf) {
        this.targetSubjectsOf = targetSubjectsOf;
    }
    public String getTargetObjectsOf() {
        return targetObjectsOf;
    }
    public void setTargetObjectsOf(String targetObjectsOf) {
        this.targetObjectsOf = targetObjectsOf;
    }
    public String getSparqlTarget() {
        return sparqlTarget;
    }
    public void setSparqlTarget(String sparqlTarget) {
        this.sparqlTarget = sparqlTarget;
    }
    public int getNumberOfTargets() {
        return numberOfTargets;
    }
    public void setNumberOfTargets(int numberOfTargets) {
        this.numberOfTargets = numberOfTargets;
    }

    

}