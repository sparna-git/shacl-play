package fr.sparna.rdf.shacl.shaclplay.draw;

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


	//@RequestMapping(
	//		value = {"/draw"},
	//		params={"url"},
	//		method=RequestMethod.GET
	//)
	//public ModelAndView drawUrl(
	//		@RequestParam(value="url", required=true) String shapesUrl,
	//		@RequestParam(value="format", required=false, defaultValue = "svg") String clientFormat,
	//		// hide Properties
	//		@RequestParam(value="hideProperties", required=false) boolean hideProperties,
	//		HttpServletRequest request,
	//		HttpServletResponse response
	//){
	//	try {
	//		log.debug("drawUrl(shapesUrl='"+shapesUrl+"')");
//
	//		// read format
	//		DrawFormat drawFormat = DrawFormat.valueOf(clientFormat.toUpperCase());
	//
	//		Model shapesModel = ModelFactory.createDefaultModel();
	//		ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
	//		modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
	//		log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
	//		doOutputDiagram(
	//				shapesModel,
	//				modelPopulator.getSourceName(),
	//				drawFormat,
	//				hideProperties,
	//				response);
	//		return null;
	//	} catch (Exception e) {
	//		e.printStackTrace();
	//		return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
	//	}
	//}

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
			
			// if source is a ULR, redirect to the API
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

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = DrawException.class, produces = "text/html")
	public String handleExceptionForDrawController(DrawException ex, org.springframework.ui.Model model){
		formData.setErrorMessage(ex.getMessage());
		formData.setCatalog(this.catalogService.getShapesCatalog());
		model.addAttribute(DrawFormData.KEY, formData);
		return "draw-form";
	}
	
	//protected void doOutputDiagram(
	//		Model shapesModel,
	//		String filename,
	//		DrawFormat drawFormat,
	//		boolean hideProperties,
	//		HttpServletResponse response
	//) throws IOException {
	//
	//	PlantUmlDiagramGenerator writer = new PlantUmlDiagramGenerator(
	//			// includes the subClassOf links in the generated diagram
	//			true,
	//			// don't generate hyperlinks
	//			false,
	//			// avoid arrows to empty boxes
	//			true,
	//			// hide Properties
	//			hideProperties,
	//			// a language for label and description reading
	//			"en"
	//	);
	//
	//	List<PlantUmlDiagramOutput> diagrams = writer.generateDiagrams(
	//			shapesModel,
	//			// OWL Model
	//			ModelFactory.createDefaultModel()
	//	);
//
	//	if(diagrams.size() == 1) {
	//		String plantUmlString = diagrams.get(0).getPlantUmlString();
	//		// always set appropriate content type
	//		response.setContentType(drawFormat.mimeType);
//
	//		switch(drawFormat) {
	//			case PNG : {
	//				// display a png file, generate from PlantUml
	//				PlantUmlPngSerializer pngSerializer = new PlantUmlPngSerializer();
	//				pngSerializer.serialize(plantUmlString, response.getOutputStream());
	//				response.getOutputStream().flush();
	//				break;
	//			}
	//			case SVG : {
	//				PlantUmlSvgSerializer svgSerializer = new PlantUmlSvgSerializer();
	//				response.setCharacterEncoding("UTF-8");
	//				svgSerializer.serializeInSVG(plantUmlString, response.getOutputStream());
	//				response.getOutputStream().flush();
	//				break;
	//			}
	//			case TXT : {
	//				response.setCharacterEncoding("UTF-8");
	//				response.getOutputStream().write(plantUmlString.getBytes("UTF-8"));
	//				response.getOutputStream().flush();
	//				break;
	//			}
	//			case HTML : {
	//				response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");
	//				PlantUmlHtmlSerializer htmlSerializer = new PlantUmlHtmlSerializer();
	//				htmlSerializer.serialize(diagrams, response.getOutputStream());
	//				response.getOutputStream().flush();
	//				break;
	//			}
	//			default : {
	//				throw new RuntimeException("Unknown output format "+ drawFormat.name());
	//			}
	//		}
	//	} else {
	//		switch(drawFormat) {
	//			case PNG :
	//			case SVG :
	//			case TXT : {
	//				// create a zip
	//				response.setContentType("application/zip");
	//				response.setHeader("Content-Disposition", "inline; filename=\""+filename+".zip\"");
//
	//				ZipOutputStream zos = new ZipOutputStream(response.getOutputStream(), Charset.forName("UTF-8"));
	//				zos.setLevel(9);
	//
	//				for (PlantUmlDiagramOutput oneDiagram : diagrams) {
	//					String uri = oneDiagram.getDiagramUri();
	//					String localPart;
	//					if(uri.indexOf('#') > -1) {
	//						localPart = uri.substring(uri.lastIndexOf('#')+1);
	//					} else {
	//						localPart = uri.substring(uri.lastIndexOf('/')+1);
	//					}
	//
	//					if(drawFormat == DrawFormat.TXT) {
	//						String entryName = URLEncoder.encode(localPart, "UTF-8") + ".txt";
	//						zos.putNextEntry(new ZipEntry(entryName));
	//						zos.write(oneDiagram.getPlantUmlString().getBytes("UTF-8"));
	//						zos.closeEntry();
	//					} else {
	//						String entryName = URLEncoder.encode(localPart, "UTF-8") + "." + drawFormat.extension;
	//						zos.putNextEntry(new ZipEntry(entryName));
	//						SourceStringReader reader = new SourceStringReader(oneDiagram.getPlantUmlString());
	//						// either SVG or PNG, cannot be HTML or TXT
	//						reader.generateImage(zos, new FileFormatOption(drawFormat.plantUmlFileFormat));
	//						zos.closeEntry();
	//					}
	//
	//				}
	//				zos.flush();
	//				zos.close();
	//				response.flushBuffer();
//
	//				break;
	//			}
	//			case HTML: {
	//				response.setContentType("text/html");
	//				response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");
	//				PlantUmlHtmlSerializer htmlSerializer = new PlantUmlHtmlSerializer();
	//				htmlSerializer.serialize(diagrams, response.getOutputStream());
	//				response.getOutputStream().flush();
	//				break;
	//			}
	//			default : {
	//				throw new RuntimeException("Unknown output format "+ drawFormat.name());
	//			}
	//		}
	//	}
	//}

		
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
	//	DrawFormData vfd = new DrawFormData();
	//	vfd.setErrorMessage(Encode.forHtml(message));
	//
	//	ShapesCatalog catalog = this.catalogService.getShapesCatalog();
	//	vfd.setCatalog(catalog);
	//
	//	if(e != null) {
	//		e.printStackTrace();
	//	}
	//	return new ModelAndView("draw-form", DrawFormData.KEY, vfd);
	//}
	
}
