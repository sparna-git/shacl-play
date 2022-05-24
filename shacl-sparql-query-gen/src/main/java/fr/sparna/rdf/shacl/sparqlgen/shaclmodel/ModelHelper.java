package fr.sparna.rdf.shacl.sparqlgen.shaclmodel;

import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

public class ModelHelper {

	public static Resource readAsURIResource(Resource subject,Property property) {
		
		if(subject.hasProperty(property)) {
			RDFNode value = subject.getProperty(property).getObject();
			if(!value.isURIResource()) {
				throw new RuntimeException("Property "+property+" on subject "+subject+" is not a URI resource");
			}
			return value.asResource();
		}else {
			return null;
		}
	}
	
	public static Resource readAsResource(Resource subject, Property property) {
		/*
		if(value == null) {
			return null;
		}
		*/
		if(subject.hasProperty(property)) {
			RDFNode value = subject.getProperty(property).getObject();
			if(!value.isResource()) {
				throw new RuntimeException("Property "+property+" on subject "+subject+" is not a resource");
			}
			return value.asResource();
		}else {
			return null;
		}
	}
	
	public static Literal readAsLiteral(Resource subject, Property property) {
		//RDFNode value = subject.getProperty(property).getObject();
		/*
		if(value == null) {
			return null;
		}
		*/
		if(subject.hasProperty(property)) {
			RDFNode value = subject.getProperty(property).getObject();
			if(!value.isLiteral()) {
				throw new RuntimeException("Property "+property+" on subject "+subject+" is not a literal");
			}
			return value.asLiteral();
		}else {
			return null;
		}
		
		
		
		
	}
	
	public static List<RDFNode> readAsRDFList(Resource subject, Property property) {
		/*
		if(value == null) {
			return null;
		}
		*/
		if(subject.hasProperty(property)) {
			RDFNode value = subject.getProperty(property).getObject();
			if(!value.canAs(( RDFList.class ))) {
				throw new RuntimeException("Property "+property+" on subject "+subject+" is not an RDF List");
			}
			return value.as( RDFList.class ).asJavaList();
		}else{
			return null;
		}
		
		
	}
	
}
