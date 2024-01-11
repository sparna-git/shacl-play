package fr.sparna.rdf.shacl.app.analyze;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;
import fr.sparna.rdf.shacl.generate.visitors.AssignValueOrInVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeValueStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.CopyStatisticsToDescriptionVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;
import fr.sparna.rdf.shacl.generate.visitors.AssignValueOrInVisitor.StatisticsBasedRequiresShValueOrInPredicate;

public class Analyze implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsAnalyze a = (ArgumentsAnalyze)args;
		
		Model shapes = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModel(shapes, a.getShapes());
		
		
		ShaclGeneratorDataProviderIfc dataProvider;
		if(a.getEndpoint() != null) {
			dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), a.getEndpoint());
		} else {
			Model inputModel = ModelFactory.createDefaultModel(); 
			InputModelReader.populateModel(inputModel, a.getInput());
			dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), inputModel);		
		}
		
		Model countModel = ModelFactory.createDefaultModel(); 
		
		ShaclVisit modelStructure = new ShaclVisit(shapes);
		modelStructure.visit(new ComputeStatisticsVisitor(
				dataProvider,
				countModel,
				(a.getEndpoint() != null)?a.getEndpoint():"https://dummy.dataset.uri"
		));

		AssignValueOrInVisitor yetAnotherTryOnAssigningValues = new AssignValueOrInVisitor(dataProvider);
		yetAnotherTryOnAssigningValues.setRequiresShValueInPredicate(yetAnotherTryOnAssigningValues.new StatisticsBasedRequiresShValueOrInPredicate(countModel));
		modelStructure.visit(yetAnotherTryOnAssigningValues);
		modelStructure.visit(new ComputeValueStatisticsVisitor(dataProvider,countModel));
		modelStructure.visit(new CopyStatisticsToDescriptionVisitor(countModel));

		// write output file
		try(OutputStream out = new FileOutputStream(a.getOutput())) {
			countModel.write(out,FileUtils.guessLang(a.getOutput().getName(), "Turtle"));
		}
		
		// if provided, also write the shapes file
		if (a.getOutputShapes() != null) {
			try(OutputStream out = new FileOutputStream(a.getOutputShapes())) {
				shapes.write(out,FileUtils.guessLang(a.getOutputShapes().getName(), "Turtle"));
			}	
		}
		
	}
}
