package fr.sparna.rdf.shacl.targets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class ShapeTargetResolver {
	
	protected Model shapeModel;
	protected Model data;

	
	public ShapeTargetResolver(Model shapeModel, Model data) {
		super();
		this.shapeModel = shapeModel;
		this.data = data;
	}
	
	public void resolveFocusNodes(FocusNodeProcessor p) {
		
		// for each subject of a target predicate...
		List<Resource> shapes = shapeModel.listResourcesWithProperty(SH.targetNode)
		.andThen(shapeModel.listResourcesWithProperty(SH.targetClass))
		.andThen(shapeModel.listResourcesWithProperty(SH.targetSubjectsOf))
		.andThen(shapeModel.listResourcesWithProperty(SH.targetObjectsOf))
		.andThen(shapeModel.listResourcesWithProperty(RDF.type, RDFS.Class))
		.andThen(shapeModel.listResourcesWithProperty(RDF.type, OWL.Class))
		// generic SPARQL-based targets
		.andThen(shapeModel.listResourcesWithProperty(SH.target)).toList();
		
		for (Resource r : shapes) {
			List<RDFNode> focusNodes = resolveFocusNodes(r, data);
			// TODO
		}
	}

	private List<RDFNode> resolveFocusNodes(Resource shape, Model data) {
		List<RDFNode> focusNodes = new ArrayList<RDFNode>();
		
		// * sh:targetNode
		StmtIterator it = shape.listProperties(SH.targetNode);
		while(it.hasNext()) {
			focusNodes.addAll(resolveTargetNode(it.next().getObject().asResource(), data));
		}
		
		// * sh:targetClass
		it = shape.listProperties(SH.targetClass);
		while(it.hasNext()) {
			focusNodes.addAll(resolveTargetClass(it.next().getObject().asResource(), data));
		}
		
		// * implicit targetClass if the shape is also a class
		if(shape.hasProperty(RDF.type, RDFS.Class)) {
			focusNodes.addAll(resolveTargetClass(shape, data));
		}
		
		// * sh:targetSubjectsOf
		it = shape.listProperties(SH.targetSubjectsOf);
		while(it.hasNext()) {
			focusNodes.addAll(resolveTargetSubjectsOf(it.next().getObject().asResource(), data));
		}
		
		// * sh:targetObjectsOf	
		it = shape.listProperties(SH.targetObjectsOf);
		while(it.hasNext()) {
			focusNodes.addAll(resolveTargetObjectsOf(it.next().getObject().asResource(), data));
		}
		
		// * sh:target
		it = shape.listProperties(SH.target);
		while(it.hasNext()) {
			Resource shTargetValue = it.next().getObject().asResource();
			if(shTargetValue.hasProperty(SH.select)) {
				focusNodes.addAll(resolveTargetSparql(shTargetValue.getProperty(SH.select).getObject().asLiteral().getLexicalForm(), data));
			}
		}
		
		return focusNodes;
	}

	private List<RDFNode> resolveTargetNode(Resource targetNode, Model data) {
		return Collections.singletonList(targetNode);
	}
	
	private List<RDFNode> resolveTargetClass(Resource targetClass, Model data) {		
		List<RDFNode> result = new ArrayList<RDFNode>();
		try {
			String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
			String select = prefixes+"\n"+"SELECT ?x WHERE { ?x rdf:type/rdfs:subClassOf* <"+targetClass.getURI()+"> }";
			Query query = QueryFactory.create(select) ;
			QueryExecution qexec = QueryExecutionFactory.create(query, data) ;
			ResultSet rs = qexec.execSelect() ;
			
			while(rs.hasNext()) {
				QuerySolution solution = rs.next();
				result.add(solution.get("x"));
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<Resource> resolveTargetSubjectsOf(Resource targetSubjectsOf, Model data) {
		return data.listSubjectsWithProperty(data.createProperty(targetSubjectsOf.getURI())).toList();
	}
	
	private List<RDFNode> resolveTargetObjectsOf(Resource targetObjectsOf, Model data) {
		return data.listObjectsOfProperty(data.createProperty(targetObjectsOf.getURI())).toList();
	}
	
	private List<RDFNode> resolveTargetSparql(String sparql, Model data) {
		List<RDFNode> result = new ArrayList<RDFNode>();
		Query query = QueryFactory.create(sparql) ;
		try(QueryExecution qexec = QueryExecutionFactory.create(query, data)){
			ResultSet rs = qexec.execSelect();
			while(rs.hasNext()) {
				QuerySolution solution = rs.next();
				result.add(solution.get(rs.getResultVars().get(0)));
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
