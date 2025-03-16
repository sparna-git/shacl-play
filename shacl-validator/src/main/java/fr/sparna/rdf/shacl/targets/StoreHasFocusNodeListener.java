package fr.sparna.rdf.shacl.targets;


import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.shacl.SHP;

/**
 * Stores the target nodes as shp:hasFocusNode triples in a target model.
 */
public class StoreHasFocusNodeListener implements FocusNodeListener {

	private Model targetModel;

	public StoreHasFocusNodeListener(Model targetModel) {
		super();
		this.targetModel = targetModel;
	}

	@Override
	public void notifyFocusNodes(Resource shape, Model data, List<RDFNode> focusNodes) {
		// add an sh:targetNode triple to the output model for each focus node
		for(RDFNode focusNode : focusNodes) {
			targetModel.add(shape, targetModel.createProperty(SHP.HAS_FOCUS_NODE), focusNode);
		}
	}

	@Override
	public void notifyEndShape(Resource shape, Model data) {
		// nothing to do
	}

	@Override
	public void notifyEnd() {
		// nothing to do
	}
	
	
}
