package fr.sparna.rdf.shacl.shaclplay.validate;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.apache.jena.util.FileUtils;
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

import fr.sparna.rdf.shacl.closeShapes.CloseShapes;
import fr.sparna.rdf.shacl.printer.report.SimpleCSVValidationResultWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReport;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.SessionData;
import fr.sparna.rdf.shacl.shaclplay.catalog.CatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.CatalogService;
import fr.sparna.rdf.shacl.shaclplay.catalog.ShapesCatalog;
import fr.sparna.rdf.shacl.validator.ShaclValidator;
import fr.sparna.rdf.shacl.validator.ShaclValidatorAsync;
import fr.sparna.rdf.shacl.validator.StringBufferProgressMonitor;


@Controller
public class ValidateController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected CatalogService catalogService;
	
	private enum SOURCE_TYPE {
		FILE,
		URL,
		INLINE
	}
	
	private enum SHAPE_SOURCE_TYPE {
		FILE,
		URL,
		INLINE,
		CATALOG
	}
	
	@RequestMapping(
			value = {"validate"},
			params={"url", "shapes"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			@RequestParam(value="url", required=true) String url,
			@RequestParam(value="shapes", required=true) String shapesCatalogId,
			@RequestParam(value="closeShapes", required=false) boolean closeShapes,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			// load shapes
			Model shapesModel = null;
			CatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);

			try {
				shapesModel = ControllerCommons.loadModel(entry.getTurtleDownloadUrl());
			} catch (RiotException e) {
				return handleValidateFormError(request, e.getMessage(), e);
			}
			
			// load data
			URL actualUrl = new URL(url);
			Model dataModel = ControllerCommons.loadModel(actualUrl);
			
			// recompute permalink
			String permalink = "validate?shapes="+shapesCatalogId+"&url="+url;
			if(closeShapes)  {
				permalink += "&closeShapes=true";
			}
			
			return doValidate(shapesModel, dataModel, permalink, closeShapes, request);
		} catch (Exception e) {
			e.printStackTrace();
			return handleValidateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

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
			HttpServletRequest request
	) {
		try {
			log.debug("validate(source='"+sourceString+"', shapeSourceString='"+shapesSourceString+"')");
			log.debug("closeSapes ? "+closeShapes);
			
			// get the source type
			SOURCE_TYPE source = SOURCE_TYPE.valueOf(sourceString.toUpperCase());		
			// get the shapes source type
			SHAPE_SOURCE_TYPE shapesSource = SHAPE_SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// initialize shapes first
			log.debug("Determining Shapes source...");
			Model shapesModel = null;
			switch(shapesSource) {
				case FILE: {
					// get uploaded file
					if(shapesFiles.isEmpty()) {
						return handleValidateFormError(request, "Uploaded shapes file is empty", null);
					}
					
					log.debug("Shapes are in one or more uploaded file : "+shapesFiles.stream().map(f -> f.getOriginalFilename()).collect(Collectors.joining(", ")));			
					try {
						shapesModel = ModelFactory.createDefaultModel();
						for (MultipartFile f : shapesFiles) {
							ControllerCommons.populateModel(shapesModel, f.getInputStream(), FileUtils.guessLang(f.getOriginalFilename(), "RDF/XML"));
						}
					} catch (RiotException e) {
						return handleValidateFormError(request, e.getMessage(), e);
					}

					break;
				}
				case URL: {
					log.debug("Shapes are in a URL "+shapesUrl);

					URL actualUrl = new URL(shapesUrl);
					shapesModel = ControllerCommons.loadModel(actualUrl);
					
					if(shapesModel.size() == 0) {
						return new ModelAndView("validate-form", ValidateFormData.KEY, ValidateFormData.error("No data could be fetched from "+shapesUrl+"."));
					}
					
					break;
				}
				case INLINE: {
					log.debug("Shapes are given inline ");
					
					try {
						shapesModel = ControllerCommons.loadModel(shapesText);
					} catch (RiotException e) {
						return handleValidateFormError(request, e.getMessage(), e);
					}
					
					if(shapesModel.size() == 0) {
						return new ModelAndView("validate-form", ValidateFormData.KEY, ValidateFormData.error("No data could be parsed from the input shapes text."));
					}

					break;
				}
				case CATALOG: {
					log.debug("Shapes are from the catalog, ID : "+shapesCatalogId);
					
					CatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);

					try {
						shapesModel = ControllerCommons.loadModel(entry.getTurtleDownloadUrl());
					} catch (RiotException e) {
						return handleValidateFormError(request, e.getMessage(), e);
					}
					
					if(shapesModel.size() == 0) {
						return new ModelAndView("validate-form", ValidateFormData.KEY, ValidateFormData.error("No data could be fetched from "+entry.getTurtleDownloadUrl()+"."));
					}

					break;
				}
				default: {
					return handleValidateFormError(request, "Cannot determine the source for shapes to use.", null);	
				}
			}
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			log.debug("Determining Data source...");
			Model dataModel = null;
			switch(source) {
			case FILE: {
				// get uploaded file
				if(files.isEmpty()) {
					return handleValidateFormError(request, "Uploaded file is empty", null);
				}
				
				log.debug("Data is in one or more uploaded file : "+files.stream().map(f -> f.getOriginalFilename()).collect(Collectors.joining(", ")));			
				try {
					dataModel = ModelFactory.createDefaultModel();
					for (MultipartFile f : files) {
						ControllerCommons.populateModel(dataModel, f.getInputStream(), FileUtils.guessLang(f.getOriginalFilename(), "RDF/XML"));
					}
				} catch (RiotException e) {
					return handleValidateFormError(request, e.getMessage(), e);
				}
				break;
			}
			case INLINE: {
				log.debug("Data is in an inline text");
				
				try {
					dataModel = ControllerCommons.loadModel(text);
				} catch (RiotException e) {
					return handleValidateFormError(request, e.getMessage(), e);
				}
				
				if(dataModel.size() == 0) {
					return new ModelAndView("validate-form", ValidateFormData.KEY, ValidateFormData.error("No data could be parsed from the input text."));
				}
				break;
			}
			case URL: {
				log.debug("Data is in a URL "+url);
				
				URL actualUrl = new URL(url);
				dataModel = ControllerCommons.loadModel(actualUrl);
				
				if(dataModel.size() == 0) {
					return new ModelAndView("validate-form", ValidateFormData.KEY, ValidateFormData.error("No data could be fetched from "+url+"."));
				}
				break;
			}
			default:
				return handleValidateFormError(request, "Cannot determine the source for data to validate", null);	
			}
			log.debug("Done Loading Data to validate. Model contains "+dataModel.size()+" triples");
			
			// compute permalink only if we can
			log.debug("Determining permalink...");
			String permalink = null;
			if(source == SOURCE_TYPE.URL) {
				if(
						shapesSource == SHAPE_SOURCE_TYPE.CATALOG
				) {
					permalink = "validate?shapes="+shapesCatalogId+"&url="+url;
					if(closeShapes)  {
						permalink += "&closeShapes=true";
					}
					log.debug("Permalink computed : "+permalink);
				}
			}
			if(permalink == null) {
				log.debug("No permalink can be computed.");
			}
			
			return doValidate(shapesModel, dataModel, permalink, closeShapes, request);
		} catch (Exception e) {
			e.printStackTrace();
			return handleValidateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
			// return new ModelAndView("validate-form", ValidateFormData.KEY, ValidateFormData.error(e.getClass().getName()+" : "+Encode.forHtml(e.getMessage())));
		}
	}
	
	private ModelAndView doValidate(
			Model shapesModel,
			Model dataModel,
			String permalink,
			boolean autoCloseShapes,
			HttpServletRequest request
	) throws Exception {
		
		Model actualShapesModel = shapesModel;
		if(autoCloseShapes) {
			log.debug("Auto-closing shapes");
			CloseShapes closeShapes = new CloseShapes();
			actualShapesModel = closeShapes.closeShapes(shapesModel);
		}
		
		if(dataModel.size() < applicationData.getLargeInputThreshold()) {
			// run the validation
			ShaclValidator validator = new ShaclValidator(
					actualShapesModel,
					// additionnal ontology Model
					null
			);
			validator.setProgressMonitor(new StringBufferProgressMonitor("SHACL validator"));
			
			Model results = validator.validate(dataModel);
			// results.write(new LogWriter(log), "Turtle");
			
			Model shapesTargetValidation = validator.validateShapesTargets(dataModel, results);
			
			// create a Model with the Union of the results and the shapes
			Model displayModel = validator.getShapesModel().union(results).union(shapesTargetValidation);		
			
			// log.debug(validator.getProgressMonitor().getBuffer().toString());
			
			String language = "en";
			
			// parse the shapes to data model
			ShapesGraph shapesGraph = new ShapesGraph(shapesModel);
			
			// stores results in the session to access them further when downloading, etc.
			SessionData sd = new SessionData();
			sd.setResults(results);
			sd.setShapesGraph(shapesGraph);
			sd.setValidatedData(dataModel);
			sd.store(request.getSession());
			
			// stores everything in the request/session, and forward to view
			ShapesDisplayData sdd = new ShapesDisplayData(
					displayModel,
					new HTMLRenderer(displayModel, language),
					shapesGraph,
					new ValidationReport(results, displayModel)
			);	
			sdd.setPermalink(permalink);
			
			sdd.setDataModel(dataModel);
			return new ModelAndView("validation-report", ShapesDisplayData.KEY, sdd);			
		} else {
			// run the validation asynchronously for large amount of data
			ShaclValidatorAsync validator = new ShaclValidatorAsync(
					shapesModel,
					dataModel,
					// complimentary model
					null
			);
			validator.setProgressMonitor(new StringBufferProgressMonitor("SHACL validator"));
			
			Thread thread = new Thread(validator);
			thread.start();
			request.getSession().setAttribute("validator", validator);
			return new ModelAndView("wait");
		}	
	}

	/**
	 * Download the validation report in an RDF variant or in CSV
	 * @param request
	 * @param response
	 * @param langName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/validate/report/download")
	public ModelAndView validationReport(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="lang", required=true) String langName
	) throws Exception {
		log.debug("validationReport(lang='"+langName+"')");
		if(langName.equalsIgnoreCase("CSV")) {
			SimpleCSVValidationResultWriter writer = new SimpleCSVValidationResultWriter();
			
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "inline; filename=\"validation-report.csv\"");

			SessionData sessionData = SessionData.get(request.getSession());
			writer.write(
					new ValidationReport( sessionData.getResults(), sessionData.getResults().union(sessionData.getShapesGraph().getShapesModel()) ),
					response.getOutputStream(),
					null
			);

			response.flushBuffer();
			return null;
		} else {
			// determine language
			Lang l = RDFLanguages.nameToLang(langName);
			if(l == null) {
				l = Lang.RDFXML;
			}
			// write results in response
			serialize(SessionData.get(request.getSession()).getResults(), l, "validation-report", response);
			return null;
		}
	}
	
	/**
	 * Serialize the RDF Model in the given Lang in the response
	 * @param m
	 * @param format
	 * @param response
	 * @throws IOException
	 */
	private void serialize(Model m, Lang format, String filename, HttpServletResponse response)
	throws IOException {
		log.debug("Setting response content type to "+format.getContentType().getContentType());
		response.setContentType(format.getContentType().getContentType());
		response.setHeader("Content-Disposition", "inline; filename=\""+filename+"."+format.getFileExtensions().get(0)+"\"");
		RDFDataMgr.write(response.getOutputStream(), m, format) ;		
	}

	/** 
	 * Return a piece of JSON for displaying in the waiintg screen
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/progress")
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
	@RequestMapping("/show")
	public ModelAndView show(
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {
		log.debug("show...");
		ShaclValidatorAsync validator = (ShaclValidatorAsync)request.getSession().getAttribute("validator");

		String language = "en";
		
		Model displayModel = validator.getShapesModel().union(validator.getResults());
		
		// parse the shapes to data model
		ShapesGraph shapesGraph = new ShapesGraph(validator.getShapesModel());
		
		// stores results in the session to access them further when downloading, etc.
		SessionData sd = new SessionData();
		sd.setResults(validator.getResults());
		sd.setShapesGraph(shapesGraph);
		sd.setValidatedData(validator.getDataModel());
		sd.store(request.getSession());
		
		// stores everything in the request/session, and forward to view
		ShapesDisplayData sdd = new ShapesDisplayData(
				displayModel,
				new HTMLRenderer(displayModel, language),
				shapesGraph,
				new ValidationReport(validator.getResults(), displayModel)
		);	
		
		// TODO : always a null permalink in this case
		sdd.setPermalink(null);
		
		sdd.setDataModel(validator.getDataModel());
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
