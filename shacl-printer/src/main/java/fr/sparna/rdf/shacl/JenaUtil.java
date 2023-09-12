package fr.sparna.rdf.shacl;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * A copy of Topbraid methods to remove the dependency from TopBraid
 */
public class JenaUtil {

	/**
	 * Gets all instances of a given class and its subclasses.
	 * @param cls  the class to get the instances of
	 * @return the instances
	 */
	public static Set<Resource> getAllInstances(Resource cls) {


		Model model = cls.getModel();
		Set<Resource> classes = getAllSubClasses(cls);
		classes.add(cls);
		Set<Resource> results = new HashSet<>();
		for(Resource subClass : classes) {
			StmtIterator it = model.listStatements(null, RDF.type, subClass);
			while (it.hasNext()) {
				results.add(it.next().getSubject());
			}
		}
		return results;

	}
	
	public static Set<Resource> getAllSubClasses(Resource cls) {
		return getAllTransitiveSubjects(cls, RDFS.subClassOf);
	}
	
	/**
	 * Returns a set of resources reachable from an object via one or more reversed steps with a given predicate.
	 * @param object  the object to start traversal at
	 * @param predicate  the predicate to walk
	 * @param monitor  an optional progress monitor to allow cancelation
	 * @return the reached resources
	 */
	public static Set<Resource> getAllTransitiveSubjects(Resource object, Property predicate) {
		Set<Resource> set = new HashSet<>();
		addTransitiveSubjects(set, object, predicate);
		set.remove(object);
		return set;
	}
	
	private static void addTransitiveSubjects(Set<Resource> reached, Resource object,
			Property predicate) {
		if (object != null) {
			reached.add(object);
			StmtIterator it = object.getModel().listStatements(null, predicate, object);
			try {
				while (it.hasNext()) {
					Resource subject = it.next().getSubject();
					if (!reached.contains(subject)) {
						addTransitiveSubjects(reached, subject, predicate);
					}
				}
			}
			finally {
				it.close();
			}
		}
	}
	
}
