package fr.sparna.rdf.shacl.app.generate;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.FilterOnStatisticsVisitor;
import fr.sparna.rdf.shacl.ShaclVisit;
import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;

public class Generate implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsGenerate a = (ArgumentsGenerate)args;
		
		// Instances of 
		
		String ENDPOINT = a.getInput();
		
		Configuration config = new Configuration("http://exemple.be/shapes/", "myshapes");
		config.setShapesOntology("http://exemple.be/shapes");
		
		SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), ENDPOINT);
		ShaclGenerator generator = new ShaclGenerator();
		Model shapes = generator.generateShapes(
				config,
				dataProvider);
		
		shapes = generator.generateShapes(config, dataProvider);
		
		ShaclVisit modelStructure = new ShaclVisit(shapes);
		modelStructure.visit(new ComputeStatisticsVisitor(dataProvider, ENDPOINT, true));
		modelStructure.visit(new FilterOnStatisticsVisitor());			

		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String outputName="shacl"+"_"+dateString;
		
		String fmtOutputFile = "";
		switch (a.getformat()) {
		case "TURTLE":
			fmtOutputFile = ".ttl";
			break;
			
		case "RDF/XML":
			fmtOutputFile = ".ttl";
			break;
		case "N-Triples":
			fmtOutputFile = ".nt";
			break;
		case "N-Quads":
			fmtOutputFile = ".nq";
			break;
		case "N3":
			fmtOutputFile = ".n3";
			break;
		case "TriG":
			fmtOutputFile = ".trig";
			break;
		case "Json-LD":
			fmtOutputFile = ".jsonld";
			break;
		default:
			fmtOutputFile = ".ttl";
			break;
		}
		
		String FileName = outputName+fmtOutputFile;
		OutputStream OutputFile = new FileOutputStream(FileName);
		shapes.write(OutputFile,"Turtle");				
		
	}
}
