package fr.sparna.rdf.shacl.shaclplay.view;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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

import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerCommons;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;


@Controller
public class ViewController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected ShapesCatalogService catalogService;
	
	private enum SHAPE_SOURCE_TYPE {
		FILE,
		URL,
		INLINE,
		CATALOG
	}

	@RequestMapping(
			value = {"view"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		ViewFormData vfd = new ViewFormData();
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		return new ModelAndView("validate-form", ViewFormData.KEY, vfd);	
	}
	
	@RequestMapping(
			value="/view",
			params={"source"},
			method = RequestMethod.POST
	)
	public ModelAndView validate(
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
			HttpServletRequest request
	) {
		try {
			log.debug("view(shapeSourceString='"+shapesSourceString+"')");
			
			// get the shapes source type
			SHAPE_SOURCE_TYPE shapesSource = SHAPE_SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// initialize shapes first
			log.debug("Determining Shapes source...");
			Model shapesModel = ModelFactory.createDefaultModel();
			switch(shapesSource) {
				case FILE: {
					// get uploaded file
					if(shapesFiles.isEmpty()) {
						return handleViewFormError(request, "Uploaded shapes file is empty", null);
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
						return handleViewFormError(request, e.getMessage(), e);
					}

					break;
				}
				case URL: {
					log.debug("Shapes are in a URL "+shapesUrl);

					URL actualUrl = new URL(shapesUrl);
					shapesModel = ControllerCommons.loadModel(shapesModel, actualUrl);
					
					if(shapesModel.size() == 0) {
						return new ModelAndView("validate-form", ViewFormData.KEY, ViewFormData.error("No data could be fetched from "+shapesUrl+"."));
					}
					
					break;
				}
				case INLINE: {
					log.debug("Shapes are given inline ");
					
					try {
						shapesModel = ControllerCommons.loadModel(shapesModel, shapesText);
					} catch (RiotException e) {
						return handleViewFormError(request, e.getMessage(), e);
					}
					
					if(shapesModel.size() == 0) {
						return new ModelAndView("validate-form", ViewFormData.KEY, ViewFormData.error("No data could be parsed from the input shapes text."));
					}

					break;
				}
				case CATALOG: {
					log.debug("Shapes are from the catalog, ID : "+shapesCatalogId);
					
					ShapesCatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);

					try {
						shapesModel = ControllerCommons.loadModel(shapesModel, entry.getTurtleDownloadUrl());
					} catch (RiotException e) {
						return handleViewFormError(request, e.getMessage(), e);
					}
					
					if(shapesModel.size() == 0) {
						return new ModelAndView("validate-form", ViewFormData.KEY, ViewFormData.error("No data could be fetched from "+entry.getTurtleDownloadUrl()+"."));
					}

					break;
				}
				default: {
					return handleViewFormError(request, "Cannot determine the source for shapes to use.", null);	
				}
			}
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
		
	/**
	 * Handles an error in the validation form (stores the message in the Model, then forward to the view).
	 * 
	 * @param request
	 * @param message
	 * @return
	 */
	protected ModelAndView handleViewFormError(
			HttpServletRequest request,
			String message,
			Exception e
	) {
		ViewFormData vfd = new ViewFormData();
		vfd.setErrorMessage(Encode.forHtml(message));
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("view-form", ViewFormData.KEY, vfd);
	}
	
}
