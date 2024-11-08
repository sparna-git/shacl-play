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
	 * Count the number of targets of this target definition
	 * 
	 * @param targetDefinition a NodeShape target definition
	 * @return the number of targets of the provided target definition
	 */
	public int countTargets(TargetDefinitionIfc targetDefinition);

	/**
	 * Count the number of instances of this class in the dataset,
	 * or returns a negative integer if this is not implemented.
	 * 
	 * @deprecated use countTargets instead
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
	public int countStatements(TargetDefinitionIfc target, String propertyPath);

	/**
	 * Counts the number of distinct objects of the given property on the given subject class
	 * or returns a negative integer if this is not implemented.
	 * 
	 * @param subjectClassUri
	 * @param propertyUri
	 * @return
	 */
	public int countDistinctObjects(TargetDefinitionIfc target, String propertyPath);
	
	/**
	 * Counts the number of statements on the instances of the given class with the given property,
	 * and the given datatypes, or return a negative integer if this is not implemented.
	 * 
	 * @param subjectClassUri
	 * @param propertyUri
	 * @param datatypes
	 * @return
	 */
	public int countStatementsWithDatatypes(TargetDefinitionIfc target, String propertyUri, List<String> datatypes);

	/**
	 * Counts the number of statements on the instances of the given class with the given property,
	 * where the object has one of the given classes, or return a negative integer if this is not implemented.
	 * 
	 * @param subjectClassUri
	 * @param propertyUri
	 * @param objectClassUris
	 * @return
	 */
	public int countStatementsWithObjectClasses(TargetDefinitionIfc target, String propertyUri, List<String> objectClassUris);
	
	/**
	 * Count the number of values of the given property on the given class, limiting to X values
	 * 
	 * @param subjectClassUri
	 * @param propertyPath
	 * @return a map containing at most "limit" items
	 */
	public Map<RDFNode, Integer> countByValues(TargetDefinitionIfc target, String propertyPath, int limit);
	
	public void registerMessageListener(Consumer<String> listener);
	
}
