package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.SHACL_PLAY;

public class ShapesGraph {
	
	private Model shaclGraph;
	private Model owlGraph;
	
	private OwlOntology ontology;
	
	public ShapesGraph(Model shaclGraph, Model owlGraph) {
		super();
		this.shaclGraph = shaclGraph;
		this.owlGraph = owlGraph;
		
		this.ontology = this.readOWL(shaclGraph);
	}

	public ShapesGraph(Model shaclGraph) {
		super();
		this.shaclGraph = shaclGraph;
	}
	
	
	public List<NodeShape> getAllNodeShapes() {	
		return ShapesGraph.readAllNodeShapes(shaclGraph, owlGraph); 
	}

	/**
	 * @return All subjects of a sh:path in the graph
	 */
	public List<PropertyShape> getAllPropertyShapes() {	
		return shaclGraph.listSubjectsWithProperty(SH.path).toList().stream().map(r -> new PropertyShape(r)).collect(Collectors.toList());
	}

	public OwlOntology getOntology() {
		return ontology;
	}

	public Model getShaclGraph() {
		return shaclGraph;
	}

	public Model getOwlGraph() {
		return owlGraph;
	}
	
	public NodeShape findNodeShapeByResource(Resource r) {
		return getAllNodeShapes().stream().filter(ns -> ns.getNodeShape().toString().equals(r.toString())).findFirst().orElse(null);
	}

	public NodeShape findNodeShapeByProperty(Resource resource) {
		return shaclGraph.listStatements(null,SH.property, resource).toList().stream().map( m -> new NodeShape(m.getSubject().asResource())).findFirst().get();
	}

	/**
	 * @param cl
	 * @return all NodeShapes that target the given class, based on sh:targetClass, or a shape that is itself a class with the requested URI, or an empty list if none found
	 */
	public List<NodeShape> findNodeShapeByTargetClass(Resource cl) {
		return getAllNodeShapes().stream().filter(ns -> 
			ns.isTargeting(cl)
		).collect(Collectors.toList());
	}

	/**
	 * @return the nodes shapes that target the node indicated in the sh:node of the given property shape
	 */
	public List<NodeShape> findNodeShapesByPropertyShapeShNode(PropertyShape ps) {
		return getAllNodeShapes().stream().filter(ns -> 
			ps.getShNode().map(n -> n.equals(ns.getNodeShape())).orElse(false)
		).collect(Collectors.toList());
	}

	/**
	 * @return the nodes shapes that target the class indicated in the sh:class of the given property shape, relying on findNodeShapeByTargetClass
	 */
	public List<NodeShape> findNodeShapesByPropertyShapeShClass(PropertyShape ps) {
		return ps.getShClass().map(cl -> this.findNodeShapeByTargetClass(cl)).orElse(new ArrayList<NodeShape>());
	}

	public List<PropertyShape> findPropertyShapesByPath(Resource path) {
		return shaclGraph.listSubjectsWithProperty(SH.path, path).toList().stream().map(r -> new PropertyShape(r)).collect(Collectors.toList());
	}

	/*
		Return the 
	 */
	public List<PropertyShape> findPropertyShapesByShortname(String shortname) {
		return shaclGraph.listSubjectsWithProperty(shaclGraph.createProperty(SHACL_PLAY.SHORTNAME), shaclGraph.createLiteral(shortname)).toList().stream().map(r -> new PropertyShape(r)).collect(Collectors.toList());
	}

	/*
		Return list of properties from SH.node
	*/
	public List<PropertyShape> findPropertyShapeByShNode(Resource findNS) {	
		List<Resource> shNode = shaclGraph.listStatements(null,SH.node, findNS).toList().stream().map( r -> r.getSubject().asResource()).collect(Collectors.toList());
		List<PropertyShape> ps = new ArrayList<>();
		for (Resource r : shNode) {
			if (!r.isAnon()) {
				ps.add(new PropertyShape(r.asResource()));
			}
		}
		return ps;
	}

	/*
		Return list of properties from SH.Class
	*/
	public List<PropertyShape> findPropertyShapeByShClass(Resource findNS) {		
		return shaclGraph.listStatements(null,SH.class_, findNS).toList().stream().map(r -> new PropertyShape(r.getSubject().asResource())).collect(Collectors.toList());
	}

