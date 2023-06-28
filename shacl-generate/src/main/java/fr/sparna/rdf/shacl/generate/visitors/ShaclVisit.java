package fr.sparna.rdf.shacl.generate.visitors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

public class ShaclVisit {

	private Model model;

	public ShaclVisit(Model model) {
		super();
		this.model = model;
	}

	public void visit(ShaclVisitorIfc visitor) {
		// first visit the model
		visitor.visitModel(this.model);
		
		// then visit the ontologies if they are here
		this.model.listStatements(null, RDF.type, OWL.Ontology).forEach(s -> {
			visitor.visitOntology(s.getSubject());
		});
		
		
		// then visit each node shapes
		for (Resource aNodeShape : this.listNodeShapes()) {
			visitor.visitNodeShape(aNodeShape);
			// and each property shape
			for(Resource aPropertyShape : this.listPropertyShapes(aNodeShape)) {
				visitor.visitPropertyShape(aPropertyShape, aNodeShape);
			}
		}
		
		// then notify end
		visitor.leaveModel(this.model);
	}

	public Set<Resource> listNodeShapes() {
		Set<Resource> nodeShapes = this.model.listResourcesWithProperty(RDF.type, SHACLM.NodeShape).toSet();
		return nodeShapes;
	}

	public Set<Resource> listPropertyShapes(Resource nodeShape) {
		List<Statement> propertyStatements = nodeShape.listProperties(SHACLM.property).toList();
		
		Set<Resource> result = new HashSet<Resource>();
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();

			if(object.isResource()) {
				result.add(object.asResource());			
			}
		}	
		return result;
	}


}
