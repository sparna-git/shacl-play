package fr.sparna.rdf.shacl.app.validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.printer.report.SimpleCSVValidationResultWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReport;
import fr.sparna.rdf.shacl.printer.report.ValidationReportDatatableFullWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReportOutputFormat;
import fr.sparna.rdf.shacl.printer.report.ValidationReportRdfWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReportWriterRegistry;
import fr.sparna.rdf.shacl.validator.ShaclValidator;
import fr.sparna.rdf.shacl.validator.Slf4jProgressMonitor;

public class Validate implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsValidate a = (ArgumentsValidate)args;
		
		// read input file or URL
		Model dataModel = InputModelReader.readInputModel(a.getInput());
		
		// if we are asked to copy input, copy it
		if(a.getCopyInput() != null) {
			log.debug("Copy input data to "+a.getCopyInput());
			dataModel.write(new FileOutputStream(a.getCopyInput().getAbsolutePath()), "Turtle");
		}
		
		// read shapes file
		OntModel shapesModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		log.debug("Reading shapes from "+a.getShapes().getAbsolutePath());
		shapesModel.read(
				new FileInputStream(a.getShapes()),
				// so that relative URI references are found in the same directory that the shapes file
				a.getShapes().toPath().toAbsolutePath().getParent().toUri().toString(),
				RDFLanguages.filenameToLang(a.getShapes().getName(), Lang.RDFXML).getName()
		);
		
		// read extra model
		Model extraModel = null;
		if(a.getExtra() != null) {
			extraModel = ModelFactory.createDefaultModel();
			RDFDataMgr.read(extraModel, new FileInputStream(a.getExtra()), RDF.getURI(), RDFLanguages.filenameToLang(a.getShapes().getName(), Lang.RDFXML));
		}
		
		// run the validator
		ShaclValidator validator = new ShaclValidator(shapesModel, extraModel);
		validator.setCreateDetails(a.isCreateDetails());
		validator.setProgressMonitor(new Slf4jProgressMonitor("SHACL validator", log));
		Model validationResults = validator.validate(dataModel);
		
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportDatatableFullWriter());
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.TTL));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.RDFXML));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.JSONLD));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.NT));
		ValidationReportWriterRegistry.getInstance().register(new SimpleCSVValidationResultWriter());
		
		for(File outputFile : a.getOutput()) {
			ValidationReportWriterRegistry.getInstance().getWriter(ValidationReportOutputFormat.forFileName(outputFile.getName()))
			.orElse(new ValidationReportRdfWriter(Lang.TTL))
			.write(new ValidationReport(validationResults), new FileOutputStream(outputFile), Locale.getDefault());
		}		
	}
}
