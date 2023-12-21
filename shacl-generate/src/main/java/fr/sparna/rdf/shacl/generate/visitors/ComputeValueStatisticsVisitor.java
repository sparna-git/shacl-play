package fr.sparna.rdf.shacl.generate.visitors;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.DASH;
import fr.sparna.rdf.shacl.SHACL_PLAY;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;

public class ComputeValueStatisticsVisitor extends DatasetAwareShaclVisitorBase implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(ComputeStatisticsVisitor.class);
	
	private Model StatisticModel;
	private Model datasetModel;
	
	public ComputeValueStatisticsVisitor(ShaclGeneratorDataProviderIfc dataProvider, Model countModel, Model datasetModel) {
		super(dataProvider);
		this.StatisticModel = countModel;
		this.datasetModel = datasetModel;
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		
		Resource p = getPropertyValueChar(aNodeShape, aPropertyShape);
		
		if (p != null) {			
			if (p.hasProperty(DASH.propertyRole)) {
				
				//get the nodeShape 
				Resource nodeShapeURI = aNodeShape.getRequiredProperty(SHACLM.targetClass).getObject().asResource();
				Resource objProperty = p.getProperty(VOID.property).getObject().asResource();
				// Create of SPARQL Query
				String querySparql = "SELECT ?o (COUNT(?s) AS ?count) "
						+ "			  WHERE { ?s a <"+nodeShapeURI.getURI()+"> ."+
											"?s <"+objProperty.getURI()+"> ?o ."
													+ "} "
													+ "Group By ?o";
				getResultSPARQL(StatisticModel, objProperty, querySparql, p);
			}
		}		
	}
	
	
	public Resource getPropertyValueChar(Resource nodeShape,Resource property) {
		
		Resource propertyStatistic = null;

		// find partition
		List<Statement> partitionStatements = this.StatisticModel.listStatements(null, DCTerms.conformsTo, nodeShape).toList();

		if (partitionStatements.size() > 0) {
			Resource partition = partitionStatements.get(0).getSubject();

			// read all properties
			List<Statement> lProperties = partition.listProperties(VOID.propertyPartition).toList();
			for (Statement pp : lProperties) {
				Resource oPP = pp.getObject().asResource();

				if (oPP.getProperty(DCTerms.conformsTo).getObject().equals(property)) {
					propertyStatistic = oPP;
					break;
				}
			}
		}
		return propertyStatistic;
		
	}

	private void getResultSPARQL(Model dataset, Resource property, String querySparql, Resource propertyResource){
		
		Query q = QueryFactory.create(querySparql); 
		try (QueryExecution qExec = QueryExecutionFactory.create(querySparql, this.datasetModel)){
			ResultSet results = qExec.execSelect();
			
			while (results.hasNext()) {
				QuerySolution querySolution = results.next();
				
				// create resource anonyymous
				Resource rAnonymous = this.StatisticModel.createResource();
				propertyResource.addProperty(propertyResource.getModel().createProperty(SHACL_PLAY.VALUE_PARTITION), rAnonymous);
				
				
				
				
				this.StatisticModel.add(rAnonymous, 
										this.StatisticModel.createProperty(SHACL_PLAY.VALUE), 
										querySolution.get("o")
										);
				this.StatisticModel.add(rAnonymous, 
										VOID.distinctSubjects, 
										querySolution.getLiteral("count"));
				
			}			
		} 
	}
	
	public static String renderSparqlPropertyPath(Resource r) {
		if(r == null) return "";
		
		if(r.isURIResource()) {
			return "<"+r.getURI()+">";
		} else if(r.canAs(RDFList.class)) {
			List<RDFNode> l = r.as(RDFList.class).asJavaList();
			return l.stream().map(i -> renderSparqlPropertyPath(i.asResource())).collect(Collectors.joining("/"));
		} else if(r.hasProperty(SHACLM.alternativePath)) {
			Resource alternatives = r.getPropertyResourceValue(SHACLM.alternativePath);
			RDFList rdfList = alternatives.as( RDFList.class );
			List<RDFNode> pathElements = rdfList.asJavaList();
			return pathElements.stream().map(p -> renderSparqlPropertyPath((Resource)p)).collect(Collectors.joining("|"));
		} else if(r.hasProperty(SHACLM.inversePath)) {
			Resource value = r.getPropertyResourceValue(SHACLM.inversePath);
			if(value.isURIResource()) {
				return "^"+renderSparqlPropertyPath(value);
			}
			else {
				return "^("+renderSparqlPropertyPath(value)+")";
			}
		} else if(r.hasProperty(SHACLM.zeroOrMorePath)) {
			Resource value = r.getPropertyResourceValue(SHACLM.zeroOrMorePath);
			if(value.isURIResource()) {
				return renderSparqlPropertyPath(value)+"*";
			}
			else {
				return "("+renderSparqlPropertyPath(value)+")*";
			}
		} else if(r.hasProperty(SHACLM.oneOrMorePath)) {
			Resource value = r.getPropertyResourceValue(SHACLM.oneOrMorePath);
			if(value.isURIResource()) {
				return renderSparqlPropertyPath(value)+"+";
			}
			else {
				return "("+renderSparqlPropertyPath(value)+")+";
			}
		} else {
			return null;
		}
	}

}
