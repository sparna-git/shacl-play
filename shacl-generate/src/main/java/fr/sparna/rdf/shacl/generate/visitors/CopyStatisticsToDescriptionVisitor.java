package fr.sparna.rdf.shacl.generate.visitors;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGenerator;

public class CopyStatisticsToDescriptionVisitor implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(CopyStatisticsToDescriptionVisitor.class);
	
	private Model statisticsModel;
	private Model shapesModel;
	private boolean mergeStatisticsInShapesModel = false;

	
	public CopyStatisticsToDescriptionVisitor(
			Model statisticsModel
	) {
		this.statisticsModel = statisticsModel;
	}
	
	public CopyStatisticsToDescriptionVisitor(
			Model statisticsModel,
			boolean mergeStatisticsInShapesModel
	) {
		this.statisticsModel = statisticsModel;
		this.mergeStatisticsInShapesModel = mergeStatisticsInShapesModel;
	}
	
	@Override
	public void visitModel(Model model) {
		this.shapesModel = model;
	}

	@Override
	public void visitOntology(Resource ontology) {

		// find dataset
		List<Statement> datasetTypeStatement = statisticsModel.listStatements(null, RDF.type, VOID.Dataset).toList();
		
		if(datasetTypeStatement.size() > 0) {
			Resource datasetSubject = datasetTypeStatement.get(0).getSubject();
			
			// read the total number of triples
			List<Statement> triplesStatement = datasetSubject.listProperties(VOID.triples).toList();
			
			if(triplesStatement.size() > 0) {
				int triples = triplesStatement.get(0).getInt();
				
				// add triples to ontology dcterms:abstract
				ShaclGenerator.concatOnProperty(
						ontology,
						DCTerms.abstract_,
						triples+" triples in the dataset.",
						"en"
				);
			}
		}
	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {
		
		// find partition
		List<Statement> partitionStatements = statisticsModel.listStatements(null, DCTerms.conformsTo, aNodeShape).toList();
		
		if(partitionStatements.size() > 0) {
			Resource partition = partitionStatements.get(0).getSubject();
			
			// read the total number of triples
			List<Statement> entitiesStatement = partition.listProperties(VOID.entities).toList();
			
			if(entitiesStatement.size() > 0) {
				int entities = entitiesStatement.get(0).getInt();
				
				// add triples to shape
				ShaclGenerator.concatOnProperty(
						aNodeShape,
						RDFS.comment,
						entities+" instances",
						"en"
				);
			}
		}
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		// find partition
		List<Statement> partitionStatements = statisticsModel.listStatements(null, DCTerms.conformsTo, aPropertyShape).toList();
		if(partitionStatements.size() > 0) {
			Resource partition = partitionStatements.get(0).getSubject();
			
			// read the total number of triples
			List<Statement> triplesStatement = partition.listProperties(VOID.triples).toList();
			List<Statement> distinctObjectsStatement = partition.listProperties(VOID.distinctObjects).toList();
			
			
			if(triplesStatement.size() > 0 && distinctObjectsStatement.size() > 0) {
				int triples = triplesStatement.get(0).getInt();
				int distinctObjects = distinctObjectsStatement.get(0).getInt();
				
				// add triples + distinct objects to shape
				ShaclGenerator.concatOnProperty(
						aPropertyShape,
						SHACLM.description,
						triples+" occurences and "+distinctObjects+" distinct values",
						"en"
				);
			}
		}
	}

	@Override
	public void leaveModel(Model model) {
		if(this.mergeStatisticsInShapesModel) {
			model.add(this.statisticsModel);
		}
	}

	public boolean isMergeStatisticsInShapesModel() {
		return mergeStatisticsInShapesModel;
	}

	public void setMergeStatisticsInShapesModel(boolean mergeStatisticsInShapesModel) {
		this.mergeStatisticsInShapesModel = mergeStatisticsInShapesModel;
	}

}
