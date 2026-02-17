package fr.sparna.rdf.shacl.shaclplay.rules;

import java.net.URLEncoder;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalogService;
import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxRules;
import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShape;
import fr.sparna.rdf.shacl.shaclplay.rules.model.BoxShapeTarget;


@Controller
@SessionAttributes("shapesModel")
public class RulesDisplayController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	protected RulesFormData RulesServices;
	
	@Autowired
	protected RulesCatalogService catalogService;
	
	
	@RequestMapping(
			value = {"rules"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		RulesFormData data = new RulesFormData();
		
		RulesCatalog catalog = this.catalogService.getRulesCatalog();
		data.setCatalog(catalog);
		
		return new ModelAndView("rules-form", RulesFormData.KEY, data);	
	}
	
	@RequestMapping(
			value = {"rules"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView rulesUrl(
			@RequestParam(value="url", required=true) String rulesUrl,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("rulesUrl(rulesUrl='"+rulesUrl+"')");		
			
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getRulesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, rulesUrl);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			BoxRulesReader p = new BoxRulesReader();
			BoxRules box = p.read(shapesModel);
			
			return new ModelAndView("display-rules", BoxRules.class.getSimpleName(), box);
			
			
		
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
			
			// if source is a URL, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/rules?url="+URLEncoder.encode(shapesUrl, "UTF-8"));
			} else if (shapesSource == SOURCE_TYPE.CATALOG) {
				AbstractCatalogEntry entry = this.catalogService.getRulesCatalog().getCatalogEntryById(shapesCatalogId);
				return new ModelAndView("redirect:/rules?url="+URLEncoder.encode(entry.getTurtleDownloadUrl().toString(), "UTF-8"));
			}
			
			
			// initialize shapes first
			log.debug("Determining Shapes source...");
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getRulesCatalog());
			modelPopulator.populateModel(
					shapesModel,
					shapesSource,
					shapesUrl,
					shapesText,
					shapesFiles,
					shapesCatalogId
			);
			
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			BoxRulesReader p = new BoxRulesReader();
			BoxRules box = p.read(shapesModel);
						
			return new ModelAndView("display-rules", BoxRules.class.getSimpleName(), box);
			
				
			
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
		RulesFormData vfd = new RulesFormData();
		vfd.setErrorMessage(Encode.forHtml(message));
		
		RulesCatalog catalog = this.catalogService.getRulesCatalog();
		vfd.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("rules-form", RulesFormData.KEY, vfd);
	}
	
	
}
