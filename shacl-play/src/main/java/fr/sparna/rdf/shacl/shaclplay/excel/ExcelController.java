package fr.sparna.rdf.shacl.shaclplay.excel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
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

import fr.sparna.rdf.shacl.excel.Generator;
import fr.sparna.rdf.shacl.excel.model.ModelStructure;
import fr.sparna.rdf.shacl.excel.writeXLS.WriteXLS;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;


@Controller
public class ExcelController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected ShapesCatalogService catalogService;

	@RequestMapping(
			value = {"xls"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		ExcelFormData vfd = new ExcelFormData();
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		return new ModelAndView("excel-form", ExcelFormData.KEY, vfd);	
	}
	
	@RequestMapping(
			value = {"xls"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView xlsUrl(
			@RequestParam(value="inputShapeUrlTemplate", required=true) String shapesUrlTemplate,
			@RequestParam(value="inputShapeUrlSource", required=true) String shapesUrlSource,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("xlsUrl(shapesUrl='"+shapesUrlTemplate+"')");
			
			
			
			/*
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			if(language == null) {
				language ="en";
			}
			*/


			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	@RequestMapping(
			value="/xls",
			//params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView xsl(
			// radio box indicating type of shapes
			@RequestParam(value="shapesSource", required=false) String shapesSourceString,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			@RequestParam(value="inputShapeUrlTemplate", required=false) String shapesUrlTemplate,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			@RequestParam(value="inputShapeUrlSource", required=false) String shapesUrlSource,
			
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			@RequestParam(value="inputShapeFileTemplate", required=false) List<MultipartFile> shapesFilesTemplate,
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			@RequestParam(value="inputShapeFileSource", required=false) List<MultipartFile> shapesFilesSource,
			
			HttpServletRequest request,
			HttpServletResponse response
	) {
		try {
			
			//log.debug("xsl(shapeSourceString='"+shapesSourceString+"')");
			
			// get the shapes source type
			String shapeSourceString;
			if (shapesUrlTemplate!=null & shapesUrlSource!=null) {
				shapeSourceString = "URL";
			} else {
				shapeSourceString = "FILE";
			}
			
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapeSourceString.toUpperCase());
			
			// initialize shapes first
			log.debug("Determining Shapes source...");
			
			// Model Template
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelTemplate = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelTemplate.populateModel(
					shapesModel,
					shapesSource,
					shapesUrlTemplate,
					null,
					shapesFilesTemplate,
					null
			);
			
			
			// Model Source
			Model shapesModelSource = ModelFactory.createDefaultModel();
			ControllerModelFactory modelSource = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelSource.populateModel(
					shapesModelSource,
					shapesSource,
					shapesUrlSource,
					null,
					shapesFilesSource,
					null
			);
			
			// Call module Excel
			// read dataset Template and set of data
			Generator model_data_source = new Generator();
			List<ModelStructure> output_data = model_data_source.readDocument(shapesModel,shapesModelSource);
			
			if (output_data.size() > 0) {				
				String filename = modelSource.getSourceName();
				outputExcel(output_data, shapesModelSource,filename,response);
			}
			
			return null;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	protected void outputExcel (
		List<ModelStructure> outputData,
		Model ModelSource,
		String Filename,
		HttpServletResponse response			
			) throws IOException {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; filename=\""+Filename+".xlsx\"");
		
		// Get OWL in DataGraph
		List<Resource> ontology = ModelSource.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		
		// Write excel
		WriteXLS write_in_excel = new WriteXLS();
		XSSFWorkbook workbook = write_in_excel.processWorkBook(ModelSource.getNsPrefixMap(),ontology, outputData);
				
		// Dowload file
		OutputStream out = response.getOutputStream();
		workbook.write(out);
		workbook.close();
	}
		
	public static String getBaseName(String fileName) {
		
		 int index = fileName.lastIndexOf('.');
		    if (index == -1) {
		        return fileName;
		    } else {
		        return fileName.substring(0, index);
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
		ExcelFormData vfd = new ExcelFormData();
		vfd.setErrorMessage(Encode.forHtml(message));
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("excel-form", ExcelFormData.KEY, vfd);
	}
}
