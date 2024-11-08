package fr.sparna.rdf.shacl.generate.visitors;

import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.SHACL_PLAY;
import fr.sparna.rdf.shacl.generate.providers.ShaclGeneratorDataProviderIfc;
import fr.sparna.rdf.shacl.generate.providers.ShaclStatisticsDataProviderIfc;

public class ComputeValueStatisticsVisitor extends DatasetAwareShaclVisitorBase implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(ComputeStatisticsVisitor.class);
	
	private Model statisticsModel;

	private ShaclStatisticsDataProviderIfc statisticsProvider;
	
	public ComputeValueStatisticsVisitor(
		ShaclGeneratorDataProviderIfc dataProvider,
		ShaclStatisticsDataProviderIfc statisticsProvider,
		Model countModel
	) {
		super(dataProvider);
		this.statisticsProvider = statisticsProvider;
		this.statisticsModel = countModel;
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		if(aPropertyShape.hasProperty(SHACLM.in)) {
			List<RDFNode> values = aPropertyShape.getProperty(SHACLM.in).getObject().as(RDFList.class).asJavaList();
			
			// false to not use prefixes in the generated query
			String propertyPath = ModelRenderingUtils.renderSparqlPropertyPath(aPropertyShape.getRequiredProperty(SHACLM.path).getObject().asResource(), false);
			
			// works only if targetClass is known
			if(
				aNodeShape.hasProperty(SHACLM.targetClass)
				||
				aNodeShape.hasProperty(RDF.type, RDFS.Class)
			) {
				// define target
				Resource target;
				if(aNodeShape.hasProperty(SHACLM.targetClass)) {
					target = aNodeShape.getRequiredProperty(SHACLM.targetClass).getResource();
				} else {
					target = aNodeShape;
				}

				Map<RDFNode, Integer> counts = this.statisticsProvider.countValues(
					target.getURI(),
					propertyPath,
					AssignValueOrInVisitor.DEFAULT_VALUES_THRESHOLD
				);
			
				// get corresponding property partition
				List<Resource> propertyPartitions = statisticsModel.listStatements(null, DCTerms.conformsTo, aPropertyShape).mapWith(t-> t.getSubject()).toList();
				
				if(propertyPartitions.size() == 0) {
					log.debug("Cannot find corresponding property partition for "+aPropertyShape);
				} else {
					if(propertyPartitions.size() > 1) {
						log.debug("More than one property partition found for "+aPropertyShape);
					}
					
					Resource propertyPartition = propertyPartitions.get(0);
					
					for (RDFNode aValue : values) {
						// get its count
						Integer count = counts.get(aValue);
						if(count != null) {
							Resource rAnonymous = this.statisticsModel.createResource();
							propertyPartition.addProperty(statisticsModel.createProperty(SHACL_PLAY.VALUE_PARTITION), rAnonymous);
							
							this.statisticsModel.add(rAnonymous, 
													this.statisticsModel.createProperty(SHACL_PLAY.VALUE), 
													aValue
													);
							this.statisticsModel.addLiteral(rAnonymous, 
													VOID.distinctSubjects, 
													count.intValue());
						}
					}
					
				}
			}
			

		}
	}

}
