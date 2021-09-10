package fr.sparna.rdf.shacl.shaclplay.rules;

import java.net.URLEncoder;
import java.util.ArrayList;
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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;


@Controller
@SessionAttributes("shapesModel")
public class RulesDisplayController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	protected RulesFormData RulesServices;
	
	@Autowired
	protected RulesTodo services;
	
	@Autowired
	protected ShapesCatalogService catalogService;
	
	
	@RequestMapping(
			value = {"rules"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		RulesFormData vfd = new RulesFormData();
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		return new ModelAndView("rules-form", RulesFormData.KEY, vfd);	
	}
	
	@RequestMapping(
			value = {"rules"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView rulesUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("rulesUrl(shapesUrl='"+shapesUrl+"')");		
			
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			return null;
		
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	
	@RequestMapping(
			value="/rules",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView rules(
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
			HttpServletRequest request,
			HttpServletResponse response
	) {
		try {
			log.debug("rules(shapeSourceString='"+shapesSourceString+"')");
			
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// if source is a ULR, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/display?url="+URLEncoder.encode(shapesUrl, "UTF-8"));
			} else if (shapesSource == SOURCE_TYPE.CATALOG) {
				AbstractCatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);
				return new ModelAndView("redirect:/displaydisplay?url="+URLEncoder.encode(entry.getTurtleDownloadUrl().toString(), "UTF-8"));
			}
			
			
			// initialize shapes first
			log.debug("Determining Shapes source...");
			Model shapesModel = ModelFactory.createDefaultModel();
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
			
			//showRulesPage(shapesModel);
			
			
			ModelMap modelView = new  ModelMap();
			BoxRules box = new RulesTodo().readModel(shapesModel);
			
			return new ModelAndView("display-rules", RulesTodo.KEY, box);
			
				
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	
	@RequestMapping(
			value="/display-rules",
			method = RequestMethod.GET
	)
	public ModelAndView showRulesPage(Model shapesModel) {
		
		RulesTodo todo = new RulesTodo();
		todo.readModel(shapesModel);
		
		System.out.print(todo);
		
		return new ModelAndView("display-rules", RulesTodo.KEY, todo);
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
		RulesFormData vfd = new RulesFormData();
		vfd.setErrorMessage(Encode.forHtml(message));
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("rules-form", RulesFormData.KEY, vfd);
	}
	
	
}
