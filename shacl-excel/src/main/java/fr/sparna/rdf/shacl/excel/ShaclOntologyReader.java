package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class ShaclOntologyReader {

	public List<ShaclOntology> readOWL(List<Resource> NodeShape) {

		List<ShaclOntology> lOwl = new ArrayList<>();

		for (Resource onto : NodeShape) {
			for (Statement owlDoc : onto.listProperties().toList()) {
				
				ShaclOntology owl = new ShaclOntology();
				
				owl.setShapeUri(owlDoc.getSubject().getURI());
				owl.setOwlProperty(owlDoc.getModel().shortForm(owlDoc.getPredicate().getURI()));
				owl.setOwlValue(owlDoc.getModel().shortForm(owlDoc.getObject().toString()));

				lOwl.add(owl);
			}
		}
		return lOwl;
	}

}
