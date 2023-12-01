package fr.sparna.rdf.shacl.generate;

import java.util.List;
import java.util.function.Consumer;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Defines the interface of objects that feed data to the SHACL generation algorithm
 * @author thomas
 *
 */
public interface ShaclGeneratorDataProviderIfc {

	/**
	 * Counts the total number of triples in the dataset.
	 * 
	 * @return
	 */
	public int countTriples();
	
	/**
	 * Returns all types found in the data
	 * @return
	 */
	public List<String> getTypes();
	
	/**
	 * Returns all types that co-occur with the given type
	 * @return
	 */
	public List<String> getCoOccuringTypes(String classUri);

	/**
	 * Returns true if potentialSuperset is a strict superset of classUri, that is all instances of A are also instances of B, AND there
	 * exist instances of B that are NOT instances of A
	 * @return
	 */
	public boolean isStrictSuperset(String classUri, String potentialSuperset);
	
	/**
	 * Returns true if there is NO instances that have the type classUri but not the type potentialSuperset (i.e all instances of A are also instances of B)
	 * @return
	 */
	public boolean isEquivalentOrSuperSet(String classUri, String potentialSuperset);

	/**
	 * Returns true if there are instances of the property on the class that have the type to search without the type to exclude
	 * @return
	 */
	public boolean hasObjectOfTypeWithoutOtherType(String classUri, String propertyUri, String classToSearch, String excludedClassUri);
	
	/**
	 * Count the number of instances of this class in the dataset,
	 * or returns a negative integer if this is not implemented.
	 * 
	 * @param classUri
	 * @return
	 */
	public int countInstances(String classUri);
	
	/**
	 * Returns all properties found on the given class
	 * @return
	 */
	public List<String> getProperties(String classUri);
	
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
	 * Lists X distinct values of the given property on the given class.
	 * 
	 * @param subjectClassUri
	 * @param propertyUri
	 * @return a list containing at most "limit" items
	 */
	public List<RDFNode> listDistinctValues(String subjectClassUri, String propertyUri, int limit);
	
	/**
	 * Tests if at least one instance of the class does not have a value for the property
	 * @param classUri
	 * @param propertyUri
	 * @return
	 */
	public boolean hasInstanceWithoutProperty(String classUri, String propertyUri);
	
	/**
	 * Tests if at least one instance of the class has more than one value for the property
	 * @param classUri
	 * @param propertyUri
	 * @return
	 */
	public boolean hasInstanceWithTwoProperties(String classUri, String propertyUri);
	
	/**
	 * Tests if at least one instance of the class has an IRI object for the property
	 * @param classUri
	 * @param propertyUri
	 * @return
	 */
	public boolean hasIriObject(String classUri, String propertyUri);
	
	/**
	 * Tests if at least one instance of the class has a literal object for the property
	 * @param classUri
	 * @param propertyUri
	 * @return
	 */
	public boolean hasLiteralObject(String classUri, String propertyUri);
	
	/**
	 * Tests if at least one instance of the class has a blank node object for the property
	 * @param classUri
	 * @param propertyUri
	 * @return
	 */
	public boolean hasBlankNodeObject(String classUri, String propertyUri);
	
	/**
	 * Returns all datatypes found on the object of the property for that class
	 * @param classUri
	 * @param propertyUri
	 * @return
	 */
	public List<String> getDatatypes(String classUri, String propertyUri);
	
	/**
	 * Tests if the property can have more than one value per language on the given class
	 * @param classUri
	 * @param propertyUri
	 * @return
	 */
	public boolean isNotUniqueLang(String classUri, String propertyUri);
	
	/**
	 * Returns the languages of the literal values of the property for the given class
	 * @param classUri
	 * @param propertyUri
	 * @return
	 */
	public List<String> getLanguages(String classUri, String propertyUri);
	
	/**
	 * Returns the types of the values of the given property in the given class 
	 * @param classUri
	 * @param propertyUri
	 * @return
	 */
	public List<String> getObjectTypes(String classUri, String propertyUri);
	
	/**
	 * Returns a name for the provided class or property URI
	 * 
	 * @param classOrPropertyUri
	 * @param lang
	 * @return
	 */
	public String getName(String classOrPropertyUri, String lang);
	
	public void registerMessageListener(Consumer<String> listener);
	
}
