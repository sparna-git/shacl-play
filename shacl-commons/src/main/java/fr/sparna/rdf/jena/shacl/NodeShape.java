package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import fr.sparna.rdf.shacl.*;
import org.topbraid.shacl.vocabulary.SH;

public class NodeShape {
	
	private Resource nodeShape;
	
	// cache of property shapes
	protected List<PropertyShape> properties = null;

	public NodeShape(Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}
	
	/**
	 * @return the underlying node shape Resource
	 */
	public Resource getNodeShape() {
		return nodeShape;
	}

	/**
	 * @return The shacl-plya:backgroundColor Literal value, or null if not present
	 */
	public Literal getBackgroundColor() {
		return ModelReadingUtils.getOptionalLiteral(
				nodeShape,
				nodeShape.getModel().createProperty(SHACL_PLAY.BACKGROUNDCOLOR)
			).orElse(null);
	}

	/**
	 * @return The shacl-play:color Literal value, or null if not present
	 */
	public Literal getColor() {
		return ModelReadingUtils.getOptionalLiteral(
				nodeShape,
				nodeShape.getModel().createProperty(SHACL_PLAY.COLOR)
			).orElse(null);
	}

	/**
	 * @return The list of foaf:depiction values, if present, or an empty list if not present
	 */
	public List<Resource> getDepiction() {
		return ModelReadingUtils.readObjectAsResource(nodeShape, FOAF.depiction);
	}

	/**
	 * @return The sh:targetClass resource value if present, or null if not present
	 */
	public Resource getTargetClass() {
		return Optional.ofNullable(nodeShape.getProperty(SH.targetClass)).map(s -> s.getResource()).orElse(null);
	}

	/**
	 * @return The list of rdfs:subClassOf of this node shape exclugind owl:Thing, if present, or an empty list if none
	 */
	public List<Resource> getSubClassOf() {
		return nodeShape.listProperties(RDFS.subClassOf).toList().stream()
				.map(s -> s.getResource())
				.filter(r -> { return r.isURIResource() && !r.getURI().equals(OWL.Thing.getURI()); })
				.collect(Collectors.toList());
	}
	
	public List<Literal> getExamples() {
		return ModelReadingUtils.readLiteral(nodeShape, SKOS.example);
	}
	
	public Optional<Literal> getPattern() {
		return ModelReadingUtils.getOptionalLiteral(nodeShape, SH.pattern);
	}
	

	/**
	 * @return The sh:order Literal value, or null if not present
	 */
	public Literal getOrder() {
		return ModelReadingUtils.getOptionalLiteral(
				nodeShape,
				SH.order
			).orElse(null);
	}

	/**
	 * @return The sh:closed Literal value, or null if not present
	 */
	public Literal getClosed() {
		return ModelReadingUtils.getOptionalLiteral(nodeShape,SH.closed).orElse(null);
	}

	/**
	 * @return The rdfs:comment list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getRdfsComment(String lang) {
		return ModelReadingUtils.readLiteralInLang(nodeShape, RDFS.comment, lang);
	}
	
	/**
	 * @return The rdfs:label list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getRdfsLabel(String lang) {
		return ModelReadingUtils.readLiteralInLang(nodeShape, RDFS.label, lang);
	}
	
	/**
	 * @return The skos:prefLabel list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getSkosPrefLabel(String lang) {
		return ModelReadingUtils.readLiteralInLang(nodeShape, SKOS.prefLabel, lang);
	}
	
	/**
	 * @return The skos:definition list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getSkosDefinition(String lang) {
		return ModelReadingUtils.readLiteralInLang(nodeShape, SKOS.definition, lang);
	}


	public String getBackgroundColorString() {
		return Optional.ofNullable(this.getBackgroundColor()).map(node -> node.asLiteral().toString()).orElse(null);
	}

	public String getColorString() {
		return Optional.ofNullable(this.getColor()).map(node -> node.asLiteral().toString()).orElse(null);
	}	

	public Float getOrderFloat() {
		return Optional.ofNullable(this.getOrder()).map(node -> node.asLiteral().getFloat()).orElse(null);
	}

	public Boolean isClosed() {
		return getClosed() != null && getClosed().getBoolean() == true;
	}	
	
	public boolean isTargeting(Resource classUri) {
		boolean hasShTargetClass = Optional.ofNullable(this.getTargetClass()).filter(c -> c.equals(classUri)).isPresent();
		boolean isItselfTheClass = this.nodeShape.hasProperty(RDF.type, RDFS.Class) && this.nodeShape.hasProperty(RDF.type, SH.NodeShape);
		
		return hasShTargetClass || isItselfTheClass;
	}	

	public String getShortFormOrId() {
		if(this.nodeShape.isURIResource()) {
			return this.getNodeShape().getModel().shortForm(this.getNodeShape().getURI());
		} else {
			// returns the blank node ID in that case
			return this.nodeShape.asResource().getId().getLabelString();
		}
	}

	public String getDiagramLabel() {
		// use the sh:targetClass if present, otherwise use the URI of the NodeShape
		return 
		ModelRenderingUtils.render(this.nodeShape, true)
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
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(this.nodeShape.getModel().getResource(this.getTargetClass().getURI()), SKOS.prefLabel, lang), true);
		}
		
		if(result == null && this.getTargetClass() != null) {
			// otherwise if we have rdfs:label on the class, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(this.nodeShape.getModel().getResource(this.getTargetClass().getURI()), RDFS.label, lang), true);
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
		
		List<Statement> propertyStatements = this.nodeShape.listProperties(SH.property).toList();
		List<PropertyShape> properties = new ArrayList<>();		
		
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			
			if(object.isResource()) {
				Resource propertyShape = object.asResource();			
				PropertyShape plantvalueproperty = new PropertyShape(propertyShape);
				properties.add(plantvalueproperty);					
			}
		
		}		

		properties.sort((PropertyShape ps1, PropertyShape ps2) -> {
			if(ps1.getShOrder().isPresent()) {
				if(ps2.getShOrder().isPresent()) {
					return ((ps1.getShOrder().map(o -> o.getDouble()).get() - ps2.getShOrder().map(o -> o.getDouble()).get()) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if(ps2.getShOrder().isPresent()) {
					return 1;
				} else {
					// both sh:order are null, try with sh:path
					if(ps1.getPathAsSparql() != null && ps2.getPathAsSparql() != null) {						
						return ps1.getPathAsSparql().compareTo(ps2.getPathAsSparql());
					} else {
						return ps1.getPropertyShape().toString().compareTo(ps2.getPropertyShape().toString());
					}
				}
			}
		});
		 
		return properties;	
	}
}