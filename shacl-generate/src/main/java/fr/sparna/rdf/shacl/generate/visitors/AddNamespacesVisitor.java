package fr.sparna.rdf.shacl.generate.visitors;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGenerator;

public class AddNamespacesVisitor implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);
	
	private transient List<String> namespaces = new ArrayList<String>();

	private Model prefixes;
	
	
	public AddNamespacesVisitor() {
		// fetch prefix.cc namespaces
		log.debug("Reading prefixes from prefix.cc...");
		this.prefixes = ModelFactory.createDefaultModel();
		RDFDataMgr.read(this.prefixes, "http://prefix.cc/popular/all.file.ttl", Lang.TURTLE);
	}

	@Override
	public void visitModel(Model model) {
		// reset namespaces
		this.namespaces = new ArrayList<String>();
	}

	@Override
	public void visitOntology(Resource ontology) {

	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {
		// read namespace of targetClass
		aNodeShape.listProperties(SHACLM.targetClass).forEach(tc -> {
			if(tc.getObject().isURIResource()) {
				namespaces.add(tc.getObject().asResource().getNameSpace());
			}
		});
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		// read namespace of path
		aPropertyShape.listProperties(SHACLM.path).forEach(p -> {
			if(p.getObject().isURIResource()) {
				namespaces.add(p.getObject().asResource().getNameSpace());
			}			
		});
		
	}
	
	@Override
	public void leaveModel(Model model) {
		// for each gathered namespace, add it to target model
		this.namespaces.forEach(ns -> {
			String prefix = this.prefixes.getNsURIPrefix(ns);
			if(prefix != null) {
				log.debug("Setting prefix '{}' for namespace '{}'", prefix, ns);
				model.setNsPrefix(prefix, ns);
			}			
		});
	}

}
