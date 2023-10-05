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
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;

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
		
		Model outputModel = ModelFactory.createDefaultModel(); 
		
		ShaclVisit modelStructure = new ShaclVisit(shapes);
		modelStructure.visit(new ComputeStatisticsVisitor(
				dataProvider,
				outputModel,
				(a.getEndpoint() != null)?a.getEndpoint():"https://dummy.dataset.uri",
				false
		));

		// write output file
		OutputStream out = new FileOutputStream(a.getOutput());
		outputModel.write(out,FileUtils.guessLang(a.getOutput().getName(), "Turtle"));				
		
	}
}
