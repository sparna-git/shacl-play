package fr.sparna.rdf.shacl.shaclplay.rules;

import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;

@Service
public class RulesTodo {
	
	//protected BoxRules p;
	
	public RulesTodo() {
		super();
		
		// TODO Auto-generated constructor stub
	}

	public static final String KEY = RulesTodo.class.getSimpleName();
	
	public BoxRules readModel(Model modelShape) {
		
		BoxRulesReader p = new BoxRulesReader();
		BoxRules boxRules = p.read(modelShape);
		return boxRules;
	}
	
	
		

}
