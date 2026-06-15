package fr.sparna.rdf.shacl.shaclplay.doc;

import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;
import fr.sparna.rdf.shacl.shaclplay.doc.service.DocService;
import fr.sparna.rdf.shacl.shaclplay.exception.DocException;
import fr.sparna.rdf.shacl.shaclplay.exception.ExceptionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;



/*
Controller pour le formulaire de génération de la documentation Shacl
 */
@Controller
public class DocController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DocController.class);

	private final ShapesCatalogService catalogService;
	private final DocService docService;
	private final DocFormData formData;


	//Injection des dépendances par Spring par le constructeur => c'est la recommendation Spring.
	@Autowired
	public DocController(ShapesCatalogService catalogService, DocService docService, DocFormData formData){
		this.catalogService = catalogService;
		this.docService = docService;
		this.formData = formData;
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(
			value = {"/doc"},
			produces = {"text/html"})
	public String validate(org.springframework.ui.Model model){
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		formData.setCatalog(catalog);
		model.addAttribute(DocFormData.KEY, formData);
		return "doc-form";
	}

	//@RequestMapping(
	//		value = {"/doc"},
	//		params={"url"},
	//		method=RequestMethod.GET
	//)
	//public ModelAndView docUrl(
	//		@RequestParam(value="url", required=true) String shapesUrl,
	//		// includeDiagram option
	//		@RequestParam(value="includeDiagram", required=false) boolean includeDiagram,
	//		// includeDiagram option
	//		@RequestParam(value="sectionDiagram", required=false, defaultValue = "true") boolean sectionDiagram,
	//		// hide Properties
	//		@RequestParam(value="hideProperties", required=false) boolean hideProperties,
	//		// List Option
	//		@RequestParam(value="format", required=false, defaultValue = "html") String format,
	//		// Logo Option
	//		@RequestParam(value="inputLogo", required=false) String urlLogo,
	//		// Language Option
	//		@RequestParam(value="language", required=false) String language,
	//		// Filter Unused NodeShape
	//		@RequestParam(value="filterUnusedNodeShapes", required=false, defaultValue="false") boolean filterUnusedNodeShapes,
	//		HttpServletRequest request,
	//		HttpServletResponse response
	//){
	//	try {
	//		log.debug("docUrl(shapesUrl='"+shapesUrl+"')");
//
	//		ShapesDocumentationWriterIfc.MODE mode = ShapesDocumentationWriterIfc.MODE.valueOf(format.toUpperCase());
	//
	//		Model shapesModel = ModelFactory.createDefaultModel();
	//		ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
	//		modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
	//		log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
	//
	//		if(language == null) {
	//			language ="en";
	//		}
	//
	//		docService.doOutputDoc(
	//				shapesModel,
	//				// true to read diagram
	//				includeDiagram,
	//				hideProperties,
	//				mode,
	//				urlLogo,
	//				modelPopulator.getSourceName(),
	//				language,
	//				sectionDiagram,
	//				filterUnusedNodeShapes,
	//				response);
	//		return null;
	//	} catch (Exception e) {
	//		e.printStackTrace();
	//		return this.handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
	//	}
	//}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(
			value = {"/doc"},
			consumes = {"multipart/form-data"},
			produces = {"text/html", "text/xml", "application/pdf"}
	)
	public ResponseEntity<ByteArrayResource> doc(
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
			// includeDiagram option
			@RequestParam(value="includeDiagram", required=false, defaultValue = "false") boolean includeDiagram,
			// includeDiagram option
			@RequestParam(value="sectionDiagram", required=false, defaultValue = "true") boolean sectionDiagram,
			// hide Properties
			@RequestParam(value="hideProperties", required=false, defaultValue="false") boolean hideProperties,
			// List Option
			@RequestParam(value="format", required=false, defaultValue = "HTML_RESPEC") String clientFormat,
			// Logo Option
			@RequestParam(value="inputLogo", required=false) String urlLogo,
			// Language Option
			@RequestParam(value="language", required=false, defaultValue = "en") String language,
			// Filter Unused NodeShape
			@RequestParam(value="filterUnusedNodeShapes", required=false, defaultValue="true") boolean filterUnusedNodeShapes,
			HttpServletRequest request
	) {
		try {
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE source = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());

            LOGGER.debug("doc(shapeSourceString='{}')", shapesSourceString);
			LOGGER.debug("REQUEST contentType={}", request.getContentType());
			LOGGER.debug("REQUEST params={}", request.getParameterMap());

			if (shapesFiles == null) LOGGER.debug("shapesFiles=null");
			else LOGGER.debug("shapesFiles.length={}", shapesFiles.size());

			Model model = ModelFactory.createDefaultModel();
			ShapesDocumentationWriterIfc.MODE format = ShapesDocumentationWriterIfc.MODE.valueOf(clientFormat.toUpperCase());
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());

			if(source == SOURCE_TYPE.URL) {
				modelPopulator.populateModelFromUrl(model, shapesUrl);
			}
			else if (source == SOURCE_TYPE.CATALOG) {
				AbstractCatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);
				modelPopulator.populateModelFromUrl(model, entry.getTurtleDownloadUrl().toString());
			}
			else{
				modelPopulator.populateModel(
						model,
						source,
						shapesUrl,
						shapesText,
						shapesFiles,
						shapesCatalogId
				);
			}

            LOGGER.debug("Done Loading Shapes. Model contains {} triples", model.size());

			return this.docService.doOutputDoc(
					model,
					// true to read diagram
					includeDiagram,
					hideProperties,
					format,
					urlLogo,
					modelPopulator.getSourceName(),
					language,
					sectionDiagram,
					filterUnusedNodeShapes
			);
		} catch (Exception e) {
			ExceptionManager.throwException(DocException.class, e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}


	/*
	@ExceptionHandler permet de définir pour CETTE classe uniquement les exceptions qu'elles capturent
	Ici le endpoint du formulaire /doc peut retourner DocException, si c'est le cas,
	on retourne la vue avec le message d'erreur.
	*/
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = DocException.class, produces = "text/html")
	public String handleExceptionForDocController(DocException ex, org.springframework.ui.Model model){
		formData.setErrorMessage(ex.getMessage());
		formData.setCatalog(this.catalogService.getShapesCatalog());
		model.addAttribute(DocFormData.KEY, formData);
		return "doc-form";
	}

	///**
	// * Handles an error in the validation form (stores the message in the Model, then forward to the view).
	// *
	// * @param request
	// * @param message
	// * @return
	// */
	//protected ModelAndView handleViewFormError(
	//		HttpServletRequest request,
	//		String message,
	//		Exception e
	//) {
	//	DocFormData vfd = new DocFormData();
	//	vfd.setErrorMessage(Encode.forHtml(message));
	//
	//	ShapesCatalog catalog = this.catalogService.getShapesCatalog();
	//	vfd.setCatalog(catalog);
	//
	//	if(e != null) {
	//		e.printStackTrace();
	//	}
	//	return new ModelAndView("doc-form", DocFormData.KEY, vfd);
	//}

}
