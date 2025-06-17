package fr.sparna.rdf.shacl.shaclplay.doc;

import java.io.ByteArrayOutputStream;
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

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.read.ShapesDocumentationReaderIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc.MODE;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXmlWriter;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;


@Controller
public class DocController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected ShapesCatalogService catalogService;

	@RequestMapping(
			value = {"doc"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		DocFormData vfd = new DocFormData();
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		return new ModelAndView("doc-form", DocFormData.KEY, vfd);	
	}
	
	@RequestMapping(
			value = {"doc"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView docUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			// includeDiagram option
			@RequestParam(value="includeDiagram", required=false) boolean includeDiagram,
			// includeDiagram option
			@RequestParam(value="sectionDiagram", required=false, defaultValue = "true") boolean sectionDiagram,
			// hide Properties
			@RequestParam(value="hideProperties", required=false) boolean hideProperties,
			// List Option
			@RequestParam(value="format", required=false, defaultValue = "html") String format,
			// Logo Option
			@RequestParam(value="inputLogo", required=false) String urlLogo,
			// Language Option
			@RequestParam(value="language", required=false) String language,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("docUrl(shapesUrl='"+shapesUrl+"')");		
			
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			if(language == null) {
				language ="en";
			}
			
			doOutputDoc(
					shapesModel,
					// true to read diagram
					includeDiagram,
					hideProperties,
					format,
					urlLogo,
					modelPopulator.getSourceName(),
					language,
					sectionDiagram,
					response);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	@RequestMapping(
			value="/doc",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView doc(
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
			// includeDiagram option
			@RequestParam(value="includeDiagram", required=false) boolean includeDiagram,
			// hide Properties
			@RequestParam(value="hideProperties", required=false) boolean hideProperties,
			// print PDF Option
			@RequestParam(value="format", required=false, defaultValue = "html") String format,
			// Logo Option
			@RequestParam(value="inputLogo", required=false) String urlLogo,
			// Language Option
			@RequestParam(value="language", required=false) String language,
			// Split Diagram
			@RequestParam(value="sectionDiagram", required=false) boolean sectionDiagram,
			HttpServletRequest request,
			HttpServletResponse response
	) {
		try {
			
			log.debug("doc(shapeSourceString='"+shapesSourceString+"')");
			
			boolean printPDF = format.toLowerCase().equals("pdf") ? true : false;
			
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// if source is a ULR, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/doc?"
					+"format="+format.toLowerCase()
					+"&url="+URLEncoder.encode(shapesUrl, "UTF-8")
					+"&includeDiagram="+includeDiagram
					+("&sectionDiagram="+sectionDiagram)
					+((hideProperties)?"&hideProperties=true":"")
					+((printPDF)?"&printPDF=true":"")
					+((!language.equals("en"))?"&language="+language:"")
					+((urlLogo != null)?"&inputLogo="+URLEncoder.encode(urlLogo, "UTF-8"):"")
				);
			} else if (shapesSource == SOURCE_TYPE.CATALOG) {
				AbstractCatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);
				return new ModelAndView("redirect:/doc?format="+format.toLowerCase()+"&url="+URLEncoder.encode(entry.getTurtleDownloadUrl().toString(), "UTF-8")+"&includeDiagram="+includeDiagram+"&hideProperties="+hideProperties+((printPDF)?"&printPDF=true":"")+((!language.equals("en"))?"&language="+language:"")+((urlLogo != null)?"&inputLogo="+URLEncoder.encode(urlLogo, "UTF-8"):""));				
			}
			
			
			// initialize shapes first
			log.debug("Determining Shapes source...");
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
			
			// defaults to english
			if(language == null) {
				language ="en";
			}
			
			doOutputDoc(
					shapesModel,
					// true to read diagram
					includeDiagram,
					hideProperties,
					format,
					urlLogo,
					modelPopulator.getSourceName(),
					language,
					sectionDiagram,
					response
			);
			return null;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	protected void doOutputDoc(
			Model shapesModel,
			boolean includeDiagram,
			boolean hideProperties,
			String format, // printPDF,
			String urlLogo,
			String filename,
			String languageInput,
			boolean includeSectionDiagram,
			HttpServletResponse response
	) throws IOException {		
		response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");

		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader(includeDiagram, urlLogo, hideProperties, includeSectionDiagram);
		ShapesDocumentation doc = reader.readShapesDocumentation(
				shapesModel,
				// OWL graph
				ModelFactory.createDefaultModel(),
				languageInput
		);
		
		
		if (format.toLowerCase().equals("html")) { 
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
			response.setContentType("text/html");
			// response.setContentType("application/xhtml+xml");
			writer.writeDoc(doc, languageInput, response.getOutputStream(), MODE.HTML);			
		} else if (format.toLowerCase().equals("xml")) {
			
			ShapesDocumentationXmlWriter writeXML = new ShapesDocumentationXmlWriter();
			response.setContentType("application/xml");
			writeXML.write(doc, languageInput, response.getOutputStream(), MODE.XML);
			
		} else if(format.toLowerCase().equals("pdf") ) {
			
			// 1. write Documentation structure to XML
			ShapesDocumentationWriterIfc writerHTML = new ShapesDocumentationJacksonXsltWriter();
			ByteArrayOutputStream htmlBytes = new ByteArrayOutputStream();
			writerHTML.writeDoc(doc,languageInput, htmlBytes,MODE.PDF);
			
			//read file html
			String htmlCode = new String(htmlBytes.toByteArray(),"UTF-8");
			
			// Convert
			response.setContentType("application/pdf");
			PdfRendererBuilder _builder = new PdfRendererBuilder();
			_builder.useFastMode();
			_builder.withHtmlContent(htmlCode,"https://shacl-play.sparna.fr/play");			
			_builder.toStream(response.getOutputStream());
			_builder.testMode(false);
			_builder.run();			
			
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
		DocFormData vfd = new DocFormData();
		vfd.setErrorMessage(Encode.forHtml(message));
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("doc-form", DocFormData.KEY, vfd);
	}
	
	

}
