package fr.sparna.rdf.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;

public class ComputeStatisticsVisitor implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);
	
	private ShaclGeneratorDataProviderIfc dataProvider;
	private Model model;
	private String datasetUri;
	private boolean addToDescription = false;

	
	public ComputeStatisticsVisitor(ShaclGeneratorDataProviderIfc dataProvider, String datasetUri, boolean addToDescription) {
		this.dataProvider = dataProvider;
		this.datasetUri = datasetUri;
		this.addToDescription = addToDescription;
	}
	
	@Override
	public void visitModel(Model model) {
		this.model = model;
		
		// create the Dataset
		model.add(model.createResource(this.datasetUri), RDF.type, VOID.Dataset);
		
		// count the total number of triples
		int count = this.dataProvider.countTriples();
		if(count >= 0) {
			log.debug("(count) dataset '{}' gets void:triples '{}'", this.datasetUri, count);
			// assert number of triples on the Dataset
			model.add(model.createResource(this.datasetUri), VOID.triples, model.createTypedLiteral(count));
		}
	}

	@Override
	public void visitOntology(Resource ontology) {
		// link Dataset to Ontology
		// TODO : could be a SHACL property ?
		model.add(model.createResource(this.datasetUri), DCTerms.conformsTo, ontology);
		
		// append to description
		if(this.addToDescription) {
			ShaclGenerator.concatOnProperty(
					ontology,
					DCTerms.abstract_,
					model.createResource(this.datasetUri).getRequiredProperty(VOID.triples).getInt()+" triples in the dataset."
			);
		}
	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {
		// link it to Dataset
		// TODO : this is not necessarily a classPartition, depending on target of shape
		model.add(model.createResource(this.datasetUri), VOID.classPartition, aNodeShape);
		// TODO : not necessarily this.
		model.add(aNodeShape, VOID._class, aNodeShape.getRequiredProperty(SHACLM.targetClass).getObject());
		
		// count number of instances
		// TODO : this requires to interpret the target of the Shape
		int count = this.dataProvider.countInstances(aNodeShape.getRequiredProperty(SHACLM.targetClass).getObject().asResource().getURI());
		if(count >= 0) {
			log.debug("(count) node shape '{}' gets void:entities '{}'", aNodeShape.getURI(), count);
			// assert number of triples
			model.add(aNodeShape, VOID.entities, model.createTypedLiteral(count));
			// append to description
			if(this.addToDescription) {
				ShaclGenerator.concatOnProperty(
						aNodeShape,
						RDFS.comment,
						count+" instances"
				);
			}
		}
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		// link it to NodeShape
		model.add(aNodeShape, VOID.propertyPartition, aPropertyShape);
		model.add(aPropertyShape, VOID.property, aPropertyShape.getRequiredProperty(SHACLM.path).getObject());

		// count number of triples
		int count = this.dataProvider.countStatements(
				aNodeShape.getRequiredProperty(SHACLM.targetClass).getObject().asResource().getURI(),
				aPropertyShape.getRequiredProperty(SHACLM.path).getObject().asResource().getURI()
		);
		if(count >= 0) {
			log.debug("(count) property shape '{}' gets void:triples '{}'", aPropertyShape.getURI(), count);
			// assert number of triples
			model.add(aPropertyShape, VOID.triples, model.createTypedLiteral(count));
			// append to description
			if(this.addToDescription) {
				ShaclGenerator.concatOnProperty(
						aPropertyShape,
						SHACLM.description,
						count+" triples"
				);
			}
		}
	}

	@Override
	public void leaveModel(Model model) {
		
	}


}
