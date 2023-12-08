package fr.sparna.rdf.shacl.targets;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public interface FocusNodeProcessor {

	public void processFocusNodes(Resource shape, Model data, List<RDFNode> focusNodes);
	
}
