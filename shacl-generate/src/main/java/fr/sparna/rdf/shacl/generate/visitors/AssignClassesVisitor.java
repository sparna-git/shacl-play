package fr.sparna.rdf.shacl.generate.visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ModelProcessorIfc;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;

public class AssignClassesVisitor extends DatasetAwareShaclVisitorBase {
	
	private static final Logger log = LoggerFactory.getLogger(AssignClassesVisitor.class);	
	
	protected ModelProcessorIfc modelProcessor;
	
	public AssignClassesVisitor(ShaclGeneratorDataProviderIfc dataProvider, ModelProcessorIfc modelProcessor) {
		super(dataProvider);
		this.modelProcessor = modelProcessor;
	}


	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		log.debug(this.getClass()+" visiting property shape "+aPropertyShape);
		Statement nodeKind = aPropertyShape.getProperty(SHACLM.nodeKind);
		if (nodeKind != null && nodeKind.getObject().equals(SHACLM.IRI)) {
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
					"type '{}' and property '{}' is considered an 'rdfs:Resource'.",
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
			shacl.add(propertyShape, SHACLM.or, orInstancesList);
			return;
		}

		log.debug("  (setShaclClass) property shape '{}' gets sh:class '{}'", propertyShape.getLocalName(), classes.get(0));
		shacl.add(propertyShape, SHACLM.class_, shacl.createResource(classes.get(0)));
	}
	
	private List<String> calculateClasses(
			Resource targetClass,
			Resource path) {
		
		List<String> classes = this.dataProvider.getObjectTypes(targetClass.getURI(), path.getURI());
		// return is 0 or 1 result
		if (classes.size() <= 1) return classes;

		// try to translate lots of types to 1
		Set<String> classSet = new HashSet<>(classes);
		String translation = this.modelProcessor.getTypeTranslation(classSet);
		if (translation != null) return Collections.singletonList(translation);

		// cleanup unused types
		this.modelProcessor.getIgnoredClasses().forEach(classSet::remove);

		// we tried, return as what's left
		return new ArrayList<>(classes);
	}
	

}
