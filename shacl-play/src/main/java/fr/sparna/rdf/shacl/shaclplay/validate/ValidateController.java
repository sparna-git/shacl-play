package fr.sparna.rdf.shacl.shaclplay.validate;

import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.apache.jena.util.FileManager;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.shacl.closeShapes.CloseShapes;
import fr.sparna.rdf.shacl.printer.report.SimpleCSVValidationResultWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReport;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerCommons;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelException;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.PreventLoadingIfPresentFileManager;
import fr.sparna.rdf.shacl.shaclplay.SessionData;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;
import fr.sparna.rdf.shacl.validator.ShaclValidator;
import fr.sparna.rdf.shacl.validator.ShaclValidatorAsync;
import fr.sparna.rdf.shacl.validator.StringBufferProgressMonitor;


@Controller
public class ValidateController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected ShapesCatalogService catalogService;
	
	
	
	@RequestMapping(
			value = {"{shapes}/badge"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView badgeFromShapeId(
			@RequestParam(value="url", required=true) String url,
			@PathVariable("shapes") String shapesCatalogId,
			@RequestParam(value="closeShapes", required=false) boolean closeShapes,
			HttpServletRequest request,
			HttpServletResponse response
	){
		return this.validateFromFromShapesId(url, shapesCatalogId, "badge", closeShapes, request, response);
	}
	
	
	@RequestMapping(
			value = {"{shapes}/report"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView reportFromShapeId(
			@RequestParam(value="url", required=true) String url,
			@PathVariable("shapes") String shapesCatalogId,
			@RequestParam(value="closeShapes", required=false) boolean closeShapes,
			HttpServletRequest request,
			HttpServletResponse response
	){
		return this.validateFromFromShapesId(url, shapesCatalogId, null, closeShapes, request, response);
	}	

	@RequestMapping(
			value = {"{shapes}/validate"},
			method=RequestMethod.GET
	)
	public ModelAndView validateFromShapeId(
			@PathVariable("shapes") String shapesId,
			HttpServletRequest request,
			HttpServletResponse response
	){
		return this.validate(shapesId, request, response);
	}
	
	
	@RequestMapping(
			value = {"/badge"},
			params={"url", "shapesUrl"},
			method=RequestMethod.GET
	)
	public ModelAndView badgeFromShapeUrl(
			@RequestParam(value="url", required=true) String url,
			@RequestParam("shapesUrl") String shapesUrl,
			@RequestParam(value="closeShapes", required=false) boolean closeShapes,
			HttpServletRequest request,
			HttpServletResponse response
	){
		return this.validateFromFromShapesUrl(url, shapesUrl, "badge", closeShapes, request, response);
	}
	
	
	/**
	 * Permalink to validation report. Triggers validation synchroniously. Called by other API methods
	 */
	@RequestMapping(
			value = {"validate"},
			params={"url", "shapes"},
			method=RequestMethod.GET
	)
	public ModelAndView validateFromFromShapesId(
			@RequestParam(value="url", required=true) String url,
			@RequestParam(value="shapes", required=true) String shapesCatalogId,
			@RequestParam(value="format", required=false) String format,
			@RequestParam(value="closeShapes", required=false) boolean closeShapes,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			// load shapes
			Model shapesModel = ModelFactory.createDefaultModel();
			ShapesCatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);

			try {
				shapesModel = ControllerCommons.populateModel(shapesModel, entry.getTurtleDownloadUrl());
			} catch (RiotException e) {
				// TODO : return an API error
				return handleValidateFormError(request, e.getMessage(), e);
			}
			
			// load data
			URL actualUrl = new URL(url);
			Model dataModel = ModelFactory.createDefaultModel();
			dataModel = ControllerCommons.populateModel(dataModel, actualUrl);

			// do actual validation, _not_ asynchronously
			Model results = doValidate(
					shapesModel,
					dataModel,
					// asynchronous flag
					false,
					closeShapes,
					// createDetails
					false,
					request
			);		
			
			// if for a human client...
			if(format == null || format.equals("html")) {
				// stores results in the session to access them further when downloading, etc.
				SessionData sd = new SessionData();
				sd.setResults(results);
				sd.setShapesGraph(new ShapesGraph(shapesModel));
				sd.setValidatedData(dataModel);
				sd.store(request.getSession());
	
				// prepare and return view
				ShapesDisplayDataFactory f = new ShapesDisplayDataFactory();
				ShapesDisplayData sdd = f.newShapesDisplayData(
						dataModel,
						shapesModel,
						results,
						new PermalinkGenerator(shapesCatalogId, actualUrl, closeShapes)
				);
	
				return new ModelAndView("validation-report", ShapesDisplayData.KEY, sdd);
			} else {
				// not for a human client
				this.writeValidationReport(results, shapesModel, response, format);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return handleValidateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

	
	/**
	 * Permalink to validation report from a shapesUrl. Triggers validation synchroniously. Called by other API methods
	 */
	@RequestMapping(
			value = {"validate"},
			params={"url", "shapesUrl"},
			method=RequestMethod.GET
	)
	public ModelAndView validateFromFromShapesUrl(
			@RequestParam(value="url", required=true) String url,
			@RequestParam(value="shapesUrl", required=true) String shapesUrl,
			@RequestParam(value="format", required=false) String format,
			@RequestParam(value="closeShapes", required=false) boolean closeShapes,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			// load shapes
			Model shapesModel = ModelFactory.createDefaultModel();
			
			try {
				shapesModel = ControllerCommons.populateModel(shapesModel, new URL(shapesUrl));
			} catch (RiotException e) {
				// TODO : return an API error
				return handleValidateFormError(request, e.getMessage(), e);
			}
			
			// load data
			URL actualUrl = new URL(url);
			Model dataModel = ModelFactory.createDefaultModel();
			dataModel = ControllerCommons.populateModel(dataModel, actualUrl);

			// do actual validation, _not_ asynchronously
			Model results = doValidate(
					shapesModel,
					dataModel,
					// asynchronous flag
					false,
					closeShapes,
					// createDetails
					false,
					request
			);		
			
			// if for a human client...
			if(format == null || format.equals("html")) {
				// stores results in the session to access them further when downloading, etc.
				SessionData sd = new SessionData();
				sd.setResults(results);
				sd.setShapesGraph(new ShapesGraph(shapesModel));
				sd.setValidatedData(dataModel);
				sd.store(request.getSession());
	
				// prepare and return view
				ShapesDisplayDataFactory f = new ShapesDisplayDataFactory();
				ShapesDisplayData sdd = f.newShapesDisplayData(
						dataModel,
						shapesModel,
						results,
						new PermalinkGenerator(new URL(shapesUrl), actualUrl, closeShapes)
				);
	
				return new ModelAndView("validation-report", ShapesDisplayData.KEY, sdd);
			} else {
				// not for a human client
				this.writeValidationReport(results, shapesModel, response, format);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return handleValidateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

	
	
	/**
	 * Opens validation form with a given shape from the catalog
	 */
	@RequestMapping(
			value = {"validate"},
			params={"shapes"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			@RequestParam(value="shapes", required=true) String shapesId,
			HttpServletRequest request,
			HttpServletResponse response
	){
		ValidateFormData vfd = new ValidateFormData();
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		if(shapesId != null) {
			vfd.setSelectedShapesKey(shapesId);
		}
		
		return new ModelAndView("validate-form", ValidateFormData.KEY, vfd);	
	}
	
	/**
	 * Opens validation form
	 */
	@RequestMapping(
			value = {"validate"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		ValidateFormData vfd = new ValidateFormData();
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		return new ModelAndView("validate-form", ValidateFormData.KEY, vfd);	
	}
	
	/**
	 * Process validation form submission
	 */
	@RequestMapping(
			value="/validate",
			params={"source"},
			method = RequestMethod.POST
	)
	public ModelAndView validate(
			// radio box indicating type of input
			@RequestParam(value="source", required=true) String sourceString,
			// url of page if source=url
			@RequestParam(value="inputUrl", required=false) String url,
			// inline content if source=text
			@RequestParam(value="inputInline", required=false) String text,
			// uploaded file if source=file
			@RequestParam(value="inputFile", required=false) List<MultipartFile> files,
			// radio box indicating type of shapes
			@RequestParam(value="shapesSource", required=true) String shapesSourceString,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			@RequestParam(value="inputShapeUrl", required=false) String shapesUrl,
			// reference to Shapes Catalog ID if shapeSource=sourceShape-inputShapeCatalog
			@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			@RequestParam(value="inputShapeFile", required=false) List<MultipartFile> shapesFiles,
			// inline Shapes if shapeSource=sourceShape-inputShapeInline
			@RequestParam(value="inputShapeInline", required=false) String shapesText,
			// closeShapes option
			@RequestParam(value="closeShapes", required=false) boolean closeShapes,
			// createDetails option
			@RequestParam(value="createDetails", required=false) boolean createDetails,
			// infer option
			@RequestParam(value="infer", required=false) Boolean infer,
			HttpServletRequest request
	) {
		try {
			log.debug("validate(source='"+sourceString+"', shapeSourceString='"+shapesSourceString+"')");
			log.debug("closeSapes ? "+closeShapes+" / createDetails ? "+createDetails);
			
			// get the source type
			ControllerModelFactory.SOURCE_TYPE source = ControllerModelFactory.SOURCE_TYPE.valueOf(sourceString.toUpperCase());		
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// initialize shapes first
			log.debug("Determining Shapes source...");
			
			// Model shapesModel = ModelFactory.createDefaultModel();
			// load shapes in an OntModel to honor owl:imports
			OntModel shapesModel = ModelFactory.createOntologyModel();
			// this will prevent the attempt to fetch the import if is already in the model
			shapesModel.getDocumentManager().setFileManager(new PreventLoadingIfPresentFileManager(shapesModel));
			
			
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModel(
					shapesModel,
					shapesSource,
					shapesUrl,
					shapesText,
					shapesFiles,
					shapesCatalogId
			);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			log.debug("Determining Data source...");
			Model dataModel;
			if(infer != null && infer) {
				log.debug("Asked for inference, will use an ontology Model...");
				OntModel tempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
				modelPopulator.populateModel(
						tempModel,
						source,
						url,
						text,
						files,
						null
				);
				
				log.debug("Before inference model has "+tempModel.size()+" triples");
				if(tempModel.size() > applicationData.getValidationMaxInputSizeWithInference()) {
					throw new ControllerModelException("Input file is too large ("+tempModel.size()+" triples). Validation with inference is limited to "+applicationData.getValidationMaxInputSizeWithInference()+" triples");
				}
				
				dataModel = ModelFactory.createDefaultModel();
				dataModel.add(tempModel);
			} else {
				dataModel = ModelFactory.createDefaultModel();
				modelPopulator.populateModel(
						dataModel,
						source,
						url,
						text,
						files,
						null
				);
			}
			
			log.debug("Done Loading Data to validate. Model contains "+dataModel.size()+" triples");
			
			// compute permalink only if we can
			log.debug("Determining permalink...");
			PermalinkGenerator pGenerator = null;
			if(source == ControllerModelFactory.SOURCE_TYPE.URL) {
				if(shapesSource == ControllerModelFactory.SOURCE_TYPE.CATALOG) {
					pGenerator = new PermalinkGenerator(shapesCatalogId, new URL(url), closeShapes);
				} else if(shapesSource == ControllerModelFactory.SOURCE_TYPE.URL) {
					pGenerator = new PermalinkGenerator(new URL(shapesUrl), new URL(url), closeShapes);
				}
			}
			if(pGenerator == null) {
				log.debug("No permalink can be computed.");
			} else {
				log.debug("Permalink computed : "+pGenerator.generatePermalink());
			}
			
			// trigger validation
			if(dataModel.size() < applicationData.getLargeInputThreshold()) {
				Model results = doValidate(shapesModel, dataModel, false, closeShapes, createDetails, request);		
				
				// stores results in the session to access them further when downloading, etc.
				SessionData sd = new SessionData();
				sd.setResults(results);
				sd.setShapesGraph(new ShapesGraph(shapesModel));
				sd.setValidatedData(dataModel);
				sd.store(request.getSession());
	
				// prepare and return view
				ShapesDisplayDataFactory f = new ShapesDisplayDataFactory();
				ShapesDisplayData sdd = f.newShapesDisplayData(
						dataModel,
						shapesModel,
						results,
						pGenerator
				);
	
				return new ModelAndView("validation-report", ShapesDisplayData.KEY, sdd);
			} else {
				doValidate(shapesModel, dataModel, true, closeShapes, createDetails, request);
				return new ModelAndView("redirect:/validate/wait");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleValidateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	private Model doValidate(
			Model shapesModel,
			Model dataModel,
			boolean async,
			boolean autoCloseShapes,
			boolean createDetails,
			HttpServletRequest request
	) throws Exception {
		
		if(dataModel.size() > applicationData.getValidationMaxInputSize()) {
			throw new ControllerModelException("Input file is too large ("+dataModel.size()+" triples). Online validation form is limited to "+applicationData.getValidationMaxInputSize()+" triples");
		}
		
		Model actualShapesModel = shapesModel;
		if(autoCloseShapes) {
			log.debug("Auto-closing shapes");
			CloseShapes closeShapes = new CloseShapes();
			actualShapesModel = closeShapes.closeShapes(shapesModel);
		}
		
		if(
				!async		
		) {
			// run the validation
			ShaclValidator validator = new ShaclValidator(
					actualShapesModel,
					// additionnal ontology Model
					null
			);
			validator.setProgressMonitor(new StringBufferProgressMonitor("SHACL validator"));
			validator.setValidateShapesTargets(true);
			validator.setCreateDetails(createDetails);
			
			Model results = validator.validate(dataModel);
			// results.write(new LogWriter(log), "Turtle");
			
			return results;
		} else {
			// run the validation asynchronously for large amount of data
			ShaclValidatorAsync validator = new ShaclValidatorAsync(
					shapesModel,
					dataModel,
					// complimentary model
					null
			);
			validator.setProgressMonitor(new StringBufferProgressMonitor("SHACL validator"));
			validator.setValidateShapesTargets(true);
			validator.setCreateDetails(createDetails);
			
			Thread thread = new Thread(validator);
			thread.start();
			request.getSession().setAttribute("validator", validator);
			return null;
		}	
	}

	/**
	 * Download the validation report in an RDF variant or in CSV
	 * @param request
	 * @param response
	 * @param format
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/validate/report/download")
	public ModelAndView validationReport(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="format", required=true) String format
	) throws Exception {
		log.debug("validationReport(format='"+format+"')");
		
		SessionData sessionData = SessionData.get(request.getSession());
		Model results = sessionData.getResults();
		Model shapesModel = sessionData.getShapesGraph().getShapesModel();
		
		writeValidationReport(results, shapesModel, response, format);
		return null;
	}
	
	private void writeValidationReport(
		Model results,
		Model shapesModel,
		HttpServletResponse response,
		String format
	) throws Exception {
		if(format.equalsIgnoreCase("csv")) {
			SimpleCSVValidationResultWriter writer = new SimpleCSVValidationResultWriter();
			
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "inline; filename=\"validation-report.csv\"");

			writer.write(
					new ValidationReport( results, results.union(shapesModel)),
					response.getOutputStream(),
					null
			);

			response.flushBuffer();

		} else if (format.equalsIgnoreCase("badge")) {
			response.addHeader("Content-Encoding", "UTF-8");	
			response.setContentType("application/json");
			ShieldsIoOutputFactory f = new ShieldsIoOutputFactory(results);
			ShieldsIoOutput soOutput = f.build();
			ControllerCommons.writeJson(soOutput, response.getWriter());
		} else {
			// determine language
			Lang l = RDFLanguages.nameToLang(format);
			if(l == null) {
				l = Lang.RDFXML;
			}
			// write results in response
			ControllerCommons.serialize(results, l, "validation-report", response);
		}
	}

	@RequestMapping("/validate/wait")
	public ModelAndView validateWait(){
		return new ModelAndView("wait");
	}
	
	/** 
	 * Return a piece of JSON for displaying in the waiintg screen
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/validate/progress")
	public void progress(
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {
		log.debug("progress...");
		ShaclValidatorAsync validator = (ShaclValidatorAsync)request.getSession().getAttribute("validator");
		
		StringBuffer results = new StringBuffer();
		results.append("{ "
				+ "\"finished\":"+validator.isFinished()
				+",\"percentage\":"+((StringBufferProgressMonitor)validator.getProgressMonitor()).getPercentage()
				+",\"logs\": \""+((StringBufferProgressMonitor)validator.getProgressMonitor()).pollLogs().replace("\n", "<br />")+"\""
				+"}");
		OutputStream out = response.getOutputStream();
		out.write(results.toString().getBytes());
		out.flush();
	}
	
	/**
	 * Called when the waiting screen has ended and the validation is finished, to display the result.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/validate/show")
	public ModelAndView show(
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {
		log.debug("showing results...");
		ShaclValidatorAsync validator = (ShaclValidatorAsync)request.getSession().getAttribute("validator");

		ShapesDisplayDataFactory f = new ShapesDisplayDataFactory();
		ShapesDisplayData sdd = f.newShapesDisplayData(
				validator.getDataModel(),
				validator.getShapesModel(),
				validator.getResults(),
				// TODO : always a null permalink in this case
				null
		);
				
		// stores results in the session to access them further when downloading, etc.
		SessionData sd = new SessionData();
		sd.setResults(validator.getResults());
		sd.setShapesGraph(new ShapesGraph(validator.getShapesModel()));
		sd.setValidatedData(validator.getDataModel());
		sd.store(request.getSession());
		
		log.debug("Ended showing results.");

		return new ModelAndView("validation-report", ShapesDisplayData.KEY, sdd);
	}
	
	/**
	 * Handles an error in the validation form (stores the message in the Model, then forward to the view).
	 * 
	 * @param request
	 * @param message
	 * @return
	 */
	protected ModelAndView handleValidateFormError(
			HttpServletRequest request,
			String message,
			Exception e
	) {
		ValidateFormData vfd = new ValidateFormData();
		vfd.setErrorMessage(Encode.forHtml(message));
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("validate-form", ValidateFormData.KEY, vfd);
	}
	
}
