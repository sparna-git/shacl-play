package fr.sparna.rdf.shacl.shaclplay.home;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.shacl.shaclplay.SessionData;


@Controller
public class HomeController {

	private Logger log= LoggerFactory.getLogger(this.getClass().getName());

	@RequestMapping(value = {"home", "/"},method=RequestMethod.GET)
	public ModelAndView home(
			HttpServletRequest request,
			HttpServletResponse response
	){

		ModelAndView model=new ModelAndView("home");
		return model;
	}

}
