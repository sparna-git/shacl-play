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

import fr.sparna.rdf.shacl.generate.visitors.AddNamespacesVisitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignLabelRoleVisitor;
import fr.sparna.rdf.shacl.generate.visitors.AssignMinCountAndMaxCountVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.FilterOnStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;

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
			Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shape");
			config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
			
			ShaclGenerator generator = new ShaclGenerator();
			
			/*
			SamplingShaclGeneratorDataProvider dataProvider1 = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), input);
		
			ShaclGenerator generator = new ShaclGenerator();
			
			Model shapes = generator.generateShapes(
					config,
					dataProvider1
			);

			
			// merge prefixes
			shapes.setNsPrefixes(input.getNsPrefixMap());

			shapes.write(System.out,"Turtle");
			*/

			// shapes = generator.generateShapes(config, "https://data.bnf.fr/sparql");
			// final String ENDPOINT = "http://sparql.europeana.eu"; 
			// final String ENDPOINT = "http://openarchaeo.huma-num.fr/federation/sparql";
			// final String ENDPOINT = "http://graphdb.sparna.fr/repositories/europeana-poc";
			// final String ENDPOINT = "http://51.159.140.210/graphdb/repositories/sparnatural-demo-anf?infer=false";
			// final String ENDPOINT = "https://data.bnf.fr/sparql";
			// final String ENDPOINT = "https://query.linkedopendata.eu/sparql";
			// final String ENDPOINT = "https://nakala.fr/sparql";
			final String ENDPOINT = "http://localhost:7200/repositories/nakala";
			// final String ENDPOINT = "http://51.159.140.210/graphdb/repositories/sparnatural-demo-anf?infer=false";
			
			
			
			/** DEBUT CODE A INTEGRER DANS LE FORMULAIRE **/
			// final String ENDPOINT = "https://sage-ails.ails.ece.ntua.gr/api/content/semanticsearch-digital-repository-of-ireland/sparql";
			// final String ENDPOINT = "https://ld.stadt-zuerich.ch/query";
			SamplingShaclGeneratorDataProvider dataProvider2 = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), ENDPOINT);
			Model shapes = generator.generateShapes(config, dataProvider2);
			
			// add count
			ShaclVisit modelStructure = new ShaclVisit(shapes);
			
			modelStructure.visit(new ComputeStatisticsVisitor(dataProvider2, ENDPOINT, true));	
			
			shapes.write(System.out,"Turtle");
			/** FIN DU CODE A INTEGRER DANS LE FORMULAIRE **/
			
			File output = new File("/home/thomas/auto-shapes-unfiltered.ttl");
			try(FileOutputStream out = new FileOutputStream(output)) {
				shapes.write(out,"Turtle");
			}
			
			// modelStructure.visit(new FilterOnStatisticsVisitor());
			// modelStructure.visit(new AddNamespacesVisitor());
			// modelStructure.visit(new FilterOnStatisticsVisitor());
			// modelStructure.visit(new AssignLabelRoleVisitor());	
			
			// File outputFiltered = new File("/home/thomas/auto-shapes-filtered.ttl");
			// try(FileOutputStream out = new FileOutputStream(outputFiltered)) {
			// 	shapes.write(out,"Turtle");
			// }
		}



	}
}
