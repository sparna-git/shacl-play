package fr.sparna.rdf.shacl.app.analyze;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.QueryExecutionServiceImpl;
import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.providers.BaseShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.providers.BaseShaclStatisticsDataProvider;
import fr.sparna.rdf.shacl.generate.providers.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.providers.ShaclGeneratorDataProviderIfc;
import fr.sparna.rdf.shacl.generate.providers.ShaclStatisticsDataProviderIfc;
import fr.sparna.rdf.shacl.generate.visitors.AssignValueOrInVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeValueStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.CopyStatisticsToDescriptionVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;

public class Analyze implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsAnalyze a = (ArgumentsAnalyze)args;
		
		Model shapes = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModel(shapes, a.getShapes());
		
		QueryExecutionServiceImpl queryExecutionService;
		if(a.getEndpoint() != null) {
			queryExecutionService = new QueryExecutionServiceImpl(a.getEndpoint());
		} else {
			Model inputModel = ModelFactory.createDefaultModel(); 
			InputModelReader.populateModelFromFile(inputModel, a.getInput());
			queryExecutionService = new QueryExecutionServiceImpl(inputModel);
		}

		ShaclGeneratorDataProviderIfc dataProvider = new BaseShaclGeneratorDataProvider(queryExecutionService);	
		ShaclStatisticsDataProviderIfc statisticsProvider = new BaseShaclStatisticsDataProvider(queryExecutionService);	
		
		Model countModel = ModelFactory.createDefaultModel(); 
		
		ShaclVisit modelStructure = new ShaclVisit(shapes);
		modelStructure.visit(new ComputeStatisticsVisitor(
				dataProvider,
				statisticsProvider,
				countModel,
				(a.getEndpoint() != null)?a.getEndpoint():"https://dummy.dataset.uri"
		));

		// we are going to assign sh:in to the property shapes based on the statistics gathered
		AssignValueOrInVisitor yetAnotherTryOnAssigningValues = new AssignValueOrInVisitor(dataProvider);
		yetAnotherTryOnAssigningValues.setRequiresShValueInPredicate(yetAnotherTryOnAssigningValues.new StatisticsBasedRequiresShValueOrInPredicate(countModel));
		modelStructure.visit(yetAnotherTryOnAssigningValues);
		// then we add value statistics to the count model
		modelStructure.visit(new ComputeValueStatisticsVisitor(dataProvider,statisticsProvider,countModel));
		// then we copy the statistics in the description of the shape
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
