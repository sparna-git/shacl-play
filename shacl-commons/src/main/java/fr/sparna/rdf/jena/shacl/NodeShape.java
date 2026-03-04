package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
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
import org.topbraid.shacl.vocabulary.SH;

public class NodeShape extends Shape  {
	
	// cache of property shapes
	protected List<PropertyShape> properties = null;

	public NodeShape(Resource nodeShape) {  
	    super(nodeShape);
	}
	
	/**
	 * @return the underlying node shape Resource
	 */
	public Resource getNodeShape() {
		return this.shape;
	}

	/**
	 * @return The list of foaf:depiction values, if present, or an empty list if not present
	 */
	public List<Resource> getDepiction() {
		return ModelReadingUtils.readObjectAsResource(shape, FOAF.depiction);
	}

	/**
	 * @return The sh:targetClass resource value if present, or null if not present
	 */
	public Resource getTargetClass() {
		return Optional.ofNullable(shape.getProperty(SH.targetClass)).map(s -> s.getResource()).orElse(null);
	}

	public List<Resource> getTargetSubjectsOf() {
		return ModelReadingUtils.readObjectAsResource(shape, SH.targetSubjectsOf);
	}

	public List<Resource> getTargetObjectsOf() {
		return ModelReadingUtils.readObjectAsResource(shape, SH.targetObjectsOf);
	}

	public List<Resource> getTarget() {
		return ModelReadingUtils.readObjectAsResource(shape, SH.target);
	}

	public List<Resource> getTargetNodes() {
		return ModelReadingUtils.readObjectAsResource(shape, SH.targetNode);
	}

	/**
	 * @return The sh:targetClass resources value if present, as weel the node shape itself if it is a class, or an empty list if none
	 */
	public List<Resource> getTargetClasses() {
		List<Resource> targets = shape.listProperties(SH.targetClass).toList().stream()
				.map(s -> s.getResource())
				.collect(Collectors.toList());
		if(this.isClassShape()) {
			targets.add(this.shape);
		}
		return targets;
	}

	/**
	 * @return true if this NodeShape is also an rdfs:Class
	 */
	public boolean isClassShape() {
		return shape.hasProperty(RDF.type, RDFS.Class);
	}

	/**
	 * @return The list of rdfs:subClassOf of this node shape exclugind owl:Thing, if present, or an empty list if none
	 */
	public List<Resource> getSubClassOf() {
		return shape.listProperties(RDFS.subClassOf).toList().stream()
				.map(s -> s.getResource())
				.filter(r -> { return r.isURIResource() && !r.getURI().equals(OWL.Thing.getURI()); })
				.collect(Collectors.toList());
	}

	/**
	 * @return The sh:closed Literal value
	 */
	public Optional<Literal> getShClosed() {
		return ModelReadingUtils.getOptionalLiteral(shape,SH.closed);
	}

	/**
	 * @return The rdfs:comment list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getRdfsComment(String lang) {
		return ModelReadingUtils.readLiteralInLang(shape, RDFS.comment, lang);
	}
	
	/**
	 * @return The rdfs:label list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getRdfsLabel(String lang) {
		return ModelReadingUtils.readLiteralInLang(shape, RDFS.label, lang);
	}
	
	/**
	 * @return The skos:prefLabel list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getSkosPrefLabel(String lang) {
		return ModelReadingUtils.readLiteralInLang(shape, SKOS.prefLabel, lang);
	}
	
	/**
	 * @return The skos:definition list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getSkosDefinition(String lang) {
		return ModelReadingUtils.readLiteralInLang(shape, SKOS.definition, lang);
	}

	public String getBackgroundColorString() {
		return this.getShaclPlayBackgroundColor().map(node -> node.asLiteral().toString()).orElse(null);
	}

	public String getColorString() {
		return this.getShaclPlayColor().map(node -> node.asLiteral().toString()).orElse(null);
	}	

	public Boolean isClosed() {
		return getShClosed().map(l -> l.getBoolean()).orElse(false);
	}	
	
	/**
	 * @return true if getTargetClasses() contains the given classUri 
	 */
	public boolean isTargeting(Resource classUri) {
		return this.getTargetClasses().stream().filter(tc -> tc.equals(classUri)).findFirst().isPresent();
	}