	public List<PropertyShape> findPropertyShapeByShQualifiedValueShape(Resource findNS) {		
		

		List<PropertyShape> properties = new ArrayList<>();
		List<Resource> rQFVS = shaclGraph
			.listResourcesWithProperty(SH.qualifiedValueShape)
			.toList()
			.stream()
			.map( resouce -> resouce.asResource())
			.collect(Collectors.toList());
		
		for (Resource r : rQFVS) {
			
			Resource res = r.getProperty(SH.qualifiedValueShape).getResource();
			PropertyShape qualifiedvaludShapeObject = new PropertyShape(res);
			
			// Type of constraints 
			if (qualifiedvaludShapeObject.getShClass().isPresent()) {
				Resource rClass = qualifiedvaludShapeObject.getShClass().get().asResource();
				
				
				Resource shTargetClass = shaclGraph.listStatements(null, SH.targetClass, rClass)
					.toList()
					.stream()
					.map( m -> m.getSubject().asResource())
					.findFirst()
					.get();
				
				if (shTargetClass != null) {
					if (shTargetClass.getURI().equals(findNS.getURI())) {
						properties.add(new PropertyShape(r));
					}
				} else {
					if (rClass.getURI().equals(findNS.getURI())) {
						properties.add(new PropertyShape(r));
					}
				}
			}
			
			if (qualifiedvaludShapeObject.getShNode().isPresent()) {
				Resource rNodeShape = qualifiedvaludShapeObject.getShNode().get().asResource();
				if (rNodeShape.getURI().equals(findNS.getURI())) {
					properties.add(new PropertyShape(r));
				}
			}

			if (qualifiedvaludShapeObject.getShDatatype().isPresent()) {
				Resource rDatatype = qualifiedvaludShapeObject.getShDatatype().get().asResource();
				if (rDatatype.getURI().equals(findNS.getURI())) {
					properties.add(new PropertyShape(r));
				}
			}

			if (qualifiedvaludShapeObject.getShNodeKind().isPresent()) {
				Resource rNodekind =  qualifiedvaludShapeObject.getShNodeKind().get().asResource();
				if (rNodekind.getURI().equals(findNS.getURI())) {
					properties.add(new PropertyShape(r));
				}
			}

			if (qualifiedvaludShapeObject.getShHasValue().isPresent()
				&& 
				!qualifiedvaludShapeObject.getShNode().isPresent() 
				&&
				!qualifiedvaludShapeObject.getShClass().isPresent()
			) {
				Resource rHasValue = qualifiedvaludShapeObject.getShHasValue().get().asResource();
				if (rHasValue.getURI().equals(findNS.getURI())) {
					properties.add(new PropertyShape(r));
				}
			}
		}

		// find in the graph the SH.qualifiedValueShape Constraint:
		return properties;

	}

	public List<PropertyShape> findPropertyShapeByShNodeInShOr(Resource findNS) {

		// Get all nodeShapes
		List<Resource> rShOr = shaclGraph.listResourcesWithProperty(SH.or)
								.toList()
								.stream()
								.map( shORValue -> shORValue.asResource())
								.collect(Collectors.toList());
		
		List<PropertyShape> x = new ArrayList<>();
		for (Resource r : rShOr) {
			// Sh Node in Sh Or
			for (Resource rNodeValue : ShOrReadingUtils.readShNodeInShOr(r.getProperty(SH.or).getList())) {
				if (rNodeValue.getURI().equals(findNS.getURI())) {
					x.add(new PropertyShape(r.asResource()));
				}
			}
			// Sh Class in Sh Or
			List<Resource> rdfListClass =  ShOrReadingUtils.readShClassInShOr(r.getProperty(SH.or).getList());
			for (Resource rNodeValue : rdfListClass) {
				if (rNodeValue.isResource() && rNodeValue != null && !rNodeValue.isAnon()) {
					if (rNodeValue.getURI().equals(findNS.getURI())) {
						x.add(new PropertyShape(r.asResource()));
					}
				}
			}
			// Sh Datatype in Sh Or
			for (Resource rNodeValue : ShOrReadingUtils.readShDatatypeInShOr(r.getProperty(SH.or).getList())) {
				if (rNodeValue.isResource() && rNodeValue != null && !rNodeValue.isAnon()) {
					if (rNodeValue.getURI().equals(findNS.getURI())) {
						x.add(new PropertyShape(r.asResource()));
					}
				}
			}
			// Sh NodeKind in Sh Or
			for (Resource rNodeValue : ShOrReadingUtils.readShNodeKindInShOr(r.getProperty(SH.or).getList())) {
				if (rNodeValue.isResource() && rNodeValue != null && !rNodeValue.isAnon()) {
					if (rNodeValue.getURI().equals(findNS.getURI())) {
						x.add(new PropertyShape(r.asResource()));
					}
				}
			}
		}

		return x;		
	}

