package fr.sparna.rdf.shacl.generate.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.shared.PrefixMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.DefaultModelProcessor;
import fr.sparna.rdf.shacl.generate.ModelProcessorIfc;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.providers.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.providers.ShaclGeneratorDataProviderIfc;

public class AssignClassesVisitor extends DatasetAwareShaclVisitorBase {
	
	private static final Logger log = LoggerFactory.getLogger(AssignClassesVisitor.class);	
	
	protected ModelProcessorIfc modelProcessor;
	protected ClassCacheProvider classCache;

	public AssignClassesVisitor(ShaclGeneratorDataProviderIfc dataProvider, ModelProcessorIfc modelProcessor) {
		super(dataProvider);
		this.modelProcessor = modelProcessor;
		this.classCache = new ClassCacheProvider(this.dataProvider);
	}


	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		log.debug(this.getClass()+" visiting property shape "+aPropertyShape);
		Statement nodeKind = aPropertyShape.getProperty(SHACLM.nodeKind);
		if (nodeKind != null && (nodeKind.getObject().equals(SHACLM.IRI) || nodeKind.getObject().equals(SHACLM.BlankNode))) {
			// TODO : targets and path may be different
			this.setShaclClass(
					aPropertyShape.getModel(),
					aNodeShape.getRequiredProperty(SHACLM.targetClass).getResource(),
					aPropertyShape.getRequiredProperty(SHACLM.path).getResource(),
					aPropertyShape
			);
		} 
	}
	
	/**
	 * Assigns the sh:class constraint on the property shape
	 * 
	 * @param shacl
	 * @param targetClass
	 * @param path
	 * @param propertyShape
	 */
	public void setShaclClass(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
	) {
		List<String> classes = calculateClasses(targetClass, path);

		// always remove owl:NamedIndividual from the result
		classes.remove("http://www.w3.org/2002/07/owl#NamedIndividual");
		
		if (classes.isEmpty()) {
			String message = ShaclGenerator.getMessage(
					"type '{}' and property '{}' does not have any sh:class",
					ShaclGenerator.shortenUri(shacl, targetClass),
					ShaclGenerator.shortenUri(shacl, path)
			);
			log.warn(message);
			return;
		}


		if (classes.size() > 1) {			
			// add sh:or list to property shape:
			//    first create RDF list and then add it to property shape
			List<Resource> orInstances = classes.stream().map(aClass -> {				
			Resource orInstance = ResourceFactory.createResource(propertyShape.getURI() + "_class_" + classes.indexOf(aClass));
				shacl.add(orInstance, SHACLM.class_, shacl.createResource(aClass));				
				return orInstance;
			}).collect(Collectors.toList());			
			
			RDFList orInstancesList = shacl.createList(orInstances.iterator());
			log.debug("  (setShaclClass) property shape '{}' gets sh:or+sh:class '{}'", propertyShape.getLocalName(), classes);
			shacl.add(propertyShape, SHACLM.or, orInstancesList);
		} else {
			log.debug("  (setShaclClass) property shape '{}' gets sh:class '{}'", propertyShape.getLocalName(), classes.get(0));
			shacl.add(propertyShape, SHACLM.class_, shacl.createResource(classes.get(0)));
		}

	}
	
	private List<String> calculateClasses(
			Resource targetClass,
			Resource path
	) {
		
		List<String> classes = this.dataProvider.getObjectTypes(targetClass.getURI(), path.getURI());

		if(classes.size() > 1) {
			List<String> typesToIgnore = calculateTypesToIgnore(classes, targetClass.getModel());
			classes.removeAll(typesToIgnore);
			if(classes.size() > 1) {
				// do it a second time
				typesToIgnore = calculateTypesToIgnore(classes, targetClass.getModel());
				classes.removeAll(typesToIgnore);
			}
		}
		
		// we tried, return as what's left
		return classes;
	}
	
	private List<String> calculateTypesToIgnore(
		List<String> classes,
		PrefixMapping prefixes
	) {
		List<String> typesToIgnore = new ArrayList<String>();
		// for each possible class, e.g. Person, Agent
		for(String aClass : classes) {
			if(this.classCache.isSupersetOfAll(aClass, classes)) {
				log.debug("  (setShaclClass) '{}' is a superset of '{}'", aClass, classes);
				// it is a redundant superclass of all possible subclasses
				// then remove all subclasses from range and keep only the superclass
				if(this.classCache.isUnionOfAll(aClass, classes)) {
					log.debug("  (setShaclClass) '{}' is the union of '{}', will remove all subclasses from the list", prefixes.shortForm(aClass), classes.stream().map(s->prefixes.shortForm(s)).collect(Collectors.toList()));
					classes.stream().filter(s -> !s.equals(aClass)).forEach(s -> typesToIgnore.add(s));
				} else {
					// there could be some subset of that superset not in range
					// remove the superset from range and keep only most precise classes
					log.debug("  (setShaclClass) '{}' can contain other subclasses than '{}', will remove it and keep subclasses", prefixes.shortForm(aClass), classes.stream().map(s->prefixes.shortForm(s)).collect(Collectors.toList()));
					typesToIgnore.add(aClass);
				}
				// break as no other type can be superset of all
				break;
			}
		}
		
		return typesToIgnore;
	}
	
	
	class ClassCacheProvider {
		private ShaclGeneratorDataProviderIfc dataProviderIfc;

		private transient Map<String, List<String>> coOccurringClassesCache = new HashMap<>();
		private transient Map<String, Boolean> supersetCache = new HashMap<>();
		
		public ClassCacheProvider(ShaclGeneratorDataProviderIfc dataProviderIfc) {
			super();
			this.dataProviderIfc = dataProviderIfc;
		}
		
		/**
		 * @return the list of types co-occuring with classUri
		 */
		public List<String> getCoOccuringTypes(String classUri) {
			List<String> result = this.coOccurringClassesCache.get(classUri);
			if(result == null) {
				result = this.dataProviderIfc.getCoOccuringTypes(classUri);
				this.coOccurringClassesCache.put(classUri, result);
				return result;
			} else {
				return result;
			}
		}

		/**
		 * @return true if potentialSuperset is a strict superset of classUri, that is whenever potentialSuperset occurs, classUri always occurs,
		 * and there are no cases where classUri occurs without potentialSuperset.
		 */
		public boolean isStrictSuperset(String classUri, String potentialSuperset) {
			String key = classUri+""+potentialSuperset;
			Boolean result = this.supersetCache.get(key);
			if(result == null) {
				result = this.dataProviderIfc.isStrictSuperset(classUri, potentialSuperset);
				this.supersetCache.put(key, result);
				return result;
			} else {
				return result;
			}
		}
		
		/**
		 * @return true if for each subset class, classUri is always a superset of it
		 */
		public boolean isSupersetOfAll(String classUri, List<String> subsets) {
			for (String aSubset : subsets) {
				if(!aSubset.equals(classUri) && !this.isStrictSuperset(aSubset,classUri)) {
					return false;
				}
			}
			return true;
		}
		
		/**
		 * @return true if all co-occuring classes of classUri are in the subsets list,
		 */
		public boolean isUnionOfAll(String classUri, List<String> subsets) {
			List<String> coOccuringClasses = this.getCoOccuringTypes(classUri);
			for (String string : coOccuringClasses) {
				if(!subsets.contains(string)) {
					return false;
				}
			}
			return true;
		}

		
	}

	public static void main(String... args) throws Exception {
		final String ENDPOINT = "http://51.159.140.210/graphdb/repositories/sparnatural-demo-anf?infer=false";
		
		SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), ENDPOINT);
		Model shapes = ModelFactory.createDefaultModel();
		
		AssignClassesVisitor me = new AssignClassesVisitor(dataProvider, new DefaultModelProcessor());
		me.setShaclClass(
				shapes,
				// target class
				shapes.createResource("https://sparnatural-demo-anf.huma-num.fr/ontology#ActeNotarie"),
				// path
				shapes.createResource("https://www.ica.org/standards/RiC/ontology#hasOrHadConstituent"),
				// property shape
				shapes.createResource("http://fake.property.shape.uri")
		);
	}
	

}
