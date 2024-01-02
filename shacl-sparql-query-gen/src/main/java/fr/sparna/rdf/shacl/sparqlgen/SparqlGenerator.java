package fr.sparna.rdf.shacl.sparqlgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.sparqlgen.construct.GenerateQueryConstructProperties;
import fr.sparna.rdf.shacl.sparqlgen.construct.PrefixDeclaration;
import fr.sparna.rdf.shacl.sparqlgen.construct.ShaclParsingStep;
import fr.sparna.rdf.shacl.sparqlgen.construct.SparqlQueryHelper;
import fr.sparna.rdf.shacl.sparqlgen.shaclmodel.NodeShape;
import fr.sparna.rdf.shacl.sparqlgen.shaclmodel.NodeShapeReader;
import fr.sparna.rdf.shacl.sparqlgen.shaclmodel.PropertyShape;
import fr.sparna.rdf.shacl.sparqlgen.shaclmodel.ShaclPrefixReader;

public class SparqlGenerator {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private SparqlGeneratorOutputListenerIfc outputListener;
	
	protected List<String> outNameFile = new ArrayList<String>();
	private List<NodeShape> allNodeShapes;
	private List<Element> pathShacl = new ArrayList<>();
	private Map<String, String> queryFiles= new HashMap<>();
	
	public SparqlGenerator(SparqlGeneratorOutputListenerIfc outputListener) {
		super();
		this.outputListener = outputListener;
	}

	public void generateSparql(
			Model shaclFile, 
			Model targetsOverrideModel, 
			boolean singleQueryGeneration
	) throws Exception {
		
		// notify listener of start
		this.outputListener.notifyStart();
		
		List<Resource> nodeShapeResources = shaclFile.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
		
		// Read NodeShape
		allNodeShapes = new ArrayList<>();
		NodeShapeReader reader = new NodeShapeReader();
		for (Resource nShape : nodeShapeResources) {
			NodeShape dbShacl = reader.read(nShape);
			allNodeShapes.add(dbShacl);
		}
		
		/* 
		 * , replace the values query in sh:Select
		 */
		if(targetsOverrideModel!= null) {
			List<Resource> nShapeResourcesOptional = targetsOverrideModel.listResourcesWithProperty(SH.target).toList();
			for (Resource nShape : nShapeResourcesOptional) {
				NodeShape dbShaclOptional = reader.read(nShape);		
				
				// Read NodeShape and find the Target cible 
				for(NodeShape nsAll : allNodeShapes) {
					if(nsAll.getNodeShapeResource().getURI().equals(dbShaclOptional.getNodeShapeResource().getURI())) {
						nsAll.setTargetSelect(dbShaclOptional.getTargetSelect());
					}
				}
			}
		}
			
		// Read Property Shape
		for (NodeShape aBox : allNodeShapes) {
			aBox.setProperties(reader.readPropertyShape(aBox.getNodeShapeResource(), allNodeShapes));
		}

		// Get Prefix sh ttl
		HashSet<String> gatheredPrefixes = new HashSet<>();
		for (NodeShape aBox : allNodeShapes) {
			List<String> prefixes = reader.readPrefixes(aBox.getNodeShapeResource());
			gatheredPrefixes.addAll(prefixes);
		}
		Map<String, String> necessaryPrefixes = ShaclPrefixReader.gatherNecessaryPrefixes(
				shaclFile.getNsPrefixMap(),
				gatheredPrefixes
		);
	
		// Start from all root NodeShapes
		for (NodeShape aNodeShape : this.allNodeShapes) {
			if (aNodeShape.getTargetSelect() != null) {
				if(!singleQueryGeneration) {
					
					queryFiles.clear();
					processOneNodeShape(
							aNodeShape,
							new ArrayList<ShaclParsingStep>(),
							aNodeShape,
							necessaryPrefixes
					);
					
					for (Entry<String,String> entry : queryFiles.entrySet()) {
						this.outputListener.notifyOutputQuery(entry.getValue(),entry.getKey());
					}
					
				} else {
					//Create construct
					// String zipPath = outputDir.getPath()+"\\"+aNodeShape.getNodeShapeResource().getLocalName()+"_combine"+".zip";
					Query qConstruct = new Query();
					
					BasicPattern bp = new BasicPattern();				
					Node vSubject = NodeFactory.createVariable("s");
					Node vPredicate = NodeFactory.createVariable("p");
					Node vObject = NodeFactory.createVariable("o");
					bp.add(new Triple(vSubject,vPredicate, vObject));
					
					qConstruct.setConstructTemplate(
							new Template(bp)
						);
					qConstruct.setQueryConstructType();
					
					//Clause Where
					ElementGroup eWhere = new ElementGroup();
					
					// Triples
					ElementTriplesBlock tBlock = new ElementTriplesBlock();
					tBlock.addTriple(new Triple(vSubject, vPredicate, vObject));
					eWhere.addElement(tBlock);
					
					//Query Filter without prefix
					PrefixMapping pm = aNodeShape.getTargetSelect().getPrefixMapping();
					pm.clearNsPrefixMap(); // Clean the prefix in the query focus
					aNodeShape.getTargetSelect().setPrefixMapping(pm);
					eWhere.addElement(new ElementSubQuery(aNodeShape.getTargetSelect()));
					
					// Building UNION clause
					
					pathShacl.clear();
					processOneNodeShapeCombine(
							aNodeShape,
							new ArrayList<ShaclParsingStep>(),
							aNodeShape,
							necessaryPrefixes
					);
					
					//skeep the result in the list
					ElementUnion eUnion = new ElementUnion();
					if(!pathShacl.isEmpty()) {
						for(Element qU: pathShacl) {
							eUnion.addElement(qU);
						}
					}
					
					eWhere.addElement(eUnion);
					qConstruct.setQueryPattern(eWhere);
					
					
					//Set prefix
					// Set prefix QueryOutput
					List<PrefixDeclaration> namespaceSections = PrefixDeclaration.fromMap(necessaryPrefixes);
					for (PrefixDeclaration prefixUse : namespaceSections) {
						qConstruct.setPrefix(prefixUse.getprefix(), prefixUse.getnamespace());
					}
					
					// query file 
					String nameQueryUnion = aNodeShape.getNodeShapeResource().getLocalName()+".rq";
					String codeSparql = qConstruct.toString();
					
					this.outputListener.notifyOutputQuery(codeSparql,nameQueryUnion);
					
					System.out.println(qConstruct);
					
				}
			}
		}		
		
		this.outputListener.notifyStop();
	}
	
