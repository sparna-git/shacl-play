package fr.sparna.rdf.shacl.shaclplay.excel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import fr.sparna.rdf.jena.QueryExecutionServiceImpl;
import fr.sparna.rdf.shacl.excel.DataParser;
import fr.sparna.rdf.shacl.excel.model.Sheet;
import fr.sparna.rdf.shacl.excel.writeXLS.WriteXLS;
import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.DefaultModelProcessor;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.providers.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerCommons;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;



@Controller
public class ExcelController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;

	@Autowired
	protected ShapesCatalogService catalogService;
	
	
	@RequestMapping(
			value = {"excel"},
			method=RequestMethod.GET
	)	
	public ModelAndView excel(
			HttpServletRequest request,
			HttpServletResponse response
	){
		ExcelFormData data = new ExcelFormData();
		
		return new ModelAndView("excel-form", ExcelFormData.KEY, data);	
	}
		
	@RequestMapping(
			value = {"excel"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView excel(
			@RequestParam(value="url", required=true) String url,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
		try {
			log.debug("schema(shapesUrl='"+url+"')");
			
			Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			// load data
			URL actualUrl = new URL(url);
			Model datasetModel = ModelFactory.createDefaultModel();
			datasetModel = ControllerCommons.populateModel(datasetModel, actualUrl);
			
			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), datasetModel);
			
			ShaclGenerator generator = new ShaclGenerator();
			Model shapes = generator.generateShapes(config,dataProvider);
			
			outputRdf2Excel(shapes,url,response);
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
			
			
	}
	
	@RequestMapping(
			value="/excel",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView excel(
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
		try {
			
			// get the source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// Configuraton
			Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			String sourceName = null;
			QueryExecutionServiceImpl queryExecutionService;
			
			// if source is a ULR, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/excel?url="+URLEncoder.encode(shapesUrl, "UTF-8"));
			} else {
				Model shapesModel = ModelFactory.createDefaultModel();
				ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
				modelPopulator.populateModel(
						shapesModel,
						shapesSource,
						null, //shapesUrl,
						null,//shapesText,
						shapesFiles,
						null //shapesCatalogId
				);
				
				queryExecutionService = new QueryExecutionServiceImpl(shapesModel);
				sourceName = modelPopulator.getSourceName(); 
				
				SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(queryExecutionService);
				
				ShaclGenerator generator = new ShaclGenerator();
				Model shapes = generator.generateShapes(config,dataProvider);
				
				//modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
				log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
				outputRdf2Excel(shapes, sourceName,response);
			}
			
			return null;
						
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private Model outputRdf2Excel(
			Model shapesModel,
			String sourceName,
			HttpServletResponse response
	) throws Exception {		
		
		// serialize in Excel
		XSSFWorkbook workbook = serializeInExcel(shapesModel); 
		
		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String filename=sourceName+"-"+"shacl"+"_"+dateString;
		
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "inline; filename=\""+filename+"."+"xlsx"+"\"");
		workbook.write(response.getOutputStream());
		response.getOutputStream().flush();
		workbook.close();
		return null;
	}
		
	public XSSFWorkbook serializeInExcel(Model dataModel) throws InvalidFormatException, IOException {
		
		// first read the SHACL template
		Model shaclTemplateGraph = ModelFactory.createDefaultModel(); 
		InputStream r = this.getClass().getResourceAsStream("/shacl-spreadsheet-template.ttl");

		RDFDataMgr.read(
			shaclTemplateGraph,
			r,
			RDF.getURI(),
			RDFLanguages.TURTLE
		);

		String uniqueLang = DataParser.guessTemplateLanguage(shaclTemplateGraph);
		if(uniqueLang == null) {
			uniqueLang = "en";
		}
		DataParser parser = new DataParser(uniqueLang);
		List<Sheet> sheets = parser.parseData(shaclTemplateGraph,dataModel);
		
		// Generate excel
		WriteXLS xlsWriter = new WriteXLS();
		XSSFWorkbook workbook = xlsWriter.generateWorkbook(dataModel.getNsPrefixMap(),sheets);
		
		return workbook;
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
		ExcelFormData data = new ExcelFormData();
		data.setErrorMessage(Encode.forHtml(message));

		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("excel-form", ExcelFormData.KEY, data);
	}
	
}
     