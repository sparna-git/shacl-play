package fr.sparna.rdf.shacl.shaclplay.jsonSchema;

import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.JSONArray;
import org.json.JSONObject;
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

import fr.sparna.jsonschema.JsonSchemaGenerator;
import fr.sparna.jsonschema.model.Schema;
import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.jena.shacl.ShapesGraph;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;


@Controller
public class JsonSchemaController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;

	@Autowired
	protected ShapesCatalogService catalogService;
	
	
	@RequestMapping(
			value = {"jsonschema"},
			method=RequestMethod.GET
	)	
	public ModelAndView schema(
			HttpServletRequest request,
			HttpServletResponse response
	){
		JsonSchemaFormData data = new JsonSchemaFormData();
		
		return new ModelAndView("jsonschema-form", JsonSchemaFormData.KEY, data);	
	}
	
	
	@RequestMapping(
			value = {"jsonschema"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView schema(
			@RequestParam(value="url", required=true) String shapesUrl,
			// URL Option
			@RequestParam(value="IdUrl", required=false) List<String> urlRoot,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
		try {
			log.debug("schema(shapesUrl='"+shapesUrl+"')");
			
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			doSchemaShapes(shapesModel,urlRoot,response);
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	@RequestMapping(
			value="rootShapes",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public void rootShapes(
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
	) throws Exception {
		
		
		//get the source type
		ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
		Model shapesModel = ModelFactory.createDefaultModel();
		ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
		modelPopulator.populateModel(
				shapesModel,
				shapesSource,
				shapesUrl,
				shapesText,
				shapesFiles,
				null //shapesCatalogId
		);
		
		//
		ShapesGraph spGraph = new ShapesGraph(shapesModel, shapesModel);
		List<NodeShape> ns = spGraph.getAllNodeShapes();	
		List<String> listOfUrisRoot = ns.stream().map(nodeShape -> nodeShape.getNodeShape().getURI()).collect(Collectors.toList());
		
		//JSON Output
		JSONArray outputJSon = new JSONArray(listOfUrisRoot);

		response.setContentType("application/schema+json");
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().write(outputJSon.toString(2).getBytes());
	}
	
	@RequestMapping(
			value="/jsonschema",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView schema(
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
		// URL Option
		@RequestParam(value="IdUrl", required=false) List<String> urlRoot,
		HttpServletRequest request,
		HttpServletResponse response
	) throws Exception {
		log.debug("jsonschema generation (shapesSource='"+shapesSourceString+"')");
		try {
			
			
			// get the source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// if source is a URL, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/jsonschema?"+"url="+URLEncoder.encode(shapesUrl, "UTF-8")+"&IdUrl="+URLEncoder.encode(String.join("",urlRoot), "UTF-8"));
			} else {
				// Model shapesModel = ModelFactory.createDefaultModel();
				// use an OntModel so that import can be resolved
				log.debug("Using an OntModel to load shapes and resolve imports");
				OntModel shapesModel = ModelFactory.createOntologyModel();
				ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
				modelPopulator.populateModel(
						shapesModel,
						shapesSource,
						null, //shapesUrl,
						shapesText,
						shapesFiles,
						null //shapesCatalogId
				);
				log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
				doSchemaShapes(shapesModel,urlRoot,response);
			}
			
			return null;
						
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	private Model doSchemaShapes(
			Model shapesModel,
			List<String> rootUri,
			HttpServletResponse response
	) throws Exception {
		
		JsonSchemaGenerator generator = new JsonSchemaGenerator("en", rootUri);
		
		// convert the shacl shapes to json schema
		Schema output = generator.convertToJsonSchema(shapesModel);
		JSONObject jsonSchemaOutput = new JSONObject(output.toString());

		response.setContentType("application/schema+json");
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().write(jsonSchemaOutput.toString(2).getBytes());
		return null;
	}
	
	/**
	 * Handles an error (stores the message in the Model, then forward to the view).
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
		JsonSchemaFormData data = new JsonSchemaFormData();
		data.setErrorMessage(Encode.forHtml(message));

		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("jsonschema-form", JsonSchemaFormData.KEY, data);
	}
	
}
