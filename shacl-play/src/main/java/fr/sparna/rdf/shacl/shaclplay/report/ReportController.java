package fr.sparna.rdf.shacl.shaclplay.report;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.MultiUnion;
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


import fr.sparna.rdf.shacl.printer.report.ValidationReport;
import fr.sparna.rdf.shacl.printer.report.ValidationReportHtmlWriter;

import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;

@Controller
public class ReportController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;

	@Autowired
	protected ShapesCatalogService catalogService;
	
	@RequestMapping(
			value = {"report"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView RaportFromUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {
		try {
			log.debug("reportFromUrl(shapesUrl='"+shapesUrl+"')");		
			
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			doReportValidation(shapesModel,modelPopulator.getSourceName(),response);
			
			return null;			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	
	@RequestMapping(
			value = {"report"},
			method=RequestMethod.GET
	)
	public ModelAndView report(
			HttpServletRequest request,
			HttpServletResponse response
	){
		ReportFormData data = new ReportFormData();
		
		return new ModelAndView("report-form", ReportFormData.KEY, data);	
	}
	
	@RequestMapping(
			value="/report",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView context(
			// radio box indicating type of shapes
			@RequestParam(value="shapesSource", required=true) String shapesSourceString,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			@RequestParam(value="inputShapeUrl", required=false) String shapesUrl,
			//@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			@RequestParam(value="inputShapeFile", required=false) List<MultipartFile> shapesFiles,
			// inline Shapes if shapeSource=sourceShape-inputShapeInline
			@RequestParam(value="inputShapeInline", required=false) String shapesText,
			// reference to Shapes Catalog ID if shapeSource=sourceShape-inputShapeCatalog
			@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			
			HttpServletRequest request,
			HttpServletResponse response			
	) throws Exception {
		try {
			
			// get the source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// if source is a ULR, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/report?url="+URLEncoder.encode(shapesUrl, "UTF-8"));
			} else {
				Model shapesModel = ModelFactory.createDefaultModel();
				ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
				modelPopulator.populateModel(
						shapesModel,
						shapesSource,
						shapesUrl,
						shapesText,
						shapesFiles,
						shapesCatalogId
				);
				
				log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
				
				doReportValidation(shapesModel,modelPopulator.getSourceName(),response);
			}
			
			return null;
						
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	private Model doReportValidation(
			Model validationReport,
			String nameFile,
			HttpServletResponse response
	) throws IOException {
		
		// encapsulate in a ValidationReport
		Model shapesModelEmpty = ModelFactory.createDefaultModel();
		// Full Model
		Model fullModel = ModelFactory.createModelForGraph(new MultiUnion(new Graph[] {
			validationReport.getGraph(),
			shapesModelEmpty.getGraph()
		}));

		ValidationReport report = new ValidationReport(validationReport, fullModel);
		ValidationReportHtmlWriter htmlOutput = new ValidationReportHtmlWriter(true);
		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		htmlOutput.write(report, response.getOutputStream(), Locale.getDefault());	

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
		ReportFormData data = new ReportFormData();
		data.setErrorMessage(Encode.forHtml(message));

		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("report-form", ReportFormData.KEY, data);
	}
	
}
