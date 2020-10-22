package fr.sparna.rdf.shacl.shaclplay.validate;

import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
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
import fr.sparna.rdf.shacl.shaclplay.ControllerCommons;
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
				shapesModel = ControllerCommons.loadModel(shapesModel, entry.getTurtleDownloadUrl());
			} catch (RiotException e) {
				// TODO : return an API error
				return handleValidateFormError(request, e.getMessage(), e);
			}
			
			// load data
			URL actualUrl = new URL(url);
			Model dataModel = ModelFactory.createDefaultModel();
			dataModel = ControllerCommons.loadModel(dataModel, actualUrl);
			
			// recompute permalink

			
			// not for a human client
			Model results = doValidate(shapesModel, dataModel, false, closeShapes, request);		
			
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
						new PermalinkGenerator(shapesCatalogId, url, closeShapes)
				);
	
				return new ModelAndView("validation-report", ShapesDisplayData.KEY, sdd);
			} else {
				this.writeValidationReport(results, shapesModel, response, format);
				return null;
			}
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
			Model shapesModel = ModelFactory.createDefaultModel();
			switch(shapesSource) {
				case FILE: {
					// get uploaded file
					if(shapesFiles.isEmpty()) {
						return handleValidateFormError(request, "Uploaded shapes file is empty", null);
					}
					
					log.debug("Shapes are in one or more uploaded file : "+shapesFiles.stream().map(f -> f.getOriginalFilename()).collect(Collectors.joining(", ")));			
					try {
						for (MultipartFile f : shapesFiles) {
							
							if(f.getOriginalFilename().endsWith("zip")) {
								log.debug("Detected a zip extension");
								ControllerCommons.populateModelFromZip(shapesModel, f.getInputStream());
							} else {
								ControllerCommons.populateModel(shapesModel, f.getInputStream(), FileUtils.guessLang(f.getOriginalFilename(), "RDF/XML"));
							}
							
						}
					} catch (RiotException e) {
						return handleValidateFormError(request, e.getMessage(), e);
					}

					break;
				}
				case URL: {
					log.debug("Shapes are in a URL "+shapesUrl);

					URL actualUrl = new URL(shapesUrl);
					shapesModel = ControllerCommons.loadModel(shapesModel, actualUrl);
					
					if(shapesModel.size() == 0) {
						return new ModelAndView("validate-form", ValidateFormData.KEY, ValidateFormData.error("No data could be fetched from "+shapesUrl+"."));
					}
					
					break;
				}
				case INLINE: {
					log.debug("Shapes are given inline ");
					
					try {
						shapesModel = ControllerCommons.loadModel(shapesModel, shapesText);
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
					
					ShapesCatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);

					try {
						shapesModel = ControllerCommons.loadModel(shapesModel, entry.getTurtleDownloadUrl());
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
			Model dataModel = ModelFactory.createDefaultModel();
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
						if(f.getOriginalFilename().endsWith("zip")) {
							log.debug("Detected a zip extension");
							ControllerCommons.populateModelFromZip(dataModel, f.getInputStream());
						} else {
							ControllerCommons.populateModel(dataModel, f.getInputStream(), FileUtils.guessLang(f.getOriginalFilename(), "RDF/XML"));
						}
						
					}
				} catch (RiotException e) {
					return handleValidateFormError(request, e.getMessage(), e);
				}
				break;
			}
			case INLINE: {
				log.debug("Data is in an inline text");
				
				try {
					dataModel = ControllerCommons.loadModel(dataModel, text);
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
				dataModel = ControllerCommons.loadModel(dataModel, actualUrl);
				
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
			PermalinkGenerator pGenerator = null;
			if(source == SOURCE_TYPE.URL) {
				if(
						shapesSource == SHAPE_SOURCE_TYPE.CATALOG
				) {
					pGenerator = new PermalinkGenerator(shapesCatalogId, url, closeShapes);
					log.debug("Permalink computed : "+pGenerator.generatePermalink());
				}
			}
			if(pGenerator == null) {
				log.debug("No permalink can be computed.");
			}
			
			// trigger validation
			if(dataModel.size() < applicationData.getLargeInputThreshold()) {
				Model results = doValidate(shapesModel, dataModel, false, closeShapes, request);		
				
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
				doValidate(shapesModel, dataModel, true, closeShapes, request);
				return new ModelAndView("wait");
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
			HttpServletRequest request
	) throws Exception {
		
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
			
			Thread thread = new Thread(validator);
			thread.start();
			request.getSession().setAttribute("validator", validator);
			return null;
			// return new ModelAndView("wait");
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

		} else if (format.equalsIgnoreCase("shields.io")) {
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
