package fr.sparna.rdf.shacl.shaclplay.catalog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.shacl.shaclplay.ApplicationData;


@Controller
public class CatalogController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected CatalogService catalogService;
	
	@RequestMapping(value = {"catalog"},method=RequestMethod.GET)
	public ModelAndView catalog(
			HttpServletRequest request,
			HttpServletResponse response
	){
		CatalogData vfd = new CatalogData();
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		return new ModelAndView("catalog", CatalogData.KEY, vfd);	
	}
		
}
