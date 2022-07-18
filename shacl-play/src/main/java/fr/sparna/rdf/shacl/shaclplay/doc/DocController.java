package fr.sparna.rdf.shacl.shaclplay.doc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.jsoup.Jsoup;
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
			// print PDF Option
			@RequestParam(value="printPDF", required=false) boolean printPDF,
			// Logo Option
			@RequestParam(value="inputLogo", required=false) String urlLogo,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("docUrl(shapesUrl='"+shapesUrl+"')");		
			
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			doOutputDoc(
					shapesModel,
					// true to read diagram
					includeDiagram,
					printPDF,
					urlLogo,
					modelPopulator.getSourceName(),
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
			// print PDF Option
			@RequestParam(value="printPDF", required=false) boolean printPDF,
			// Logo Option
			@RequestParam(value="inputLogo", required=false) String urlLogo,
			HttpServletRequest request,
			HttpServletResponse response
	) {
		try {
			
			log.debug("doc(shapeSourceString='"+shapesSourceString+"')");
			
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// if source is a ULR, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/doc?url="+URLEncoder.encode(shapesUrl, "UTF-8")+"&includeDiagram="+includeDiagram);
			} else if (shapesSource == SOURCE_TYPE.CATALOG) {
				AbstractCatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);
				return new ModelAndView("redirect:/doc?url="+URLEncoder.encode(entry.getTurtleDownloadUrl().toString(), "UTF-8")+"&includeDiagram="+includeDiagram);
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
			
			doOutputDoc(
					shapesModel,
					// true to read diagram
					includeDiagram,
					printPDF,
					urlLogo,
					modelPopulator.getSourceName(),
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
			boolean printPDF,
			String urlLogo,
			String filename,
			HttpServletResponse response
	) throws IOException {		
		response.setContentType("text/html");
		response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");

		//Logo
		String name_img=null;
		if(urlLogo != null) {
			String logo=null;
			if(urlLogo.contains("url,") || urlLogo.contains("on,") ) {
				logo=urlLogo.split(",")[1];
			}else {
				logo=urlLogo;
			}
			
			if(new File(logo).exists() && !printPDF) {
				File fileImg = new File(logo); 
				File fileOut = new File(logo);
				name_img = fileImg.getName();
				// copy imagen file in the output directory
				Path sourceImg = FileSystems.getDefault().getPath(logo);
				Path outputDirImg = FileSystems.getDefault().getPath(fileOut.getParentFile().getPath()+"\\"+name_img);
				try {
					Files.copy(sourceImg, outputDirImg, StandardCopyOption.REPLACE_EXISTING);
					
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e);
				}
			}else {
				name_img = logo;
			}
		}
		
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader(includeDiagram, name_img);
		ShapesDocumentation doc = reader.readShapesDocumentation(
				shapesModel,
				// OWL graph
				ModelFactory.createDefaultModel(),
				"en",
				filename,
				false
		);
		
		
		
		if(printPDF) {
			
			// 1. write Documentation structure to XML
			ShapesDocumentationWriterIfc writerHTML = new ShapesDocumentationJacksonXsltWriter();
			FileOutputStream inputHTML = new FileOutputStream(new File("/temp/inputHTML.html"));
			writerHTML.write(doc, "en", inputHTML);
			
			//read file html
			FileReader fr=new FileReader("/temp/inputHTML.html");
			BufferedReader br= new BufferedReader(fr);
			StringBuilder contentHTML=new StringBuilder(1024);
			String s;
			while((s=br.readLine())!=null)
			    {
				contentHTML.append(s);
			    } 
			
			String htmlCode =  contentHTML.toString();
			
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			// Convert
			
			PdfRendererBuilder _builder = new PdfRendererBuilder();
			 
			
			String baseUri = FileSystems.getDefault()
		              .getPath("C:/", "temp/")
		              .toUri()
		              .toString();
			
			_builder.useFastMode();
			_builder.withHtmlContent(htmlCode, baseUri);
			
			
			_builder.toStream(outputStream);
			_builder.testMode(false);
			_builder.run();
			
			
			// View in html
			response.setContentType("application/pdf");
	        response.setContentLength(outputStream.size());
	        response.getOutputStream().write(outputStream.toByteArray());
	        response.getOutputStream().flush();
	        outputStream.close();			
		}else {
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
			writer.write(doc, "en", response.getOutputStream());
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
