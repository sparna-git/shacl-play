package fr.sparna.rdf.shacl.shaclplay.sparql;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.ZipOutputStream;

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

import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;
import fr.sparna.rdf.shacl.sparqlgen.SparqlGenerator;
import fr.sparna.rdf.shacl.sparqlgen.SparqlGeneratorZipOutputListener;

@Controller
public class SparqlController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected ShapesCatalogService catalogService;

	@RequestMapping(
			value = {"sparql"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		SparqlFormData  vfd = new SparqlFormData ();
		
		//ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		//vfd.setCatalog(catalog);
		
		return new ModelAndView("sparql-form", SparqlFormData.KEY, vfd);	
	}
	
	@RequestMapping(
			value="/sparql",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView sparql(
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
			// radio box indicating type of input
			@RequestParam(value="targetOverrideSource", required=true) String targetOverrideSourceString,
			// url of page if source=url
			@RequestParam(value="targetOverrideUrl", required=false) String targetOverrideUrl,
			// inline content if source=text
			@RequestParam(value="targetOverrideInline", required=false) String targetOverrideText,
			// uploaded file if source=file
			@RequestParam(value="targetOverrideFile", required=false) List<MultipartFile> targetOverrideFiles,
			
			// type of queries option
			@RequestParam(value="formatCombine", required=false) boolean typeQuery,
			
			HttpServletRequest request,
			HttpServletResponse response
	) {
		try {
			log.debug("sparql(shapeSourceString='"+shapesSourceString+"')");
			
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(targetOverrideSourceString.toUpperCase());
			
			// get the targets override source
			ControllerModelFactory.SOURCE_TYPE targetOverrideSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase()); 	
			
			// initialize shapes first
			log.debug("Determining Shapes source...");
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModel(
					shapesModel,
					targetOverrideSource,
					shapesUrl,
					shapesText,
					shapesFiles,
					null
			);
			
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			log.debug("Determining Target override source...");
			
			// Optional Shapes for target override
			Model targetOverridesModel = null;
			if(!(targetOverrideSource == SOURCE_TYPE.FILE && targetOverrideFiles.get(0).getOriginalFilename().equals(""))) {

				targetOverridesModel = ModelFactory.createDefaultModel();
				modelPopulator.populateModel(
						targetOverridesModel,
						shapesSource,
						targetOverrideUrl,
						targetOverrideText,
						targetOverrideFiles,
						null
				);
			}
			
			doOutputSparql(
					shapesModel,
					targetOverridesModel,
					typeQuery,
					modelPopulator.getSourceName(),
					response
			);
			return null;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	protected void doOutputSparql(
			Model shapesModel,
			Model targetOverridesModel,
			boolean singleQueryGeneration,
			String sourceName,
			HttpServletResponse response
	) throws IOException {		
		
		// write in the response
		response.addHeader("Content-Disposition", "attachment; filename=\""+sourceName+".zip\"");
		response.setContentType("application/zip");
		
		// prepare zip output stream
		ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
		SparqlGeneratorZipOutputListener zipOutputListener = new SparqlGeneratorZipOutputListener(zos);
		
		// Call the sparqlgenerator model
		SparqlGenerator qGenerator = new SparqlGenerator(zipOutputListener);
		try {
			qGenerator.generateSparql(shapesModel, targetOverridesModel, singleQueryGeneration);			
		} catch (Exception e) {
			e.printStackTrace();
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
		SparqlFormData vfd = new SparqlFormData();
		vfd.setErrorMessage(Encode.forHtml(message));
		
		//ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		//vfd.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("sparql-form", SparqlFormData.KEY, vfd);
	}
	
}
