package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VOID;

import fr.sparna.rdf.shacl.doc.model.ChartDataset;
import fr.sparna.rdf.shacl.doc.model.ChartDatasetValues;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.DefaultModelProcessor;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;
import fr.sparna.rdf.shacl.generate.visitors.AssignLabelRoleVisitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignPartitionRoleVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeValueStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.CopyStatisticsToDescriptionVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;

public class DatasetDocumentationModelReader implements DatasetDocumentationReaderIfc {

	@Override
	public ShapesDocumentation readDatasetDocumentation(
			Model dataset,
			Model owlModel,
			String lang,
			boolean outExpandDiagram
	) {
		Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
		config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
		
		// get Statistic values
		SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100),dataset);		
		Model modelStatistic = doGenerateStatistic(
				dataProvider,
				config,
				// arbitrary model name for Dataset name generation
				"data",
				dataset
		);
		
		// Generated data for raport 
		return outputResult(dataset, modelStatistic);
	}

	private Model doGenerateStatistic(
			ShaclGeneratorDataProviderIfc dataProvider,
			Configuration config,
			String sourceName,
			Model datasetModel
	) {
		ShaclGenerator generator = new ShaclGenerator();
		Model shapes = generator.generateShapes(
				config,
				dataProvider);
		
		ShaclVisit shaclVisit = new ShaclVisit(shapes);
		
		// add dash:LabelRole
		shaclVisit.visit(new AssignLabelRoleVisitor());
		
		// If Ocurrencesinstances Check is True, building the ComputeStatisticsVisitor 
		Model countModel = ModelFactory.createDefaultModel();
		shaclVisit.visit(new ComputeStatisticsVisitor(dataProvider, countModel, sourceName, false));
		shaclVisit.visit(new CopyStatisticsToDescriptionVisitor(countModel));
		shaclVisit.visit(new AssignPartitionRoleVisitor(dataProvider, countModel));
		
		//Save of values in class 		
		shaclVisit.visit(new ComputeValueStatisticsVisitor(dataProvider,countModel,datasetModel));
		shapes.add(countModel);
		
		
		
		return shapes;
	}
	
	private ShapesDocumentation outputResult(
			Model datasetModel, 
			Model modelStatistic
	) {


		Model outputModel = datasetModel.union(modelStatistic);		

		Model ontologyModel = ModelFactory.createDefaultModel(); // Create empty ontology model 


		// Create Document Raport html
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader(false, null);
		//send result model to documentation 
		ShapesDocumentation documentValidation = reader.readShapesDocumentation(outputModel, ontologyModel,"en",false);
		// post processing 
		postProcessing(documentValidation, modelStatistic, datasetModel);

		return documentValidation;	
	}
	
	private void postProcessing(ShapesDocumentation spDocumentation, Model Statisticts, Model datasetModel) {
		
		// for Statistic
		List<Resource> nodeDataset = Statisticts.listResourcesWithProperty(RDF.type,VOID.Dataset).toList();
		List<Statement> classpartition = new ArrayList<>();
		for (Resource r : nodeDataset) {
			if (r.hasProperty(VOID.classPartition)) {
				classpartition.addAll(r.listProperties(VOID.classPartition).toList());				
			}
		}
		
		//
		for (ShapesDocumentationSection ds: spDocumentation.getSections()) {
			// Return to Property Shape Resource
			List<ChartDataset> listChartData = new ArrayList<>();
			List<Statement> getClassProperty = conformsToExist(
																ds.getSectionId(), //SectionId
																classpartition,  // List of Statistic Partition
																VOID.propertyPartition // Constraint
																);
			if (getClassProperty.size() > 0) {
				for (PropertyShapeDocumentation dsp : ds.getPropertySections()) {
					
					// Get resource statistic to write in output html
					List<Statement> lpsp = new ArrayList<>();
					for (Statement psp : getClassProperty) {				
						Resource r = psp.getObject().asResource();
						
						String constraintName = r.getModel().shortForm(r.getPropertyResourceValue(VOID.property).asResource().getURI());
						if (
								(constraintName.equals(dsp.getPropertyUri().getLabel())) 
								|| 
								(constraintName.equals(dsp.getPropertyUri().getHref()))
							) {
							lpsp.add(psp);
						}				
					}
					
					// Write in the property Documentation the number of statistics
					if (lpsp.size() > 0) {		
						for (Statement p : lpsp) {
							
							Resource pStatistic = p.getObject().asResource();
							
							if (pStatistic.hasProperty(VOID.triples)) {
								int nOccurrences = pStatistic.getProperty(VOID.triples).getObject().asLiteral().getInt();
								dsp.setNumberOfoccurrences(Integer.valueOf(nOccurrences));
							}
							
							if (pStatistic.hasProperty(VOID.distinctObjects)) {
								int nDistinctOj = pStatistic.getProperty(VOID.distinctObjects).getObject().asLiteral().getInt();
								dsp.setValuesdistincts(Integer.valueOf(nDistinctOj));
							}
							
						  // if the property shape is flag true, get all resource statistic
							// if (pStatistic.hasProperty(SHACL_PLAY.objectPartition)) {
								// 
								ChartDataset cd = new ChartDataset();
								
								
								List<ChartDatasetValues> dataValues = new ArrayList<>();
								
								/*
								List<Statement> objPartition = pStatistic.listProperties(SHACL_PLAY.objectPartition).toList();
								Map<String, Integer> q = new java.util.HashMap<>();
								
								
								for (Statement sT : objPartition) {
									ChartDatasetValues cdValues = new ChartDatasetValues();
									Resource r = sT.getObject().asResource();
									
									String name = null;
									if (r.hasProperty(SHACL_PLAY.object)) {
										if (r.getProperty(SHACL_PLAY.object).getObject().isResource()) {
											String nameValue = r.getProperty(SHACL_PLAY.object).getObject().toString();
											int index = nameValue.lastIndexOf('/');
											name = nameValue.substring(index+1);
										} else {
											name = r.getProperty(SHACL_PLAY.object).getObject().toString();
										}
									}
									
									cdValues.setObjectName(name);
									cdValues.setValues(r.getProperty(VOID.distinctSubjects).getLiteral().getInt());
									
									dataValues.add(cdValues);
									
								}
								*/
								
								cd.setPropertyName(pStatistic.getProperty(VOID.property).getObject().getModel().shortForm(pStatistic.getProperty(VOID.property).getObject().asResource().getURI()));
								cd.setDatavalues(dataValues);
								//cd.setMapChart(q);
								// 
								listChartData.add(cd);						
							// }
						}
						
					}
					
					
				}
			}// End if for get statistic result
			
			if (listChartData.size() > 0) {
				ds.setChartDataSection(listChartData);				
			}
			
		}		
	}
	
	private List<Statement> conformsToExist(String ShapeId, List<Statement> cp, Property constraint) {
		
		List<Statement> propertiesStatistic = new ArrayList<>();
		for (Statement f : cp) {			
			Resource partition = f.getObject().asResource();
			if (partition.hasProperty(DCTerms.conformsTo)) {
				Statement node = partition.getProperty(DCTerms.conformsTo);
				
				if ((node.getPredicate().equals(DCTerms.conformsTo)) 
						&&
					(node.getModel().shortForm(node.getObject().toString()).equals(ShapeId))
					) {
					propertiesStatistic.addAll(partition.listProperties(constraint).toList());
				}
			}
		}	
		
		return propertiesStatistic;
	}
	
}
