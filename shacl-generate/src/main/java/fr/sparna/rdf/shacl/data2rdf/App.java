package fr.sparna.rdf.shacl.data2rdf;

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
        ShaclGenerator generator = new ShaclGenerator(new PaginatedQuery(100));
        Model input = ModelFactory.createDefaultModel();
        try(FileInputStream in = new FileInputStream(args[0])) {
        	RDFDataMgr.read(
            		input,
            		in,
            		RDF.getURI(),
            		RDFLanguages.filenameToLang(args[0], Lang.TTL)
           );
        	Configuration config = new Configuration();
        	config.setShapesNamespace("http://exemple.be/shapes/");
           Model shapes = generator.generateShapes(
        		   config,
        		   input
           );
           
           shapes.write(System.out,"Turtle");
           
           shapes = generator.generateShapes(config, "https://data.bnf.fr/sparql");
           // shapes.write(System.out,"Turtle");
        }
        
        
        
    }
}
