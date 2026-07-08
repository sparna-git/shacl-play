package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.vocabularies.DCT;
import fr.sparna.rdf.vocabularies.SCHEMA;

import org.topbraid.shacl.vocabulary.SH;

public class NodeShape extends Shape  {
	
	// cache of property shapes
	protected List<PropertyShape> properties = null;

	public NodeShape(Resource nodeShape) {  
	    super(nodeShape);
	}

	/***** TARGET SPECIFICATIONS *******/

	/**
	 * @return The values of SH.targetClass, or an empty list if none 
	 */
	public List<Resource> getTargetClass() {
		return ModelReadingUtils.readObjectAsResource(resource, SH.targetClass);
	}

	/**
	 * @return The values of SH.targetSubjectsOf, or an empty list if none 
	 */
	public List<Resource> getTargetSubjectsOf() {
		return ModelReadingUtils.readObjectAsResource(resource, SH.targetSubjectsOf);
	}

	/**
	 * @return The values of SH.targetObjectsOf, or an empty list if none 
	 */
	public List<Resource> getTargetObjectsOf() {
		return ModelReadingUtils.readObjectAsResource(resource, SH.targetObjectsOf);
	}

	/**
	 * @return The values of SH.target, or an empty list if none 
	 */
	public List<Resource> getTarget() {
		return ModelReadingUtils.readObjectAsResource(resource, SH.target);
	}

/**
	 * @return The values of SH.targetNode, or an empty list if none 
	 */	
	public List<Resource> getTargetNode() {
		return ModelReadingUtils.readObjectAsResource(resource, SH.targetNode);
	}

	/**
	 * @return All targeted classes, that is the sh:targetClass resources value if present, plus the node shape itself if it is a class, or an empty list if none
	 */
	public List<Resource> getAllTargetedClasses() {
		List<Resource> targets = resource.listProperties(SH.targetClass).toList().stream()
				.map(s -> s.getResource())
				.collect(Collectors.toList());
		
		if(this.isClassShape()) {
			targets.add(this.resource);
		}

		return targets;
	}

	/**
	 * @return true if getTargetClasses() contains the given classUri 
	 */
	public boolean isTargeting(Resource classUri) {
		return this.getAllTargetedClasses().stream().filter(tc -> tc.equals(classUri)).findFirst().isPresent();
	}

	/**
	 * @return true if this node shape has at least one target 
	 * (sh:targetClass or itself a class, sh:targetSubjectsOf, sh:targetObjectsOf, sh:targetNode or sh:target)
	 */
	public boolean hasTarget() {
		return 
			getAllTargetedClasses().size() > 0
			||
			getTargetSubjectsOf().size() > 0
			||
			getTargetObjectsOf().size() > 0
			||
			getTarget().size() > 0
			||
			getTargetNode().size() > 0
		;
	}

	public Literal getShTargetShSelect() {
		return Optional.ofNullable(this.resource.getPropertyResourceValue(SH.target)).map(
				r -> Optional.ofNullable(r.getProperty(SH.select)).map(l -> l.getLiteral()).orElse(null)
		).orElse(null);
	}

	/***** / TARGET SPECIFICATIONS *******/

	/***** SUBCLASS OF MANAGEMENT  *******/

	/**
	 * @return true if this NodeShape is also an rdfs:Class
	 */
	public boolean isClassShape() {
		return resource.hasProperty(RDF.type, RDFS.Class);
	}

	/**
	 * @return The list of rdfs:subClassOf of this node shape excluding owl:Thing, if present, or an empty list if none
	 */
	public List<Resource> getSubClassOf() {
		return resource.listProperties(RDFS.subClassOf).toList().stream()
				.map(s -> s.getResource())
				.filter(r -> { return r.isURIResource() && !r.getURI().equals(OWL.Thing.getURI()); })
				.collect(Collectors.toList());
	}

	public List<Resource> getRdfsSubClassOf() {
		return NodeShape.getRdfsSubClassOfOf(resource);
	}

	/**
	 * @return the list of resources that this resource is a rdfs:subClassOf of, excluding owl:Thing and blank nodes.
	 */
	public static List<Resource> getRdfsSubClassOfOf(Resource resource) {
		return resource.listProperties(RDFS.subClassOf).toList().stream()
				.map(s -> s.getResource())
				.filter(r -> { return r.isURIResource() && !r.getURI().equals(OWL.Thing.getURI()); })
				.collect(Collectors.toList());
	}

	/***** / SUBCLASS OF MANAGEMENT  *******/


	/***** PROPERTY SHAPES MANAGEMENT  *******/

	public List<PropertyShape> getProperties() {
		if(this.properties != null) {
			return properties;
		} else {
			this.properties = this.readProperties();
			return this.properties;
		}
	}

