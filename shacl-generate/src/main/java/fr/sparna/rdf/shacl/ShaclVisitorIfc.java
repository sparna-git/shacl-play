package fr.sparna.rdf.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public interface ShaclVisitorIfc {
	
	void visitModel(Model model);

	void visitOntology(Resource ontology);
	
	void visitNodeShape(Resource aNodeShape);

	void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape);

	void leaveModel(Model model);

}
