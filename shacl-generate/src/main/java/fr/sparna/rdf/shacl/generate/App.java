package fr.sparna.rdf.shacl.generate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;

import fr.sparna.rdf.shacl.AddNamespacesVisitor;
import fr.sparna.rdf.shacl.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.FilterOnStatisticsVisitor;
import fr.sparna.rdf.shacl.ShaclModel;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args ) throws Exception
	{
		System.out.println( "Hello SHACL!" );
		
		Model input = ModelFactory.createDefaultModel();
		try(FileInputStream in = new FileInputStream(args[0])) {
			RDFDataMgr.read(
					input,
					in,
					RDF.getURI(),
					RDFLanguages.filenameToLang(args[0], Lang.TTL)
					);
			Configuration config = new Configuration("http://exemple.be/shapes/", "myshapes");
			config.setShapesOntology("http://exemple.be/shapes");
			
			SamplingShaclGeneratorDataProvider dataProvider1 = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), input);
		
			ShaclGenerator generator = new ShaclGenerator();
			
			Model shapes = generator.generateShapes(
					config,
					dataProvider1
			);
			// merge prefixes
			shapes.setNsPrefixes(input.getNsPrefixMap());

			shapes.write(System.out,"Turtle");

			// shapes = generator.generateShapes(config, "https://data.bnf.fr/sparql");
			// final String ENDPOINT = "http://sparql.europeana.eu"; 
			// final String ENDPOINT = "http://openarchaeo.huma-num.fr/federation/sparql";
			// final String ENDPOINT = "http://graphdb.sparna.fr/repositories/europeana-poc";
			// final String ENDPOINT = "http://51.159.140.210/graphdb/repositories/sparnatural-demo-anf?infer=false";
			// final String ENDPOINT = "https://data.bnf.fr/sparql";
			// final String ENDPOINT = "https://query.linkedopendata.eu/sparql";
			final String ENDPOINT = "https://nakala.fr/sparql";
			SamplingShaclGeneratorDataProvider dataProvider2 = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), ENDPOINT);
			shapes = generator.generateShapes(config, dataProvider2);
			
			// add count
			ShaclModel modelStructure = new ShaclModel(shapes);
			modelStructure.visit(new ComputeStatisticsVisitor(dataProvider2, ENDPOINT, true));
			
			
			shapes.write(System.out,"Turtle");
			
			File output = new File("/home/thomas/auto-shapes-unfiltered.ttl");
			try(FileOutputStream out = new FileOutputStream(output)) {
				shapes.write(out,"Turtle");
			}
			
			modelStructure.visit(new FilterOnStatisticsVisitor());
			modelStructure.visit(new AddNamespacesVisitor());
			
			File outputFiltered = new File("/home/thomas/auto-shapes-filtered.ttl");
			try(FileOutputStream out = new FileOutputStream(outputFiltered)) {
				shapes.write(out,"Turtle");
			}
		}



	}
}