	public List<PropertyShape> getResourceByUsage(Resource nodeshape) {

		List<PropertyShape> ListOfPropertiesUsage = new ArrayList<>();
		// for SH Node
		ListOfPropertiesUsage.addAll(this.findPropertyShapeByShNode(nodeshape));
		// for SH Class
		ListOfPropertiesUsage.addAll(this.findPropertyShapeByShClass(nodeshape));
		// sh:node in sh:Or
		ListOfPropertiesUsage.addAll(this.findPropertyShapeByShNodeInShOr(nodeshape));
		// sh:qualifiedValueShape
		ListOfPropertiesUsage.addAll(this.findPropertyShapeByShQualifiedValueShape(nodeshape));
		
		return ListOfPropertiesUsage;
	}
	
	public void pruneEmptyAndUnusedNodeShapes() {
		List<NodeShape> unusedNodeShapes = getAllNodeShapes().stream().filter(
			ns -> (
				ns.isPureValueShape()
				&&
				!ns.isUsedInShapesGraph()
			)
		).collect(Collectors.toList());
		unusedNodeShapes.forEach(ns -> this.deleteNodeShape(ns.getNodeShape()));

		// do that a second time so that potential sh:node references to the deleted node shapes are also deleted
		// this should be a loop, of course
		unusedNodeShapes = getAllNodeShapes().stream().filter(
			ns -> (
				ns.isPureValueShape()
				&&
				!ns.isUsedInShapesGraph()
			)
		).collect(Collectors.toList());
		unusedNodeShapes.forEach(ns -> this.deleteNodeShape(ns.getNodeShape()));
	}

	public void deleteNodeShape(Resource nodeShape) {
		// delete any blank nodes that are linked to this node shape
		this.shaclGraph.listStatements(nodeShape, null, (RDFNode) null).toList()
		.stream().filter(s -> s.getObject().isAnon()).forEach(s -> this.shaclGraph.removeAll(s.getObject().asResource(), null, null));

		this.shaclGraph.removeAll(nodeShape, null, null);
		this.shaclGraph.removeAll(null, null, nodeShape);
	}
	
	private OwlOntology readOWL(Model shaclGraph) {
		
		// Lecture de OWL
		// this is tricky, because we can have multiple ones if SHACL is merged with OWL or imports OWL
		List<Resource> sOWL = shaclGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		
		// let's decide first to exclude the ones that are owl:import-ed from others
		List<Resource> filteredOWL = sOWL.stream().filter(onto1 -> {
			return !sOWL.stream().anyMatch(onto2 -> onto2.hasProperty(OWL.imports, onto1));
		}).collect(Collectors.toList());
		
		OwlOntology ontologyObject = null;
		if(filteredOWL.size() > 0) {
			ontologyObject = new OwlOntology(filteredOWL.get(0));
		}
				
		return ontologyObject;
	}
	
	/**
	 * @return all instances of sh:NodeShape in the graph, as NodeShape objects
	 */
	private static List<NodeShape> readAllNodeShapes(Model shaclGraph, Model owlGraph) {
		
		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		// 1. Lire toutes les classes		
		List<NodeShape> allNodeShapes = new ArrayList<>();
		for (Resource nodeShape : nodeShapes) {
			allNodeShapes.add(new NodeShape(nodeShape));
		}
		
		return allNodeShapes; 		
	}

	class NodeShapeDisplayLabelComparator implements Comparator<NodeShape> {

		private String lang;		

		public NodeShapeDisplayLabelComparator(String lang) {
			this.lang = lang;
		}

		@Override
		public int compare(NodeShape ns1, NodeShape ns2) {
			if (ns1.getShOrder().orElse(null) != null) {
				if (ns2.getShOrder().orElse(null) != null) {
					return ((ns1.getShOrder().get() - ns2.getShOrder().get()) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if (ns2.getShOrder().orElse(null) != null) {
					return 1;
				} else {
					// both sh:order are null, try with their display label
					return ns1.getDisplayLabel(lang).compareTo(ns2.getDisplayLabel(lang));
				}
			}
		}
		
	}

	class ShOrderComparator implements Comparator<Resource> {		

		public ShOrderComparator() {
		}

		private static Double getShOrderOf(Resource r) {
			return Optional.ofNullable(r.getProperty(SH.order)).map(s -> s.getDouble()).orElse(null);
		}


		@Override
		public int compare(Resource r1, Resource r2) {
			if (getShOrderOf(r1) != null) {
				if (getShOrderOf(r2) != null) {
					return ((getShOrderOf(r1) - getShOrderOf(r2)) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if (getShOrderOf(r2) != null) {
					return 1;
				} else {
					return 1;
				}
			}
		}
		
	}
	
}
