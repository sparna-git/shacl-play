package fr.sparna.rdf.shacl.generate.visitors;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.DASH;
import fr.sparna.rdf.shacl.SHACL_PLAY;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;

public class AssignPartitionRoleVisitor extends DatasetAwareShaclVisitorBase {

	private static final Logger log = LoggerFactory.getLogger(AssignPartitionRoleVisitor.class);

	final int DistinctObjectValues = 10;
	final int OccurrencesValues = 20;

	private Model modelStatistic;

	public AssignPartitionRoleVisitor(ShaclGeneratorDataProviderIfc dataProvider, Model countModel) {
		super(dataProvider);
		this.modelStatistic = countModel;
		// Add new namespace
		this.modelStatistic.setNsPrefix("shacl-play", "https://shacl-play.sparna.fr/ontology#");
		this.modelStatistic.setNsPrefix("dash", "http://datashapes.org/dash#");
	}

	@Override
	public void visitModel(Model model) {
		log.debug(this.getClass() + " visiting model.");
		// this.modelStatistic = model;
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {

		Resource rStatisticDataShape = getPropertiesPartitionStatistic(aNodeShape, aPropertyShape);
		if (rStatisticDataShape != null) {
			int card_min_value = 0;
			int card_max_value = 0;

			if (aPropertyShape.hasProperty(SHACLM.minCount)) {
				card_min_value = aPropertyShape.getProperty(SHACLM.minCount).getLiteral().getInt();
			}

			if (aPropertyShape.hasProperty(SHACLM.maxCount)) {
				card_max_value = aPropertyShape.getProperty(SHACLM.maxCount).getLiteral().getInt();
			}

			if (card_min_value == 1 && card_max_value == 1) {

				if ((rStatisticDataShape.hasProperty(VOID.distinctObjects))
						&& (rStatisticDataShape.hasProperty(VOID.triples))) {

					if ((rStatisticDataShape.getProperty(VOID.distinctObjects).getLiteral()
							.getInt() < DistinctObjectValues)
							&& (rStatisticDataShape.getProperty(VOID.triples).getLiteral()
									.getInt() < OccurrencesValues)) {

						// create property
						// TODO : il faut SHACL_PLAY.OBJECTPARTITION mais en Resource
						rStatisticDataShape.addProperty(DASH.propertyRole, rStatisticDataShape.getModel().createResource(SHACL_PLAY.VALUE_PARTITION));
					}
				}

			}

		}

	}

	public Resource getPropertiesPartitionStatistic(Resource aNodeShape, Resource property) {

		Resource propertyStatistic = null;

		// find partition
		List<Statement> partitionStatements = this.modelStatistic.listStatements(null, DCTerms.conformsTo, aNodeShape)
				.toList();

		if (partitionStatements.size() > 0) {
			Resource partition = partitionStatements.get(0).getSubject();

			// read all properties
			List<Statement> lProperties = partition.listProperties(VOID.propertyPartition).toList();
			for (Statement pp : lProperties) {
				Resource oPP = pp.getObject().asResource();

				if (oPP.getProperty(DCTerms.conformsTo).getObject().equals(property)) {
					propertyStatistic = oPP;
					break;
				}
			}
		}
		return propertyStatistic;
	}

}
