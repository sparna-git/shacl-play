package fr.sparna.rdf.shacl.generate.visitors;

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

import fr.sparna.rdf.shacl.generate.ModelProcessorIfc;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;

public class AssignDatatypesAndClassesToIriOrLiteralVisitor extends DatasetAwareShaclVisitorBase implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(AssignDatatypesAndClassesToIriOrLiteralVisitor.class);
	
	private Model model;
	private ModelProcessorIfc modelProcessor;

	
	public AssignDatatypesAndClassesToIriOrLiteralVisitor(ShaclGeneratorDataProviderIfc dataProvider, ModelProcessorIfc modelProcessor) {
		super(dataProvider);
		this.modelProcessor = modelProcessor;
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
		if(
				nodeKind == null
				||
				nodeKind.getObject().equals(SHACLM.IRIOrLiteral)
				||
				nodeKind.getObject().equals(SHACLM.BlankNodeOrLiteral)
		) {
			log.debug("Property shape "+aPropertyShape.getURI()+" has nodeKind IRIOrLiteral - splitting");
			
			Resource targetClass = aNodeShape.getRequiredProperty(SHACLM.targetClass).getResource();
			Resource path = aPropertyShape.getRequiredProperty(SHACLM.path).getResource();
			
			// create the literal shape
			Resource literalShape = this.model.createResource(aPropertyShape.getURI() + "_literal");
			literalShape.addProperty(SHACLM.nodeKind, SHACLM.Literal);
			
			// assign the datatypes to the literal shapes
			AssignDatatypesVisitor adv = new AssignDatatypesVisitor(this.dataProvider);
			adv.setShaclDatatype(
					this.model,
					targetClass,
					path,
					literalShape
			);
			
			// create the IRI shape
			Resource resourceShape = this.model.createResource(aPropertyShape.getURI() + "_resource");
			if(nodeKind.getObject().equals(SHACLM.IRIOrLiteral)) {
				resourceShape.addProperty(SHACLM.nodeKind, SHACLM.IRI);
			} else if(nodeKind.getObject().equals(SHACLM.BlankNodeOrLiteral)) {
				resourceShape.addProperty(SHACLM.nodeKind, SHACLM.BlankNode);
			} else {
				resourceShape.addProperty(SHACLM.nodeKind, SHACLM.BlankNodeOrIRI);
			}
			
			// assign the classes to the resource shape
			AssignClassesVisitor acv = new AssignClassesVisitor(this.dataProvider, this.modelProcessor);
			acv.setShaclClass(
					this.model,
					targetClass,
					path,
					literalShape
			);
			
			// OR the literal and resource shape, and link to shape
			RDFList literalOrIriList = this.model.createList(Arrays.asList(new Resource[] {literalShape, resourceShape}).iterator());
			aPropertyShape.addProperty(SHACLM.or, literalOrIriList);
		}
	}

}
