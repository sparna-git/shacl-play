package fr.sparna.rdf.shacl.targets;


import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.SHP;


/**
 * A listener that adds a "shacl-play:targetMatched false" triple to the target model for each shape that did not match any focus node, 
 * and also adds a global sh:hasMatched triple to the validation report.
 */
public class AddHasTargetListener implements ShapesTargetListener {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private Model targetModel;

	private List<Resource> shapesWithTargets = new ArrayList<Resource>();

	public AddHasTargetListener(Model targetModel) {
		super();
		this.targetModel = targetModel;
	}

	@Override
	public void notifyTargets(Resource shape, Model data, List<RDFNode> focusNodes) {
		if(!focusNodes.isEmpty()) {
			if(!shapesWithTargets.contains(shape)) {
				shapesWithTargets.add(shape);
			}
		}
	}

	@Override
	public void notifyEndShape(Resource shape, Model data) {
		if(!shapesWithTargets.contains(shape)) {
			log.debug("Shape "+shape+" did not match any focus node");
			targetModel.add(targetModel.createLiteralStatement(
					shape,
					targetModel.createProperty(SHP.TARGET_MATCHED),
					false
			));
		}
	}

	@Override
	public void notifyEnd() {
		targetModel.add(targetModel.createLiteralStatement(
			targetModel.listResourcesWithProperty(RDF.type, SH.ValidationReport).next(),
			targetModel.createProperty(SHP.HAS_MATCHED),
			(this.shapesWithTargets.size() > 0)
		));
	}
	
}
