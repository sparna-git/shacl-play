package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.atlas.logging.Log;
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
	 * @return The shacl-plya:backgroundColor Literal value, or null if not present
	 */
	public Literal getBackgroundColor() {
		return ModelReadingUtils.getOptionalLiteral(
				shape,
				shape.getModel().createProperty(SHACL_PLAY.BACKGROUNDCOLOR)
			).orElse(null);
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
		return Optional.ofNullable(this.getBackgroundColor()).map(node -> node.asLiteral().toString()).orElse(null);
	}

	public String getColorString() {
		return this.getColor().map(node -> node.asLiteral().toString()).orElse(null);
	}	

	public Boolean isClosed() {
		return getShClosed().map(l -> l.getBoolean()).orElse(false);
	}	
	
	public boolean isTargeting(Resource classUri) {
		boolean hasShTargetClass = Optional.ofNullable(this.getTargetClass()).filter(c -> c.equals(classUri)).isPresent();
		boolean isItselfTheClass = this.shape.hasProperty(RDF.type, RDFS.Class) && this.shape.hasProperty(RDF.type, SH.NodeShape);
		
		return hasShTargetClass || isItselfTheClass;
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