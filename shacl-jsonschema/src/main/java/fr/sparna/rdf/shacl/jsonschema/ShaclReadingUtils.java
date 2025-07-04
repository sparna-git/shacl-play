package fr.sparna.rdf.shacl.jsonschema;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.SHACL_PLAY;

public class ShaclReadingUtils {

    // read properties	
	public static Set<String> findShortNamesOfPath(Resource path) {
		List<Resource> propertyShapesWithPath = findPropertyShapesWithPath(path);
		Set<String> shortnames = new HashSet<>();
		for (Resource resource : propertyShapesWithPath) {
			// read shortname constraint
			shortnames.addAll(readDatatypeProperty(resource, path.getModel().createProperty(SHACL_PLAY.SHORTNAME)).stream().map(l -> l.getString()).collect(Collectors.toSet()));
		}
		
		return shortnames;		
	}
	
	public static List<Literal> readDatatypeProperty(Resource r, Property p) {
		return r.listProperties(p).toList().stream().map(s -> s.getObject()).filter(n -> n.isLiteral()).map(n -> n.asLiteral()).collect(Collectors.toList());
	}
		
	public static Set<Resource> findDatatypesOfPath(Resource path) {
		List<Resource> propertyShapesWithPath = findPropertyShapesWithPath(path);
		Set<Resource> datatypes = new HashSet<>();
		for (Resource resource : propertyShapesWithPath) {
			// read sh:datatype constraint
			datatypes.addAll(readObjectProperty(resource, SH.datatype));
		}
		return datatypes;		
	}
	
	public static List<Resource> findPropertyShapesWithPath(Resource path) {
		return path.getModel().listSubjectsWithProperty(SH.path, path).toList();
	}
	
	public static List<Resource> readObjectProperty(Resource r, Property p) {
		return r.listProperties(p).toList().stream().map(s -> s.getObject()).filter(n -> n.isResource()).map(n -> n.asResource()).collect(Collectors.toList());
	}
}
