package fr.sparna.rdf.shacl.generate;

import java.io.FileInputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;

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
			final String ENDPOINT = "https://data.bnf.fr/sparql";
			SamplingShaclGeneratorDataProvider dataProvider2 = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), ENDPOINT);
			shapes = generator.generateShapes(config, dataProvider2);
			shapes.write(System.out,"Turtle");
		}



	}
}
