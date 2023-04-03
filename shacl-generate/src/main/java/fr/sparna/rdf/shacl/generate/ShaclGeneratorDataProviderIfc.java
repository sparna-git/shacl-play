package fr.sparna.rdf.shacl.generate;

import java.util.List;
import java.util.function.Consumer;

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
	public int countStatements(String subjectClassUri, String propertyUri);
	
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
	
	/**
	 * Returns the number of values if the given property for the given class has less than the provided number of values
	 * @param classUri
	 * @param propertyUri
	 * @param limit
	 * @return
	 */
	public int hasLessThanValues(String classUri, String propertyUri, int limit);
	
	public void registerMessageListener(Consumer<String> listener);
	
}
