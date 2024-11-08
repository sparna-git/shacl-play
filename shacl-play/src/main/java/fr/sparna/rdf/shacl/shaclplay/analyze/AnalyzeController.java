package fr.sparna.rdf.shacl.shaclplay.analyze;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import fr.sparna.rdf.jena.QueryExecutionServiceImpl;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc.MODE;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXmlWriter;
import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.DefaultModelProcessor;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorAsync;
import fr.sparna.rdf.shacl.generate.progress.StringBufferProgressMonitor;
import fr.sparna.rdf.shacl.generate.providers.BaseShaclStatisticsDataProvider;
import fr.sparna.rdf.shacl.generate.providers.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.providers.ShaclGeneratorDataProviderIfc;
import fr.sparna.rdf.shacl.generate.providers.ShaclStatisticsDataProviderIfc;
import fr.sparna.rdf.shacl.generate.visitors.AssignLabelRoleVisitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignValueOrInVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeValueStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.CopyStatisticsToDescriptionVisitor;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;

@Controller
public class AnalyzeController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	public static final int LARGE_DATASET_THRESHOLD = 10000;
	
	@RequestMapping(
			value = {"analyze"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView analyzeDatasetUrl(
			@RequestParam(value="url", required=true) String url,
			// Output format
			@RequestParam(value="format", required=false, defaultValue = "HTML") String format,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("analyzeDatasetUrl(url='"+url+"')");
			Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), url);
			
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
			return handleAnalyzeFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

	
	@RequestMapping(
			value = {"analyze"},
			method=RequestMethod.GET
	)
	public ModelAndView analyze(
			HttpServletRequest request,
			HttpServletResponse response
	){
		AnalyzeFormData data = new AnalyzeFormData();
		
		return new ModelAndView("analyze-form", AnalyzeFormData.KEY, data);	
	}
	
	@RequestMapping(
			value="/analyze",
			params={"source"},
			method = RequestMethod.POST
	)
	public ModelAndView analyze(
			// radio box indicating type of shapes
			@RequestParam(value="source", required=true) String sourceString,
			// reference to data URL if source=source-inputUrl
			@RequestParam(value="inputUrl", required=false) String url,
			// uploaded data if source=source-inputFile
			@RequestParam(value="inputFile", required=false) List<MultipartFile> inputFiles,
			// inline data if source=source-inputInline
			@RequestParam(value="inputInline", required=false) String text,
			// reference to a SPARQL endpoint URL if source=source-inputUrlEndpoint
			@RequestParam(value="inputUrlEndpoint", required=false) String endpoint,
			// Format of the output
			@RequestParam(value="format", required=false, defaultValue = "HTML") String format,
			// Language Option
			@RequestParam(value="language", required=false) String language,
			HttpServletRequest request,
			HttpServletResponse response			
	) {
		log.debug("analyze");
		try {

			// get the source type
			ControllerModelFactory.SOURCE_TYPE source = ControllerModelFactory.SOURCE_TYPE.valueOf(sourceString.toUpperCase());
			
			
			Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			String sourceName = null;
			
			QueryExecutionServiceImpl queryExecutionService;

			// first build the data provider, either for an endpoint or by loading a Model
			Model datasetModel = ModelFactory.createDefaultModel();
			if(source == SOURCE_TYPE.ENDPOINT) {
				log.debug("Generating shapes for endpoint "+endpoint);
				queryExecutionService = new QueryExecutionServiceImpl(endpoint);
				sourceName = ControllerModelFactory.getSourceNameForUrl(endpoint);
			} else {
				// if source is a URL, redirect to the API
				if(source == SOURCE_TYPE.URL) {
					return new ModelAndView("redirect:/analyze?url="+URLEncoder.encode(url, "UTF-8"));
				} else {
					// Load data
					log.debug("Determining dataset source...");
					ControllerModelFactory modelPopulator = new ControllerModelFactory(null);
					modelPopulator.populateModel(
							datasetModel,
							source,
							url,
							text,
							inputFiles,
							null
					);
					log.debug("Done Loading dataset. Model contains "+datasetModel.size()+" triples");

					queryExecutionService = new QueryExecutionServiceImpl(datasetModel);
					sourceName = modelPopulator.getSourceName();
				}
			}

			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(queryExecutionService);
			BaseShaclStatisticsDataProvider statisticsProvider = new BaseShaclStatisticsDataProvider(queryExecutionService);
			
			// defaults to english
			if(language == null) {
				language ="en";
			}
			
			boolean async = requiresAsyncGeneration(endpoint, datasetModel);
			
			
			// now generate the shapes
			Model shapes = doGenerateShapes(
					dataProvider,
					statisticsProvider,
					config,
					(source == SOURCE_TYPE.ENDPOINT)?endpoint:(source == SOURCE_TYPE.URL)?url:"https://dummy.dataset.uri",
					true,
					// async ?
					async,
					request
			);
			
			if(!async) {
				// generate the documentation
				ShapesDocumentationModelReader reader = new ShapesDocumentationModelReader(false, null,false);
				ShapesDocumentation sd = reader.readShapesDocumentation(
						// shapes + statistics
						shapes,
						// owl ontology
						ModelFactory.createDefaultModel(),
						// language
						language						
				);
				
				// then serialize				
				serialize(
						sd,
						format,
						language,
						sourceName,
						response						
				);
				return null;
			} else {
				// stores format and source name and language to fetch them later on 'show'
				request.getSession().setAttribute("format", format);
				request.getSession().setAttribute("sourceName", sourceName);
				request.getSession().setAttribute("language", language);
				return new ModelAndView("redirect:/analyze/wait");
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleAnalyzeFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

	private boolean requiresAsyncGeneration(String endpointUrl, Model dataset) {
		return (
				(
						endpointUrl != null
						&&
						!endpointUrl.isEmpty()
				)
				||
				( 
						dataset != null
						&&
						dataset.size() > LARGE_DATASET_THRESHOLD
				)
		);
	}
	
	private Model doGenerateShapes(
			ShaclGeneratorDataProviderIfc dataProvider,
			ShaclStatisticsDataProviderIfc statisticsProvider,
			Configuration config,
			String targetDatasetUri,
			boolean withCount,
			boolean async,
			HttpServletRequest request
	) {
		if(!async) {
			ShaclGenerator generator = new ShaclGenerator();
			generator.getExtraVisitors().add(new AssignLabelRoleVisitor());
			
			// if we requested statistics, add extra visitors
			Model countModel = ModelFactory.createDefaultModel();
			if (withCount) {

				generator.getExtraVisitors().add(new ComputeStatisticsVisitor(dataProvider, statisticsProvider, countModel, targetDatasetUri));
				AssignValueOrInVisitor yetAnotherTryOnAssigningValues = new AssignValueOrInVisitor(dataProvider);
				yetAnotherTryOnAssigningValues.setRequiresShValueInPredicate(yetAnotherTryOnAssigningValues.new StatisticsBasedRequiresShValueOrInPredicate(countModel));
				generator.getExtraVisitors().add(yetAnotherTryOnAssigningValues);
				generator.getExtraVisitors().add(new ComputeValueStatisticsVisitor(dataProvider,statisticsProvider, countModel));
				generator.getExtraVisitors().add(new CopyStatisticsToDescriptionVisitor(countModel));
			}
			
			Model shapes = generator.generateShapes(
					config,
					dataProvider);
			
			// merge the count model with shapes model, in case the count model was populated
			shapes.add(countModel);
			
			return shapes;
		} else {
			ShaclGeneratorAsync generator = new ShaclGeneratorAsync(config, dataProvider);		
			generator.getExtraVisitors().add(new AssignLabelRoleVisitor());
			
			// if we requested statistics, add extra visitors
			Model countModel = ModelFactory.createDefaultModel();
			if (withCount) {	
				generator.getExtraVisitors().add(new ComputeStatisticsVisitor(dataProvider, statisticsProvider, countModel, targetDatasetUri));
				AssignValueOrInVisitor yetAnotherTryOnAssigningValues = new AssignValueOrInVisitor(dataProvider);
				yetAnotherTryOnAssigningValues.setRequiresShValueInPredicate(yetAnotherTryOnAssigningValues.new StatisticsBasedRequiresShValueOrInPredicate(countModel));
				generator.getExtraVisitors().add(yetAnotherTryOnAssigningValues);
				generator.getExtraVisitors().add(new ComputeValueStatisticsVisitor(dataProvider,statisticsProvider, countModel));
				// true to also merge the statistics in shapes model
				generator.getExtraVisitors().add(new CopyStatisticsToDescriptionVisitor(countModel, true));
			}
			
			// to save some time during generation process :
			// generator.setSkipDatatypes(true);
			generator.setProgressMonitor(new StringBufferProgressMonitor("SHACL generator"));
			
			Thread thread = new Thread(generator);
			thread.start();
			request.getSession().setAttribute("generator", generator);
			return null;
		}	
	}

	
	protected void serialize(
			ShapesDocumentation sd,
			String format,
			String language,
			String filename,
			HttpServletResponse response
	) throws IOException {		
		
		if (format.toLowerCase().equals("html")) {
			response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
			response.setContentType("text/html");
			writer.writeDoc(
					sd,  
					language,	
					response.getOutputStream(),
					MODE.HTML	
			);
		} else if (format.toLowerCase().equals("xml")) {
			response.setHeader("Content-Disposition", "inline; filename=\""+filename+".xml\"");
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationXmlWriter();
			response.setContentType("application/xml");
			writer.writeDoc(
					sd,
					language,	
					response.getOutputStream(),
					MODE.XML	
			);
		} else if(format.toLowerCase().equals("pdf") ) {
			response.setHeader("Content-Disposition", "inline; filename=\""+filename+".pdf\"");
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
			// 1. write Documentation structure to XML
			ByteArrayOutputStream htmlBytes = new ByteArrayOutputStream();
			writer.writeDoc(
					sd,
					language,	
					response.getOutputStream(),
					MODE.XML	
			);
			
			//read file html
			String htmlCode = new String(htmlBytes.toByteArray(),"UTF-8");
			// htmlCode.replace("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">", "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
			
			// Render PDF
			response.setContentType("application/pdf");
			PdfRendererBuilder _builder = new PdfRendererBuilder();			 
			_builder.useFastMode();
			
			_builder.withHtmlContent(htmlCode,"http://shacl-play.sparna.fr/play");			
			
			_builder.toStream(response.getOutputStream());
			_builder.testMode(false);
			_builder.run();
		}
	}
	
	@RequestMapping("/analyze/wait")
	public ModelAndView generateWait(){
		return new ModelAndView("wait");
	}
	
	/** 
	 * Return a piece of JSON for displaying in the waiintg screen
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/analyze/progress")
	public void progress(
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {
		log.debug("progress...");
		ShaclGeneratorAsync generator = (ShaclGeneratorAsync)request.getSession().getAttribute("generator");
		
		StringBuffer results = new StringBuffer();
		results.append("{ "
				+ "\"finished\":"+generator.isFinished()
				+",\"percentage\":"+((StringBufferProgressMonitor)generator.getProgressMonitor()).getPercentage()
				+",\"logs\": \""+((StringBufferProgressMonitor)generator.getProgressMonitor()).pollLogs().replace("\n", "<br />")+"\""
				+"}");
		OutputStream out = response.getOutputStream();
		out.write(results.toString().getBytes());
		out.flush();
	}

	
	/**
	 * Called when the waiting screen has ended and the generation is finished.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/analyze/show")
	public ModelAndView show(
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {
		log.debug("getting analysis results...");
		
		ShaclGeneratorAsync generator = (ShaclGeneratorAsync)request.getSession().getAttribute("generator");
		Model generatedShapes = generator.getGeneratedShapes();
		
		String format = (String)request.getSession().getAttribute("format");
		String sourceName = (String)request.getSession().getAttribute("sourceName");
		String language = (String)request.getSession().getAttribute("language");
		
		// generate the documentation
		ShapesDocumentationModelReader reader = new ShapesDocumentationModelReader(false, null,false);
		ShapesDocumentation sd = reader.readShapesDocumentation(
				// shapes
				generatedShapes,
				// owl ontology
				ModelFactory.createDefaultModel(),
				// language
				language						
		);
		
		// then serialize				
		serialize(
				sd,
				format,
				language,
				sourceName,
				response						
		);
		
		return null;
	}

			
	/**
	 * Handles an error in the form (stores the message in the Model, then forward to the view).
	 * 
	 * @param request
	 * @param message
	 * @return
	 */
	protected ModelAndView handleAnalyzeFormError(
			HttpServletRequest request,
			String message,
			Exception e
	) {
		AnalyzeFormData data = new AnalyzeFormData();
		data.setErrorMessage(Encode.forHtml(message));

		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("analyze-form", AnalyzeFormData.KEY, data);
	}
	
}