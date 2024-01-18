package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

public class ShOrReadingUtils {

	public static List<Resource> readShClassAndShNodeInShOr(RDFList shOrList){
		
		List<Resource> result = new ArrayList<>();
		List<RDFNode> rdflist = shOrList.asJavaList();
		
		rdflist.stream().forEach(item -> {
			if(item.isResource()) {
				if(item.asResource().hasProperty(SH.node)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.node);
					result.add(value);
				} else if(item.asResource().hasProperty(SH.class_)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.class_);
					result.add(value);
				} else if(item.asResource().hasProperty(SH.or)) {
					List<Resource> recursiveResult = ShOrReadingUtils.readShClassAndShNodeInShOr(item.asResource().getProperty(SH.or).getList());
					result.addAll(recursiveResult);
				} 
			} 
		});
		
		return result;
	}
	
	public static List<Resource> readShDatatypeAndShNodeKindInShOr(RDFList shOrList){
		
		List<Resource> result = new ArrayList<>();
		List<RDFNode> rdflist = shOrList.asJavaList();
		
		rdflist.stream().forEach(item -> {
			if(item.isResource()) {
				if(item.asResource().hasProperty(SH.datatype)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.datatype);
					result.add(value);
				}  else if(item.asResource().hasProperty(SH.nodeKind)) {
					Resource value = item.asResource().getPropertyResourceValue(SH.nodeKind);
					result.add(value);
				} else if(item.asResource().hasProperty(SH.or)) {
					List<Resource> recursiveResult = ShOrReadingUtils.readShDatatypeAndShNodeKindInShOr(item.asResource().getProperty(SH.or).getList());
					result.addAll(recursiveResult);
				}
			} 
		});
		
		return result;
	}
	
public static List<Resource> readShClassAndShNodeAndShDatatypeAndShNodeKindInShOr(RDFList shOrList){
		
		List<Resource> result = new ArrayList<>();
		result.addAll(ShOrReadingUtils.readShClassAndShNodeInShOr(shOrList));
		result.addAll(ShOrReadingUtils.readShDatatypeAndShNodeKindInShOr(shOrList));
		
		return result;
	}
	
}
