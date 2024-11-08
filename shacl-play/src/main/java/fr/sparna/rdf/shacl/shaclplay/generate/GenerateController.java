package fr.sparna.rdf.shacl.shaclplay.generate;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
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

import fr.sparna.rdf.jena.QueryExecutionServiceImpl;
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
import fr.sparna.rdf.shacl.shaclplay.ControllerCommons;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;

@Controller
public class GenerateController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	public static final int LARGE_DATASET_THRESHOLD = 10000;
	
	@RequestMapping(
			value = {"generate"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView generateUrl(
			@RequestParam(value="url", required=true) String url,
			// Output format
			@RequestParam(value="format", required=false, defaultValue = "Turtle") String format,
			// compute statistics option
			@RequestParam(value="statistics", required=false) boolean computeStatistics,
			HttpServletRequest request,
			// compute statistics option
			@RequestParam(value="generateLabels", required=false, defaultValue="true") boolean generateLabels,
			HttpServletResponse response
	){
		try {
			log.debug("generateUrl(url='"+url+"')");

			Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			// load data
			URL actualUrl = new URL(url);
			Model datasetModel = ModelFactory.createDefaultModel();
			datasetModel = ControllerCommons.populateModel(datasetModel, actualUrl);
			
			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), datasetModel);
			BaseShaclStatisticsDataProvider statisticsProvider = new BaseShaclStatisticsDataProvider(new PaginatedQuery(100), datasetModel);
			// don't use rdfs:subClassOf - SHACL was just generated according to dataset structure, it is not needed
			statisticsProvider.setAssumeNoSubclassOf(true);

			Model shapes = doGenerateShapes(
					dataProvider,
					statisticsProvider,
					config,
					url,
					computeStatistics,
					// async ?
					requiresAsyncGeneration(null, datasetModel),
					generateLabels,
					request
			);		
			
			serialize(shapes, format, url, response);
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

	
	@RequestMapping(
			value = {"generate"},
			method=RequestMethod.GET
	)
	public ModelAndView generate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		GenerateFormData data = new GenerateFormData();
		
		return new ModelAndView("generate-form", GenerateFormData.KEY, data);	
	}
	
	@RequestMapping(
			value="/generate",
			params={"source"},
			method = RequestMethod.POST
	)
	public ModelAndView generate(
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
			// Format output file
			@RequestParam(value="format", required=false, defaultValue = "Turtle") String format,
			// statistics option
			@RequestParam(value="statistics", required=false) boolean computeStatistics,
			// generate labels option
			@RequestParam(value="generateLabels", required=false) boolean generateLabels,
			HttpServletRequest request,
			HttpServletResponse response			
	) {
		log.debug("generate(statistics='"+computeStatistics+"')");
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
					return new ModelAndView("redirect:/generate?url="+URLEncoder.encode(url, "UTF-8")+"&format="+format+(computeStatistics?"&statistics=true":""));
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
			
			boolean async = requiresAsyncGeneration(endpoint, datasetModel);

			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(queryExecutionService);
			BaseShaclStatisticsDataProvider statisticsProvider = new BaseShaclStatisticsDataProvider(queryExecutionService);
			// don't use rdfs:subClassOf - SHACL was just generated according to dataset structure, it is not needed
			statisticsProvider.setAssumeNoSubclassOf(true);
			
			// now generate the shapes
			Model shapes = doGenerateShapes(
					dataProvider,
					statisticsProvider,
					config,
					(source == SOURCE_TYPE.ENDPOINT)?endpoint:(source == SOURCE_TYPE.URL)?url:"https://dummy.dataset.uri",
					computeStatistics,
					// async ?
					async,
					// generate labels
					true,
					request
			);			
			
			if(!async) {
				serialize(shapes, format, sourceName, response);
				return null;
			} else {
				// stores format and source name to fetch them later on 'show'
				request.getSession().setAttribute("format", format);
				request.getSession().setAttribute("sourceName", sourceName);
				return new ModelAndView("redirect:/generate/wait");
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
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
			boolean generateLabels,
			HttpServletRequest request
	) {
		if(!async) {
			ShaclGenerator generator = new ShaclGenerator();
			generator.getExtraVisitors().add(new AssignLabelRoleVisitor());
			generator.setGenerateLabels(generateLabels);
			
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
			
			generator.setSkipDatatypes(true);
			generator.setProgressMonitor(new StringBufferProgressMonitor("SHACL generator"));
			Thread thread = new Thread(generator);
			thread.start();
			request.getSession().setAttribute("generator", generator);
			return null;
		}	
	}
	
	
	private void serialize(
			Model dataModel,
			String fileFormat,
			String sourceName,
			HttpServletResponse response
	) throws Exception {

		Lang l = RDFLanguages.nameToLang(fileFormat);
		if(l == null) {
			l = Lang.RDFXML;
		}
		
		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String filename=sourceName+"-"+"shacl"+"_"+dateString;
		
		ControllerCommons.serialize(dataModel, l, filename, response);
	}
	
	@RequestMapping("/generate/wait")
	public ModelAndView generateWait(){
		return new ModelAndView("wait");
	}
	
	/** 
	 * Return a piece of JSON for displaying in the waiintg screen
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/generate/progress")
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
	@RequestMapping("/generate/show")
	public ModelAndView show(
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {
		log.debug("getting generation results...");
		ShaclGeneratorAsync generator = (ShaclGeneratorAsync)request.getSession().getAttribute("generator");
		Model generatedShapes = generator.getGeneratedShapes();
		
		String format = (String)request.getSession().getAttribute("format");
		String sourceName = (String)request.getSession().getAttribute("sourceName");
		
		serialize(generatedShapes, format, sourceName, response);
		return null;
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
		GenerateFormData data = new GenerateFormData();
		data.setErrorMessage(Encode.forHtml(message));

		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("generate-form", GenerateFormData.KEY, data);
	}
	
}
