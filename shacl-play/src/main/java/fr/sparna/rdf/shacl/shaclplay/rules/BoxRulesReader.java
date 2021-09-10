package fr.sparna.rdf.shacl.shaclplay.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

public class BoxRulesReader {
	
	public BoxRules read(Model GraphModel) {
		
		BoxRules p = new BoxRules(); 
		/*
		 * Lectura de la Ontologia 
		 * Recuperamos informacion de la Etiqueta y del comentario
		 */
		List<Resource> owlReaderModel = GraphModel.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();		
		for(Resource aOwlModel : owlReaderModel) {
			p.setLabel(this.readOwlLabel(aOwlModel));
			p.setComments(this.readOwlComment(aOwlModel));			
		}
	
		
		/*
		 * Lectura del nombre de espacio
		 * Lectura de la informacion Prefijo
		 * Lectura de la informacion del Nombre de Espacio
		 */
		List<Resource> prefixOWL = GraphModel.listResourcesWithProperty(RDF.type, SH.PrefixDeclaration).toList();
		List<BoxNameSpace> lNameSpace = new ArrayList<>();
		for(Resource aprefixOWL : prefixOWL) {
			BoxNameSpace nSpaceRules = new BoxNameSpace();
			
			nSpaceRules.setPrefix(this.readShPrefix(aprefixOWL));
			nSpaceRules.setNameSpace(this.readShNameSpace(aprefixOWL));
			lNameSpace.add(nSpaceRules);
		}		
		lNameSpace.sort(Comparator.comparing(BoxNameSpace::getPrefix));		
		p.setNameSpaceRules(lNameSpace);
		
		/*
		 * Leemos informacion del o los nodos que tenga el modelo de grafos
		 * 
		 */
		
		//Lectura de todos los nodos que existan en el modelo
		List<Resource> nodeShapes =  GraphModel.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
		BoxShapeReader nodeShapeReader = new BoxShapeReader();
		List<BoxShape> BoxShapeAll = nodeShapes.stream().map(res -> nodeShapeReader.readShape(res, nodeShapes)).collect(Collectors.toList());
		List<BoxShapeTarget> aShape = new ArrayList<>();
		//Recuperamos los datos de las propiedades (Target y Rules)
		for(BoxShape aBoxshape : BoxShapeAll) {
			p.setShapeRules(nodeShapeReader.read(GraphModel));			
		}			
		return p;		
	}
	
	
	public String readShPrefix(Resource nodeShape) {
		String value = null;
		if(nodeShape.hasProperty(SH.prefix)) {
			value = nodeShape.getProperty(SH.prefix).getString();
		}
		return value;
	}
	
	public String readShNameSpace(Resource nodeShape) {
		String value = null;
		if(nodeShape.hasProperty(SH.namespace)) {
			value = nodeShape.getProperty(SH.namespace).getString();
		}
		return value;
	}
	
	
	public String readOwlLabel(Resource nodeShape) {
		String value = null;
		if(nodeShape.hasProperty(RDFS.label)) {
			value = nodeShape.getProperty(RDFS.label).getString();
		}
		return value;
	}
		
	public String readOwlComment(Resource nodeShape) {
		String value = null;
		if(nodeShape.hasProperty(RDFS.comment)) {
			value = nodeShape.getProperty(RDFS.comment).getString();
		}
		return value;
	}

}
