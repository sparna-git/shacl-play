package fr.sparna.rdf.shacl.generate;

import java.util.List;

/**
 * Defines the interface of objects that feed data to the SHACL generation algorithm
 * @author thomas
 *
 */
public interface ShaclGeneratorDataProviderIfc {

	/**
	 * Returns all types found in the data
	 * @return
	 */
	public List<String> getTypes();
	
	/**
	 * Returns all properties found on the given class
	 * @return
	 */
	public List<String> getProperties(String classUri);
	
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
	
	
	
}
