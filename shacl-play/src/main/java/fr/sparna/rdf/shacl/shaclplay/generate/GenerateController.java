package fr.sparna.rdf.shacl.shaclplay.generate;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.shacl.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.FilterOnStatisticsVisitor;
import fr.sparna.rdf.shacl.ShaclVisit;
import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerCommons;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalogService;

@Controller
public class GenerateController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected RulesCatalogService catalogService;
	
	@RequestMapping(
			value = {"generate"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView generateUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			// Output format
			@RequestParam(value="format", required=false, defaultValue = "Turtle") String format,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("generateUrl(shapesUrl='"+shapesUrl+"')");
			
			
			//section of generate module
			String ENDPOINT = shapesUrl;
			
			//  
			Configuration config = new Configuration("https://shacl-play.sparna.fr/shapes/", "shapes");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), ENDPOINT);
			ShaclGenerator generator = new ShaclGenerator();
			Model shapes = generator.generateShapes(
					config,
					dataProvider);
			
			shapes = generator.generateShapes(config, dataProvider);
			
			ShaclVisit modelStructure = new ShaclVisit(shapes);
			modelStructure.visit(new ComputeStatisticsVisitor(dataProvider, ENDPOINT, true));
			modelStructure.visit(new FilterOnStatisticsVisitor());			

			String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String outputName="shacl"+"_"+dateString;
			
			return doGenerateSHACL(modelStructure, shapes, outputName, format,response);
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	@RequestMapping(
			value = {"generate"},
			params={"rules"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			@RequestParam(value="rules", required=true) String rulesId,
			HttpServletRequest request,
			HttpServletResponse response
	){
		GenerateFormData data = new GenerateFormData();
		
		RulesCatalog catalog = this.catalogService.getRulesCatalog();
		data.setCatalog(catalog);
		
		if(rulesId != null) {
			data.setSelectedShapesKey(rulesId);
		}
		
		return new ModelAndView("generate-form", GenerateFormData.KEY, data);	
	}
	
	@RequestMapping(
			value = {"generate"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		GenerateFormData data = new GenerateFormData();
		
		RulesCatalog catalog = this.catalogService.getRulesCatalog();
		data.setCatalog(catalog);
		
		return new ModelAndView("generate-form", GenerateFormData.KEY, data);	
	}
	
	@RequestMapping(
			value="/generate",
			params={"inputUrl"},
			method = RequestMethod.POST
	)
	public ModelAndView validate(
			// radio box indicating type of input
			//@RequestParam(value="source", required=false) String sourceString,
			// url of page if source=url
			@RequestParam(value="inputUrl", required=true) String url,
			// inline content if source=text
			//@RequestParam(value="inputInline", required=false) String text,
			// uploaded file if source=file
			//@RequestParam(value="inputFile", required=false) List<MultipartFile> files,
			// radio box indicating type of shapes
			//@RequestParam(value="shapesSource", required=false) String shapesSourceString,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			//@RequestParam(value="inputShapeUrl", required=false) String shapesUrl,
			// reference to Shapes Catalog ID if shapeSource=sourceShape-inputShapeCatalog
			//@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			//@RequestParam(value="inputShapeFile", required=false) List<MultipartFile> shapesFiles,
			// inline Shapes if shapeSource=sourceShape-inputShapeInline
			//@RequestParam(value="inputShapeInline", required=false) String shapesText,
			// Output format
			@RequestParam(value="format", required=false, defaultValue = "Turtle") String format,
			HttpServletRequest request,
			HttpServletResponse response			
	) {
		try {
			
			// get the source type
			//ControllerModelFactory.SOURCE_TYPE source = ControllerModelFactory.SOURCE_TYPE.valueOf(sourceString.toUpperCase());
			
			// if source is a ULR, redirect to the API
			if(url != null) {
				return new ModelAndView("redirect:/generate?url="+URLEncoder.encode(url, "UTF-8")+"&format="+format);
			} 
			/*
			else if (shapesSource == SOURCE_TYPE.CATALOG) {
				AbstractCatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);
				return new ModelAndView("redirect:/doc?url="+URLEncoder.encode(entry.getTurtleDownloadUrl().toString(), "UTF-8")+"&includeDiagram="+includeDiagram+((printPDF)?"&printPDF=true":"")+((!language.equals("en"))?"&language="+language:"")+((urlLogo != null)?"&inputLogo="+URLEncoder.encode(urlLogo, "UTF-8"):""));
			}
			*/
			
			// Generate
			//section of generate module
			String ENDPOINT = url;
			
			//  
			Configuration config = new Configuration("http://exemple.be/shapes/", "myshapes");
			config.setShapesOntology("http://exemple.be/shapes");
			
			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), ENDPOINT);
			ShaclGenerator generator = new ShaclGenerator();
			Model shapes = generator.generateShapes(
					config,
					dataProvider);
			
			shapes = generator.generateShapes(config, dataProvider);
			
			ShaclVisit modelStructure = new ShaclVisit(shapes);
			modelStructure.visit(new ComputeStatisticsVisitor(dataProvider, ENDPOINT, true));
			modelStructure.visit(new FilterOnStatisticsVisitor());			

			String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String outputName="shacl"+"_"+dateString;
			
			return doGenerateSHACL(modelStructure, shapes, outputName, format,response);			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	private ModelAndView doGenerateSHACL(
			ShaclVisit shapesModel,
			Model dataModel,
			String filename,
			String FileFmt,
			HttpServletResponse response
	) throws Exception {
		
		
		String langName = FileFmt;
		Lang l = RDFLanguages.nameToLang(langName);
		if(l == null) {
			l = Lang.RDFXML;
		}
		// write results in response
		// ControllerCommons.serialize(results, l, "shacl-play-convert", response);
		ControllerCommons.serialize(dataModel, l, filename, response);
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
		GenerateFormData data = new GenerateFormData();
		data.setErrorMessage(Encode.forHtml(message));
		
		RulesCatalog catalog = this.catalogService.getRulesCatalog();
		data.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("generate-form", GenerateFormData.KEY, data);
	}
	
}
