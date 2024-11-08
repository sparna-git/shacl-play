package fr.sparna.rdf.shacl.generate.visitors;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.providers.ShaclGeneratorDataProviderIfc;

public class AssignMinCountAndMaxCountVisitor extends DatasetAwareShaclVisitorBase {
	
	private static final Logger log = LoggerFactory.getLogger(AssignMinCountAndMaxCountVisitor.class);	
	
	public AssignMinCountAndMaxCountVisitor(ShaclGeneratorDataProviderIfc dataProvider) {
		super(dataProvider);
	}


	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		log.debug(this.getClass()+" visiting property shape "+aPropertyShape);
		
		this.setMinCount(
				aPropertyShape.getModel(),
				aNodeShape.getRequiredProperty(SHACLM.targetClass).getResource(),
				aPropertyShape.getRequiredProperty(SHACLM.path).getResource(),
				aPropertyShape
		);
		this.setMaxCount(
				aPropertyShape.getModel(),
				aNodeShape.getRequiredProperty(SHACLM.targetClass).getResource(),
				aPropertyShape.getRequiredProperty(SHACLM.path).getResource(),
				aPropertyShape
		);

	}
	
	private void setMinCount(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
			) {
		if (log.isTraceEnabled()) log.trace("(setMinCount) start");

		boolean hasInstanceWithoutProperty = this.dataProvider.hasInstanceWithoutProperty(targetClass.getURI(), path.getURI());
		if (!hasInstanceWithoutProperty) {
			log.debug("  (setMinCount) property shape '{}' gets sh:minCount '{}'", propertyShape.getLocalName(), 1);
			shacl.add(propertyShape, SHACLM.minCount, ResourceFactory.createTypedLiteral("1", XSDDatatype.XSDinteger));
		} else {
			log.debug("  (setMinCount) property shape '{}' cannot have sh:minCount", propertyShape.getLocalName());
		}
	}

	private void setMaxCount(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
			) {
		if (log.isTraceEnabled()) log.trace("(setMaxCount) start");

		boolean hasInstanceWithTwoProperties = this.dataProvider.hasInstanceWithTwoProperties(targetClass.getURI(), path.getURI());
		if (!hasInstanceWithTwoProperties) {
			log.debug("  (setMaxCount) property shape '{}' gets sh:maxCount '{}'", propertyShape.getLocalName(), 1);
			shacl.add(propertyShape, SHACLM.maxCount, ResourceFactory.createTypedLiteral("1", XSDDatatype.XSDinteger));
		} else {
			log.debug("  (setMaxCount) property shape '{}' cannot have sh:maxCount", propertyShape.getLocalName());
		}
	}
}
