package fr.sparna.rdf.shacl.generate.visitors;

import java.util.List;
import java.util.function.Predicate;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;

public class AssignValueOrInVisitor extends DatasetAwareShaclVisitorBase {
	
	private static final Logger log = LoggerFactory.getLogger(AssignValueOrInVisitor.class);	
	
	public static final int DEFAULT_VALUES_THRESHOLD = 3;
	
	/**
	 * The maximum number of distinct values for a given property for which sh:value or sh:in will be generated 
	 */
	private int valuesInThreshold = DEFAULT_VALUES_THRESHOLD;
	
	private Predicate<Resource> requiresShValueInPredicate;
	
	public AssignValueOrInVisitor(ShaclGeneratorDataProviderIfc dataProvider, int valuesInThreshold) {
		super(dataProvider);
		this.requiresShValueInPredicate = AssignValueOrInVisitor.getDefaultRequiresShValueInPredicate();
		this.valuesInThreshold = valuesInThreshold;
	}
	
	public AssignValueOrInVisitor(ShaclGeneratorDataProviderIfc dataProvider) {
		this(dataProvider, DEFAULT_VALUES_THRESHOLD);
	}


	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		log.debug(this.getClass()+" visiting property shape "+aPropertyShape);
		
		// if it makes sense, try to find sh:valueIn
		if(this.requiresShValueInPredicate != null && this.requiresShValueInPredicate.test(aPropertyShape)) {
			this.setInOrHasValue(
					aPropertyShape.getModel(),
					aNodeShape.getRequiredProperty(SHACLM.targetClass).getResource(),
					aPropertyShape.getRequiredProperty(SHACLM.path).getResource(),
					aPropertyShape
			);
		}

	}

	private void setInOrHasValue(
			Model shacl,
			Resource targetClass,
			Resource path,
			Resource propertyShape
	) {
		
		if (log.isTraceEnabled()) log.trace("(setInOrHasValue) start");

		List<RDFNode> distinctValues = this.dataProvider.listDistinctValues(targetClass.getURI(), path.getURI(), this.valuesInThreshold+1);
		if(distinctValues.size() <= this.valuesInThreshold) {
			log.debug("  (setInOrHasValue) found a maximum of '{}' distinct values, will set sh:in or sh:value", distinctValues.size());
			if(distinctValues.size() == 1) {
				shacl.add(propertyShape, SHACLM.hasValue, distinctValues.get(0));
			} else {
				RDFList list = shacl.createList(distinctValues.iterator());
				shacl.add(propertyShape, SHACLM.in, list);
			}
		}
		
	}

	public int getValuesInThreshold() {
		return valuesInThreshold;
	}

	public void setValuesInThreshold(int valuesInThreshold) {
		this.valuesInThreshold = valuesInThreshold;
	}
	
	public Predicate<Resource> getRequiresShValueInPredicate() {
		return requiresShValueInPredicate;
	}

	public void setRequiresShValueInPredicate(Predicate<Resource> requiresShValueInPredicate) {
		this.requiresShValueInPredicate = requiresShValueInPredicate;
	}


	public static Predicate<Resource> getDefaultRequiresShValueInPredicate() {
		return (propertyShape -> {
			return propertyShape.getProperty(SHACLM.minCount) != null
					&&
					propertyShape.getProperty(SHACLM.minCount).getObject().isLiteral()
					&&
					propertyShape.getProperty(SHACLM.minCount).getObject().asLiteral().getInt() == 1
					&&
					propertyShape.getProperty(SHACLM.maxCount) != null
					&&
					propertyShape.getProperty(SHACLM.maxCount).getObject().isLiteral()
					&&
					propertyShape.getProperty(SHACLM.maxCount).getObject().asLiteral().getInt() == 1;
		});
	}
	
}
