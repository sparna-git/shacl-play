package fr.sparna.rdf.shacl.generate.visitors;

import java.util.Arrays;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ModelProcessorIfc;
import fr.sparna.rdf.shacl.generate.providers.ShaclGeneratorDataProviderIfc;

public class AssignDatatypesAndClassesToIriOrLiteralVisitor extends DatasetAwareShaclVisitorBase implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(AssignDatatypesAndClassesToIriOrLiteralVisitor.class);
	
	private Model model;
	private AssignDatatypesVisitor assignDatatypesVisitor;
	private AssignClassesVisitor assignClassesVisitor;

	
	public AssignDatatypesAndClassesToIriOrLiteralVisitor(ShaclGeneratorDataProviderIfc dataProvider, ModelProcessorIfc modelProcessor) {
		super(dataProvider);
		// create now as classes visitor has a cache of co-occuring classes and class subsets
		this.assignDatatypesVisitor = new AssignDatatypesVisitor(dataProvider);
		this.assignClassesVisitor = new AssignClassesVisitor(dataProvider, modelProcessor);
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
				// null nodeKind indicate the property can be literal, IRI or blank node
				nodeKind == null
				||
				nodeKind.getObject().equals(SHACLM.IRIOrLiteral)
				||
				nodeKind.getObject().equals(SHACLM.BlankNodeOrLiteral)
		) {
			log.debug("Property shape "+aPropertyShape.getURI()+" has nodeKind IRIOrLiteral or BlankNodeOrLiteral, or no nodeKind - splitting");
			
			Resource targetClass = aNodeShape.getRequiredProperty(SHACLM.targetClass).getResource();
			Resource path = aPropertyShape.getRequiredProperty(SHACLM.path).getResource();
			
			// create the literal shape
			Resource literalShape = this.model.createResource(aPropertyShape.getURI() + "_literal");
			literalShape.addProperty(SHACLM.nodeKind, SHACLM.Literal);
			
			// assign the datatypes to the literal shapes
			this.assignDatatypesVisitor.setShaclDatatype(
					this.model,
					targetClass,
					path,
					literalShape
			);
			
			// create the resource / IRI shape
			Resource resourceShape = this.model.createResource(aPropertyShape.getURI() + "_resource");
			// node kind may be null in case it was blank node or IRI or literal
			if(nodeKind != null) {
				if(nodeKind.getObject().equals(SHACLM.IRIOrLiteral)) {
					resourceShape.addProperty(SHACLM.nodeKind, SHACLM.IRI);
				} else if(nodeKind.getObject().equals(SHACLM.BlankNodeOrLiteral)) {
					resourceShape.addProperty(SHACLM.nodeKind, SHACLM.BlankNode);
				} 
			} else {
				// node kind was unset, so our new shape can be either blank nodes or IRIs
				resourceShape.addProperty(SHACLM.nodeKind, SHACLM.BlankNodeOrIRI);
			}
			
			// assign the classes to the resource shape
			this.assignClassesVisitor.setShaclClass(
					this.model,
					targetClass,
					path,
					resourceShape
			);
			
			// OR the literal and resource shape, and link to shape
			RDFList literalOrIriList = this.model.createList(Arrays.asList(new Resource[] {literalShape, resourceShape}).iterator());
			aPropertyShape.addProperty(SHACLM.or, literalOrIriList);
		}
	}

}
