package fr.sparna.rdf.shacl.shaclplay.ontology;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class OntologyController {

	private Logger log= LoggerFactory.getLogger(this.getClass().getName());

	@RequestMapping(value = {"ontology"},method=RequestMethod.GET)
	public ModelAndView ontology(
			HttpServletRequest request,
			HttpServletResponse response
	){

		ModelAndView model=new ModelAndView("ontology");
		return model;
	}

}
