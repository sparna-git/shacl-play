package fr.sparna.rdf.shacl.shaclplay.jsonld;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.shacl.jsonld.JsonLdContextGenerator;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;


@Controller
public class JsonLdController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;

	@Autowired
	protected ShapesCatalogService catalogService;
	
	@RequestMapping(
			value = {"context"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView jsonLdContextFromUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {
		try {
			log.debug("jsonLdContextFromUrl(shapesUrl='"+shapesUrl+"')");		
			
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			JsonLdContextGenerator contextGenerator = new JsonLdContextGenerator();
			String context = contextGenerator.generateJsonLdContext(shapesModel);
			
			response.setContentType("application/ld+json");
			response.setCharacterEncoding("UTF-8");
			response.getOutputStream().write(context.getBytes(Charset.forName("UTF-8")));
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	


}
