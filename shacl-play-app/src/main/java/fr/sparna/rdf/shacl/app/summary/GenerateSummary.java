package fr.sparna.rdf.shacl.app.summary;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.MultiUnion;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.printer.report.ValidationReport;
import fr.sparna.rdf.shacl.printer.report.ValidationReportDatatableSummaryWriter;


public class GenerateSummary implements CliCommandIfc {

	@Override
	public void execute(Object o) throws Exception {
		ArgumentsGenerateSummary args = (ArgumentsGenerateSummary)o;	
		
		// read input files
		Model validationReport = InputModelReader.readInputModel(args.getInput());
		
		Model shapesModel = ModelFactory.createDefaultModel();
		if(args.getShapes() != null) {
			shapesModel = InputModelReader.readInputModel(args.getShapes());
		}
		
		// union results and shapes
		Model fullModel = ModelFactory.createModelForGraph(new MultiUnion(new Graph[] {
				validationReport.getGraph(),
				shapesModel.getGraph()
		}));
		
		// encapsulate in a ValidationReport
		ValidationReport report = new ValidationReport(validationReport, fullModel);
		
		// create output dir if not existing
		File outputDir = args.getOutput().getParentFile();
		if(args.getOutput().getParentFile() != null && !outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		// print
		ValidationReportDatatableSummaryWriter printer = new ValidationReportDatatableSummaryWriter();
		printer.write(report, new FileOutputStream(args.getOutput()), Locale.getDefault());
	}

}
