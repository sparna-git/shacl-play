package fr.sparna.rdf.jena.shacl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.vocabularies.SHACL_PLAY;

public class PropertyShape extends Shape {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public PropertyShape(Resource propertyShape) {
		super(propertyShape);
	}

	@Override
	public String getDisplayLabel(Model owlModel, String lang) {
		
		// try with sh:name
		String result = ModelRenderingUtils.render(this.getShName(lang), true);
		
		// try with skos:prefLabel
		if(result == null) {
			result = ModelRenderingUtils.render(this.getSkosPrefLabel(lang), true);
		}

		// try with rdfs:label
		if(result == null) {
			result = ModelRenderingUtils.render(this.getRdfsLabel(lang), true);
		}

		// try with schema:name
		if(result == null) {
			result = ModelRenderingUtils.render(this.getSchemaName(lang), true);
		}

		if(result == null && this.getShPath() == null) {
			// problem. Return something so at least we can debug
			return this.getResource().toString();
		}
		
		if(result == null && this.getShPath().isURIResource() && owlModel != null) {
			// otherwise if we have skos:prefLabel on the property, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShPath().getURI()), SKOS.prefLabel, lang), true);
		}
		
		if(result == null && this.getShPath().isURIResource() && owlModel != null) {
			// otherwise if we have rdfs:label on the property, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShPath().getURI()), RDFS.label, lang), true);
		}
		
		// otherwise return empty string (could be null now that we use another method for sorting)
		if(result == null) {
			result = "";
		}
		
		return result;
	}

	@Override
	public String getDisplayDescription(Model owlModel, String lang) {

		// try with sh:description
		String result = ModelRenderingUtils.render(this.getShDescription(lang), true);

		// try with skos:definition
		if(result == null) {
			result = ModelRenderingUtils.render(this.getSkosDefinition(lang), true);
		}

		// try with rdfs:comment
		if(result == null) {
			result = ModelRenderingUtils.render(this.getRdfsComment(lang), true);
		}

		// try with schema:description
		if(result == null) {
			result = ModelRenderingUtils.render(this.getSchemaDescription(lang), true);
		}
		
		if(result == null && this.getShPath() == null) {
			// problem. Return something so at least we can debug
			return "";
		}
		
		if(result == null && this.getShPath().isURIResource()) {
			// otherwise if we have skos:definition on the property, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShPath().getURI()), SKOS.definition, lang), true);
		}
		
		if(result == null && this.getShPath().isURIResource()) {
			// otherwise if we have rdfs:comment on the property, take it
			result = ModelRenderingUtils.render(ModelReadingUtils.readLiteralInLang(owlModel.getResource(this.getShPath().getURI()), RDFS.comment, lang), true);
		}
		
		return result;
	}	

	@Override
	public String getDisplayColor() {
		return this.getShaclPlayColor().map(node -> node.asLiteral().getLexicalForm()).orElse(null);
	}

	@Override
	public String getDisplayBackgroundColor() {
		return this.getShaclPlayBackgroundColor().map(node -> node.asLiteral().getLexicalForm()).orElse(null);
	}

	/**
	 * Could be either an IRI or a boolean set to false
	 */
	public Optional<RDFNode> getEmbed() {
		return ModelReadingUtils.getOptionalRdfNode(resource, resource.getModel().createProperty(SHACL_PLAY.EMBED));
	}
	
	public List<RDFNode> getShIn() {
		if (resource.hasProperty(SH.in)) {
			Resource list = resource.getProperty(SH.in).getList().asResource();
			return list.as(RDFList.class).asJavaList();
		} else {
			return null;
		}
	}

	/**
	 * Returns true if the property shape could be an IRI property.
  	 * This is the case if it has a nodeKind of IRI, or if it has a class.
	 * @return
	 */
	public boolean couldBeIriProperty() {
		return
		(
			resource.hasProperty(SH.nodeKind)
			&&
			resource.getProperty(SH.nodeKind).getObject().asResource().equals(SH.IRI)
		)
		||
		resource.hasProperty(SH.class_);
 	}

	
	public Optional<Resource> getShQualifiedValueShape() {
		return ModelReadingUtils.getOptionalResource(resource, SH.qualifiedValueShape);
	}
	
	public Resource getShPath() {
		return resource.getRequiredProperty(SH.path).getObject().asResource();
	}
	
	public Optional<Literal> getShName() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.name);
	}

	/**
	 * @return The sh:name list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getShName(String lang) {
		return ModelReadingUtils.readLiteralInLang(resource, SH.name, lang);
	}
	
	public Optional<Literal> getShDescription() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.description);
	}

	/**
	 * @return The sh:description list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getShDescription(String lang) {
		return ModelReadingUtils.readLiteralInLang(resource, SH.description, lang);
	}
	
	public Optional<RDFNode> getShHasValue() {
		return ModelReadingUtils.getOptionalRdfNode(this.resource, SH.hasValue);
	}
	
	public Optional<Literal> getShUniqueLang() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.uniqueLang);
	}
	
	public Optional<Integer> getShMinCount() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.minCount).map(l -> l.getInt());
	}
	
	public Optional<Integer> getShMaxCount() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.maxCount).map(l -> l.getInt());
	}
	
	public Optional<Integer> getShQualifiedMinCount() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.qualifiedMinCount).map(l -> l.getInt());
	}
	
	public Optional<Integer> getShQualifiedMaxCount() {
		return ModelReadingUtils.getOptionalLiteral(this.resource, SH.qualifiedMaxCount).map(l -> l.getInt());
	}
	
	public String getShNodeLabel() {
		return this.getShNode().map(r -> ModelRenderingUtils.render(r, true)).orElse(null);
	}
	
	public String getShQualifiedValueShapeLabel() {
		return this.getShQualifiedValueShape().map(r -> ModelRenderingUtils.render(r, true)).orElse(null);
	}	

	public PropertyPath getPropertyPath() {
		return new PropertyPath(this.getShPath());
	}	
	
	public boolean isUniqueLang() {
		return this.getShUniqueLang().isPresent() && this.getShUniqueLang().get().getBoolean();
	}

	/**
	 * @return true if shacl-play embed is either false or shacl-play:EmbedNever
	 */
	public boolean isEmbedNever() {
		return (
			this.getEmbed().isPresent()
			&&
			(
				(this.getEmbed().get().isResource() && this.getEmbed().get().asResource().getURI().equals(SHACL_PLAY.EMBED_NEVER))
				||
				(this.getEmbed().get().isLiteral() && (!this.getEmbed().get().asLiteral().getBoolean()))
			)
		);
	}

	public static class PropertyShapeComparator implements Comparator<PropertyShape> {

		public PropertyShapeComparator() {
		}

		@Override
		public int compare(PropertyShape ps1, PropertyShape ps2) {
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
						return ps1.getResource().toString().compareTo(ps2.getResource().toString());
					}
				}
			}
		}
		
	}

}
