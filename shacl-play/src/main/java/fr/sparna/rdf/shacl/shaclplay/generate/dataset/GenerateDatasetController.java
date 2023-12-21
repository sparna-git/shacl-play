package fr.sparna.rdf.shacl.shaclplay.generate.dataset;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VOID;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.shacl.DASH;
import fr.sparna.rdf.shacl.SHACL_PLAY;
import fr.sparna.rdf.shacl.doc.model.ChartDataset;
import fr.sparna.rdf.shacl.doc.model.ChartDatasetValues;
import fr.sparna.rdf.shacl.doc.model.ParserModel;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import fr.sparna.rdf.shacl.doc.read.ParserModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationReaderIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc.MODE;
import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.DefaultModelProcessor;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;
import fr.sparna.rdf.shacl.generate.visitors.AssignLabelRoleVisitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignPartitionRoleVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticSPARQLProperty;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.CopyStatisticsToDescriptionVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerCommons;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import io.vavr.collection.HashMap;

@Controller
public class GenerateDatasetController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	
	@RequestMapping(
			value = {"dataset-doc"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView generateDatasetUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			// Output format
			@RequestParam(value="format", required=false, defaultValue = "HTML") String format,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("generateDatasetUrl(shapesUrl='"+shapesUrl+"')");
			

			Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), shapesUrl);
			
			/*
			Model shapes = doGenerateStatistic(
					dataProvider,
					config,
					shapesUrl,
					datasetModel
			);		
			
			serialize(shapes, format,response);
			*/
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

	
	@RequestMapping(
			value = {"dataset-doc"},
			method=RequestMethod.GET
	)
	public ModelAndView generateDataset(
			HttpServletRequest request,
			HttpServletResponse response
	){
		GenerateDatasetFormData data = new GenerateDatasetFormData();
		
		return new ModelAndView("dataset-doc", GenerateDatasetFormData.KEY, data);	
	}
	
	@RequestMapping(
			value="/dataset-doc",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView generateDataset(
			// radio box indicating type of shapes
			@RequestParam(value="shapesSource", required=true) String datasetSourceString,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			@RequestParam(value="inputShapeUrl", required=false) String datasetUrl,
			//@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			@RequestParam(value="inputShapeFile", required=false) List<MultipartFile> datasetFiles,
			// inline Shapes if shapeSource=sourceShape-inputShapeInline
			//@RequestParam(value="inputShapeInline", required=false) String shapesText,
			
			// Format output file
			@RequestParam(value="format", required=false, defaultValue = "HTML") String format,
			HttpServletRequest request,
			HttpServletResponse response			
	) {
		try {

			// get the source type
			ControllerModelFactory.SOURCE_TYPE datasetSource = ControllerModelFactory.SOURCE_TYPE.valueOf(datasetSourceString.toUpperCase());
			
			
			// if source is a ULR, redirect to the API
			if(datasetSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/generateDatasetUrl?url="+URLEncoder.encode(datasetUrl, "UTF-8")+"&format="+format);
			} else {
				
				// initialize shapes first
				log.debug("Determining dataset source...");
				Model datasetModel = ModelFactory.createDefaultModel();
				ControllerModelFactory modelPopulator = new ControllerModelFactory(null);
				modelPopulator.populateModel(
						datasetModel,
						datasetSource,
						datasetUrl,
						null,
						datasetFiles,
						null
				);
				log.debug("Done Loading Shapes. Model contains "+datasetModel.size()+" triples");

				Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
				config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
				
				// get Statistic values
				SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100),datasetModel);		
				Model modelStatistic = doGenerateStatistic(
						dataProvider,
						config,
						modelPopulator.getSourceName(),
						datasetModel
				);	
				
				// Generated data for raport 
				outputResult(datasetModel, modelStatistic,response);
				
				
				return null;
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	private void outputResult(Model datasetModel, 
							  Model modelStatistic, 
							  //Model Statistic,
							  HttpServletResponse response) throws IOException {
		
			
		Model outputModel = datasetModel.union(modelStatistic);		
		
		Model defaultModel = ModelFactory.createDefaultModel(); // Create model empty, this model is not used
		ParserModel resultModel = new ParserModelReader().readMetadata(outputModel, defaultModel, "en");
		
		
		// Create Document Raport html
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader(false, null);
		//send result model to documentation 
		ShapesDocumentation documentValidation = reader.readShapesDocumentation(resultModel,null,"en",null,false);
		// pre processing 
		preProcessing(documentValidation, modelStatistic, datasetModel, resultModel);
		
		/*
		 * view html
		 */
		ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
		response.setContentType("text/html");
		// response.setContentType("application/xhtml+xml");
		writer.writeDatasetDoc(documentValidation,  //set of data
				"en",  // language default	
				response.getOutputStream(), //instance of output
				MODE.HTML // this option is update to format config
				);		
	}
	
	private void preProcessing(ShapesDocumentation spDocumentation, Model Statisticts, Model datasetModel, ParserModel parseModel) {
		
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
							if (pStatistic.hasProperty(SHACL_PLAY.objectPartition)) {
								// 
								ChartDataset cd = new ChartDataset();
								
								
								List<ChartDatasetValues> dataValues = new ArrayList<>();
								
								List<Statement> objPartition = pStatistic.listProperties(SHACL_PLAY.objectPartition).toList();
								Map<String, Integer> q = new java.util.HashMap<>();
								/*
								Map<String, Integer> q = objPartition.stream()
										.collect(Collectors.toMap
												(o -> o.getProperty(SHACL_PLAY.object).getObject().toString(), 
												 o -> o.getProperty(VOID.distinctSubjects).getLiteral().getInt()
														)
												);
								*/ 
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
								
								cd.setPropertyName(pStatistic.getProperty(VOID.property).getObject().getModel().shortForm(pStatistic.getProperty(VOID.property).getObject().asResource().getURI()));
								cd.setDatavalues(dataValues);
								//cd.setMapChart(q);
								// 
								listChartData.add(cd);						
							}
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
		shaclVisit.visit(new ComputeStatisticSPARQLProperty(dataProvider,countModel,datasetModel));
		shapes.add(countModel);
		
		
		
		return shapes;
	}
			
	/**
	 * Handles an error in the validation form (stores the message in the Model, then forward to the view).
	 * 
	 * @param request
	 * @param message
	 * @return
	 */
	protected ModelAndView handleGenerateFormError(
			HttpServletRequest request,
			String message,
			Exception e
	) {
		GenerateDatasetFormData data = new GenerateDatasetFormData();
		data.setErrorMessage(Encode.forHtml(message));

		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("generate-form-dataset", GenerateDatasetFormData.KEY, data);
	}
	
}