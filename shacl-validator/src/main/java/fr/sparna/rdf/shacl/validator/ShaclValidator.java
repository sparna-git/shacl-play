package fr.sparna.rdf.shacl.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.MultiUnion;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.jenax.progress.ProgressMonitor;
import org.topbraid.jenax.util.ARQFactory;
import org.topbraid.jenax.util.RDFLabels;
import org.topbraid.shacl.arq.SHACLFunctions;
import org.topbraid.shacl.engine.ShapesGraph;
import org.topbraid.shacl.engine.filters.ExcludeMetaShapesFilter;
import org.topbraid.shacl.validation.ValidationEngine;
import org.topbraid.shacl.validation.ValidationEngineConfiguration;
import org.topbraid.shacl.validation.ValidationEngineFactory;
import org.topbraid.shacl.vocabulary.SH;
import org.topbraid.shacl.vocabulary.TOSH;

import fr.sparna.rdf.shacl.targets.AddHasTargetListener;
import fr.sparna.rdf.shacl.targets.ShapesTargetResolver;
import fr.sparna.rdf.shacl.targets.StoreHasFocusNodeListener;

/**
 * A wrapper around the Shapes API
 * @author Thomas Francart
 *
 */
public class ShaclValidator {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * The model containing the original shapes
	 */
	protected Model shapesModel;
	
	/**
	 * The additionnal data to add to the validated data before validation
	 */
	protected Model complimentaryModel;
	
	/**
	 * Still not quite sure what this is used for
	 */
	protected boolean validateShapes = false;
	
	/**
	 * A ProgressMonitor (that will store all progress logs in a StringBuffer, or log them in a log stream)
	 */
	protected ProgressMonitor progressMonitor;
	
	/**
	 * Whether to add a step to validate that each shapes actually matched some focus nodes
	 */
	protected boolean resolveTargets = false;
	
	/**
	 * Create more details for OrComponent and AndComponent ?
	 */
	protected boolean createDetails = false;
	
	public ShaclValidator(Model shapesModel) {
		this(shapesModel, null);
	}
	
	public ShaclValidator(Model shapesModel, Model complimentaryModel) {
		super();
		
		// stores the original shapes Model
		this.shapesModel = shapesModel;
		// stores the complimentary Model
		this.complimentaryModel = complimentaryModel;
	}

	public Model validate(Model dataModel) throws ShaclValidatorException {
		log.info("Validating data with "+dataModel.size()+" triples... we "+(this.createDetails?"ARE":"are NOT")+" creating details");
		
		Model validatedModel;
		if(this.complimentaryModel != null) {
			log.info("Adding a complimentary model of "+complimentaryModel.size()+" triples...");
			validatedModel = ModelFactory.createModelForGraph(new MultiUnion(new Graph[] {
					dataModel.getGraph(),
					complimentaryModel.getGraph()
				}));
		} else {
			validatedModel = dataModel;
		}		
		
		// Ensure that the SHACL, DASH and TOSH graphs are present in the shapes Model
		Model actualShapesModel = ModelFactory.createDefaultModel();
		actualShapesModel.add(shapesModel);
		
		if(!actualShapesModel.contains(TOSH.hasShape, RDF.type, (RDFNode)null)) { // Heuristic
			Model unionModel = org.topbraid.shacl.util.SHACLSystemModel.getSHACLModel();
			MultiUnion unionGraph = new MultiUnion(new Graph[] {
				unionModel.getGraph(),
				actualShapesModel.getGraph()
			});
			actualShapesModel = ModelFactory.createModelForGraph(unionGraph);
		}
		
		// Make sure all sh:Functions are registered
		SHACLFunctions.registerFunctions(actualShapesModel);
		
		// Create Dataset that contains both the data model and the shapes model
		// (here, using a temporary URI for the shapes graph)
		URI shapesGraphURI = URI.create("urn:x-shacl-shapes-graph:" + UUID.randomUUID().toString());
		Dataset dataset = ARQFactory.get().getDataset(validatedModel);
		dataset.addNamedModel(shapesGraphURI.toString(), actualShapesModel);
		
		// Run the validator
		ShapesGraph shapesGraph = new ShapesGraph(actualShapesModel);
		if(!validateShapes) {
			shapesGraph.setShapeFilter(new ExcludeMetaShapesFilter());
		}
		
		ValidationEngine engine = ValidationEngineFactory.get().create(
				// the Dataset to validate
				dataset,
				// the URI of the shapes graph in the dataset
				shapesGraphURI,
				// the shapes graph
				shapesGraph,
				// an optional Report resource
				null);
		
		// set this to improve details of AndConstraintComponent or OrConstraintComponent
		ValidationEngineConfiguration config = new ValidationEngineConfiguration();
		config.setReportDetails(this.createDetails);
		engine.setConfiguration(config);
		
		// set custom label function to properly display lists
		engine.setLabelFunction(v -> {
			if(v.isResource() && v.canAs( RDFList.class )) {
				return "["+v.as( RDFList.class ).asJavaList().stream().map(node -> RDFLabels.get().getNodeLabel(node)).collect(Collectors.joining(", "))+"]";
			}
			return RDFLabels.get().getNodeLabel(v);
		});
		
		// sets the progress monitor
		engine.setProgressMonitor(this.progressMonitor);
		
		try {
			Resource report = engine.validateAll();
			Model results = report.getModel();
			
			// register prefixes for nice validation report output in ttl
			results.setNsPrefixes(validatedModel.getNsPrefixMap());
			shapesModel.getNsPrefixMap().entrySet().stream().forEach(e -> { results.setNsPrefix(e.getKey(), e.getValue()); });
			
			// Number of validation results : results.listSubjectsWithProperty(RDF.type, SH.ValidationResult).toList().size()
			log.info("Done validating data with "+dataModel.size()+" triples. Validation results contains "+results.size()+" triples.");
			
			if(this.resolveTargets) {
				resolveFocusNodes(dataModel, results);
				secondPassValidation(dataModel, results);
				thirdPassValidation(dataModel, results);
			}
			
			return results;			
			
		} catch (InterruptedException e) {
			throw new ShaclValidatorException();
		}
	}
	
