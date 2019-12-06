package fr.sparna.rdf.shacl.app.infer;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.rules.RuleUtil;

import fr.sparna.rdf.shacl.app.CliCommandIfc;
import fr.sparna.rdf.shacl.app.InputModelReader;
import fr.sparna.rdf.shacl.validator.Slf4jProgressMonitor;

public class Infer implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsInfer a = (ArgumentsInfer)args;
		
		// read input file or URL
		Model dataModel = InputModelReader.readInputModel(a.getInput());
		
		// read shapes file
		OntModel shapesModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		log.debug("Reading shapes from "+a.getShapes().getAbsolutePath());
		shapesModel.read(
				new FileInputStream(a.getShapes()),
				a.getShapes().toPath().toAbsolutePath().getParent().toUri().toString(),
				RDFLanguages.filenameToLang(a.getShapes().getName(), Lang.RDFXML).getName()
		);

		// do the actual rule execution
		Model results = RuleUtil.executeRules(
				dataModel,
				shapesModel,
				null,
				new Slf4jProgressMonitor("SHACL Infer", log)
		);
		
		results.write(
				new FileOutputStream(a.getOutput()),
				RDFLanguages.filenameToLang(a.getOutput().getName(), Lang.RDFXML).getName()
		);		
	}

}
