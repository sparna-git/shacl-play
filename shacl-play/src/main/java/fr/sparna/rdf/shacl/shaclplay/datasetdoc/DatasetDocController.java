package fr.sparna.rdf.shacl.shaclplay.datasetdoc;

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
import fr.sparna.rdf.shacl.doc.read.DatasetDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc.MODE;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationXmlWriter;
import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.DefaultModelProcessor;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;

@Controller
public class DatasetDocController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	
	@RequestMapping(
			value = {"dataset-doc"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView generateDatasetUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			// Output format
			@RequestParam(value="format", required=false, defaultValue = "HTML") String format,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("generateDatasetUrl(shapesUrl='"+shapesUrl+"')");
			

			Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), shapesUrl);
			
			/*
			Model shapes = doGenerateStatistic(
					dataProvider,
					config,
					shapesUrl,
					datasetModel
			);		
			
			serialize(shapes, format,response);
			*/
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}

	
	@RequestMapping(
			value = {"dataset-doc"},
			method=RequestMethod.GET
	)
	public ModelAndView generateDataset(
			HttpServletRequest request,
			HttpServletResponse response
	){
		DatasetDocFormData data = new DatasetDocFormData();
		
		return new ModelAndView("dataset-doc-form", DatasetDocFormData.KEY, data);	
	}
	
	@RequestMapping(
			value="/dataset-doc",
			params={"source"},
			method = RequestMethod.POST
	)
	public ModelAndView generateDataset(
			// radio box indicating type of input
			@RequestParam(value="source", required=true) String datasetSourceString,
			// uploaded file if source=file
			@RequestParam(value="inputFile", required=false) List<MultipartFile> datasetFiles,
			// url of page if source=url
			@RequestParam(value="inputUrl", required=false) String datasetUrl,
			// inline content if source=text
			@RequestParam(value="inputInline", required=false) String text,
			// Format of the output
			@RequestParam(value="format", required=false, defaultValue = "HTML") String format,
			// Language Option
			@RequestParam(value="language", required=false) String language,
			HttpServletRequest request,
			HttpServletResponse response			
	) {
		try {

			// get the source type
			ControllerModelFactory.SOURCE_TYPE datasetSource = ControllerModelFactory.SOURCE_TYPE.valueOf(datasetSourceString.toUpperCase());
			
			
			// if source is a URL, redirect to the API
			if(datasetSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/dataset-doc?url="+URLEncoder.encode(datasetUrl, "UTF-8")+"&format="+format);
			} else {
				
				// load dataset
				log.debug("Populating model...");
				Model datasetModel = ModelFactory.createDefaultModel();
				ControllerModelFactory modelPopulator = new ControllerModelFactory(null);
				modelPopulator.populateModel(
						datasetModel,
						datasetSource,
						datasetUrl,
						text,
						datasetFiles,
						null
				);
				log.debug("Done Loading Dataset. Model contains "+datasetModel.size()+" triples");
				
				// defaults to english
				if(language == null) {
					language ="en";
				}
				
				doOutputDoc(
						datasetModel,
						// true to read diagram
						true,
						format,
						// logo URL
						null,
						modelPopulator.getSourceName(),
						language,
						response
				);

				return null;
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	protected void doOutputDoc(
			Model dataset,
			// currently not used
			boolean includeDiagram,
			String format, // html or pdf or xml
			String urlLogo,
			String filename,
			String language,
			HttpServletResponse response
	) throws IOException {	
		response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");
		
		DatasetDocumentationModelReader reader = new DatasetDocumentationModelReader();
		ShapesDocumentation sd = reader.readDatasetDocumentation(dataset, ModelFactory.createDefaultModel(), format, false);

		
		if (format.toLowerCase().equals("html")) {
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
			response.setContentType("text/html");
			writer.writeDatasetDoc(
					sd,  
					language,	
					response.getOutputStream(),
					MODE.HTML	
			);
		} else if (format.toLowerCase().equals("xml")) {	
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationXmlWriter();
			response.setContentType("application/xml");
			writer.writeDatasetDoc(
					sd,
					language,	
					response.getOutputStream(),
					MODE.XML	
			);
		} else if(format.toLowerCase().equals("pdf") ) {
			ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
			// 1. write Documentation structure to XML
			ByteArrayOutputStream htmlBytes = new ByteArrayOutputStream();
			writer.writeDatasetDoc(
					sd,
					language,	
					response.getOutputStream(),
					MODE.XML	
			);
			
			//read file html
			String htmlCode = new String(htmlBytes.toByteArray(),"UTF-8");
			// htmlCode.replace("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">", "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
			
			// Convert
			response.setContentType("application/pdf");
			PdfRendererBuilder _builder = new PdfRendererBuilder();			 
			_builder.useFastMode();
			
			_builder.withHtmlContent(htmlCode,"http://shacl-play.sparna.fr/play");			
			
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
	protected ModelAndView handleGenerateFormError(
			HttpServletRequest request,
			String message,
			Exception e
	) {
		DatasetDocFormData data = new DatasetDocFormData();
		data.setErrorMessage(Encode.forHtml(message));

		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("dataset-doc-form", DatasetDocFormData.KEY, data);
	}
	
}