	/**
	 * @return true if this node shape has at least one target 
	 * (sh:targetClass or itself a class, sh:targetSubjectsOf, sh:targetObjectsOf, sh:targetNode or sh:target)
	 */
	public boolean hasTarget() {
		return 
			getTargetClasses().size() > 0
			||
			getTargetSubjectsOf().size() > 0
			||
			getTargetObjectsOf().size() > 0
			||
			getTarget().size() > 0
			||
			getTargetNodes().size() > 0
		;
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

		return hasNoActivePropertyShape.test(this) && this.hasTarget() == false;
	}

	public boolean isUsedInShapesGraph() {
		boolean isUsedInShNode = this.getNodeShape().getModel().contains(null, SH.node, this.getNodeShape());
		boolean isUsedInQualifiedValueShape = this.getNodeShape().getModel().contains(null, SH.qualifiedValueShape, this.getNodeShape());
		boolean isUsedInRdfsSubClassOf = this.getNodeShape().getModel().contains(null, RDFS.subClassOf, this.getNodeShape());

		return isUsedInShNode || isUsedInQualifiedValueShape || isUsedInRdfsSubClassOf;
	}

	public String getShortFormOrId() {
		if(this.shape.isURIResource()) {
			return this.getNodeShape().getModel().shortForm(this.getNodeShape().getURI());
		} else {
			// returns the blank node ID in that case
			return this.shape.asResource().getId().getLabelString();
		}
	}

	public String getDiagramLabel() {
		// use the sh:targetClass if present, otherwise use the URI of the NodeShape
		return 
		ModelRenderingUtils.render(this.shape, true)
		+
		Optional.ofNullable(this.getTargetClass()).map(targetClass -> " ("+ModelRenderingUtils.render(targetClass, true)+")")
		.orElse("");
	}

	public String getDisplayLabel(String lang) {
		String result = ModelRenderingUtils.render(this.getSkosPrefLabel(lang), true);
		
		if(result == null) {
			result = ModelRenderingUtils.render(this.getRdfsLabel(lang), true);
		}				
		
		if(result == null && this.getTargetClass() != null) {
			// otherwise if we have skos:prefLabel on the class, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(this.shape.getModel().getResource(this.getTargetClass().getURI()), SKOS.prefLabel, lang), true);
		}
		
		if(result == null && this.getTargetClass() != null) {
			// otherwise if we have rdfs:label on the class, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(this.shape.getModel().getResource(this.getTargetClass().getURI()), RDFS.label, lang), true);
		}
		
		if(result == null) {
			result = this.getShortFormOrId();
		}
		
		return result;
	}
	

	public List<PropertyShape> getProperties() {
		if(this.properties != null) {
			return properties;
		} else {
			this.properties = this.readProperties();
			return this.properties;
		}
	}

	private List<PropertyShape> readProperties() {
		
		List<Statement> propertyStatements = this.shape.listProperties(SH.property).toList();
		List<PropertyShape> properties = new ArrayList<>();		
		
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			
			if(object.isResource()) {
				Resource propertyShape = object.asResource();
				properties.add(new PropertyShape(propertyShape));					
			}
		
		}		

		properties.sort((PropertyShape ps1, PropertyShape ps2) -> {
			if(ps1.getShOrder().isPresent()) {
				if(ps2.getShOrder().isPresent()) {
					return ((ps1.getShOrder().get() - ps2.getShOrder().get()) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if(ps2.getShOrder().isPresent()) {
					return 1;
				} else {
					// both sh:order are null, try with sh:path
					if(ps1.getPropertyPath().renderSparqlPropertyPath() != null && ps2.getPropertyPath().renderSparqlPropertyPath() != null) {						
						return ps1.getPropertyPath().renderSparqlPropertyPath().compareTo(ps2.getPropertyPath().renderSparqlPropertyPath());
					} else {
						return ps1.getPropertyShape().toString().compareTo(ps2.getPropertyShape().toString());
					}
				}
			}
		});
		 
		return properties;	
	}
}