package fr.sparna.rdf.shacl.shaclplay.convert;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
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
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerCommons;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalogService;
import fr.sparna.rdf.shacl.validator.Slf4jProgressMonitor;


@Controller
public class ConvertController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected RulesCatalogService catalogService;
	
	@RequestMapping(
			value = {"convert"},
			params={"url", "shapes"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			@RequestParam(value="url", required=true) String url,
			@RequestParam(value="shapes", required=true) String shapesCatalogId,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			// load shapes
			Model shapesModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
			RulesCatalogEntry entry = this.catalogService.getRulesCatalog().getCatalogEntryById(shapesCatalogId);

			try {
				shapesModel = ControllerCommons.populateModel(shapesModel, entry.getTurtleDownloadUrl());
			} catch (RiotException e) {
				return handleConvertFormError(request, e.getMessage(), e);
			}
			
			// load data
			URL actualUrl = new URL(url);
			Model dataModel = ModelFactory.createDefaultModel();
			ControllerCommons.populateModel(dataModel, actualUrl);
			String dataName = url.substring(url.lastIndexOf('/')+1, url.lastIndexOf('.'));
			
			// recompute permalink
			String permalink = "convert?rules="+shapesCatalogId+"&url="+url;
			
			// compute output fileName
			// compute filename by concatenating original filename and shape name
			String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String outputName = dataName+"-"+shapesCatalogId+"-"+dateString;
			
			return doConvert(shapesModel, dataModel, permalink, outputName, response);
		} catch (Exception e) {
			e.printStackTrace();
			return handleConvertFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

	@RequestMapping(
			value = {"convert"},
			params={"rules"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			@RequestParam(value="rules", required=true) String rulesId,
			HttpServletRequest request,
			HttpServletResponse response
	){
		ConvertFormData data = new ConvertFormData();
		
		RulesCatalog catalog = this.catalogService.getRulesCatalog();
		data.setCatalog(catalog);
		
		if(rulesId != null) {
			data.setSelectedShapesKey(rulesId);
		}
		
		return new ModelAndView("convert-form", ConvertFormData.KEY, data);	
	}
	
	@RequestMapping(
			value = {"convert"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		ConvertFormData data = new ConvertFormData();
		
		RulesCatalog catalog = this.catalogService.getRulesCatalog();
		data.setCatalog(catalog);
		
		return new ModelAndView("convert-form", ConvertFormData.KEY, data);	
	}
	
	@RequestMapping(
			value="/convert",
			params={"source"},
			method = RequestMethod.POST
	)
	public ModelAndView validate(
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
			HttpServletRequest request,
			HttpServletResponse response
	) {
		try {
			log.debug("convert(source='"+sourceString+"', shapeSourceString='"+shapesSourceString+"')");
			
			// get the source type
			ControllerModelFactory.SOURCE_TYPE source = ControllerModelFactory.SOURCE_TYPE.valueOf(sourceString.toUpperCase());		
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// initialize shapes first
			log.debug("Determining Shapes source...");
			Model shapesModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getRulesCatalog());
			modelPopulator.populateModel(
					shapesModel,
					shapesSource,
					shapesUrl,
					shapesText,
					shapesFiles,
					shapesCatalogId
			);			
			String shapeName = modelPopulator.getSourceName();
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			log.debug("Determining Data source...");
			Model dataModel = ModelFactory.createDefaultModel();
			modelPopulator.populateModel(
					dataModel,
					source,
					url,
					text,
					files,
					null
			);
			String dataName = modelPopulator.getSourceName();
			log.debug("Done Loading Data to validate. Model contains "+dataModel.size()+" triples");
			
			// compute permalink only if we can
			log.debug("Determining permalink...");
			String permalink = null;
			if(source == ControllerModelFactory.SOURCE_TYPE.URL) {
				if(
						shapesSource == ControllerModelFactory.SOURCE_TYPE.CATALOG
				) {
					permalink = "convert?rules="+shapesCatalogId+"&url="+url;
					log.debug("Permalink computed : "+permalink);
				}
			}
			if(permalink == null) {
				log.debug("No permalink can be computed.");
			}
			
			// compute filename by concatenating original filename and shape name
			String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String outputName = dataName+"-"+shapeName+"-"+dateString;
			
			return doConvert(shapesModel, dataModel, permalink, outputName, response);
		} catch (Exception e) {
			e.printStackTrace();
			return handleConvertFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	private ModelAndView doConvert(
			Model shapesModel,
			Model dataModel,
			String permalink,
			String filename,
			HttpServletResponse response
	) throws Exception {
		
		// do the actual rule execution
		final Model results = RuleUtil.executeRules(
				dataModel,
				shapesModel,
				null,
				new Slf4jProgressMonitor("shacl-play-convert", log)
		);
		
		// register namespaces from original Mode
		dataModel.getNsPrefixMap().entrySet().stream().forEach(e -> results.setNsPrefix(e.getKey(), e.getValue()));
		// register sh, owl, rdfs namespaces
		results.setNsPrefix("owl", OWL.NS);
		results.setNsPrefix("sh", SH.NS);
		results.setNsPrefix("rdfs", RDFS.uri);
		
		postProcessLists(results, SH.ignoredProperties);
		
		// determine language
		String langName="Turtle";
		Lang l = RDFLanguages.nameToLang(langName);
		if(l == null) {
			l = Lang.RDFXML;
		}
		// write results in response
		// ControllerCommons.serialize(results, l, "shacl-play-convert", response);
		ControllerCommons.serialize(results, l, filename, response);
		return null;
	}
	
	private Model postProcessLists(Model model, Property p) {
		List<Resource> resources = model.listResourcesWithProperty(p).toList();
		for (Resource r : resources) {
			List<Statement> statements = model.listStatements(r, p, (RDFNode)null).toList();
			
			// recreate a list
			List<RDFNode> listContent = new ArrayList<>();
			for (Statement s : statements) {
				if(! s.getObject().canAs(RDFList.class)) {
					listContent.add(s.getObject());
				}
			}
			
			if(!listContent.isEmpty() ) {
				// remove statements from original resource
				model.remove(statements);
				
				// add link to list
				model.add(r, p, model.createList(listContent.toArray(new RDFNode[] {})));
			}
		}
		
		return model;
	}

	
	/**
	 * Handles an error in the validation form (stores the message in the Model, then forward to the view).
	 * 
	 * @param request
	 * @param message
	 * @return
	 */
	protected ModelAndView handleConvertFormError(
			HttpServletRequest request,
			String message,
			Exception e
	) {
		ConvertFormData data = new ConvertFormData();
		data.setErrorMessage(Encode.forHtml(message));
		
		RulesCatalog catalog = this.catalogService.getRulesCatalog();
		data.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("convert-form", ConvertFormData.KEY, data);
	}
	
}
