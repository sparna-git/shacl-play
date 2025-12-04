package fr.sparna.rdf.jena.shacl;

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.SHACL_PLAY;

public class PropertyShape extends Shape {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public PropertyShape(Resource propertyShape) {
		super(propertyShape);
	}
	
	public Resource getPropertyShape() {
		return this.getShape();
	}
	
	/**
	 * Could be either an IRI or a boolean set to false
	 */
	public Optional<RDFNode> getEmbed() {
		return ModelReadingUtils.getOptionalRdfNode(shape, shape.getModel().createProperty(SHACL_PLAY.EMBED));
	}
	
	public List<RDFNode> getShIn() {
		if (shape.hasProperty(SH.in)) {
			Resource list = shape.getProperty(SH.in).getList().asResource();
			return list.as(RDFList.class).asJavaList();
		} else {
			return null;
		}
	}

	/**
	 * Returns true if the property shape could be a literal property.
  	 * This is the case if it has a datatype, or if it has a nodeKind of Literal,
  	 * or if it has a languageIn, or if it has minLength, maxLength, minInclusive,
  	 * maxInclusive, minExclusive, or maxExclusive.
	 * @return
	 */
	public boolean couldBeLiteralProperty() {
		  return 
		  shape.hasProperty(SH.datatype)
		  ||
		  ( 
			shape.hasProperty(SH.nodeKind)
			&& 
	  		shape.getProperty(SH.nodeKind).getObject().asResource().equals(SH.Literal) 
		  )
		  ||
		  shape.hasProperty(SH.languageIn)
	  	  ||
		  shape.hasProperty(SH.minLength)
		  ||
		  shape.hasProperty(SH.maxLength)
	  	  ||
		  shape.hasProperty(SH.minInclusive)
		  ||
		  shape.hasProperty(SH.maxInclusive)
	  	  ||
		  shape.hasProperty(SH.minExclusive)
		  ||
		  shape.hasProperty(SH.maxExclusive);
	}

	/**
	 * Returns true if the property shape could be an IRI property.
  	 * This is the case if it has a nodeKind of IRI, or if it has a class.
	 * @return
	 */
	public boolean couldBeIriProperty() {
		return
		(
			shape.hasProperty(SH.nodeKind)
			&&
			shape.getProperty(SH.nodeKind).getObject().asResource().equals(SH.IRI)
		)
		||
		shape.hasProperty(SH.class_);
 	}

	
	public Optional<Resource> getShQualifiedValueShape() {
		return ModelReadingUtils.getOptionalResource(shape, SH.qualifiedValueShape);
	}
	
	public Resource getShPath() {
		return shape.getRequiredProperty(SH.path).getResource();
	}
	
	public Optional<Literal> getShName() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.name);
	}

	/**
	 * @return The sh:name list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getShName(String lang) {
		return ModelReadingUtils.readLiteralInLang(shape, SH.name, lang);
	}
	
	public Optional<Literal> getShDescription() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.description);
	}

	/**
	 * @return The sh:name list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getShDescription(String lang) {
		return ModelReadingUtils.readLiteralInLang(shape, SH.description, lang);
	}
	
	public Optional<RDFNode> getShHasValue() {
		return ModelReadingUtils.getOptionalRdfNode(this.shape, SH.hasValue);
	}
	
	public Optional<Literal> getShUniqueLang() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.uniqueLang);
	}
	
	public Optional<Integer> getShMinCount() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.minCount).map(l -> l.getInt());
	}
	
	public Optional<Integer> getShMaxCount() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.maxCount).map(l -> l.getInt());
	}
	
	public Optional<Integer> getShQualifiedMinCount() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.qualifiedMinCount).map(l -> l.getInt());
	}
	
	public Optional<Integer> getShQualifiedMaxCount() {
		return ModelReadingUtils.getOptionalLiteral(this.shape, SH.qualifiedMaxCount).map(l -> l.getInt());
	}
	
	
	public String getShNodeLabel() {
		return this.getShNode().map(r -> ModelRenderingUtils.render(r, true)).orElse(null);
	}
	
	public String getShQualifiedValueShapeLabel() {
		return this.getShQualifiedValueShape().map(r -> ModelRenderingUtils.render(r, true)).orElse(null);
	}
	
	
	public String getColorString() {
		return this.getShaclPlayColor().map(node -> node.asLiteral().toString()).orElse(null);
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
	
	public String getPlantUmlCardinalityString() {
		if(this.getShMinCount().isEmpty() && this.getShMaxCount().isEmpty()) {
			return null;
		}
		
		return "[" + this.getShMinCount().map(l-> Integer.toString(l)).orElse("0") + ".." + this.getShMaxCount().map(l-> Integer.toString(l)).orElse("*") + "]" ;
	}

	public String getPlantUmlQualifiedCardinalityString() {
		if(this.getShQualifiedMinCount().isEmpty() && this.getShQualifiedMaxCount().isEmpty()) {
			return null;
		}
		
		return "[" + this.getShQualifiedMinCount().map(l-> Integer.toString(l)).orElse("0") + ".." + this.getShQualifiedMaxCount().map(l-> Integer.toString(l)).orElse("*") + "]" ;
	}
	
	public String getPlantUmlLengthString() {
		if(this.getShMinLength().isEmpty() && this.getShMaxLength().isEmpty()) {
			return null;
		}
		
		return "{field} (Length [" + this.getShMinLength().map(l-> Integer.toString(l)).orElse("0") + ".." + this.getShMaxCount().map(l-> Integer.toString(l)).orElse("*") + "])" ;
	}
	
	public String getPlantUmlRangeString() {
		if(this.getShMinInclusive().isEmpty() && this.getShMinExclusive().isEmpty() && this.getShMaxExclusive().isEmpty() && this.getShMaxInclusive().isEmpty()) {
			return null;
		}
		
		String minString = this.getShMinInclusive().map(
				l-> "{field} (range : ["+ModelRenderingUtils.render(l)
		).orElse(this.getShMinExclusive().map( 
				l-> "{field} (range : ]"+ModelRenderingUtils.render(l)
		).orElse(
				"{field} (range : ]*"
		));
		
		String maxString = this.getShMaxInclusive().map(
				l-> ModelRenderingUtils.render(l)+"])"
		).orElse(this.getShMaxExclusive().map( 
				l-> ModelRenderingUtils.render(l)+"[)"
		).orElse(
				"*[)"
		));
		
		return minString+"-"+maxString;
	}
	
	
}