	protected void processOneNodeShape(
			// node shape we are currently processing
			NodeShape nodeShape,
			// steps from root NodeShape : each step indicate which PropertyPath was followed, if it was a reverse
			// and to which NodeShape
			List<ShaclParsingStep> stepsFromRootNodeShape,
			// root node shape we started from
			NodeShape rootNodeShape,
			// prefixes we need to properly generate the query
			Map<String, String> necessaryPrefixes
			
	) throws IOException {


		String focusVar = (stepsFromRootNodeShape.size() > 0)?(stepsFromRootNodeShape.get(stepsFromRootNodeShape.size()-1).getVarName()):"this";

		// Query instance
		Query qConstruct = new Query();
		// ElementGroup Where Clause
		ElementGroup eWhere = new ElementGroup();

		// creating construct
		qConstruct.setConstructTemplate(
				new GenerateQueryConstructProperties().generateQueryProperties(
						nodeShape.getProperties(),
						focusVar)
				);
		// Query Type
		qConstruct.setQueryConstructType();

		// Creating optional clause
		for(PropertyShape sproperty:nodeShape.getProperties()) {

			// we ignore the inverse path here
			if(sproperty.isPathURI()) {		
			
				ElementGroup group = new ElementGroup();
	
				// Generated Optional Element 
				ElementTriplesBlock eOptional= SparqlQueryHelper.initElementTriplesBlock(
						NodeFactory.createVariable(focusVar),
						NodeFactory.createURI(sproperty.getPath().getURI()),
						NodeFactory.createVariable(sproperty.getPath().getLocalName())
						);
	
				group.addElement(eOptional);
	
				Var nVar = Var.alloc(sproperty.getPath().getLocalName());
	
				// Other conditions
				/*
				 * Read the property sh:hasValue
				 */
				if(sproperty.getHasValue() != null) {
					ElementData eDataValues = new ElementData();
					eDataValues.add(nVar);
	
					if(sproperty.getHasValue().isURIResource()) {
						eDataValues.add(new BindingFactory().binding(
								nVar, 
								new NodeFactory().createURI(sproperty.getHasValue().asResource().getURI()))
								);	
					}
					group.addElement(eDataValues);
				}
	
				// Sh In
				/*
				 * Read the property sh:In
				 */
				if(sproperty.getIn() != null) {
	
					List<Node> eListNodeIn = new ArrayList<Node>();
					for (RDFNode rdfIn : sproperty.getIn()) {
						eListNodeIn.add(new NodeFactory().createURI(rdfIn.toString()));
					}
	
					ElementData nDataIn = SparqlQueryHelper.initElementData(nVar, eListNodeIn);						
					group.addElement(nDataIn);
				}
	
				/*
				 * Read the property sh:LanguageIn
				 */
				if(sproperty.getLanguageIn() != null) {
	
					ElementFilter eFilter = SparqlQueryHelper.initElementFilter(
							new ExprVar(sproperty.getPath().getLocalName().toString()), 
							sproperty.getLanguageIn());
					group.addElement(eFilter);						
				}
	
				eWhere.addElement(new ElementOptional(group));
			}

		} // End for properties

		// Set prefix QueryOutput
		List<PrefixDeclaration> namespaceSections = PrefixDeclaration.fromMap(necessaryPrefixes);
		for (PrefixDeclaration prefixUse : namespaceSections) {
			qConstruct.setPrefix(prefixUse.getprefix(), prefixUse.getnamespace());
		}

		if(stepsFromRootNodeShape != null && stepsFromRootNodeShape.size() != 0) {

			Query querySelect = new Query();
			querySelect.setQuerySelectType();
			querySelect.setDistinct(true);	
			querySelect.getProject().add(Var.alloc(
					stepsFromRootNodeShape.get(stepsFromRootNodeShape.size()-1).getVarName())
					);

			ElementGroup querySelectWhere = new ElementGroup();
			querySelect.setQueryPattern(querySelectWhere);


			for(int i=0; i<stepsFromRootNodeShape.size(); i++) {
				// for each step...
				ShaclParsingStep currentStep = stepsFromRootNodeShape.get(i);

				// generate the path
				Node subject = (i==0)?Var.alloc("this"):Var.alloc(stepsFromRootNodeShape.get(i-1).getVarName());
				Path predicatePath = currentStep.getPath();
				Node object = Var.alloc(currentStep.getVarName());

				ElementPathBlock ePathBlock = new ElementPathBlock();
				ePathBlock.addTriple(new TriplePath(subject,predicatePath ,object));					
				querySelectWhere.addElement(ePathBlock);

				// add filtering criterias from sh:hasValue and sh:in
				for(PropertyShape nsproperty : currentStep.getNodeShape().getProperties()) {					
					if(nsproperty.hasSingleValue()) {
						// if the property shape has a single value (either sh:hasValue or a sh:in with 1 value)
						// then we don't use a SPARQL VALUES
						RDFNode singleValue = nsproperty.getSingleValue();
						
						ElementTriplesBlock eBlockT = SparqlQueryHelper.initElementTriplesBlock(
								Var.alloc(currentStep.getVarName()),
								NodeFactory.createURI(nsproperty.getPath().getURI()), 
								singleValue.asNode()
								);
						
						querySelectWhere.addElement(eBlockT);
					} else if(nsproperty.requiresValues()) {
						// if it has multiple values, we insert a SPARQL VALUES
						ElementGroup eGpo = new ElementGroup();
						List<Node> eListNodeIn = new ArrayList<Node>();

						Var varProperty = Var.alloc(Var.alloc(nsproperty.getPath().getLocalName()));

						for (RDFNode rdfIn : nsproperty.getIn()) {
							eListNodeIn.add(rdfIn.asNode());
						}

						//Triple
						ElementTriplesBlock eBlockT = SparqlQueryHelper.initElementTriplesBlock(
								Var.alloc(currentStep.getVarName()),
								NodeFactory.createURI(nsproperty.getPath().getURI()), 
								varProperty
								);

						eGpo.addElement(eBlockT);
						ElementData nDataIn = SparqlQueryHelper.initElementData(varProperty, eListNodeIn);						
						eGpo.addElement(nDataIn);

						querySelectWhere.addElement(eGpo);
					}
				} // 

			} // end for each step


			// Add SubQuery of initial NodeShape
			try {				
				Query subSubQuery = rootNodeShape.getTargetSelect();
				PrefixMapping pm = subSubQuery.getPrefixMapping();
				pm.clearNsPrefixMap(); // Clean the prefix in the query focus
				subSubQuery.setPrefixMapping(pm);
				querySelectWhere.addElement(new ElementSubQuery(subSubQuery));						
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			// add as subQuery
			eWhere.addElement(new ElementSubQuery(querySelect));

		} else {

			// no path, put query directly as sub-query
			if (nodeShape.getTargetSelect() != null) {
				try {
					PrefixMapping pm = rootNodeShape.getTargetSelect().getPrefixMapping();
					pm.clearNsPrefixMap(); // Clean the prefix in the query focus
					nodeShape.getTargetSelect().setPrefixMapping(pm);
					eWhere.addElement(new ElementSubQuery(nodeShape.getTargetSelect()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}


		}

		qConstruct.setQueryPattern(eWhere);
		

		String nameFile = this.getOutputFileName(rootNodeShape, stepsFromRootNodeShape);	
		
		if(queryFiles.containsKey(nameFile)) {			
			//get the last property
			ShaclParsingStep sps = stepsFromRootNodeShape.get(stepsFromRootNodeShape.size()-1);
			String currentProperty = sps.getPropertyShape().getPath().getLocalName();
			String[] newName = nameFile.split(".rq");
			String newNameFile = newName[0]+ "_"+currentProperty+".rq";
			nameFile = newNameFile;
		}
		
		//Save in Map the name file and Query 
		queryFiles.put(nameFile, qConstruct.toString());
		
		//out.close();
		System.out.println(qConstruct);


		// Node in the property
		for(PropertyShape ps : nodeShape.getProperties()) {
			// Node
			if(ps.getNode()!=null) {
				// Stop condition : we get to a NodeShape we have already traversed in the path
				boolean alreadyVisitedInPath = stepsFromRootNodeShape.stream().anyMatch(s -> s.getNodeShape().getNodeShapeResource().getURI().equals(ps.getNode().getNodeShapeResource().getURI()));
				if(
						!alreadyVisitedInPath
						&&
						!ps.getNode().getNodeShapeResource().getURI().equals(rootNodeShape.getNodeShapeResource().getURI())							
				) {
					ArrayList<ShaclParsingStep> newSteps = new ArrayList<ShaclParsingStep>(stepsFromRootNodeShape);
					// add step in our route
					if(ps.isPathURI()) {
						newSteps.add(new ShaclParsingStep(ps, false, ps.getNode()));
					} else if(
						ps.getPath().hasProperty(SH.inversePath)	
					) {
						newSteps.add(new ShaclParsingStep(ps, true, ps.getNode()));
					} else {
						throw new IllegalArgumentException("Cannot handle this SHACL path. Can handle only simple property or sh:inversePath");
					}
					

					processOneNodeShape(
							ps.getNode(),
							newSteps,
							rootNodeShape,
							necessaryPrefixes
					);
				}
			} 
		}

		// Or
		for(PropertyShape psOr : nodeShape.getProperties()) {
			if(psOr.getOrNode()!=null) {
				for(NodeShape psOrNodeShape: psOr.getOrNode()) {
					
					boolean alreadyVisitedInPath = stepsFromRootNodeShape
							.stream().anyMatch(s -> s.getNodeShape().getNodeShapeResource().getURI().equals(psOrNodeShape.getNodeShapeResource().getURI()));
					
					if(
							!alreadyVisitedInPath
							&&
							!psOrNodeShape.getNodeShapeResource().getURI().equals(rootNodeShape.getNodeShapeResource().getURI())							
					) {
						ArrayList<ShaclParsingStep> newSteps = new ArrayList<ShaclParsingStep>(stepsFromRootNodeShape);
						// add step in our route
						if(psOr.isPathURI()) {
							newSteps.add(new ShaclParsingStep(psOr, false, psOrNodeShape));
						} else if(
								psOr.getPath().hasProperty(SH.inversePath)	
						) {
							newSteps.add(new ShaclParsingStep(psOr, true, psOrNodeShape));
						} else {
							throw new IllegalArgumentException("Cannot handle this SHACL path. Can handle only simple property or sh:inversePath");
						}
						
						processOneNodeShape(
								psOrNodeShape,
								newSteps,
								rootNodeShape,
								necessaryPrefixes
					);
						
					}
				}
			}
		} 
	}
	
	
	
	protected void processOneNodeShapeCombine(
			// node shape we are currently processing
			NodeShape nodeShape,
			// steps from root NodeShape : each step indicate which PropertyPath was followed, if it was a reverse
			// and to which NodeShape
			List<ShaclParsingStep> stepsFromRootNodeShape,
			// root node shape we started from
			NodeShape rootNodeShape,
			// prefixes we need to properly generate the query
			Map<String, String> necessaryPrefixes
			
	) throws IOException {


		String focusVar = (stepsFromRootNodeShape.size() > 0)?(stepsFromRootNodeShape.get(stepsFromRootNodeShape.size()-1).getVarName()):"this";

		ElementGroup eWhere = new ElementGroup();
		ElementGroup eWherePath = new ElementGroup();

		Var vPredicate = Var.alloc("p");
		
		
		//Building Values
		ElementData eDataValues = new ElementData();
		for(PropertyShape sproperty:nodeShape.getProperties()) {
			if(sproperty.isPathURI()) {
				eDataValues.add(vPredicate);
				eDataValues.add(new BindingFactory().binding(
							vPredicate, 
							new NodeFactory().createURI(sproperty.getPath().getURI()))
							);	
			}
			
		}
		
		if(stepsFromRootNodeShape != null && stepsFromRootNodeShape.size() != 0) {
			//ElementGroup querySelectWhere = new ElementGroup();
			for(int i=0; i<stepsFromRootNodeShape.size(); i++) {
				// for each step...
				ShaclParsingStep currentStep = stepsFromRootNodeShape.get(i);
				
				// generate the path
				Node subject = (i==0)?Var.alloc("this"):Var.alloc(stepsFromRootNodeShape.get(i-1).getVarName());
				Path predicatePath = currentStep.getPath();
				//Node object = Var.alloc(currentStep.getVarName());
				
				Node object = null;
				if(currentStep.getVarName().equals(nodeShape.getNodeShapeResource().getLocalName().toString())
						) {
					object = Var.alloc("s");
				}else {
					object = Var.alloc(currentStep.getVarName());
				}
				ElementPathBlock ePathBlock = new ElementPathBlock();
				ePathBlock.addTriple(new TriplePath(subject,predicatePath ,object));
				eWhere.addElement(ePathBlock);
				
				// add filtering criterias from sh:hasValue and sh:in
				for(PropertyShape nsproperty : currentStep.getNodeShape().getProperties()) {					
					if(nsproperty.hasSingleValue()) {
						// if the property shape has a single value (either sh:hasValue or a sh:in with 1 value)
						// then we don't use a SPARQL VALUES
						RDFNode singleValue = nsproperty.getSingleValue();
						
						ElementTriplesBlock eBlockT = SparqlQueryHelper.initElementTriplesBlock(
								//Var.alloc(currentStep.getVarName()),
								object,
								NodeFactory.createURI(nsproperty.getPath().getURI()), 
								singleValue.asNode()
								);
						eWhere.addElement(eBlockT);
						
					} else if(nsproperty.requiresValues()) {
						// if it has multiple values, we insert a SPARQL VALUES
						ElementGroup eGpo = new ElementGroup();
						List<Node> eListNodeIn = new ArrayList<Node>();

						Var varProperty = Var.alloc(Var.alloc(nsproperty.getPath().getLocalName()));

						for (RDFNode rdfIn : nsproperty.getIn()) {
							eListNodeIn.add(rdfIn.asNode());
						}

						//Triple
						ElementTriplesBlock eBlockT = SparqlQueryHelper.initElementTriplesBlock(
								//Var.alloc(currentStep.getVarName()),
								object,
								NodeFactory.createURI(nsproperty.getPath().getURI()), 
								varProperty
								);

						eGpo.addElement(eBlockT);
						ElementData nDataIn = SparqlQueryHelper.initElementData(varProperty, eListNodeIn);						
						eGpo.addElement(nDataIn);
					}
				} //
									
			} // end for each step
		}
		
		
		if(eDataValues != null && eWhere.isEmpty()){
			ElementBind eBind = new ElementBind(Var.alloc("s") , new ExprVar("this"));
			eWhere.addElement(eBind);
			eWhere.addElement(eDataValues);
		}else {
			eWhere.addElement(eDataValues);
		}
		
		
		if(eWhere != null) {
			pathShacl.add(eWhere);
		}

		// Node in the property
		for(PropertyShape ps : nodeShape.getProperties()) {
			// Node
			if(ps.getNode()!=null) {
				// Stop condition : we get to a NodeShape we have already traversed in the path
				boolean alreadyVisitedInPath = stepsFromRootNodeShape.stream().anyMatch(s -> s.getNodeShape().getNodeShapeResource().getURI().equals(ps.getNode().getNodeShapeResource().getURI()));
				if(
						!alreadyVisitedInPath
						&&
						!ps.getNode().getNodeShapeResource().getURI().equals(rootNodeShape.getNodeShapeResource().getURI())							
				) {
					ArrayList<ShaclParsingStep> newSteps = new ArrayList<ShaclParsingStep>(stepsFromRootNodeShape);
					// add step in our route
					if(ps.isPathURI()) {
						newSteps.add(new ShaclParsingStep(ps, false, ps.getNode()));
					} else if(
						ps.getPath().hasProperty(SH.inversePath)	
					) {
						newSteps.add(new ShaclParsingStep(ps, true, ps.getNode()));
					} else {
						throw new IllegalArgumentException("Cannot handle this SHACL path. Can handle only simple property or sh:inversePath");
					}
					

					processOneNodeShapeCombine(
							ps.getNode(),
							newSteps,
							rootNodeShape,
							necessaryPrefixes
					);
				}
			} 
		}

		// Or
		for(PropertyShape psOr : nodeShape.getProperties()) {
			if(psOr.getOrNode()!=null) {
				for(NodeShape psOrNodeShape: psOr.getOrNode()) {
					
					boolean alreadyVisitedInPath = stepsFromRootNodeShape
							.stream().anyMatch(s -> s.getNodeShape().getNodeShapeResource().getURI().equals(psOrNodeShape.getNodeShapeResource().getURI()));
					
					if(
							!alreadyVisitedInPath
							&&
							!psOrNodeShape.getNodeShapeResource().getURI().equals(rootNodeShape.getNodeShapeResource().getURI())							
					) {
						ArrayList<ShaclParsingStep> newSteps = new ArrayList<ShaclParsingStep>(stepsFromRootNodeShape);
						// add step in our route
						if(psOr.isPathURI()) {
							newSteps.add(new ShaclParsingStep(psOr, false, psOrNodeShape));
						} else if(
								psOr.getPath().hasProperty(SH.inversePath)	
						) {
							newSteps.add(new ShaclParsingStep(psOr, true, psOrNodeShape));
						} else {
							throw new IllegalArgumentException("Cannot handle this SHACL path. Can handle only simple property or sh:inversePath");
						}
						
						processOneNodeShapeCombine(
								psOrNodeShape,
								newSteps,
								rootNodeShape,
								necessaryPrefixes
					);
						
					}
				}
			}
		}
	}
	
	
	
	
	protected String getOutputFileName(NodeShape rootNodeShape, List<ShaclParsingStep> steps) {
		String result = rootNodeShape.getNodeShapeResource().getLocalName();
		for (ShaclParsingStep shaclParsingStep : steps) {
			result += "_"+shaclParsingStep.getVarName();
		}
				
		result += ".rq";
		return result;
	}
	
	

	
}
