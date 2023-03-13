package fr.sparna.rdf.shacl.generate.sparnatural;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;

public class SparnaturalShaclTransformer {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);
	
	private ShaclGeneratorDataProviderIfc dataProvider;
	private Model shacl;
	
	public SparnaturalShaclTransformer(
			ShaclGeneratorDataProviderIfc dataProvider,
			Model shacl
	) {
		this.shacl = shacl;
		this.dataProvider = dataProvider;
	}

	
	public SparnaturalShaclTransformer splitIRIOrLiteral() {
		List<Resource> propertyShapesIriOrLiteral = this.shacl.listSubjectsWithProperty(SHACLM.path).toList();
		propertyShapesIriOrLiteral.removeIf(ps -> !shacl.contains(ps, SHACLM.nodeKind, SHACLM.IRIOrLiteral));
		
		for (Resource psIriOrLiteral : propertyShapesIriOrLiteral) {
			// delete its nodeKind and set it to sh:IRI
			psIriOrLiteral.removeAll(SHACLM.nodeKind);
			psIriOrLiteral.addProperty(SHACLM.nodeKind, SHACLM.IRI);
			
			// create a copy of it with nodeKind Literal
			Resource psCopy = shacl.createResource(psIriOrLiteral.getURI()+"_literal");
			// copy path
			psCopy.addProperty(SHACLM.path, psIriOrLiteral.getRequiredProperty(SHACLM.path).getObject());
			// set sh:nodeKind to sh:Literal
			psIriOrLiteral.addProperty(SHACLM.nodeKind, SHACLM.Literal);
			// set name if present
			if(psIriOrLiteral.hasProperty(SHACLM.name)) {
				psIriOrLiteral.listProperties(SHACLM.name).forEach(s -> psCopy.addProperty(SHACLM.name, s.getString()+" (literal)", s.getObject().asLiteral().getLanguage()));
			}
			
			// TODO : compute the datatype of new property shape and the sh:class of the original one
			
			// attach it to original NodeShape
			shacl.listResourcesWithProperty(SHACLM.property, psIriOrLiteral).forEach(r -> {
				r.addProperty(SHACLM.property, psCopy);
			});
		}
		
		return this;
	}
	
}
