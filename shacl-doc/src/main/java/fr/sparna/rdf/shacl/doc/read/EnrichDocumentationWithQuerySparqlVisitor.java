package fr.sparna.rdf.shacl.doc.read;

import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.vocabulary.DCTerms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PropertyShape;
import fr.sparna.rdf.shacl.doc.model.Link;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisitorIfc;



public class EnrichDocumentationWithQuerySparqlVisitor implements ShaclVisitorIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private Model statisticsModel;
	private ShapesDocumentation documentation;
	
	final Var subject = Var.alloc("s");
	final Var object = Var.alloc("o");
	final Node type = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); 
	
	public EnrichDocumentationWithQuerySparqlVisitor(Model statisticsModel, ShapesDocumentation documentation) {
		super();
		this.statisticsModel = statisticsModel;
		this.documentation = documentation;
	}
	
	
	@Override
	public void visitModel(Model model) {
		// nothing
	}

	@Override
	public void visitOntology(Resource ontology) {
		// nothing
	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {
		
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		
		PropertyShape ps = new PropertyShape(aPropertyShape);
		NodeShape ns = new NodeShape(aNodeShape);
		
		// this works only if we have a targetClass and a URI in sh:path
		if(ns.getShTargetClass() != null && ps.getShPath().isURIResource()) {
			// find corresponding section in doc
			ShapesDocumentationSection section = this.documentation.findSectionByUriOrId(ns.getURIOrId());
			if(section != null) {
				PropertyShapeDocumentation propertySection = section.findPropertyShapeDocumentationSectionByUriOrId(ps.getURIOrId());
				
				if(propertySection != null) {
					
					// Query instance
					Query q = QueryFactory.create();
					
					q.getProject().add(Var.alloc("result"));
					q.setQuerySelectType();
					q.setDistinct(true);
							
					ElementGroup eWhere = new ElementGroup();		
					ElementTriplesBlock eBlock = new ElementTriplesBlock();		
					
					// Dataset
					//eBlock.addTriple(new Triple(this.subject, this.type,NodeFactory.createURI(section.getTargetClass().getHref())));
					
					for (Link r : section.getTargetClass()) {
						eBlock.addTriple(new Triple(this.subject, this.type,NodeFactory.createURI(r.getHref())));
					}
					
					
					
					
					eBlock.addTriple(new Triple(this.subject, NodeFactory.createURI(ps.getShPath().getURI()),Var.alloc("result")));
					
					eWhere.addElement(eBlock);
					
					q.setQueryPattern(eWhere);
					
					//output Script Sparql Query
					propertySection.setSparqlQueryProperty(q.toString());
				}
			}
		}
		

		
		
		List<Resource> propertyPartitions = this.statisticsModel.listStatements(null, DCTerms.conformsTo, aPropertyShape).mapWith(t-> t.getSubject()).toList();
		
		if(propertyPartitions.size() == 0) {
			log.debug("Cannot find corresponding property partition for "+aPropertyShape);
		} else {
			if(propertyPartitions.size() > 1) {
				log.debug("More than one property partition found for "+aPropertyShape);
			}
			Resource aPropertyPartition = propertyPartitions.get(0);
			

		}		
				
	}
	
	@Override
	public void leaveModel(Model model) {
		// nothing
	}

}
