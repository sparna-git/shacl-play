package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.vocabularies.SH12;
import fr.sparna.rdf.vocabularies.SHACL_PLAY;

public class ShapesGraph {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private static final String SHACL_GRAPH = "https://shacl-play.sparna.fr/graph/base-shacl";
	private static final String SUPPORTIVE_GRAPH = "https://shacl-play.sparna.fr/graph/supportive-shacl";
	private static final String OWL_GRAPH = "https://shacl-play.sparna.fr/graph/owl";

	private transient Dataset dataset;
	private transient ShapesGraphResource resource;

	private transient List<NodeShape> allNodeShapes;
	
	public ShapesGraph(Model shaclGraph, Model supportiveShaclGraph, Model owlGraph) {
		super();

		// create a dataset with the shacl graph as default graph, and the owl graph and supportive shacl graph as named graphs
		this.dataset = DatasetFactory.create();

		this.dataset.addNamedModel(SHACL_GRAPH, shaclGraph);
		
		if(owlGraph != null) {
			this.dataset.addNamedModel(OWL_GRAPH, owlGraph);
		}
		if(supportiveShaclGraph != null) {
			this.dataset.addNamedModel(SUPPORTIVE_GRAPH, supportiveShaclGraph);
		}
	}

	public ShapesGraph(Model shaclGraph) {
		this(shaclGraph, null, null);
	}

	public ShapesGraph(Model shaclGraph, Model owlGraph) {
		this(shaclGraph, null, owlGraph);
	}
	
	/**
	 * Returns the resource that represents this shapes graph, 
	 * either a sh:ShapesGraph or an owl:Ontology if no sh:ShapesGraph is found.
	 * Can return null if no such resource is found in the graph.
	 */
	public ShapesGraphResource getResource() {
		if(this.resource == null) {
			Resource r = this.findShapesGraph();
			if(r == null) {
				r = this.findOntology();
			}
			if(r != null) {
				this.resource = new ShapesGraphResource(r);
			}
		}

		return this.resource;
	}

	/**
	 * @return the list of all NodeShapes in the union of all graphs in the dataset (shacl, supportive, owl)
	 */
	public List<NodeShape> getAllNodeShapes() {	
		if(this.allNodeShapes == null) {
			// get the union of all graphs in the dataset as a single model
			this.allNodeShapes = this.readAllNodeShapes(this.dataset.getUnionModel());
		}
		return this.allNodeShapes;
	}

	public List<NodeShape> getRootNodeShapes() {	
		// read from SHACL graph, not from union model, since we want the roots of this graph
		return this.readAllNodeShapes(this.getShaclGraph())
		.stream()
		// TODO : should test agains the URI of this ShapesGraph
		.filter(nodeShape -> !nodeShape.getShaclPlayIsRootOf().isEmpty())
		.collect(Collectors.toList());
	}

	/**
	 * @return All subjects of a sh:path in the graph
	 */
	public List<PropertyShape> getAllPropertyShapes() {	
		return this.dataset.getUnionModel().listSubjectsWithProperty(SH.path).toList().stream().map(r -> new PropertyShape(r, this)).collect(Collectors.toList());
	}

	public Model getShaclGraph() {
		return this.dataset.getNamedModel(SHACL_GRAPH);
	}

	public Model getOwlGraph() {
		return this.dataset.getNamedModel(OWL_GRAPH);
	}
	
	public Model getSupportiveGraph() {
		return this.dataset.getNamedModel(SUPPORTIVE_GRAPH);
	}
	
