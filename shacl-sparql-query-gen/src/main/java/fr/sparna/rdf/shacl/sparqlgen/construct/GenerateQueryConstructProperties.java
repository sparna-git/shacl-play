package fr.sparna.rdf.shacl.sparqlgen.construct;

import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.syntax.Template;

import fr.sparna.rdf.shacl.sparqlgen.shaclmodel.PropertyShape;


public class GenerateQueryConstructProperties {
	
	
	public Template generateQueryProperties(List<PropertyShape> source, String Subject) {
		
		final BasicPattern bp = new BasicPattern();

		for(PropertyShape sBoxProperty : source) {
			
			// we ignore the inverse path here
			if(sBoxProperty.isPathURI()) {
				// Construct
				Node vSubject = NodeFactory.createVariable(Subject);
				Node vPredicate = NodeFactory.createURI(sBoxProperty.getPath().getURI());
				Node vObject = NodeFactory.createVariable(sBoxProperty.getPath().getLocalName());
				
				bp.add(new Triple(vSubject,vPredicate, vObject));
			}			

		}
		
		return new Template(bp);
	}
	
}
