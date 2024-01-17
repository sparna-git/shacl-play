package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

public class ConstraintSHOrArrow {

	private Resource resourceShOr;

	public ConstraintSHOrArrow(Resource resourceShOr) {
		super();
		this.resourceShOr = resourceShOr;
	}
	
	public List<String> getResourcefromShOr(){
		
		List<String> result = new ArrayList<>();
		
		Resource list = this.resourceShOr.getProperty(SH.or).getList().asResource();
		List<RDFNode> rdflist = list.as(RDFList.class).asJavaList();
		
		rdflist.stream().forEach(item -> {
			if(item.isResource()) {
				if(item.asResource().hasProperty(SH.node)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.node);
					String resolvedClassReference = value.getModel().shortForm(value.getURI());
					result.add(resolvedClassReference);
				} else if(item.asResource().hasProperty(SH.class_)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.class_);
					String resolvedClassReference = value.getModel().shortForm(value.getURI());
					result.add(resolvedClassReference);
				} else if(item.asResource().hasProperty(SH.or)) {
					// actually, this would be a NodeShape theoretically...
					ConstraintSHOrArrow recursiveShape = new ConstraintSHOrArrow(item.asResource());
					result.addAll(recursiveShape.getResourcefromShOr());
				} 
			} 
		});
		
		
		if (result.size() > 0) {
			return result;
		} 
		
		return null;
	}	
}
