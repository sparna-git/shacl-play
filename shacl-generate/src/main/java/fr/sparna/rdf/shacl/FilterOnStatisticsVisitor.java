package fr.sparna.rdf.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGenerator;

public class FilterOnStatisticsVisitor extends AbstractFilterVisitor implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);
	
	private double thresholdPercentage = 0.1;
	
	public boolean filter(Resource aPropertyShape, Resource aNodeShape) {
		// read the count
		Statement triplesCountStatement = aPropertyShape.getProperty(VOID.triples);
		if(triplesCountStatement != null) {
			int propertyCount = triplesCountStatement.getInt();
			
			// read number of entities on the NodeShape
			Statement entitiesCountStatement = aNodeShape.getProperty(VOID.entities);
			if(entitiesCountStatement != null) {
				int entitiesCount = entitiesCountStatement.getInt();
				
				// then compare with threshold
				double usagePercentage = (propertyCount*100/entitiesCount);
				if(usagePercentage < this.thresholdPercentage) {
					log.debug("(remove) removing property shape '{}' as it is present on only {}% of entities", aPropertyShape, usagePercentage);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getMessage() {
		return "Removed rarely used property shapes, below threshold of "+this.thresholdPercentage+"%";
	}

	

}