	public void resolveFocusNodes(Model dataModel, Model existingValidationReport) throws ShaclValidatorException {
		
		// recreate complete model by adding complimentary Model
		Model validatedModel;
		if(this.complimentaryModel != null) {
			validatedModel = ModelFactory.createModelForGraph(new MultiUnion(new Graph[] {
					dataModel.getGraph(),
					complimentaryModel.getGraph()
				}));
		} else {
			validatedModel = dataModel;
		}
		
		ShapesTargetResolver targetResolver = new ShapesTargetResolver(this.shapesModel, validatedModel);
		// stores the focus nodes both in the original data graph and in the validation report for review
		// in the data graph, this will be used for second pass and third pass validation
		targetResolver.getListeners().add(new StoreHasFocusNodeListener(existingValidationReport));
		targetResolver.getListeners().add(new StoreHasFocusNodeListener(dataModel));
		// we keep that so that the validation report can print the purple color when no shapes matched anything
		targetResolver.getListeners().add(new AddHasTargetListener(existingValidationReport));

		targetResolver.resolveTargets();
	}

	/**
	 * Checks that no resource exists in the data graph that is not the target of any shape
	 * @param dataModel
	 * @param existingValidationReport
	 * @throws ShaclValidatorException
	 */
	public void secondPassValidation(Model dataModel, Model existingValidationReport) throws ShaclValidatorException {
		// read hardcoded closed graphs shapes
		InputStream cgis = getClass().getClassLoader().getResourceAsStream("closed-graph-shapes.ttl");
		// then load as a Jena model
		Model secondPhaseShapesModel = ModelFactory.createDefaultModel();
		RDFDataMgr.read(secondPhaseShapesModel, cgis, Lang.TURTLE);

		ShaclValidator closedgraphValidator = new ShaclValidator(secondPhaseShapesModel);
		closedgraphValidator.setProgressMonitor(this.progressMonitor);
		closedgraphValidator.setCreateDetails(this.createDetails);
		// don't recurse infinitely !
		closedgraphValidator.setResolveTargets(false);

		// then validate the original data model that was enhanced with links to the shapes
		Model secondPhaseResults = closedgraphValidator.validate(dataModel);
		// and merge validation results with original validation results
		mergeValidationReports(existingValidationReport, secondPhaseResults);
	}