	private List<PropertyShape> readProperties() {
		
		List<Statement> propertyStatements = this.resource.listProperties(SH.property).toList();
		List<PropertyShape> properties = new ArrayList<>();		
		
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			
			if(object.isResource()) {
				Resource propertyShape = object.asResource();
				properties.add(new PropertyShape(propertyShape));					
			}
		
		}		

		properties.sort(new PropertyShape.PropertyShapeComparator());
		 
		return properties;	
	}

	/***** / PROPERTY SHAPES MANAGEMENT  *******/


	/**
	 * @return The sh:closed Literal value
	 */
	public Optional<Literal> getShClosed() {
		return ModelReadingUtils.getOptionalLiteral(resource,SH.closed);
	}

	public Boolean isClosed() {
		return getShClosed().map(l -> l.getBoolean()).orElse(false);
	}	

	/**
	 * @return true if this node shape has no active property shapes and no target, meaning it 
	 * describes only the value nodes of some property shapes
	 */
	public boolean isPureValueShape() {
		Predicate<NodeShape> hasNoActivePropertyShape = new Predicate<NodeShape>() {

			@Override
			public boolean test(NodeShape ns) {
				// as soon as we find one non-deactivated property shape, we return false
				for (PropertyShape ps : ns.getProperties()) {
					if (!ps.isDeactivated()) {
						return false;
					}
				}
				// no property shape active at all, return true
				return true;
			}
		};

		return hasNoActivePropertyShape.test(this) && !this.hasTarget() && this.getRdfsSubClassOf().isEmpty();
	}

	/***** USAGE INDICATOR  *******/

	public boolean isUsedInShapesGraph() {
		List<Shape> usage = getUsage();
		return (usage.size() > 0);
	}

	/**
	 * 
	 * @return the list of NodeShapes and Property shapes that refer to this NodeShape in the shapes graph, 
	 * either through sh:node, sh:qualifiedValueShape or rdfs:subClassOf, or sh:class targeting a class that is the target of this one
	 */
	public List<Shape> getUsage() {

		// iterate through all shapes in the model and find those that refer to this node shape
		// use a shape to avoid returning a property shape 3 times if it is used in multiple node shapes
		Set<Shape> usage = new HashSet<>();
		ShapesGraph graph = new ShapesGraph(this.getResource().getModel());
		for(NodeShape ns : graph.getAllNodeShapes()) {

			// check if this node shape is used in sh:node
			
			if(ns.getShNode().map(n -> n.equals(this.getResource())).orElse(false)) {
				usage.add(ns);
			}
			// check if this node shape is used in a rdfs:subClassOf of this node shape
			if(ns.getRdfsSubClassOf().stream().filter(c -> c.equals(this.getResource())).findFirst().isPresent()) {
				usage.add(ns);
			}

			// check all property shapes
			for(PropertyShape ps : ns.getProperties()) {

				// check if this node shape is used in sh:qualifiedValueShape
				if(ps.getShQualifiedValueShape().map(n -> n.equals(this.getResource())).orElse(false)) {
					usage.add(ps);
				}
				// check if this node shape is used in sh:node
				if(ps.getShNode().map(n -> n.equals(this.getResource())).orElse(false)) {
					usage.add(ps);
				}
				// check if this node shape is used in sh:class targeting a class that is the target of this one
				if(ps.getShClass().map(c -> this.getAllTargetedClasses().stream().filter(tc -> tc.equals(c)).findFirst().isPresent()).orElse(false)) {
					usage.add(ps);
				}
				// check if this node shape is used in an sh:or containing a sh:node or a sh:class targeting a class that is the target of this one
				if(ShOrReadingUtils.readShNodeInShOr(ps.getShOr()).stream().filter(n -> n.equals(this.getResource())).findFirst().isPresent()) {
					usage.add(ps);
				}
				
				if(ShOrReadingUtils.readShClassInShOr(ps.getShOr()).stream().filter(c -> this.getAllTargetedClasses().stream().filter(tc -> tc.equals(c)).findFirst().isPresent()).findFirst().isPresent()) {
					usage.add(ps);
				}
			}		
		}
		return new ArrayList<>(usage);
	}

	/***** / USAGE INDICATOR  *******/
		
	/***** TEXTUAL ANNOTATIONS (label, description)  *******/

	@Override
	public String getDisplayLabel(Model owlModel, String lang) {
		// try with skos:prefLabel
		String result = ModelRenderingUtils.render(this.getSkosPrefLabel(lang), true);
		
		// try with rdfs:label
		if(result == null) {
			result = ModelRenderingUtils.render(this.getRdfsLabel(lang), true);
		}	
		
		// try with schema:name
		if(result == null) {
			result = ModelRenderingUtils.render(this.getSchemaName(lang), true);
		}

		if(this.getAllTargetedClasses().size() > 0) {
			if(result == null) {			
				// otherwise if we have skos:prefLabel on the class, take it
				for (Resource t : this.getAllTargetedClasses()) {
					String res = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(t.getURI()), SKOS.prefLabel, lang), true);
					if (res != null) {
						result = res;
					}
				}
			}

			if(result == null) {			
				// otherwise if we have rdfs:label on the class, take it
				for (Resource t : this.getAllTargetedClasses()) {
					String res = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(t.getURI()), RDFS.label, lang), true);
					if (res != null) {
						result = res;
					}
				}			
			}

			if(result == null) {			
				// otherwise if we have schema:name on the class, take it
				for (Resource t : this.getAllTargetedClasses()) {
					String res = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(t.getURI()), SCHEMA.name, lang), true);
					if (res != null) {
						result = res;
					}
				}			
			}
		}
		
		// default to short form or id
		if(result == null) {
			result = this.getShortFormOrId();
		}
		
		return result;
	}

	@Override
	public String getDisplayDescription(Model owlModel, String lang) {
		// try with skos:definition
		String result = ModelRenderingUtils.render(this.getSkosDefinition(lang), true);
		
		// try with rdfs:comment
		if(result == null) {
			result = ModelRenderingUtils.render(this.getRdfsComment(lang), true);
		}

		// try with schema:description
		if(result == null) {
			result = ModelRenderingUtils.render(this.getSchemaDescription(lang), true);
		}

		if(this.getAllTargetedClasses().size() > 0) {
			if(result == null) {
				// otherwise if we have skos:definition on the class, take it
				for (Resource t : this.getAllTargetedClasses()) {
					String res = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(t.getURI()), SKOS.definition, lang), true);
					if (res != null) {
						result = res;
					}
				}				
			}

			if(result == null) {
				// otherwise if we have rdfs:comment on the class, take it
				for (Resource t : this.getAllTargetedClasses()) {
					String res = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(t.getURI()), RDFS.comment, lang), true);
					if (res != null) {
						result = res;
					}
				}
			}

			if(result == null) {
				// otherwise if we have schema:description on the class, take it
				for (Resource t : this.getAllTargetedClasses()) {
					String res = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(t.getURI()), SCHEMA.description, lang), true);
					if (res != null) {
						result = res;
					}
				}
			}
		}
		
		return result;
	}
	

	/***** / TEXTUAL ANNOTATIONS (label, description)  *******/


	public List<SparqlConstraint> getSparqlConstraints() {
		return this.resource.listProperties(SH.sparql).toList().stream().map( c-> c.getResource()).map(r -> new SparqlConstraint(r)).collect(Collectors.toList());
	}

	public List<Resource> getShTargetClassRdfsSubclassOfInverseOfShTargetClass() {
		Set<Resource> result = new HashSet<Resource>();
		List<Resource> targetClasses = this.getAllTargetedClasses();
		if(targetClasses != null) {
			
			List<Resource> subClassesOf = targetClasses.stream().flatMap( t -> NodeShape.getRdfsSubClassOfOf(t).stream()).collect(Collectors.toList());
			
			if(subClassesOf != null && subClassesOf.size() > 0) {
				for (Resource aSuperClass : subClassesOf) {
					List<Resource> shapeWithThisTarget = this.resource.getModel().listStatements(null, SH.targetClass, aSuperClass).toList().stream().map(s -> s.getSubject()).collect(Collectors.toList());
					for (Resource aShapeWithSuperClassAsTarget : shapeWithThisTarget) {
						result.add(aShapeWithSuperClassAsTarget);
					}
				}
			}
		}
		
		return new ArrayList<Resource>(result);
	}

	/**
	 * Returns a list containing :
	 * 1. the shapes that this one is subClassOf 
	 * 2. plus the shapes that target a class, which the class that this shape target is a subClassOf.
	 * 3. plus a shape that is referenced by this shape by a sh:node
	 * 
	 * @return
	 */
	public List<Resource> getSuperShapes() {
		Set<Resource> superShapes = new HashSet<Resource>();
		superShapes.addAll(this.getRdfsSubClassOf());
		superShapes.addAll(this.getShTargetClassRdfsSubclassOfInverseOfShTargetClass());
		superShapes.addAll(this.getShNodeAsList());
		return new ArrayList<Resource>(superShapes);
	}
	

	/**
	 * @return The list of foaf:depiction values, if present, or an empty list if not present
	 */
	public List<Resource> getDepiction() {
		return ModelReadingUtils.readObjectAsResource(resource, FOAF.depiction);
	}
	
}