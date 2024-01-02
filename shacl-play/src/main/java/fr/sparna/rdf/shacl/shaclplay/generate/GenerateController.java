package fr.sparna.rdf.shacl.shaclplay.generate;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.DefaultModelProcessor;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;
import fr.sparna.rdf.shacl.generate.visitors.AssignLabelRoleVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.CopyStatisticsToDescriptionVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerCommons;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;

@Controller
public class GenerateController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	
	@RequestMapping(
			value = {"generate"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView generateUrl(
			@RequestParam(value="url", required=true) String url,
			// Output format
			@RequestParam(value="format", required=false, defaultValue = "Turtle") String format,
			// compute statistics option
			@RequestParam(value="statistics", required=false) boolean computeStatistics,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("generateUrl(url='"+url+"')");
			

			Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), url);
			
			Model shapes = doGenerateShapes(
					dataProvider,
					config,
					url,
					computeStatistics
			);		
			
			serialize(shapes, format, url, response);
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

	
	@RequestMapping(
			value = {"generate"},
			method=RequestMethod.GET
	)
	public ModelAndView generate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		GenerateFormData data = new GenerateFormData();
		
		return new ModelAndView("generate-form", GenerateFormData.KEY, data);	
	}
	
	@RequestMapping(
			value="/generate",
			params={"source"},
			method = RequestMethod.POST
	)
	public ModelAndView generate(
			// radio box indicating type of shapes
			@RequestParam(value="source", required=true) String sourceString,
			// reference to data URL if source=source-inputUrl
			@RequestParam(value="inputUrl", required=false) String url,
			// uploaded data if source=source-inputFile
			@RequestParam(value="inputFile", required=false) List<MultipartFile> inputFiles,
			// inline data if source=source-inputInline
			@RequestParam(value="inputInline", required=false) String text,
			// reference to a SPARQL endpoint URL if source=source-inputUrlEndpoint
			@RequestParam(value="inputUrlEndpoint", required=false) String endpoint,
			// Format output file
			@RequestParam(value="format", required=false, defaultValue = "Turtle") String format,
			// statistics option
			@RequestParam(value="statistics", required=false) boolean computeStatistics,
			HttpServletRequest request,
			HttpServletResponse response			
	) {
		try {

			// get the source type
			ControllerModelFactory.SOURCE_TYPE source = ControllerModelFactory.SOURCE_TYPE.valueOf(sourceString.toUpperCase());
			
			SamplingShaclGeneratorDataProvider dataProvider;
			Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			String sourceName = null;
			
			// first build the data provider, either for an endpoint or by loading a Model
			if(source == SOURCE_TYPE.ENDPOINT) {
				log.debug("Generating shapes for endpoint "+endpoint);
				dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100),endpoint);
				sourceName = ControllerModelFactory.getSourceNameForUrl(endpoint);
			} else {
				// if source is a URL, redirect to the API
				if(source == SOURCE_TYPE.URL) {
					return new ModelAndView("redirect:/generate?url="+URLEncoder.encode(url, "UTF-8")+"&format="+format+(computeStatistics?"&statistics=true":""));
				} else {
					// Load data
					log.debug("Determining dataset source...");
					Model datasetModel = ModelFactory.createDefaultModel();
					ControllerModelFactory modelPopulator = new ControllerModelFactory(null);
					modelPopulator.populateModel(
							datasetModel,
							source,
							url,
							text,
							inputFiles,
							null
					);
					log.debug("Done Loading dataset. Model contains "+datasetModel.size()+" triples");

					dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100),datasetModel);
					sourceName = modelPopulator.getSourceName();
				}
			}
			
			// now generate the shapes
			Model shapes = doGenerateShapes(
					dataProvider,
					config,
					(source == SOURCE_TYPE.ENDPOINT)?endpoint:(source == SOURCE_TYPE.URL)?url:"https://dummy.dataset.uri",
					computeStatistics
			);			
			
			serialize(shapes, format, sourceName, response);
			return null;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	private Model doGenerateShapes(
			ShaclGeneratorDataProviderIfc dataProvider,
			Configuration config,
			String targetDatasetUri,
			boolean withCount
	) {
		ShaclGenerator generator = new ShaclGenerator();
		Model shapes = generator.generateShapes(
				config,
				dataProvider);
		
		ShaclVisit shaclVisit = new ShaclVisit(shapes);	
		
		// add dash:LabelRole
		shaclVisit.visit(new AssignLabelRoleVisitor());
		
		// If withCount, process the ComputeStatisticsVisitor 
		if (withCount) {
			Model countModel = ModelFactory.createDefaultModel();
			shaclVisit.visit(new ComputeStatisticsVisitor(dataProvider, countModel, targetDatasetUri, true));
			shaclVisit.visit(new CopyStatisticsToDescriptionVisitor(countModel));
			shapes.add(countModel);
		}
		
		return shapes;
	}
	
	
	private void serialize(
			Model dataModel,
			String fileFormat,
			String sourceName,
			HttpServletResponse response
	) throws Exception {

		Lang l = RDFLanguages.nameToLang(fileFormat);
		if(l == null) {
			l = Lang.RDFXML;
		}
		
		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String filename=sourceName+"-"+"shacl"+"_"+dateString;
		
		ControllerCommons.serialize(dataModel, l, filename, response);
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

		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("generate-form", GenerateFormData.KEY, data);
	}
	
}
