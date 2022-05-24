package fr.sparna.rdf.shacl.sparqlgen.construct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathParser;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;

import fr.sparna.rdf.shacl.sparqlgen.shaclmodel.NodeShape;
import fr.sparna.rdf.shacl.sparqlgen.shaclmodel.PropertyShape;

public class QueryGenerator {
	
	final Node vthat = new NodeFactory().createVariable("that");
	final Node vthis = new NodeFactory().createVariable("this");
	
	public void queryGeneratorContruct(
			NodeShape shNode, 
			Resource path,
			Query qSelect,
			Map<String, String> necessaryPrefixes,
			String typeSh,
			File outputFile) throws IOException {
		
		
		// Query instance
		Query qConstruct = new Query();
		// ElementGroup Where Clause
		ElementGroup eWhere = new ElementGroup();
		
		// Create Construct Query
		qConstruct.setConstructTemplate(
					new GenerateQueryConstructProperties().generateQueryProperties(
							shNode.getProperties(),
							path.getLocalName()));
		// Query Type
		qConstruct.setQueryConstructType();
		
		// find the Sh:Node Property to ShaclBox & get t 
		for(PropertyShape nsproperty : shNode.getProperties()) {
			
			ElementGroup newGpo = new ElementGroup();			
			/*
			 * Optional Process
			 */
			ElementTriplesBlock eOptional= SparqlQueryHelper.initElementTriplesBlock(
					NodeFactory.createVariable(path.getLocalName()),
					NodeFactory.createURI(nsproperty.getPath().getURI()),
					NodeFactory.createVariable(nsproperty.getPath().getLocalName())
			);
			
			newGpo.addElement(eOptional);
			
			/*
			 * shValues process
			 */
			if(nsproperty.getHasValue() != null) {
				ElementData eDataValues = new ElementData();
				
				Var varProperty = Var.alloc(Var.alloc(nsproperty.getPath().getLocalName()));
				eDataValues.add(varProperty);
				
				if(nsproperty.getHasValue().isURIResource()) {
					eDataValues.add(new BindingFactory().binding(
							varProperty, 
							new NodeFactory().createURI(nsproperty.getHasValue().asResource().getURI()))
						);	
				}
				newGpo.addElement(eDataValues);
			}
			
			/*
			 * shIn process
			 */
			if(nsproperty.getIn() != null) {
				List<Node> eListNodeIn = new ArrayList<Node>();
				
				Var varProperty = Var.alloc(Var.alloc(nsproperty.getPath().getLocalName()));
				
				for (RDFNode rdfIn : nsproperty.getIn()) {
					eListNodeIn.add(new NodeFactory().createURI(rdfIn.toString()));
				}
				
				ElementData nDataIn = SparqlQueryHelper.initElementData(varProperty, eListNodeIn);						
				newGpo.addElement(nDataIn);
			}
			
			/*
			 * shLanguageIn process
			 */
			if(nsproperty.getLanguageIn() != null) {
				ElementFilter eFilter = SparqlQueryHelper.initElementFilter(
						new ExprVar(nsproperty.getPath().getLocalName().toString()), 
						nsproperty.getLanguageIn());
				newGpo.addElement(eFilter);
			}
			
			eWhere.addElement(new ElementOptional(newGpo));
		 
		}		
		
		
		
		Query querySelect = new Query();		
		if(path != null & shNode != null ) {
			
			ElementGroup querySelectWhere = new ElementGroup();
			Node subject=null;
			Node predicate = null;
			Node object=null;
			if(typeSh.equals("node")) {
				
				// Generate Triples
				querySelect.getProject().add(Var.alloc(path.getLocalName()));
				querySelect.setQuerySelectType();
				querySelect.setDistinct(true);				
				
				subject = vthis;			
				//new NodeFactory().createURI(subjectproperty.getURI());
				if(path.isURIResource()) {
					predicate =new NodeFactory().createURI(path.getURI());
				}
				if(path.isLiteral()) {
					predicate =new NodeFactory().createLiteral(path.getLocalName());
				}
				
				object =new NodeFactory().createVariable(path.getLocalName());
				querySelectWhere.addElement(SparqlQueryHelper.initElementTriplesBlock(subject, predicate, object));
				
			}
			
			if(typeSh.equals("pathReverse")) {
				
				//Var qVarSelect = qSelect.getProjectVars().get(0);
				//qSelect.getProjectVars().clear();
				// Rename the Values in the focus Query
				//Expr v = new ExprVar(Var.alloc("this"));
				//qSelect.getProject().add(Var.alloc("that"),v);
				// Get the prefix
				PrefixMapping prefixMap = new PrefixMappingImpl();
				List<PrefixDeclaration> namespaceSections = PrefixDeclaration.fromMap(necessaryPrefixes);
				for (PrefixDeclaration prefixUse : namespaceSections) {
					prefixMap.setNsPrefix(prefixUse.getprefix(),prefixUse.getnamespace());
				}		
				
				querySelect.setPrefixMapping(prefixMap);
				//Contruc Query Select
				querySelect.getProject().add(Var.alloc(path.getLocalName()));
				querySelect.setQuerySelectType();
				querySelect.setDistinct(true);	
				
				
				PrefixMapping pmQuery = querySelect.getPrefixMapping();
				
				subject = vthis;
				String spathReverse = "^"+path.getModel().shortForm(path.asResource().getURI());
				Path predicatePath = PathParser.parse(spathReverse,pmQuery);
				
				// Name of object - input the NodeShape.path
				object = new NodeFactory().createVariable(path.getLocalName());
				
				ElementPathBlock ePathBlock = new ElementPathBlock();
				ePathBlock.addTriple(new TriplePath(subject,predicatePath ,object));
				
				pmQuery.clearNsPrefixMap();
				querySelect.setPrefixMapping(pmQuery);
				
				querySelectWhere.addElement(ePathBlock);
				
			}
			
			querySelect.setQueryPattern(querySelectWhere);
			
			
			//Other Clause hasValues et sh:In
			for(PropertyShape nsproperty : shNode.getProperties()) {
				// hasValue in the path reverse  
				ElementGroup eGpo = new ElementGroup();
				if(nsproperty.getHasValue() != null) {
					
					Var varProperty = Var.alloc(Var.alloc(nsproperty.getPath().getLocalName()));
					// Create Triples
					ElementTriplesBlock eBlockT = SparqlQueryHelper.initElementTriplesBlock(
							new NodeFactory().createVariable(path.getLocalName()),
							new NodeFactory().createURI(nsproperty.getPath().getURI()), 
							varProperty
						);
					
					eGpo.addElement(eBlockT);
					
					ElementData eDataValues = new ElementData();
					
					eDataValues.add(varProperty);
					if(nsproperty.getHasValue().isURIResource()) {
						eDataValues.add(new BindingFactory().binding(
								varProperty, 
								new NodeFactory().createURI(nsproperty.getHasValue().asResource().getURI()))
							);	
					}
					
					eGpo.addElement(eDataValues);
					querySelectWhere.addElement(eGpo);
				}
				
				// hasIn in the path reverse
				if(nsproperty.getIn() != null) {
					List<Node> eListNodeIn = new ArrayList<Node>();
					
					Var varProperty = Var.alloc(Var.alloc(nsproperty.getPath().getLocalName()));
					
					for (RDFNode rdfIn : nsproperty.getIn()) {
						eListNodeIn.add(new NodeFactory().createURI(rdfIn.toString()));
					}
					
					//Triple
					ElementTriplesBlock eBlockT = SparqlQueryHelper.initElementTriplesBlock(
							new NodeFactory().createVariable(path.getLocalName()),
							new NodeFactory().createURI(nsproperty.getPath().getURI()), 
							varProperty
							);
					
					eGpo.addElement(eBlockT);
					ElementData nDataIn = SparqlQueryHelper.initElementData(varProperty, eListNodeIn);						
					eGpo.addElement(nDataIn);
							
					querySelectWhere.addElement(eGpo);
				}
			} // 
			
			// Add SubQuery
			if (qSelect != null) {
				try {				
						PrefixMapping pm = qSelect.getPrefixMapping();  //qSelect.getPrefixMapping();
						pm.clearNsPrefixMap(); // Clean the prefix in the query focus
						qSelect.setPrefixMapping(pm);
						querySelectWhere.addElement(new ElementSubQuery(qSelect));						
					} catch (Exception e) {
							e.printStackTrace();
					}
			}
		}

		
		if(querySelect != null) {
			eWhere.addElement(new ElementSubQuery(querySelect));
		}
		
		
		
		
		// Set prefix QueryOutput

		List<PrefixDeclaration> namespaceSections = PrefixDeclaration.fromMap(necessaryPrefixes);
		for (PrefixDeclaration prefixUse : namespaceSections) {
			qConstruct.setPrefix(prefixUse.getprefix(), prefixUse.getnamespace());
		}
				
		qConstruct.setQueryPattern(eWhere);
				
		/*
		* Write file 
		*/
		if(qConstruct != null) {
			WriteFileOutput.writeFileOutput(outputFile, qConstruct);
		}	
	}
}
