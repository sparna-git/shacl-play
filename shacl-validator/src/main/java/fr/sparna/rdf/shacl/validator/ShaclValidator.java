package fr.sparna.rdf.shacl.validator;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.MultiUnion;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
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
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.shacl.validation.sparql.AbstractSPARQLExecutor;
import org.topbraid.shacl.vocabulary.SH;
import org.topbraid.shacl.vocabulary.TOSH;

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
	 * The additionnal data to add to the validated before validation
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
		log.info("Validating data with "+dataModel.size()+" triples...");
		
		// Create Dataset that contains both the main query model and the shapes model
		// (here, using a temporary URI for the shapes graph)		
		Model validatedModel;
		if(this.complimentaryModel != null) {
			log.info("Addind a complimentary model of "+complimentaryModel.size()+" triples...");
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
			
			return results;			
			
		} catch (InterruptedException e) {
			throw new ShaclValidatorException();
		}
	}
	
	public Model validateShapesTargets(Model dataModel, Model validationResultsModel) throws ShaclValidatorException {
		
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
		
		ShapeTargetValidator targetValidator = new ShapeTargetValidator();
		List<Resource> shapesWithoutTarget = targetValidator.listShapesWithEmptyTargets(this.shapesModel, validatedModel);
		
		Model resultModel = ModelFactory.createDefaultModel();
		shapesWithoutTarget.forEach(aShapeWithoutTarget -> { 
			log.debug("Shape "+aShapeWithoutTarget+" did not match any focus node");
			resultModel.add(resultModel.createLiteralStatement(
					aShapeWithoutTarget,
					// TODO : this should be completely changed
					SH.target,
					false
			));
		});
		
		return resultModel;
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

	public static void main(String...strings) throws Exception {
		Model dataModel = ModelFactory.createDefaultModel();
		// RDFDataMgr.read(dataModel, new FileInputStream(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/sparna/eu.europa.publications/eli/validator/shacl-validator/src/test/resources/sample-legifrance.ttl")), Lang.TURTLE);
		RDFDataMgr.read(dataModel, new FileInputStream(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/sparna/eu.europa.publications/eli/validator/eli-validator/src/test/resources/test-legilux.ttl")), Lang.TURTLE);
		
		Model shapesModel = ModelFactory.createDefaultModel();
		RDFDataMgr.read(shapesModel, new FileInputStream(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/sparna/eu.europa.publications/eli/validator/eli-validator/src/main/webapp/shapes/eli-1.1-shapes.ttl")), Lang.TURTLE);
		
		System.out.println("Validate data model size "+dataModel.size()+" with shapes model size "+shapesModel.size());
		
//		ShaclValidator validator = new ShaclValidator(shapesModel);
//		validator.setProgressMonitor(new StringBufferProgressMonitor("test"));
//		Model results = validator.validate(dataModel);
//		// results.write(System.out, "Turtle");
//		System.out.println(results.size());
		
		Resource report2 = ValidationUtil.validateModel(dataModel, shapesModel, true);
		System.out.println(report2.getModel().size());
	}
	
}