	/**
	 * Checks that all shapes have at least one focus node
	 * @param dataModel
	 * @param existingValidationReport
	 * @throws ShaclValidatorException
	 */
	public void thirdPassValidation(Model dataModel, Model existingValidationReport) throws ShaclValidatorException {
		// now validate a merge of the data graph and the shapes graph
		// this is to validate that all shapes have at least one focus node
		InputStream scis = getClass().getClassLoader().getResourceAsStream("shapes-coverage-shapes.ttl");
		// then load as a Jena model
		Model thirdPhaseShapesModel = ModelFactory.createDefaultModel();
		RDFDataMgr.read(thirdPhaseShapesModel, scis, Lang.TURTLE);

		ShaclValidator shapesCoverageValidator = new ShaclValidator(thirdPhaseShapesModel);
		shapesCoverageValidator.setProgressMonitor(this.progressMonitor);
		shapesCoverageValidator.setCreateDetails(this.createDetails);
		// don't recurse infinitely !
		shapesCoverageValidator.setResolveTargets(false);

		// then prepare a union of the data graph and the shapes graph
		// because we are targeting all shapes that have targets
		Model shapesCoverageDataModel = ModelFactory.createDefaultModel();
		shapesCoverageDataModel.add(dataModel);
		shapesCoverageDataModel.add(this.shapesModel);

		Model shapesCoverageResults = shapesCoverageValidator.validate(shapesCoverageDataModel);

		// and merge validation results with original validation results
		mergeValidationReports(existingValidationReport, shapesCoverageResults);
	}

	/**
	 * Merges another validation report into the existing one
	 * @param existingValidationReport
	 * @param otherValidationReport
	 */
	private static void mergeValidationReports(Model existingValidationReport, Model otherValidationReport) {
		// add all sh:ValidationResult from secondPhaseResults to existingValidationReport
		otherValidationReport.listResourcesWithProperty(RDF.type, SH.ValidationResult).forEachRemaining(vr -> {
			// if the validation result contains a property that refers to a blank node, add the blank node to the existing validation report, recursively
			// this is for sh:resultPath pointing to blank node property paths
			addResourceToModel(existingValidationReport, vr);
			// and link them from the report with SH.result
			existingValidationReport.listResourcesWithProperty(RDF.type, SH.ValidationReport).forEachRemaining(report -> {
				report.addProperty(SH.result, vr);
			});
		});
		
		// adjust the sh:conforms property on the existing report
		// if it was true but we added some results, it should be false
		if(existingValidationReport.listResourcesWithProperty(SH.result).hasNext()) {
			existingValidationReport.listResourcesWithProperty(RDF.type, SH.ValidationReport).forEachRemaining(report -> {
				report.removeAll(SH.conforms);
				report.addProperty(SH.conforms, existingValidationReport.createTypedLiteral(false));
			});
		}
	}

	private static void addResourceToModel(Model model, Resource resource) {
		model.add(resource.listProperties().toList());
		if(resource.isAnon()) {			
			resource.listProperties().forEachRemaining(p -> {
				if(p.getObject().isResource()) {
					addResourceToModel(model, p.getObject().asResource());
				}
			});
		}
	}


	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public Model getShapesModel() {
		return shapesModel;
	}
	
	public boolean isCreateDetails() {
		return createDetails;
	}

	public void setCreateDetails(boolean createDetails) {
		this.createDetails = createDetails;
	}

	public boolean isResolveTargets() {
		return resolveTargets;
	}

	public void setResolveTargets(boolean resolveTargets) {
		this.resolveTargets = resolveTargets;
	}

	public static void main(String...strings) throws Exception {
		Model dataModel = ModelFactory.createDefaultModel();
		// RDFDataMgr.read(dataModel, new FileInputStream(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/sparna/eu.europa.publications/eli/validator/shacl-validator/src/test/resources/sample-legifrance.ttl")), Lang.TURTLE);
		RDFDataMgr.read(dataModel, new FileInputStream(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/shacl-play/shacl-validator/src/test/resources/test-createDetails.ttl")), Lang.TURTLE);
		
		Model shapesModel = ModelFactory.createDefaultModel();
		RDFDataMgr.read(shapesModel, new FileInputStream(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/shacl-play/shacl-validator/src/test/resources/test-createDetails.ttl")), Lang.TURTLE);
		
		System.out.println("Validate data model size "+dataModel.size()+" with shapes model size "+shapesModel.size());
		
		ShaclValidator validator = new ShaclValidator(shapesModel);
		validator.setProgressMonitor(new StringBufferProgressMonitor("test"));
		validator.setCreateDetails(true);
		Model results = validator.validate(dataModel);
		results.write(System.out, "Turtle");
		// System.out.println(results.size());
		
//		Resource report2 = ValidationUtil.validateModel(dataModel, shapesModel, true);
//		System.out.println(report2.getModel().size());
	}
	
}
