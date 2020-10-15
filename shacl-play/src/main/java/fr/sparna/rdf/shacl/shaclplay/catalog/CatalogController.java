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
import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalogData;
import fr.sparna.rdf.shacl.shaclplay.catalog.rules.RulesCatalogService;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogData;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;


@Controller
public class CatalogController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected ShapesCatalogService shapesCatalogService;

	@Autowired
	protected RulesCatalogService rulesCatalogService;
	
	@RequestMapping(value = {"shapes-catalog"},method=RequestMethod.GET)
	public ModelAndView shapesCatalog(
			HttpServletRequest request,
			HttpServletResponse response
	){
		ShapesCatalogData vfd = new ShapesCatalogData();
		
		ShapesCatalog catalog = this.shapesCatalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		return new ModelAndView("shapes-catalog", ShapesCatalogData.KEY, vfd);	
	}

	@RequestMapping(value = {"rules-catalog"},method=RequestMethod.GET)
	public ModelAndView rulesCatalog(
			HttpServletRequest request,
			HttpServletResponse response
	){
		RulesCatalogData data = new RulesCatalogData();
		
		RulesCatalog catalog = this.rulesCatalogService.getRulesCatalog();
		data.setCatalog(catalog);
		
		return new ModelAndView("rules-catalog", RulesCatalogData.KEY, data);	
	}
	
}
