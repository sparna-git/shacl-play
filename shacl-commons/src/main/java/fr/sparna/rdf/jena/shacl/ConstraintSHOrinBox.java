package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

public class ConstraintSHOrinBox {

	private Resource resourceShOr;

	public ConstraintSHOrinBox(Resource resourceShOr) {
		super();
		this.resourceShOr = resourceShOr;
	}
	
	public List<String> getResourcefromShOr(){
		
		List<String> result = new ArrayList<>();
		
		Resource list = this.resourceShOr.getProperty(SH.or).getList().asResource();
		List<RDFNode> rdflist = list.as(RDFList.class).asJavaList();
		
		rdflist.stream().forEach(item -> {
			if(item.isResource()) {
				if(item.asResource().hasProperty(SH.datatype)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.datatype);
					String resolveDataTypeRef = value.getModel().shortForm(value.getURI());
					result.add(resolveDataTypeRef);
				} else if(item.asResource().hasProperty(SH.or)) {
					// actually, this would be a NodeShape theoretically...
					ConstraintSHOrinBox recursiveShape = new ConstraintSHOrinBox(item.asResource());
					result.addAll(recursiveShape.getResourcefromShOr());
				} else if(item.asResource().hasProperty(SH.nodeKind)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.nodeKind);
					String resolveDataTypeRef = value.getModel().shortForm(value.getURI());
					result.add(resolveDataTypeRef);
				} 
			} 
		});		
		
		if (result.size() > 0) {
			return result;
		} 
		
		return null;
	}	
}
