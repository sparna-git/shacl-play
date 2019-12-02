package fr.sparna.rdf.shacl.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class ShapeTargetValidator {
	
	public List<Resource> listShapesWithEmptyTargets(
			Model shapeModel,
			Model data
	) {
		List<Resource> result = new ArrayList<Resource>();
		// for each sh:Shape...
		ResIterator i = shapeModel.listResourcesWithProperty(RDF.type, ResourceFactory.createResource(SH.BASE_URI+"NodeShape"));
		while(i.hasNext()) {
			Resource r = i.next();
			boolean hasTarget = targetMatched(r, data);
			if(!hasTarget) {
				result.add(r);
			}
		}
		return result;
	}
	
	private boolean targetMatched(Resource shape, Model data) {
		Boolean hasTarget = false;
		
		// * sh:targetNode
		StmtIterator it = shape.listProperties(SH.targetNode);
		while(it.hasNext()) {
			if(findTargetNode(it.next().getObject().asResource(), data)) {
				hasTarget = true;
			}
		}
		
		// * sh:targetClass
		it = shape.listProperties(SH.targetClass);
		while(it.hasNext()) {
			if(findTargetClass(it.next().getObject().asResource(), data)) {
				hasTarget = true;
			}
		}
		
		// * implicit targetClass if the shape is also a class
		if(shape.hasProperty(RDF.type, RDFS.Class)) {
			if(findTargetClass(shape, data)) {
				hasTarget = true;
			}
		}
		
		// * sh:targetSubjectsOf
		it = shape.listProperties(SH.targetSubjectsOf);
		while(it.hasNext()) {
			if(findTargetSubjectsOf(it.next().getObject().asResource(), data)) {
				hasTarget = true;
			}
		}
		
		// * sh:targetObjectsOf	
		it = shape.listProperties(SH.targetObjectsOf);
		while(it.hasNext()) {
			if(findTargetObjectsOf(it.next().getObject().asResource(), data)) {
				hasTarget = true;
			}
		}
		
		return hasTarget;
	}

	private boolean findTargetNode(Resource targetNode, Model data) {
		return data.containsResource(targetNode);
	}
	
	private boolean findTargetClass(Resource targetClass, Model data) {
		// return data.contains(null, RDF.type, targetClass);
		
		try {
			String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
			String ask = prefixes+"\n"+"ASK { ?x rdf:type/rdfs:subClassOf* <"+targetClass.getURI()+"> }";
			Query query = QueryFactory.create(ask) ;
			QueryExecution qexec = QueryExecutionFactory.create(query, data) ;
			boolean result = qexec.execAsk() ;
			qexec.close() ;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean findTargetSubjectsOf(Resource targetSubjectsOf, Model data) {
		return data.contains(null, data.createProperty(targetSubjectsOf.getURI()), (RDFNode)null);
	}
	
	private boolean findTargetObjectsOf(Resource targetObjectsOf, Model data) {
		return data.contains(null, data.createProperty(targetObjectsOf.getURI()), (RDFNode)null);
	}
	
}
