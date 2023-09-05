package fr.sparna.rdf.shacl.app.generate;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.DefaultModelProcessor;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.FilterOnStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;

public class Generate implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsGenerate a = (ArgumentsGenerate)args;
		
		/*
		Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shape");
		config.setShapesOntology("https://shacl-play.sparna.fr/shapes/");
		
		SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), ENDPOINT);
		ShaclGenerator generator = new ShaclGenerator();
		Model shapes = generator.generateShapes(
				config,
				dataProvider);
		
		shapes = generator.generateShapes(config, dataProvider);
		
		ShaclVisit modelStructure = new ShaclVisit(shapes);
		modelStructure.visit(new ComputeStatisticsVisitor(dataProvider, ENDPOINT, true));
		modelStructure.visit(new FilterOnStatisticsVisitor());			
		*/
		
		Model shapes = ModelFactory.createDefaultModel();
		shapes.add(shapes.createStatement(RDFS.Resource, RDF.type, RDFS.Class));
		
		OutputStream out = new FileOutputStream(a.getOutput());
		shapes.write(out,FileUtils.guessLang(a.getOutput().getName(), "Turtle"));				
		
	}
}
