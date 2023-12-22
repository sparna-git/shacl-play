package fr.sparna.rdf.shacl.shaclplay.generate.dataset;

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

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.read.DatasetDocumentationModelReader;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationJacksonXsltWriter;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc;
import fr.sparna.rdf.shacl.doc.write.ShapesDocumentationWriterIfc.MODE;
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
		
		return new ModelAndView("dataset-doc", DatasetDocFormData.KEY, data);	
	}
	
	@RequestMapping(
			value="/dataset-doc",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView generateDataset(
			// radio box indicating type of shapes
			@RequestParam(value="shapesSource", required=true) String datasetSourceString,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			@RequestParam(value="inputShapeUrl", required=false) String datasetUrl,
			//@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			@RequestParam(value="inputShapeFile", required=false) List<MultipartFile> datasetFiles,
			// inline Shapes if shapeSource=sourceShape-inputShapeInline
			//@RequestParam(value="inputShapeInline", required=false) String shapesText,
			
			// Format output file
			@RequestParam(value="format", required=false, defaultValue = "HTML") String format,
			HttpServletRequest request,
			HttpServletResponse response			
	) {
		try {

			// get the source type
			ControllerModelFactory.SOURCE_TYPE datasetSource = ControllerModelFactory.SOURCE_TYPE.valueOf(datasetSourceString.toUpperCase());
			
			
			// if source is a ULR, redirect to the API
			if(datasetSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/generateDatasetUrl?url="+URLEncoder.encode(datasetUrl, "UTF-8")+"&format="+format);
			} else {
				
				// initialize shapes first
				log.debug("Determining dataset source...");
				Model datasetModel = ModelFactory.createDefaultModel();
				ControllerModelFactory modelPopulator = new ControllerModelFactory(null);
				modelPopulator.populateModel(
						datasetModel,
						datasetSource,
						datasetUrl,
						null,
						datasetFiles,
						null
				);
				log.debug("Done Loading Shapes. Model contains "+datasetModel.size()+" triples");
				
				DatasetDocumentationModelReader reader = new DatasetDocumentationModelReader();
				ShapesDocumentation sd = reader.readDatasetDocumentation(datasetModel, ModelFactory.createDefaultModel(), format, false);

				/*
				 * view html
				 */
				ShapesDocumentationWriterIfc writer = new ShapesDocumentationJacksonXsltWriter();
				response.setContentType("text/html");
				writer.writeDatasetDoc(sd,  //set of data
						"en",  // language default	
						response.getOutputStream(), //instance of output
						MODE.HTML // this option is update to format config
				);		

				return null;
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return handleGenerateFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
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
		return new ModelAndView("dataset-doc", DatasetDocFormData.KEY, data);
	}
	
}