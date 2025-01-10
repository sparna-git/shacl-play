package fr.sparna.rdf.shacl.shaclplay.jsonld;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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

import fr.sparna.rdf.shacl.jsonld.JsonLdContextGenerator;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;

@Controller
public class JsonLdController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;

	@Autowired
	protected ShapesCatalogService catalogService;
	
	@RequestMapping(
			value = {"context"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView jsonLdContextFromUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {
		try {
			log.debug("jsonLdContextFromUrl(shapesUrl='"+shapesUrl+"')");		
			
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			doContextShapes(shapesModel,response);
			
			return null;			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	
	@RequestMapping(
			value = {"context"},
			method=RequestMethod.GET
	)
	public ModelAndView context(
			HttpServletRequest request,
			HttpServletResponse response
	){
		JsonLdFormData data = new JsonLdFormData();
		
		return new ModelAndView("context-form", JsonLdFormData.KEY, data);	
	}
	
	/*
	@RequestMapping(
			value="/context",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView context(
			
			// radio box indicating type of shapes
			@RequestParam(value="shapesSource", required=true) String shapesSourceString,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			@RequestParam(value="inputShapeUrl", required=false) String shapesUrl,
			//@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			@RequestParam(value="inputShapeFile", required=false) List<MultipartFile> shapesFiles,
			// inline Shapes if shapeSource=sourceShape-inputShapeInline
			@RequestParam(value="inputShapeInline", required=false) String shapesText,
			// reference to Shapes Catalog ID if shapeSource=sourceShape-inputShapeCatalog
	) public getNodeShapes 
	*/
	
	@RequestMapping(
			value="/context",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView context(
			// radio box indicating type of shapes
			@RequestParam(value="shapesSource", required=true) String shapesSourceString,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			@RequestParam(value="inputShapeUrl", required=false) String shapesUrl,
			//@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			@RequestParam(value="inputShapeFile", required=false) List<MultipartFile> shapesFiles,
			// inline Shapes if shapeSource=sourceShape-inputShapeInline
			@RequestParam(value="inputShapeInline", required=false) String shapesText,
			// reference to Shapes Catalog ID if shapeSource=sourceShape-inputShapeCatalog
			@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			
			HttpServletRequest request,
			HttpServletResponse response			
	) throws Exception {
		try {
			
			// get the source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// if source is a ULR, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/context?url="+URLEncoder.encode(shapesUrl, "UTF-8"));
			} else {
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
				//modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
				log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
				
				doContextShapes(shapesModel,response);
			}
			
			return null;
						
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	private Model doContextShapes(
			Model shapesModel,
			HttpServletResponse response
	) throws IOException {
		
		
		JsonLdContextGenerator contextGenerator = new JsonLdContextGenerator();
		String context = contextGenerator.generateJsonLdContext(shapesModel);
		
		response.setContentType("application/ld+json");
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().write(context.getBytes(Charset.forName("UTF-8")));
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
		JsonLdFormData data = new JsonLdFormData();
		data.setErrorMessage(Encode.forHtml(message));

		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("context-form", JsonLdFormData.KEY, data);
	}
	
}
