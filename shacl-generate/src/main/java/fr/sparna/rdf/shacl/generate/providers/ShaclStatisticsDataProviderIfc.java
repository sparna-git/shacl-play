package fr.sparna.rdf.shacl.generate.providers;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.jena.rdf.model.RDFNode;

public interface ShaclStatisticsDataProviderIfc {

	/**
	 * Counts the total number of triples in the dataset.
	 * 
	 * @return
	 */
	public int countTriples();
	
	/**
	 * Count the number of instances of this class in the dataset,
	 * or returns a negative integer if this is not implemented.
	 * 
	 * @param classUri
	 * @return
	 */
	public int countInstances(String classUri);
	
	/**
	 * Counts the number of statements on the instances of the given class with the given property,
	 * or returns a negative integer if this is not implemented.
	 * 
	 * @param subjectClassUri
	 * @param propertyUri
	 * @return
	 */
	public int countStatements(String subjectClassUri, String propertyPath);

	/**
	 * Counts the number of distinct objects of the given property on the given subject class
	 * or returns a negative integer if this is not implemented.
	 * 
	 * @param subjectClassUri
	 * @param propertyUri
	 * @return
	 */
	public int countDistinctObjects(String subjectClassUri, String propertyPath);
	
	/**
	 * Counts the number of statements on the instances of the given class with the given property,
	 * and the given datatypes, or return a negative integer if this is not implemented.
	 * 
	 * @param subjectClassUri
	 * @param propertyUri
	 * @param datatypes
	 * @return
	 */
	public int countStatementsWithDatatypes(String subjectClassUri, String propertyUri, List<String> datatypes);

	/**
	 * Counts the number of statements on the instances of the given class with the given property,
	 * where the object has one of the given classes, or return a negative integer if this is not implemented.
	 * 
	 * @param subjectClassUri
	 * @param propertyUri
	 * @param objectClassUris
	 * @return
	 */
	public int countStatementsWithObjectClasses(String subjectClassUri, String propertyUri, List<String> objectClassUris);
	
	/**
	 * Count the number of values of the given property on the given class, limiting to X values
	 * 
	 * @param subjectClassUri
	 * @param propertyPath
	 * @return a map containing at most "limit" items
	 */
	public Map<RDFNode, Integer> countValues(String subjectClassUri, String propertyPath, int limit);
	
	public void registerMessageListener(Consumer<String> listener);
	
}
