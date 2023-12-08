package fr.sparna.rdf.shacl.targets;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

public class StoreAsTargetNodeProcessor implements FocusNodeProcessor {

	private Model targetModel;

	@Override
	public void processFocusNodes(Resource shape, Model data, List<RDFNode> focusNodes) {
		// add an sh:targetNode triple to the output model for each focus node
		focusNodes.forEach(f -> {
			targetModel.add(shape, SH.focusNode, f);
		});
	}
	
	
}
