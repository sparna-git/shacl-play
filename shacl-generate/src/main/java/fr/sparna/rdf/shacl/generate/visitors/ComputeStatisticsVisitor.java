package fr.sparna.rdf.shacl.generate.visitors;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VOID;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.providers.ShaclGeneratorDataProviderIfc;
import fr.sparna.rdf.shacl.generate.providers.ShaclStatisticsDataProviderIfc;

public class ComputeStatisticsVisitor extends DatasetAwareShaclVisitorBase implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(ComputeStatisticsVisitor.class);
	
	private Model model;
	private String datasetUri;
	
	// In the case of this visitor the output model can be different from the input model
	private Model outputModel;

	private ShaclStatisticsDataProviderIfc statisticsProvider;
	
	public ComputeStatisticsVisitor(
			ShaclGeneratorDataProviderIfc dataProvider,
			ShaclStatisticsDataProviderIfc statisticsProvider,
			Model outputModel,
			String datasetUri
	) {
		super(dataProvider);
		this.statisticsProvider = statisticsProvider;
		this.datasetUri = datasetUri;
		this.outputModel = outputModel;
	}
	
	@Override
	public void visitModel(Model model) {
		this.model = model;
		
		// create the Dataset
		outputModel.add(outputModel.createResource(this.datasetUri), RDF.type, VOID.Dataset);
		
		// count the total number of triples
		int count = this.statisticsProvider.countTriples();
		if(count >= 0) {
			log.debug("(count) dataset '{}' gets void:triples '{}'", this.datasetUri, count);
			// assert number of triples on the Dataset
			outputModel.add(outputModel.createResource(this.datasetUri), VOID.triples, outputModel.createTypedLiteral(count));
		}
		
		// add void + dct namespace
		outputModel.setNsPrefix("void", VOID.NS);
		outputModel.setNsPrefix("dct", DCTerms.NS);
		outputModel.setNsPrefix("xsd", XSD.NS);
		outputModel.setNsPrefix("dcat", DCAT.NS);
		outputModel.setNsPrefix("sh", SH.NS);
	}

	@Override
	public void visitOntology(Resource ontology) {
		// link Dataset to Ontology
		outputModel.add(outputModel.createResource(this.datasetUri), SHACLM.suggestedShapesGraph, ontology);
	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {
		// link it to Dataset
		// TODO : this is not necessarily a classPartition, depending on target of shape
		String partitionUri = buildNodeShapePartitionUri(this.datasetUri,aNodeShape,this.model);
		outputModel.add(outputModel.createResource(this.datasetUri), VOID.classPartition, model.createResource(partitionUri));
		
		// link class partition to NodeShape
		outputModel.add(outputModel.createResource(partitionUri), DCTerms.conformsTo, aNodeShape);		
		
		// count number of instances
		// TODO : this requires to interpret the target of the Shape
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
			// TODO : not necessarily a void:class predicate
			outputModel.add(outputModel.createResource(partitionUri), VOID._class, target);
			
			int count = this.statisticsProvider.countInstances(target.getURI());
			if(count >= 0) {
				log.debug("(count) node shape '{}' gets void:entities '{}'", aNodeShape.getURI(), count);
				// assert number of triples
				outputModel.add(outputModel.createResource(partitionUri), VOID.entities, model.createTypedLiteral(count));
			}
		} else {
			log.warn("Found node shape without sh:targetClass '{}', cannot compute statistics", aNodeShape.getURI());
		}

	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		// get corresponding class + property partition
		Resource classPartition = outputModel.createResource(buildNodeShapePartitionUri(this.datasetUri,aNodeShape,this.model));
		Resource propertyPartition = outputModel.createResource(buildPropertyShapePartitionUri(this.datasetUri,aNodeShape,aPropertyShape,this.model));
		
		// assert void:property on the property partition
		outputModel.add(propertyPartition, VOID.property, aPropertyShape.getRequiredProperty(SHACLM.path).getObject());
		
		// link property partition to class partition 
		outputModel.add(classPartition, VOID.propertyPartition, propertyPartition);
		
		// link property partition to PropertyShape
		outputModel.add(propertyPartition, DCTerms.conformsTo, aPropertyShape);

		// false to not use prefixes in the generated query
		String propertyPath = ModelRenderingUtils.renderSparqlPropertyPath(aPropertyShape.getRequiredProperty(SHACLM.path).getObject().asResource(), false);
		
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

			// count number of triples
			int count = this.statisticsProvider.countStatements(
					target.getURI(),
					propertyPath
			);
			
			// assert void:triples
			log.debug("(count) property shape '{}' gets void:triples '{}'", aPropertyShape.getURI(), count);
			outputModel.add(propertyPartition, VOID.triples, model.createTypedLiteral(count));
			
			// count number of distinct objects
			int countDistinctObjects = this.statisticsProvider.countDistinctObjects(
				target.getURI(),
					propertyPath
			);
			
			// assert void:distinctObjects
			log.debug("(count) property shape '{}' gets void:distinctObjects '{}'", aPropertyShape.getURI(), countDistinctObjects);
			outputModel.add(propertyPartition, VOID.distinctObjects, model.createTypedLiteral(countDistinctObjects));
		}
		else {
			log.warn("Cannot count occurrences and distinct values inside node shape without targetClass '{}', for property shape '{}'", aNodeShape.getURI(), aPropertyShape.getURI());
		}
	}

	@Override
	public void leaveModel(Model model) {
		
	}
	
	protected boolean nodeShapeHasOtherPropertyShapeWithSamePath(Resource aPropertyShape, Resource aNodeShape) {
		// read sh:path
		String path = aPropertyShape.getRequiredProperty(SHACLM.path).getObject().asResource().getURI();
		
		// lookup property shapes with same path
		List<RDFNode> otherPropertyShapesWithSamePath = aNodeShape.listProperties(SHACLM.property)
			.filterDrop(statement -> {return statement.getObject().equals(aPropertyShape);})
			.toList().stream().map(statement -> statement.getObject()).collect(Collectors.toList());
		
		return otherPropertyShapesWithSamePath.size() > 0;
	}


	private static String buildPropertyShapePartitionUri(String datasetUri, Resource nodeShape, Resource propertyShape, Model shacl) {
		// concat shape to partition URI
		String partitionUri;
		try {
			partitionUri = datasetUri+"/"+URLEncoder.encode(nodeShape.getURI(), "UTF-8")+URLEncoder.encode(propertyShape.getURI(), "UTF-8");
			return partitionUri;
		} catch (UnsupportedEncodingException e) {
			// should never happen
			e.printStackTrace();
			return null;
		}		
	}

	private static String buildNodeShapePartitionUri(String datasetUri, Resource nodeShape, Model shacl) {
		// concat shape to partition URI
		String partitionUri;
		try {
			partitionUri = datasetUri+"/"+URLEncoder.encode(nodeShape.getURI(), "UTF-8");
			return partitionUri;
		} catch (UnsupportedEncodingException e) {
			// should never happen
			e.printStackTrace();
			return null;
		}		
	}


}
