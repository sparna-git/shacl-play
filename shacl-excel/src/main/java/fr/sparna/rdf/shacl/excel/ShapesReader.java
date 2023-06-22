package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontScalePercentOrPercentString;
import org.topbraid.shacl.vocabulary.SH;

public class ShapesReader {
	
	ConstraintValueReader cValue = new ConstraintValueReader();
	
	public Shapes read(Resource nodeShape, List<Resource> nodeShapes) {
		
		Shapes shClass = new Shapes(nodeShape);
		List<ShapesValues> lShapes = new ArrayList<>();
		List<XslTemplate> template = new ArrayList<>();
		
		List<Statement> spo = nodeShape.listProperties().toList();//.stream().map(m -> m.getPredicate()).distinct().collect(Collectors.toList());	
		for (Statement confSentence : spo) {
			
			String s = null;
			String p = null;
			String o = null;
			String dataType ="";
			
			if (confSentence.getObject().isResource()) {
				
				s = confSentence.getModel().shortForm(confSentence.getSubject().getURI().toString());
				p = confSentence.getModel().shortForm(confSentence.getPredicate().getURI().toString());
				o = confSentence.getModel().shortForm(confSentence.getObject().toString());
				
			}else if (confSentence.getObject().isLiteral()) {
					s = confSentence.getModel().shortForm(confSentence.getSubject().getURI().toString());
					p = confSentence.getModel().shortForm(confSentence.getPredicate().getURI().toString());
					o = confSentence.getModel().shortForm(confSentence.getObject().asLiteral().getLexicalForm());
					
					String data_type = confSentence.getObject().asLiteral().getDatatypeURI(); 
					String data_Language = confSentence.getObject().asLiteral().getLanguage();
					if (data_Language.isEmpty() && ((data_type.equals("http://www.w3.org/2001/XMLSchema#string")) || (data_type.equals("http://www.w3.org/2001/XMLSchema#integer")))){
						dataType="^^"+confSentence.getModel().shortForm(confSentence.getObject().asLiteral().getDatatypeURI());
					}else if (!data_Language.isEmpty()) {
						dataType="@"+data_Language;
					}
			}
			 					
			ShapesValues spv = new ShapesValues();
			
			spv.setSubject(s);
			spv.setPredicate(p);
			spv.setObject(o);
			spv.setDatatype(dataType);
			
			lShapes.add(spv);
		}
		
		shClass.setShapes(lShapes);
		
		return shClass;
	}
}
