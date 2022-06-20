package fr.sparna.rdf.shacl.sparqlgen;

public interface SparqlGeneratorOutputListenerIfc {

	public void notifyStart() throws Exception ;	
	
	public void notifyOutputQuery(String query, String filename) throws Exception;
	
	public void notifyStop() throws Exception ;	
	
}
