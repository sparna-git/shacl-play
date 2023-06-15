package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclClassesReader {
	
	public ShaclClasses read(Resource nodeShape, List<Resource> nodeShapes) {
		
		
		ShaclClasses shClass = new ShaclClasses(nodeShape);
		List<ShapesValues> lShapes = new ArrayList<>();
		//ShaclConstraintComponent sh = new ShaclConstraintComponent();
		
		
		
		List<Statement> lproperties = nodeShape.listProperties().toList();
		
		for (Statement prop : lproperties) {
			
			String s = prop.getModel().shortForm(prop.getSubject().getURI().toString());
			String p = prop.getModel().shortForm(prop.getPredicate().getURI().toString());
			String o = prop.getModel().shortForm(prop.getObject().toString());
			
			if(!p.equals("sh:property")) {
				ShapesValues spv = new ShapesValues();
				
				spv.setSubject(s);
				spv.setPredicate(p);
				spv.setObject(o);
				
				lShapes.add(spv);
			}
		}
		
		shClass.setShapes(lShapes);
		
		return shClass;
	}
	
}
