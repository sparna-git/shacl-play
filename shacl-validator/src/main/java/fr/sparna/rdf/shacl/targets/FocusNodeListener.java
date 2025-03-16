package fr.sparna.rdf.shacl.targets;


import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * A listener interface for the shape focus nodes resolver. Implementations of this interface will be notified of the focus nodes.
 */
public interface FocusNodeListener {

	public void notifyFocusNodes(Resource shape, Model data, List<RDFNode> focusNodes);

	public void notifyEndShape(Resource shape, Model data);

	public void notifyEnd();
	
}
