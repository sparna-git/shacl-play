package fr.sparna.rdf.shacl.app.validate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.MultiUnion;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.printer.report.SimpleCSVValidationResultWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReport;
import fr.sparna.rdf.shacl.printer.report.ValidationReportHtmlWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReportOutputFormat;
import fr.sparna.rdf.shacl.printer.report.ValidationReportRawDatatableWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReportRdfWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReportSummaryDatatableWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReportWriterRegistry;
import fr.sparna.rdf.shacl.validator.ShaclValidator;
import fr.sparna.rdf.shacl.validator.Slf4jProgressMonitor;

public class Validate implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsValidate a = (ArgumentsValidate)args;		
				
		// read shapes file
		OntModel shapesModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		log.debug("Reading shapes from "+a.getShapes());
		InputModelReader.populateModelFromFile(shapesModel, a.getShapes(), a.getNamespaceMappings());

		// read input file or URL
		Model dataModel = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(dataModel, a.getInput(), a.getNamespaceMappings());
		
		// if we are asked to copy input, copy it
		if(a.getCopyInput() != null) {
			log.debug("Copy input data to "+a.getCopyInput());
			dataModel.write(new FileOutputStream(a.getCopyInput().getAbsolutePath()), "Turtle");
		}
		
		// read extra model
		Model extraModel = null;
		if(a.getExtra() != null) {
			extraModel = ModelFactory.createDefaultModel();
			InputModelReader.populateModelFromFile(extraModel, a.getExtra(), a.getNamespaceMappings());
		}
		
		// run the validator
		ShaclValidator validator = new ShaclValidator(shapesModel, extraModel);
		validator.setCreateDetails(a.isCreateDetails());
		validator.setProgressMonitor(new Slf4jProgressMonitor("SHACL validator", log));

		// we are asking for the extra gathering of focus nodes and the validation of
		// whether shapes matched focus nodes and whether all nodes were targeted by at least a shape
		validator.setResolveTargets(!a.isAvoidResolvingTargets());

		Model validationResults = validator.validate(dataModel);
		
		// union results and shapes
		Model fullModel = ModelFactory.createModelForGraph(new MultiUnion(new Graph[] {
				validationResults.getGraph(),
				shapesModel.getGraph()
		}));
		
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRawDatatableWriter());
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportSummaryDatatableWriter());
		ValidationReportWriterRegistry.getInstance().register(new SimpleCSVValidationResultWriter());
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportHtmlWriter(true));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.TTL));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.RDFXML));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.JSONLD));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.NT));
		
		for(File outputFile : a.getOutput()) {
			
			// create output dir if not existing
			File outputDir = outputFile.getParentFile();
			if(outputDir != null && !outputDir.exists()) {
				outputDir.mkdirs();
			}
			
			log.debug("Writing validation report to "+outputFile.getAbsolutePath());
			
			ValidationReportWriterRegistry.getInstance().getWriter(ValidationReportOutputFormat.forFileName(outputFile.getName()))
			.orElse(new ValidationReportRdfWriter(Lang.TTL))
			.write(new ValidationReport(validationResults, fullModel), new FileOutputStream(outputFile), Locale.getDefault());
		}		
	}
}
