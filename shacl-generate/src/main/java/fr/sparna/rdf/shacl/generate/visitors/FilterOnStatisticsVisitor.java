package fr.sparna.rdf.shacl.generate.visitors;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGenerator;

public class FilterOnStatisticsVisitor extends AbstractFilterVisitor implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.0####", DecimalFormatSymbols.getInstance( Locale.ENGLISH ));
	
	private double thresholdPercentage = 0.1;
	
	private Model statisticsModel;
	
	public FilterOnStatisticsVisitor(Model statisticsModel) {
		super();
		this.statisticsModel = statisticsModel;
	}

	// filter node shapes that have an entity count == 0
	public boolean filterNodeShape(Resource aNodeShape) {
		// read number of entities on the NodeShape	
		Resource classPartition = findPartitionCorrespondingToShape(aNodeShape);
		Integer entitiesCount = getCount(classPartition);
		if(entitiesCount != null && entitiesCount == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean filterPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		// read the count on a partition that is dcterms:conformsTo
		Resource propertyPartition = findPartitionCorrespondingToShape(aPropertyShape);
		if(propertyPartition != null) {
			Integer propertyCount = getCount(propertyPartition);
			if(propertyCount != null) {
				// read number of entities on the NodeShape	
				Resource classPartition = findPartitionCorrespondingToShape(aNodeShape);
				Integer entitiesCount = getCount(classPartition);
				if(entitiesCount != null) {
					// avoid division by 0
					if(entitiesCount != 0) {
						// then compare with threshold
						double usagePercentage = (propertyCount*100/entitiesCount);
						if(usagePercentage < this.thresholdPercentage) {
							log.debug("(remove) removing property shape '{}' as it is present on only {}% of entities", aPropertyShape, DECIMAL_FORMAT.format(usagePercentage));
							return true;
						}
					}						
				}
			}
		}

		return false;
	}
	
	private Resource findPartitionCorrespondingToShape(Resource nodeOrPropertyShape) {
		List<Resource> resources = statisticsModel.listSubjectsWithProperty(DCTerms.conformsTo, nodeOrPropertyShape).toList();
		if(resources.size() > 0) {
			if(resources.size() > 1) {
				log.warn("More that one dct:conformsTo was found to link a partition to shape "+nodeOrPropertyShape.getURI());
			}
			return resources.get(0);
		}
		return null;
	}

	/**
	 * Returns either the entities count associated to a class partition, or the triple count associated to a property partition
	 * 
	 * @param partition the class or property partition
	 * @return the count, or null if not found
	 */
	private Integer getCount(Resource partition) {
		Statement entitiesCountStatement = partition.getProperty(VOID.entities);
		if(entitiesCountStatement != null) {
			return entitiesCountStatement.getInt();
		} else {
			// try with triple count
			Statement triplesCountStatement = partition.getProperty(VOID.triples);
			if(triplesCountStatement != null) {
				return triplesCountStatement.getInt();
			} else {
				// nothing found
				return null;
			}
		}
	}

	@Override
	public String getMessage() {
		return "Removed rarely used property shapes, below threshold of "+this.thresholdPercentage+"%";
	}

	

}
