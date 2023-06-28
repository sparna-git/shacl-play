package fr.sparna.rdf.shacl.generate.visitors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.generate.ShaclGeneratorDataProviderIfc;

public class DatasetAwareShaclVisitorBase implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(DatasetAwareShaclVisitorBase.class);
	
	protected ShaclGeneratorDataProviderIfc dataProvider;

	public DatasetAwareShaclVisitorBase(ShaclGeneratorDataProviderIfc dataProvider) {
		super();
		this.dataProvider = dataProvider;
	}

	@Override
	public void visitModel(Model model) {
		
	}

	@Override
	public void visitOntology(Resource ontology) {

	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {

	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {

	}

	@Override
	public void leaveModel(Model model) {

	}
	
	
	
}
