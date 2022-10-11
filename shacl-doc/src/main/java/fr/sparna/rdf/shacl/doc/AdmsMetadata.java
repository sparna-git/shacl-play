package fr.sparna.rdf.shacl.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class AdmsMetadata {

	protected List<RDFNode> Adms_Status = null;

	public AdmsMetadata(Resource nodeShape) {
		// read adms status metadata
		this.Adms_Status = readAdmsStatus(nodeShape);

	}

	public List<RDFNode> readAdmsStatus(Resource nodeShape) {

		List<RDFNode> result = new ArrayList<RDFNode>();
		// Get URI ADMS Status
		if (nodeShape.hasProperty(nodeShape.getModel().createProperty("http://www.w3.org/ns/adms#status"))) {
			for (RDFNode n : nodeShape
					.listProperties(nodeShape.getModel().createProperty("http://www.w3.org/ns/adms#status")).toList()
					.stream().map(s -> s.getObject()).collect(Collectors.toList())) {
				// we keep it either if it is not a Literal or if Literal has no language or the
				// requested language
				if (!n.isLiteral() || n.asLiteral().getLanguage() == null || n.asLiteral().getLanguage().equals("")) {
					result.add(n);
				}
			}
			return result;
		}
		return null;
	}

	public List<RDFNode> getAdms_Status() {
		return Adms_Status;
	}

	public void setAdms_Status(List<RDFNode> adms_Status) {
		Adms_Status = adms_Status;
	}

}
