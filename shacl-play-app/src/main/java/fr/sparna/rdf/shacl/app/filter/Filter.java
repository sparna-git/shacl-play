package fr.sparna.rdf.shacl.app.filter;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.generate.visitors.CopyStatisticsToDescriptionVisitor;
import fr.sparna.rdf.shacl.generate.visitors.FilterOnStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;

public class Filter implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsFilter a = (ArgumentsFilter)args;
		
		Model shapes = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(shapes, a.getShapes());
		
		Model statistics = ModelFactory.createDefaultModel(); 
		InputModelReader.populateModelFromFile(statistics, a.getStatistics());
		
		ShaclVisit modelStructure = new ShaclVisit(shapes);
		if(a.isDescription()) {
			modelStructure.visit(new CopyStatisticsToDescriptionVisitor(statistics));
		}
		modelStructure.visit(new FilterOnStatisticsVisitor(statistics));
		
		// write output shapes file
		OutputStream out = new FileOutputStream(a.getOutput());
		shapes.write(out,FileUtils.guessLang(a.getOutput().getName(), "Turtle"));				
		
	}
}
