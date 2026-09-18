package fr.sparna.rdf.shacl.shaclplay.draw;

import fr.sparna.rdf.shacl.diagram.DrawFormat;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;
import fr.sparna.rdf.shacl.shaclplay.draw.service.DrawService;
import fr.sparna.rdf.shacl.shaclplay.exception.DrawException;
import fr.sparna.rdf.shacl.shaclplay.exception.ExceptionManager;
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

@Controller
public class DrawController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DrawController.class);

	private final ShapesCatalogService catalogService;
	private final DrawService drawService;
	private final DrawFormData formData;

	@Autowired
	public DrawController(ShapesCatalogService catalogService, DrawService drawService, DrawFormData formData){
		this.catalogService = catalogService;
		this.drawService = drawService;
		this.formData = formData;
	}


	@ResponseStatus(HttpStatus.OK)
	@GetMapping(
			value = {"/draw"},
			produces = {"text/html"}
	)
	public String validate(org.springframework.ui.Model model){
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		formData.setCatalog(catalog);
		model.addAttribute(DrawFormData.KEY, formData);
		return "draw-form";
	}

	@PostMapping(
			value="/draw",
			params={"shapesSource"}
	)
	public ResponseEntity<ByteArrayResource> draw(
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
			// output format svg / png
			@RequestParam(value="format", required=false, defaultValue = "svg") String format,
			// hide Properties
			@RequestParam(value="hideProperties", required=false) boolean hideProperties
	) {
		try {
            LOGGER.debug("draw(shapeSourceString='{}')", shapesSourceString);
			
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE source = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// read format 
			DrawFormat fmt = DrawFormat.valueOf(format.toUpperCase());

			// initialize shapes first
			LOGGER.debug("Determining Shapes source...");
			Model model = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			
			// if source is a URL, redirect to the API
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

			return this.drawService.doOutputDiagram(
					model,
					modelPopulator.getSourceName(),
					fmt,
					hideProperties
			);

		} catch (Exception e) {
			ExceptionManager.throwException(DrawException.class, e.getMessage());
		}
		return ResponseEntity.badRequest().build();
	}

	/*
	@ExceptionHandler permet de définir pour CETTE classe uniquement les exceptions qu'elles capturent
	Ici le endpoint du formulaire /draw peut retourner DrawException, si c'est le cas,
	on retourne la vue avec le message d'erreur.
	*/
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = DrawException.class, produces = "text/html")
	public String handleExceptionForDrawController(DrawException ex, org.springframework.ui.Model model){
		formData.setErrorMessage(ex.getMessage());
		formData.setCatalog(this.catalogService.getShapesCatalog());
		model.addAttribute(DrawFormData.KEY, formData);
		return "draw-form";
	}
	
}
