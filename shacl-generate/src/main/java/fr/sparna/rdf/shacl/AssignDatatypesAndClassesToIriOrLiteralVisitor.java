package fr.sparna.rdf.shacl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.AssignDatatypesVisitor;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;

public class AssignDatatypesAndClassesToIriOrLiteralVisitor extends DatasetAwareShaclVisitorBase implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(AssignDatatypesAndClassesToIriOrLiteralVisitor.class);
	
	private Model model;

	
	public AssignDatatypesAndClassesToIriOrLiteralVisitor(ShaclGeneratorDataProviderIfc dataProvider) {
		super(dataProvider);
	}
	
	@Override
	public void visitModel(Model model) {
		log.debug(this.getClass()+" visiting model.");
		this.model = model;
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		log.debug(this.getClass()+" visiting property shape "+aPropertyShape);
		// read the sh:nodeKind
		Statement nodeKind = aPropertyShape.getProperty(SHACLM.nodeKind);
		if(nodeKind != null) {
			if(nodeKind.getObject().equals(SHACLM.IRIOrLiteral)) {
				log.debug("Property shape "+aPropertyShape.getURI()+" has nodeKind IRIOrLiteral - splitting");
				
				Resource targetClass = aNodeShape.getRequiredProperty(SHACLM.targetClass).getResource();
				Resource path = aPropertyShape.getRequiredProperty(SHACLM.path).getResource();
				
				List<String> datatypes = this.dataProvider.getDatatypes(targetClass.getURI(), path.getURI());
				
				// create the literal shape
				Resource literalShape = this.model.createResource(aPropertyShape.getURI() + "_literal");
				literalShape.addProperty(SHACLM.nodeKind, SHACLM.Literal);
				
				AssignDatatypesVisitor adv = new AssignDatatypesVisitor(this.dataProvider);
				adv.setShaclDatatype(
						this.model,
						targetClass,
						path,
						literalShape
				);
				
				// create the IRI shape
				Resource iriShape = this.model.createResource(aPropertyShape.getURI() + "_iri");
				iriShape.addProperty(SHACLM.nodeKind, SHACLM.IRI);
				
				// OR the literal and IRI shape, and link to shape
				RDFList literalOrIriList = this.model.createList(Arrays.asList(new Resource[] {literalShape, iriShape}).iterator());
				aPropertyShape.addProperty(SHACLM.or, literalOrIriList);
				

			}
		}
	}

}
