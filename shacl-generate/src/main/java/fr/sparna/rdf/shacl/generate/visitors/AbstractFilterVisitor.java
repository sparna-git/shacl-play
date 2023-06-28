package fr.sparna.rdf.shacl.generate.visitors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGenerator;

public abstract class AbstractFilterVisitor implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);
	
	private transient int filteredNodeShapesCount = 0;
	private transient int filteredPropertyShapesCount = 0;

	
	@Override
	public void visitModel(Model model) {
		// reset count
		this.filteredPropertyShapesCount = 0;
	}

	@Override
	public void visitOntology(Resource ontology) {

	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {
		if(this.filterNodeShape(aNodeShape)) {
			this.removeNodeShape(aNodeShape);
			this.filteredNodeShapesCount++;
		}
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		if(this.filterPropertyShape(aPropertyShape, aNodeShape)) {
			this.removePropertyShape(aPropertyShape);
			this.filteredPropertyShapesCount++;
		}
	}
	
	@Override
	public void leaveModel(Model model) {
		model.listStatements(null, RDF.type, OWL.Ontology).forEach(s -> {
			ShaclGenerator.concatOnProperty(
					s.getSubject(),
					DCTerms.abstract_,
					this.getMessage()+". "+this.filteredPropertyShapesCount+" property shapes filtered out."
			);
		});
	}

	private void removePropertyShape(Resource aPropertyShape) {
		// TODO : improve as this will not correctly remove blank nodes of property paths
		aPropertyShape.getModel().removeAll(aPropertyShape, null, null);
		aPropertyShape.getModel().removeAll(null, null, aPropertyShape);
	}
	
	private void removeNodeShape(Resource aNodeShape) {
		throw new RuntimeException("Not yet implemented");
	}
	
	public abstract boolean filterNodeShape(Resource aNodeShape);
	
	public abstract boolean filterPropertyShape(Resource aPropertyShape, Resource aNodeShape);
	
	public abstract String getMessage();

}
