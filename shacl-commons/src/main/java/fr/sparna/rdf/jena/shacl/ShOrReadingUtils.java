package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

public class ShOrReadingUtils {

	public static List<Resource> readShNodeInShOr(RDFList shOrList){
		
		List<Resource> result = new ArrayList<>();
		List<RDFNode> rdflist = shOrList.asJavaList();
		
		rdflist.stream().forEach(item -> {
			if(item.isResource()) {
				if(item.asResource().hasProperty(SH.node)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.node);
					result.add(value);
				} else if(item.asResource().hasProperty(SH.or)) {
					List<Resource> recursiveResult = ShOrReadingUtils.readShNodeInShOr(item.asResource().getProperty(SH.or).getList());
					result.addAll(recursiveResult);
				} 
			} 
		});
		
		return result;
	}
	
	public static List<Resource> readShClassInShOr(RDFList shOrList) {		
		List<Resource> result = new ArrayList<>();
		List<RDFNode> rdflist = shOrList.asJavaList();
		
		rdflist.stream().forEach(item -> {
			if(item.isResource()) {
				if(item.asResource().hasProperty(SH.class_)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.class_);
					result.add(value);
				} else if(item.asResource().hasProperty(SH.or)) {
					List<Resource> recursiveResult = ShOrReadingUtils.readShClassInShOr(item.asResource().getProperty(SH.or).getList());
					result.addAll(recursiveResult);
				} 
			} 
		});
		
		return result;
	}
	
	public static List<Resource> readShDatatypeInShOr(RDFList shOrList){
		
		List<Resource> result = new ArrayList<>();
		List<RDFNode> rdflist = shOrList.asJavaList();
		
		rdflist.stream().forEach(item -> {
			if(item.isResource()) {
				if(item.asResource().hasProperty(SH.datatype)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.datatype);
					result.add(value);
				}  else if(item.asResource().hasProperty(SH.or)) {
					List<Resource> recursiveResult = ShOrReadingUtils.readShDatatypeInShOr(item.asResource().getProperty(SH.or).getList());
					result.addAll(recursiveResult);
				}
			} 
		});
		
		return result;
	}
	
	public static List<Resource> readShNodeKindInShOr(RDFList shOrList){
		
		List<Resource> result = new ArrayList<>();
		List<RDFNode> rdflist = shOrList.asJavaList();
		
		rdflist.stream().forEach(item -> {
			if(item.isResource()) {
				if(item.asResource().hasProperty(SH.nodeKind)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.nodeKind);
					result.add(value);
				} else if(item.asResource().hasProperty(SH.or)) {
					List<Resource> recursiveResult = ShOrReadingUtils.readShNodeKindInShOr(item.asResource().getProperty(SH.or).getList());
					result.addAll(recursiveResult);
				}
			} 
		});
		
		return result;
	}
	
	public static List<Resource> readShClassAndShNodeAndShDatatypeAndShNodeKindInShOr(RDFList shOrList){
		
		List<Resource> result = new ArrayList<>();
		result.addAll(ShOrReadingUtils.readShClassInShOr(shOrList));
		result.addAll(ShOrReadingUtils.readShNodeInShOr(shOrList));
		result.addAll(ShOrReadingUtils.readShDatatypeInShOr(shOrList));
		result.addAll(ShOrReadingUtils.readShNodeKindInShOr(shOrList));
		
		return result;
	}
	
}
