package fr.sparna.rdf.shacl.generate.visitors;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;

public class AssignNodeKindVisitor extends DatasetAwareShaclVisitorBase {
	
	private static final Logger log = LoggerFactory.getLogger(AssignNodeKindVisitor.class);	
	
	public AssignNodeKindVisitor(ShaclGeneratorDataProviderIfc dataProvider) {
		super(dataProvider);
	}


	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		log.debug(this.getClass()+" visiting property shape "+aPropertyShape);
		
		this.setNodeKind(
				aPropertyShape.getModel(),
				aNodeShape.getRequiredProperty(SHACLM.targetClass).getResource(),
				aPropertyShape.getRequiredProperty(SHACLM.path).getResource(),
				aPropertyShape
		);

	}

	/**
	 * Assigns the sh:nodeKind constraint on the property shape
	 * 
	 * @param configuration
	 * @param shacl
	 * @param targetClass
	 * @param path
	 * @param propertyShape
	 */
	private void setNodeKind(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
	) {
		if (log.isTraceEnabled()) log.trace("(setNodeKind) start");

		boolean hasIri = this.dataProvider.hasIriObject(targetClass.getURI(), path.getURI());
		boolean hasBlank = this.dataProvider.hasBlankNodeObject(targetClass.getURI(), path.getURI());
		boolean hasLiteral = this.dataProvider.hasLiteralObject(targetClass.getURI(), path.getURI());

		Resource nodeKindValue = calculateNodeKind(hasIri, hasBlank, hasLiteral);
		if (nodeKindValue != null) {
			log.debug("  (setNodeKind) property shape '{}' gets node kind '{}'", propertyShape.getLocalName(), nodeKindValue.getLocalName());
			shacl.add(propertyShape, SHACLM.nodeKind, nodeKindValue);
		}
		else {
			log.warn("  (setNodeKind) no sh:nodeKind could be derived for '{}'", propertyShape.getURI());
		}
	}
	

	/**
	 * Computes sh:nodeKind value based on flags retrieved in the data
	 * 
	 * @param hasIri
	 * @param hasBlank
	 * @param hasLiteral
	 * @return
	 */
	private Resource calculateNodeKind(boolean hasIri, boolean hasBlank, boolean hasLiteral) {
		if (hasIri && !hasBlank && !hasLiteral) return SHACLM.IRI;
		if (!hasIri && hasBlank && !hasLiteral) return SHACLM.BlankNode;
		if (!hasIri && !hasBlank && hasLiteral) return SHACLM.Literal;
		if (hasIri && hasBlank && !hasLiteral) return SHACLM.BlankNodeOrIRI;
		if (hasIri && !hasBlank && hasLiteral) return SHACLM.IRIOrLiteral;
		if (!hasIri && hasBlank && hasLiteral) return SHACLM.BlankNodeOrLiteral;
		return null;
	}
}
