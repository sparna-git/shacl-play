package fr.sparna.rdf.shacl.app.generate;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.DefaultModelProcessor;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;
import fr.sparna.rdf.shacl.generate.visitors.AssignDatatypesAndClassesToIriOrLiteralVisitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignLabelRoleVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.FilterOnStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;

public class Generate implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsGenerate a = (ArgumentsGenerate)args;
		
		Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shape");
		config.setShapesOntology("https://shacl-play.sparna.fr/shapes/");
		
		Model shapes;
		ShaclGeneratorDataProviderIfc dataProvider;
		if(a.getEndpoint() != null) {
			dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), a.getEndpoint());
			ShaclGenerator generator = new ShaclGenerator();
			shapes = generator.generateShapes(
					config,
					dataProvider);
			shapes = generator.generateShapes(config, dataProvider);
		} else {
			Model inputModel = ModelFactory.createDefaultModel(); 
			InputModelReader.populateModel(inputModel, a.getInput());
			dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), inputModel);
			ShaclGenerator generator = new ShaclGenerator();
			shapes = generator.generateShapes(
					config,
					dataProvider);
			
			// copy over the namespaces from original model
			shapes.setNsPrefixes(inputModel.getNsPrefixMap());
			
		}
		
		ShaclVisit modelStructure = new ShaclVisit(shapes);
		modelStructure.visit(new ComputeStatisticsVisitor(dataProvider, (a.getEndpoint() != null)?a.getEndpoint():"https://dummy.dataset.uri", true));
		modelStructure.visit(new FilterOnStatisticsVisitor());
		modelStructure.visit(new AssignLabelRoleVisitor());
		modelStructure.visit(new AssignDatatypesAndClassesToIriOrLiteralVisitor(dataProvider, new DefaultModelProcessor()));
		
		// write output file
		OutputStream out = new FileOutputStream(a.getOutput());
		shapes.write(out,FileUtils.guessLang(a.getOutput().getName(), "Turtle"));				
		
	}
}
