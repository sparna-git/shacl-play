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

/**
 * Resolves the target definitions of shapes to find their focus nodes, and notify the listeners of the results.
 */
public class ShapeFocusNodesResolver {
	
	protected Model shapeModel;
	protected Model data;

	protected List<FocusNodeListener> listeners = new ArrayList<FocusNodeListener>();

	
	public ShapeFocusNodesResolver(Model shapeModel, Model data) {
		super();
		this.shapeModel = shapeModel;
		this.data = data;
	}
	
	public void resolveFocusNodes() {
		
		// for each subject of a target predicate...
		List<Resource> shapes = shapeModel.listResourcesWithProperty(SH.targetNode)
		.andThen(shapeModel.listResourcesWithProperty(SH.targetClass))
		.andThen(shapeModel.listResourcesWithProperty(SH.targetSubjectsOf))
		.andThen(shapeModel.listResourcesWithProperty(SH.targetObjectsOf))
		.andThen(shapeModel.listResourcesWithProperty(RDF.type, RDFS.Class).filterKeep(r -> r.hasProperty(RDF.type, SH.NodeShape)))
		.andThen(shapeModel.listResourcesWithProperty(RDF.type, OWL.Class).filterKeep(r -> r.hasProperty(RDF.type, SH.NodeShape)))
		// generic SPARQL-based targets
		.andThen(shapeModel.listResourcesWithProperty(SH.target)).toList();
		
		for (Resource shape : shapes) {
			resolveFocusNodes(shape, data);
		}

		// notify of end
		for(FocusNodeListener listener : listeners) {
			listener.notifyEnd();
		}
	}

	public void setListeners(List<FocusNodeListener> listeners) {
		this.listeners = listeners;
	}

	public List<FocusNodeListener> getListeners() {
		return this.listeners;
	}

	private void resolveFocusNodes(Resource shape, Model data) {

		// * sh:targetNode
		StmtIterator it = shape.listProperties(SH.targetNode);
		while(it.hasNext()) {
			notifyListeners(
				shape,
				data,
				resolveTargetNode(it.next().getObject().asResource(), data)
			);
		}
		
		// * sh:targetClass
		it = shape.listProperties(SH.targetClass);
		while(it.hasNext()) {
			notifyListeners(
				shape,
				data,
				resolveTargetClass(it.next().getObject().asResource(), data)
			);
		}
		
		// * implicit targetClass if the shape is also a class
		if(shape.hasProperty(RDF.type, RDFS.Class)) {
			notifyListeners(
				shape,
				data,
				resolveTargetClass(shape, data)
			);
		}
		
		// * sh:targetSubjectsOf
		it = shape.listProperties(SH.targetSubjectsOf);
		while(it.hasNext()) {
			notifyListeners(
				shape,
				data,
				resolveTargetSubjectsOf(it.next().getObject().asResource(), data)
			);
		}
		
		// * sh:targetObjectsOf	
		it = shape.listProperties(SH.targetObjectsOf);
		while(it.hasNext()) {
			notifyListeners(
				shape,
				data,
				resolveTargetObjectsOf(it.next().getObject().asResource(), data)
			);
		}
		
		// * sh:target
		it = shape.listProperties(SH.target);
		while(it.hasNext()) {
			Resource shTargetValue = it.next().getObject().asResource();
			if(shTargetValue.hasProperty(SH.select)) {
				notifyListeners(
					shape,
					data,
					resolveTargetSparql(shTargetValue.getProperty(SH.select).getObject().asLiteral().getLexicalForm(), data)
				);
			}
		}

		// notify of end shape
		for(FocusNodeListener listener : listeners) {
			listener.notifyEndShape(shape, data);
		}
	}

	private void notifyListeners(Resource shape, Model data, List<RDFNode> focusNodes) {
		for(FocusNodeListener listener : listeners) {
			listener.notifyFocusNodes(shape, data, focusNodes);
		}
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
	
	private List<RDFNode> resolveTargetSubjectsOf(Resource targetSubjectsOf, Model data) {
		List<Resource> resources = data.listSubjectsWithProperty(data.createProperty(targetSubjectsOf.getURI())).toList();
		// cast to a List<RDFNode> to match the return type
		return new ArrayList<RDFNode>(resources);
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
