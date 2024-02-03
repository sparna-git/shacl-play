package fr.sparna.rdf.shacl.app.report;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.MultiUnion;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.printer.report.SimpleCSVValidationResultWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReport;
import fr.sparna.rdf.shacl.printer.report.ValidationReportRawDatatableWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReportSummaryDatatableWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReportHtmlWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReportOutputFormat;
import fr.sparna.rdf.shacl.printer.report.ValidationReportRdfWriter;
import fr.sparna.rdf.shacl.printer.report.ValidationReportWriterRegistry;


public class GenerateReport implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object o) throws Exception {
		ArgumentsGenerateReport args = (ArgumentsGenerateReport)o;		
		
		// read input files
		Model validationReport = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(validationReport, args.getInput());
		
		Model shapesModel = ModelFactory.createDefaultModel();
		if(args.getShapes() != null) {
			shapesModel = InputModelReader.populateModelFromFile(shapesModel, args.getShapes());
		}
		
		// union results and shapes
		Model fullModel = ModelFactory.createModelForGraph(new MultiUnion(new Graph[] {
				validationReport.getGraph(),
				shapesModel.getGraph()
		}));
		
		// encapsulate in a ValidationReport
		ValidationReport report = new ValidationReport(validationReport, fullModel);
		
		
		// print
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRawDatatableWriter());
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportSummaryDatatableWriter());
		ValidationReportWriterRegistry.getInstance().register(new SimpleCSVValidationResultWriter());
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportHtmlWriter(true));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.TTL));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.RDFXML));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.JSONLD));
		ValidationReportWriterRegistry.getInstance().register(new ValidationReportRdfWriter(Lang.NT));
		
		for(File outputFile : args.getOutput()) {
			
			// create output dir if not existing
			File outputDir = outputFile.getParentFile();
			if(outputFile != null && outputDir != null && !outputDir.exists()) {
				outputDir.mkdirs();
			}
			
			
			log.debug("Writing validation report to "+outputFile.getAbsolutePath());
			ValidationReportWriterRegistry.getInstance().getWriter(ValidationReportOutputFormat.forFileName(outputFile.getName()))
			.orElse(new ValidationReportRdfWriter(Lang.TTL))
			.write(report, new FileOutputStream(outputFile), Locale.getDefault());
		}
	}

}