	public NodeShape findNodeShapeByResource(Resource r) {
		return getAllNodeShapes().stream().filter(ns -> ns.getResource().equals(r)).findFirst().orElse(null);
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

	public List<NodeShape> findNodeShapeByPropertyShape(PropertyShape ps) {
		return getAllNodeShapes()
				.stream()
				.filter( ns -> ns.getProperties()
					.stream()
					.filter( psp -> psp.getResource().equals(ps.getResource()))
					.findFirst()
					.isPresent()
				)
				.collect(Collectors.toList());
	}


	/**
	 * @return the nodes shapes that target the node indicated in the sh:node of the given (property) shape
	 */
	public List<NodeShape> findNodeShapesByPropertyShapeShNode(Shape ps) {
		return getAllNodeShapes().stream().filter(ns -> 
			ps.getShNode().map(n -> n.equals(ns.getResource())).orElse(false)
		).collect(Collectors.toList());
	}

	/**
	 * @return the nodes shapes that target the class indicated in the sh:class of the given (property) shape, relying on findNodeShapeByTargetClass
	 */
	public List<NodeShape> findNodeShapesByPropertyShapeShClass(Shape ps) {
		return ps.getShClass().map(cl -> this.findNodeShapeByTargetClass(cl)).orElse(new ArrayList<NodeShape>());
	}

	public List<PropertyShape> findPropertyShapesByPath(Resource path) {
		return this.dataset.getUnionModel().listSubjectsWithProperty(SH.path, path).toList().stream().map(r -> new PropertyShape(r, this)).collect(Collectors.toList());
	}

	public List<PropertyShape> findPropertyShapesByShortname(String shortname) {
		Model workingGraph = this.dataset.getUnionModel();
		return workingGraph.listSubjectsWithProperty(
			workingGraph.createProperty(SHACL_PLAY.SHORTNAME),
			workingGraph.createLiteral(shortname)).toList().stream().map(r -> new PropertyShape(r, this)
		).collect(Collectors.toList());
	}

	public List<PropertyShape> findPropertyShapesByShortnameOrPath(String shortname, Resource path) {
		List<PropertyShape> shapes = new ArrayList<>();
		// find the (unique) property shape with its shortname, or with a path (which can return multiple property shapes)
		List<PropertyShape> propertyShapesWithPath = new ArrayList<>();
		if(shortname != null) {
			propertyShapesWithPath = this.findPropertyShapesByShortname(shortname);
		}
		// if nothing found, try with the path
		if(propertyShapesWithPath.isEmpty()) {
			propertyShapesWithPath = this.findPropertyShapesByPath(path);
		}
		shapes.addAll(propertyShapesWithPath);

		return shapes;
	}
	
	/**
	 * Removes node shapes that are pure value shapes or pure rule shapes, and that are not used in any other shape in the graph.
	 * This ensures the documentation does not include node shapes that are not relevant (e.g. the shacl-ep case)
	 */
	public void pruneEmptyAndUnusedNodeShapes() {
		List<NodeShape> unusedNodeShapes;
		int cleanedCount = 0;
		int iteration = 0;
		// clean recursively while we keep deleting something
		do {
			unusedNodeShapes = getAllNodeShapes().stream().filter(
				ns -> (
					(ns.isPureValueShape() || ns.isPureRuleShape())
					&&
					!ns.isUsedInShapesGraph()
				)
			).collect(Collectors.toList());

			System.out.println("Deleting "+unusedNodeShapes.size()+" unused node shapes from the shapes graph : "+unusedNodeShapes.stream().map(ns -> ns.getResource().getURI()).collect(Collectors.joining(", ")));
			// deletes the unused node shapes from the SHACL graph, and also from the supportive graph if any
			unusedNodeShapes.forEach(ns -> ShapesGraph.deleteResource(this.getShaclGraph(), ns.getResource()));
			if (this.getSupportiveGraph() != null) {
			  unusedNodeShapes.forEach(ns -> ShapesGraph.deleteResource(this.getSupportiveGraph(), ns.getResource()));
			}
			this.allNodeShapes = null; // reset the cache of all node shapes, since we just deleted some

			cleanedCount += unusedNodeShapes.size();
			iteration++;
		} while(unusedNodeShapes.size() > 0);
		log.trace("Done pruning empty & unused node shapes. Cleaned " + cleanedCount + " shapes in " + iteration + " iterations	.");
		if(cleanedCount > 0) {
			this.allNodeShapes = null; // reset the cache of all node shapes, since we just deleted some
		}
		
	}

	/**
	 * Removes all unused node shapes from the supportive graph
	 */
	public void cleanSupportiveGraph() {
		if(this.getSupportiveGraph() == null)
			return;

		List<NodeShape> unusedNodeShapes;
		// clean recursively while we keep deleting something
		do {
			unusedNodeShapes = this.readAllNodeShapes(this.getSupportiveGraph()).stream()
			.filter(ns -> !ns.isUsedInShapesGraph())
			.collect(Collectors.toList());

			unusedNodeShapes.forEach(ns -> {
				ShapesGraph.deleteResource(this.getSupportiveGraph(), ns.getResource());
			});
			this.allNodeShapes = null; // reset the cache of all node shapes, since we just deleted some
		} while(unusedNodeShapes.size() > 0);

		
	}

	/**
	 * @return a map of namespace prefixes to URIs for all namespaces that are actually used in the shapes graph
	 */
	public Map<String, String> getNamespaces() {
		// Collect all necessary prefix names from the shapes graph
		Set<String> necessaryPrefixes = new HashSet<>();
		
		// Get all node shapes
		List<NodeShape> allNodeShapes = getAllNodeShapes();
		
		// For each node shape, collect prefixes from its properties and its property shapes
		for (NodeShape nodeShape : allNodeShapes) {
			Resource shapeResource = nodeShape.getResource();
			collectPrefixesFromResource(necessaryPrefixes, shapeResource);
			
			// Also collect prefixes from property shapes
			for (PropertyShape propertyShape : nodeShape.getProperties()) {
				Resource psResource = propertyShape.getResource();
				collectPrefixesFromResource(necessaryPrefixes, psResource);
			}
		}
		
		// Filter the model's namespace map to only include necessary prefixes
		return gatherNecessaryPrefixes(this.dataset.getUnionModel().getNsPrefixMap(), necessaryPrefixes);
	}

	/**
	 * Collects all prefix names used in the given resource's properties
	 * @param prefixes Set to collect prefix names into
	 * @param resource Resource to extract prefixes from
	 */
	private void collectPrefixesFromResource(Set<String> prefixes, Resource resource) {
		// Properties that may contain URIs that need prefixes
		Property[] propertiesToCheck = {
			SH.path,
			SH.targetClass,
			SH.class_,
			SH.datatype,
			SH.in,
			SH.hasValue,
			SH.target,
			SH.targetNode,
			SH.targetSubjectsOf,
			SH.targetObjectsOf
		};
		
		for (Property property : propertiesToCheck) {
			appendPrefix(prefixes, resource, property);
		}
	}

	/**
	 * Appends the prefix name from a property value to the prefixes set
	 * @param prefixes Set to collect prefix names into
	 * @param resource Resource to check
	 * @param property Property to check on the resource
	 */
	private void appendPrefix(Set<String> prefixes, Resource resource, Property property) {
		if(resource.hasProperty(property)) {
			RDFNode object = resource.getProperty(property).getObject();
			if(object.isURIResource()) {
				String qname = resource.getModel().qnameFor(object.asNode().getURI());
				// can be null if cannot be abbreviated
				if(qname != null) {
					prefixes.add(qname.split(":")[0]);
				}
			} else if(object.canAs(RDFList.class)) {
				List<RDFNode> nodes = object.as(RDFList.class).asJavaList();
				for (RDFNode aNode : nodes) {
					if(aNode.isURIResource()) {
						String qname = resource.getModel().qnameFor(aNode.asResource().getURI());
						if(qname != null) {
							prefixes.add(qname.split(":")[0]);
						}
					}
				}
			}
		}
	}

	/**
	 * Filters a map of all prefixes to only include those that are in the necessaryPrefixes set
	 * @param allPrefixes Map of all available prefixes (prefix -> URI)
	 * @param necessaryPrefixes Set of prefix names that are needed
	 * @return Filtered map containing only necessary prefixes
	 */
	private Map<String, String> gatherNecessaryPrefixes(Map<String, String> allPrefixes, Set<String> necessaryPrefixes) {
		HashMap<String, String> result = new HashMap<>();
		
		for (String aPrefix : necessaryPrefixes) {
			String uri = allPrefixes.get(aPrefix);
			if(uri != null) {
				result.put(aPrefix, uri);
			}
		}
		
		return result;
	}

	public static void deleteResource(Model model, Resource resource) {
		// recursively delete any blank nodes that are linked to this resource
		model.listStatements(resource, null, (RDFNode) null)
		.toList()
		.stream()
		.filter(s -> s.getObject().isAnon())
		.forEach(s -> 
			ShapesGraph.deleteResource(model, s.getObject().asResource())
		);

		model.removeAll(resource, null, null);
		model.removeAll(null, null, resource);
	}
	
	/**
	 * Find the (unique) owl:Ontology resource in the graph, if any, or null if none found.
	 * If multiple are found, returns the first one that is not imported by another ontology in the graph.
	 * @return
	 */
	private Resource findOntology() {
		// look only in base SHACL graph
		// this is tricky, because we can have multiple ones if SHACL is merged with OWL or imports OWL
		List<Resource> sOWL = this.getShaclGraph().listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		
		// let's decide first to exclude the ones that are owl:import-ed from others
		List<Resource> filteredOWL = sOWL.stream().filter(onto1 -> {
			return !sOWL.stream().anyMatch(onto2 -> onto2.hasProperty(OWL.imports, onto1));
		}).collect(Collectors.toList());
		
		if(filteredOWL.size() > 0) {
			return filteredOWL.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Finds the first resource of type sh:ShapesGraph in the graph, or null if none found.
	 * If multiple are found, returns the first one.
	 * @return
	 */
	private Resource findShapesGraph() {
		// look only in base SHACL graph
		return this.getShaclGraph().listResourcesWithProperty(RDF.type, SH12.shapesGraph).toList().stream().findFirst().orElse(null);
	}
	
	/**
	 * @return all instances of sh:NodeShape in the graph, as NodeShape objects
	 */
	private List<NodeShape> readAllNodeShapes(Model graph) {
		
		List<Resource> nodeShapes = graph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();	
		List<NodeShape> allNodeShapes = new ArrayList<>();
		for (Resource nodeShape : nodeShapes) {
			allNodeShapes.add(new NodeShape(nodeShape, this));
		}
		
		return allNodeShapes; 		
	}
	
}
