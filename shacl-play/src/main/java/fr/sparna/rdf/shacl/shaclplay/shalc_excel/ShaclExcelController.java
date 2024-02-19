package fr.sparna.rdf.shacl.shaclplay.shalc_excel;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
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
import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;


@Controller
public class ShaclExcelController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected ShapesCatalogService catalogService;

	@RequestMapping(
			value = {"shaclexcel"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		ShaclExcelFormData vfd = new ShaclExcelFormData();
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		return new ModelAndView("shacl-excel", ShaclExcelFormData.KEY, vfd);	
	}
	
	@RequestMapping(
			value = {"shaclexcel"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView shaclexcelUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			// Language Option
			@RequestParam(value="language", required=false) String language,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("shaclexcelUrl(shapesUrl='"+shapesUrl+"')");		
			
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	@RequestMapping(
			value="/shaclexcel",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView shaclexcel(
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
			// Language Option
			@RequestParam(value="language", required=false) String language,
			HttpServletRequest request,
			HttpServletResponse response
	) {
		try {
			
			log.debug("shaclexcel(shapeSourceString='"+shapesSourceString+"')");
			
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// if source is a ULR, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/shaclexcel?url"+URLEncoder.encode(shapesUrl, "UTF-8")+((!language.equals("en"))?"&language="+language:""));
			} else if (shapesSource == SOURCE_TYPE.CATALOG) {
				AbstractCatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);
				return new ModelAndView("redirect:/shaclexcel?url="+URLEncoder.encode(entry.getTurtleDownloadUrl().toString(), "UTF-8")+((!language.equals("en"))?"&language="+language:""));				
			}
			
			return null;			
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	protected void doOutputShaclExcel(
			Model shapesModel,
			boolean includeDiagram,
			String filename,
			String languageInput,
			HttpServletResponse response
	) throws IOException {		
		response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");
				
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
		ShaclExcelFormData vfd = new ShaclExcelFormData();
		vfd.setErrorMessage(Encode.forHtml(message));
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("shacl-excel", ShaclExcelFormData.KEY, vfd);
	}
	
	

}
