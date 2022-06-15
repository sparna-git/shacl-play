package fr.sparna.rdf.shacl.shaclplay.sparql;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
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

import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;
import fr.sparna.rdf.shacl.sparqlgen.SparqlGenerator;

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
			value = {"sparql"},
			params={"url","urlOptional"},
			method=RequestMethod.GET
	)
	
	public ModelAndView sparqlUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			@RequestParam(value="urlOptional", required=true) String shapesUrlOptional,
			@RequestParam(value="formatCombine", required=true) Boolean typeQuery,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("sparqlUrl(shapesUrl='"+shapesUrl+"')");		
			
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
			
			Model shapesModelOptional = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulatorOptional = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModelOptional, shapesUrlOptional);
			
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			doOutputSparql(
					shapesModel,
					shapesModelOptional,
					typeQuery,
					modelPopulator.getSourceName(),
					response);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	@RequestMapping(
			value="/sparql",
			params={"source"},
			method = RequestMethod.POST
	)
	public ModelAndView sparql(
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
			@RequestParam(value="formatCombine", required=false) boolean typeQuery,
			HttpServletRequest request,
			HttpServletResponse response
	) {
		try {
			log.debug("sparql(shapeSourceString='"+shapesSourceString+"')");
			
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(sourceString.toUpperCase());
			
			// get the shapes source type Optional
			ControllerModelFactory.SOURCE_TYPE shapesSourceOptional = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			
			// if source is a ULR, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/sparql?url="+URLEncoder.encode(sourceString, "UTF-8"));
			} 
			
			// if source is a ULR, redirect to the API
			if(shapesSourceOptional == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/sparql?url="+URLEncoder.encode(shapesSourceString, "UTF-8"));
			} 
			
			
			// initialize shapes first
			log.debug("Determining Shapes source...");
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModel(
					shapesModel,
					shapesSource,
					url,
					text,
					files,
					null
			);
			
			
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			log.debug("Determining Shapes source...");
			
			//Optional
			Model shapesModelOptional = null;
			String namefileOptional=null;
			for (MultipartFile file : shapesFiles) {
				namefileOptional=file.getOriginalFilename();
			}
			if(namefileOptional.length() > 0) {
				shapesModelOptional = ModelFactory.createDefaultModel();
				ControllerModelFactory modelPopulatorOptional = new ControllerModelFactory(this.catalogService.getShapesCatalog());
				modelPopulatorOptional.populateModel(
								shapesModelOptional,
								shapesSourceOptional,
								shapesUrl,
								shapesText,
								shapesFiles,
								null
					);
			}
			
			
			
			doOutputSparql(
					shapesModel,
					shapesModelOptional,
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
			Model shapesModelOpt,
			Boolean typeQuery,
			String filename,
			HttpServletResponse response
	) throws IOException {		
		//response.setContentType("text/html");
		//response.setContentType("application/x-download");
		
		//response.setHeader("Content-Disposition","attachment;filename=\""+filename+".zip\"");
		
		//Directory home/download
		String home=System.getProperty("user.home");
		File outputDir = new File(home+"/Downloads/");
		
		// Call the sparqlgenerator model
		SparqlGenerator qGenerator = new SparqlGenerator(outputDir);
		try {
			qGenerator.generateSparql(shapesModel, shapesModelOpt, typeQuery);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
