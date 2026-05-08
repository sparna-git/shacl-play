package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.DCT;

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
	 * @return The values of SH.targetClass, or an empty list if none 
	 */
	public List<Resource> getTargetClass() {
		return ModelReadingUtils.readObjectAsResource(shape, SH.targetClass);
	}

	/**
	 * @return The values of SH.targetSubjectsOf, or an empty list if none 
	 */
	public List<Resource> getTargetSubjectsOf() {
		return ModelReadingUtils.readObjectAsResource(shape, SH.targetSubjectsOf);
	}

	/**
	 * @return The values of SH.targetObjectsOf, or an empty list if none 
	 */
	public List<Resource> getTargetObjectsOf() {
		return ModelReadingUtils.readObjectAsResource(shape, SH.targetObjectsOf);
	}

	/**
	 * @return The values of SH.target, or an empty list if none 
	 */
	public List<Resource> getTarget() {
		return ModelReadingUtils.readObjectAsResource(shape, SH.target);
	}

/**
	 * @return The values of SH.targetNode, or an empty list if none 
	 */	
	public List<Resource> getTargetNode() {
		return ModelReadingUtils.readObjectAsResource(shape, SH.targetNode);
	}

	/**
	 * @return All targeted classes, that is the sh:targetClass resources value if present, plus the node shape itself if it is a class, or an empty list if none
	 */
	public List<Resource> getAllTargetedClasses() {
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
	 * @return The list of rdfs:subClassOf of this node shape excluding owl:Thing, if present, or an empty list if none
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

	public Boolean isClosed() {
		return getShClosed().map(l -> l.getBoolean()).orElse(false);
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
		if(this.shape.isURIResource() && this.shape != null) {
			return this.getNodeShape().getModel().shortForm(this.getNodeShape().getURI());
		} else {
			// returns the blank node ID in that case
			return this.shape.asResource().getId().getLabelString();
		}
	}

	public String getDisplayLabel(String lang) {
		return this.getDisplayLabel(this.getNodeShape().getModel(), lang);
	}

	public String getDisplayLabel(Model owlModel, String lang) {
		String result = ModelRenderingUtils.render(this.getSkosPrefLabel(lang), true);
		
		if(result == null) {
			result = ModelRenderingUtils.render(this.getRdfsLabel(lang), true);
		}				
		
		if((result == null) && (this.getAllTargetedClasses().size() > 0)) {			
			// otherwise if we have skos:prefLabel on the class, take it
			for (Resource t : this.getAllTargetedClasses()) {
				String res = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(t.getURI()), SKOS.prefLabel, lang), true);
			    if (res != null) {
			    	result = res;
			    }
			}
			
		}
		
		if((result == null) && (this.getAllTargetedClasses().size() > 0)) {
			// otherwise if we have rdfs:label on the class, take it
			for (Resource t : this.getAllTargetedClasses()) {
				String res = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(t.getURI()), RDFS.label, lang), true);
			    if (res != null) {
			    	result = res;
			    }
			}
		
		}
		
		// default to short form or id
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

	public Literal getShTargetShSelect() {
		return Optional.ofNullable(this.shape.getPropertyResourceValue(SH.target)).map(
				r -> Optional.ofNullable(r.getProperty(SH.select)).map(l -> l.getLiteral()).orElse(null)
		).orElse(null);
	}

	public Literal getShSparqlDCTDescription() {
		return Optional.ofNullable(this.shape.getPropertyResourceValue(SH.sparql)).map(
				r -> Optional.ofNullable(r.getProperty(DCT.Description)).map(l -> l.getLiteral()).orElse(null)
		).orElse(null);
	}

	/* ######## FOAF ########  */

	/**
	 * @return The list of foaf:depiction values, if present, or an empty list if not present
	 */
	public List<Resource> getDepiction() {
		return ModelReadingUtils.readObjectAsResource(shape, FOAF.depiction);
	}

	
	/* ######## RDFs ########  */

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

	/* ######## SHACL Play ########  */
	
	public String getBackgroundColorString() {
		return this.getShaclPlayBackgroundColor().map(node -> node.asLiteral().toString()).orElse(null);
	}

	public String getColorString() {
		return this.getShaclPlayColor().map(node -> node.asLiteral().toString()).orElse(null);
	}
	
	public Boolean getMainBoolean() {
		return this.getShaclPlayMain().map(node -> node.asLiteral().getBoolean()).orElse(false);
	}

	/* ######## SKOS ########  */

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
	
}