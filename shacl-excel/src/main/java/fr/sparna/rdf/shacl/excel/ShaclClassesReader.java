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
				
				spv.setNameShapes(p);
				spv.setValues(o);
				
				lShapes.add(spv);
			}
		}
		
		lShapes.stream().sorted((s1,s2) -> {
			if (!s1.getNameShapes().isEmpty()) {
				if(!s2.getNameShapes().isEmpty()) {
					return s1.getNameShapes().compareTo(s2.getNameShapes());
				}else {
					return -1;
				}
			}else {
				if(s2.getNameShapes().isEmpty()) {
					return 1;
				}else {
					return s2.getNameShapes().compareTo(s1.getNameShapes());
				}
			}
			
		});
		
		
		shClass.setShapes(lShapes);
		
		return shClass;
	}
	
}
