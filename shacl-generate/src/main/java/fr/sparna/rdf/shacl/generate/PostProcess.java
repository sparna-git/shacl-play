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
import fr.sparna.rdf.shacl.AssignLabelRoleVisitor;
import fr.sparna.rdf.shacl.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.FilterOnStatisticsVisitor;
import fr.sparna.rdf.shacl.ShaclVisit;
import fr.sparna.rdf.shacl.AssignDatatypesAndClassesToIriOrLiteralVisitor;
import fr.sparna.rdf.shacl.AssignIconVisitor;

/**
 * Hello world!
 *
 */
public class PostProcess 
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
			
			
			ShaclVisit modelStructure = new ShaclVisit(input);
			
			final String ENDPOINT = "https://nakala.fr/sparql";
			
			SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100), ENDPOINT);
			
			modelStructure.visit(new AssignIconVisitor());	
			modelStructure.visit(new AddNamespacesVisitor());
			modelStructure.visit(new FilterOnStatisticsVisitor());
			modelStructure.visit(new AssignLabelRoleVisitor());
			modelStructure.visit(new AssignDatatypesAndClassesToIriOrLiteralVisitor(dataProvider));	
			
			
			File output = new File("/home/thomas/auto-shapes-post-processed-shacl.ttl");
			try(FileOutputStream out = new FileOutputStream(output)) {
				input.write(out,"Turtle");
			}
		}



	}
}
