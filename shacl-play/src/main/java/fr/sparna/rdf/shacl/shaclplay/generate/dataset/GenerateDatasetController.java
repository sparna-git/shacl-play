package fr.sparna.rdf.shacl.shaclplay.generate.dataset;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.CopyStatisticsToDescriptionVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerCommons;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;

@Controller
public class GenerateDatasetController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	
	@RequestMapping(
			value = {"generate-dataset"},
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
			
			Model shapes = doGenerateShapes(
					dataProvider,
					config,
					shapesUrl					
			);		
			
			serialize(shapes, format,response);
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

	
	@RequestMapping(
			value = {"generate-dataset"},
			method=RequestMethod.GET
	)
	public ModelAndView generateDataset(
			HttpServletRequest request,
			HttpServletResponse response
	){
		GenerateDatasetFormData data = new GenerateDatasetFormData();
		
		return new ModelAndView("generate-form-dataset", GenerateDatasetFormData.KEY, data);	
	}
	
	@RequestMapping(
			value="/generate-dataset",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView generateDataset(
			// radio box indicating type of shapes
			@RequestParam(value="shapesSource", required=true) String shapesSourceString,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			@RequestParam(value="inputShapeUrl", required=false) String shapesUrl,
			//@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			@RequestParam(value="inputShapeFile", required=false) List<MultipartFile> shapesFiles,
			// inline Shapes if shapeSource=sourceShape-inputShapeInline
			//@RequestParam(value="inputShapeInline", required=false) String shapesText,
			
			// Format output file
			@RequestParam(value="format", required=false, defaultValue = "HTML") String format,
			HttpServletRequest request,
			HttpServletResponse response			
	) {
		try {

			// get the source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			
			// if source is a ULR, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/generateDatasetUrl?url="+URLEncoder.encode(shapesUrl, "UTF-8")+"&format="+format);
			} else {
				
				// initialize shapes first
				log.debug("Determining Shapes source...");
				Model shapesModel = ModelFactory.createDefaultModel();
				ControllerModelFactory modelPopulator = new ControllerModelFactory(null);
				modelPopulator.populateModel(
						shapesModel,
						shapesSource,
						shapesUrl,
						null,
						shapesFiles,
						null
				);
				log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");

				Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
				config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
				
				SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100),shapesModel);
				
				Model shapes = doGenerateShapes(
						dataProvider,
						config,
						modelPopulator.getSourceName()
				);	
				
				// Generated output result in html
				outputResult(shapesModel, shapes,response);
				
				
				//serialize(shapes, format,response);
				return null;
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	private void outputResult(Model shapesModel, 
							  Model shapes, 
							  //Model Statistic,
							  HttpServletResponse response) throws IOException {
		
		
		Model outputModel = shapesModel.union(shapes);		
		
		// Create model empty, this model is not used
		Model defaultModel = ModelFactory.createDefaultModel();
		ParserModel resultModel = new ParserModelReader().readMetadata(outputModel, defaultModel,"en");
		
		// Create Document Raport html
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader(false, null);
		//send result model to documentation 
		ShapesDocumentation documentValidation = reader.readShapesDocumentation(resultModel,null,"en",null,false);
		// pre processing 
		preProcessing(documentValidation, shapes);
				
		/*
		 * view html
		 */
		ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
		response.setContentType("text/html");
		// response.setContentType("application/xhtml+xml");
		writer.write(documentValidation, 
				"en", 
				response.getOutputStream(), 
				MODE.HTML,
				"dataset2html.xsl");		
	}
	
	private void preProcessing(ShapesDocumentation spDocumentation, Model Statisticts) {
		
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
			// find the SectionID in classPartitiion in conformsTo
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
						}
					}
				}
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
	
	
	private Model doGenerateShapes(
			ShaclGeneratorDataProviderIfc dataProvider,
			Configuration config,
			String sourceName
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
		shaclVisit.visit(new ComputeStatisticsVisitor(dataProvider, countModel, sourceName, true));
		shaclVisit.visit(new CopyStatisticsToDescriptionVisitor(countModel));
		shapes.add(countModel);
		
		return shapes;
	}
	
	
	private void serialize(
			Model dataModel,
			String fileFormat,
			HttpServletResponse response
	) throws Exception {

		Lang l = RDFLanguages.nameToLang(fileFormat);
		if(l == null) {
			l = Lang.RDFXML;
		}
		
		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String filename="shacl"+"_"+dateString;
		
		ControllerCommons.serialize(dataModel, l, filename, response);
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
