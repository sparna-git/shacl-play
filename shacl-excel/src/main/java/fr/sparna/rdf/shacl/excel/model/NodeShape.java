package fr.sparna.rdf.shacl.excel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class NodeShape {
	
	private Resource nodeShape;
	
	public NodeShape (Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}
	
	public Double getSHOrder() {
		return Optional.ofNullable(this.nodeShape.getProperty(SH.order)).map(s -> s.getDouble()).orElse(null);
	}
	
	public Resource getSHTargetClass() {
		return Optional.ofNullable(nodeShape.getProperty(SH.targetClass)).map(s -> s.getResource()).orElse(null);
	}

	public Resource getSHTargetObjectOf() {
		return Optional.ofNullable(nodeShape.getProperty(SH.targetObjectsOf)).map(s -> s.getResource()).orElse(null);
	}

	public Resource getSHTargetSubjectsOf() {
		return Optional.ofNullable(nodeShape.getProperty(SH.targetSubjectsOf)).map(s -> s.getResource()).orElse(null);
	}
	
	public List<PropertyShape> getPropertyShapes() {
		List<PropertyShape> propertyShapes = nodeShape.listProperties(SH.property).toList().stream()
				.map(s -> s.getResource())
				.map(r -> new PropertyShape(r))
				.collect(Collectors.toList());
		
		return propertyShapes;
	}

	public Resource getNodeShape() {
		return nodeShape;
	}
}
