package fr.sparna.rdf.shacl.generate.visitors;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.providers.ShaclGeneratorDataProviderIfc;

public class AssignDatatypesVisitor extends DatasetAwareShaclVisitorBase {
	
	private static final Logger log = LoggerFactory.getLogger(AssignDatatypesVisitor.class);
	
	public AssignDatatypesVisitor(ShaclGeneratorDataProviderIfc dataProvider) {
		super(dataProvider);
	}


	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		log.debug(this.getClass()+" visiting property shape "+aPropertyShape);
		Statement nodeKind = aPropertyShape.getProperty(SHACLM.nodeKind);
		if (nodeKind != null && nodeKind.getObject().equals(SHACLM.Literal)) {
			// TODO : targets and path may be different
			this.setShaclDatatype(
					aPropertyShape.getModel(),
					aNodeShape.getRequiredProperty(SHACLM.targetClass).getResource(),
					aPropertyShape.getRequiredProperty(SHACLM.path).getResource(),
					aPropertyShape
			);
		} 
	}
	
	// make that public so that we can reuse this logic
	public void setShaclDatatype(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
	) {
		List<String> datatypes = this.dataProvider.getDatatypes(targetClass.getURI(), path.getURI());
		
		if(datatypes.size() > 1) {
			log.warn(datatypes.size()+" datatypes found for property '{}' in class '{}'", path.getURI(), targetClass.getURI());
			
			// add sh:or list to property shape:
			//    first create RDF list and then add it to property shape		
			List<Resource> orInstances = datatypes.stream().map(datatype -> {
				Resource orInstance = ResourceFactory.createResource(propertyShape.getURI() + "_datatype_" + datatypes.indexOf(datatype));
				shacl.add(orInstance, SHACLM.datatype, shacl.createResource(datatype));				
				return orInstance;
			}).collect(Collectors.toList());
			
			
			RDFList orInstancesList = shacl.createList(orInstances.iterator());
			shacl.add(propertyShape, SHACLM.or, orInstancesList);
		} else {
			log.debug("  (setShaclDatatype) property shape '{}' gets sh:datatype '{}'", propertyShape.getLocalName(), datatypes.get(0));
			shacl.add(propertyShape, SHACLM.datatype, shacl.createResource(datatypes.get(0)));
			
			if (RDF.langString.getURI().equals(datatypes.get(0))) {
		      this.setLanguageIn(shacl, targetClass, path, propertyShape);
		    }
		}
		
	}
	
	public void setLanguageIn(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape) {
		List<String> languages = this.dataProvider.getLanguages(targetClass.getURI(), path.getURI());

		List<Literal> languagesAsLiterals = languages.stream().map(s -> shacl.createLiteral(s)).collect(Collectors.toList());
		RDFList languagesList = shacl.createList(languagesAsLiterals.iterator());

		log.debug("  (setLanguageIn) property shape '{}' gets sh:languageIn '{}'", propertyShape.getLocalName(), languages);
		shacl.add(propertyShape, SHACLM.languageIn, languagesList);
	}
}