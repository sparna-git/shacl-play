package fr.sparna.rdf.jena.shacl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

public class PropertyShape {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Resource propertyShape;
	
	protected List<String> value_inverseOf;
	
	public PropertyShape(Resource propertyShape) {
		super();
		this.propertyShape = propertyShape;
	}
	
	public Resource getPropertyShape() {
		return propertyShape;
	}

	
	public Optional<Literal> getColor() {
		return ModelReadingUtils.getOptionalLiteral(propertyShape, propertyShape.getModel().createProperty(SHACL_PLAY.COLOR));
	}
	
	/**
	 * Could be either an IRI or a boolean set to false
	 */
	public Optional<RDFNode> getEmbed() {
		return ModelReadingUtils.getOptionalRdfNode(propertyShape, propertyShape.getModel().createProperty(SHACL_PLAY.EMBED));
	}
	
	public Optional<Resource> getShNode() {
		return ModelReadingUtils.getOptionalResource(propertyShape, SH.node);
	}
	
	public List<RDFNode> getShIn() {
		if (propertyShape.hasProperty(SH.in)) {
			Resource list = propertyShape.getProperty(SH.in).getList().asResource();
			return list.as(RDFList.class).asJavaList();
		} else {
			return null;
		}
	}
	
	public RDFList getShOr() {
		if (propertyShape.hasProperty(SH.or)) {
			return propertyShape.getProperty(SH.or).getList(); 
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
		  propertyShape.hasProperty(SH.datatype)
		  ||
		  ( 
			propertyShape.hasProperty(SH.nodeKind)
			&& 
	  		propertyShape.getProperty(SH.nodeKind).getObject().asResource().equals(SH.Literal) 
		  )
		  ||
		  propertyShape.hasProperty(SH.languageIn)
	  	  ||
		  propertyShape.hasProperty(SH.minLength)
		  ||
		  propertyShape.hasProperty(SH.maxLength)
	  	  ||
		  propertyShape.hasProperty(SH.minInclusive)
		  ||
		  propertyShape.hasProperty(SH.maxInclusive)
	  	  ||
		  propertyShape.hasProperty(SH.minExclusive)
		  ||
		  propertyShape.hasProperty(SH.maxExclusive);
	}

	/**
	 * Returns true if the property shape could be an IRI property.
  	 * This is the case if it has a nodeKind of IRI, or if it has a class.
	 * @return
	 */
	public boolean couldBeIriProperty() {
		return
		(
			propertyShape.hasProperty(SH.nodeKind)
			&&
			propertyShape.getProperty(SH.nodeKind).getObject().asResource().equals(SH.IRI)
		)
		||
		propertyShape.hasProperty(SH.class_);
 	}
  	
 	
	
	public Optional<Resource> getShClass() {
		return ModelReadingUtils.getOptionalResource(propertyShape, SH.class_);
	}
	
	public Optional<Resource> getShQualifiedValueShape() {
		return ModelReadingUtils.getOptionalResource(propertyShape, SH.qualifiedValueShape);
	}
	
	public Optional<Resource> getShPath() {
		return ModelReadingUtils.getOptionalResource(propertyShape, SH.path);
	}
	
	public Optional<Resource> getShDatatype() {
		return ModelReadingUtils.getOptionalResource(this.propertyShape, SH.datatype);
	}
	
	public Optional<Resource> getShNodeKind() {
		return ModelReadingUtils.getOptionalResource(this.propertyShape, SH.nodeKind);
	}
	
	public Optional<Literal> getShPattern() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.pattern);
	}
	
	public Optional<Literal> getShName() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.name);
	}

	/**
	 * @return The sh:name list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getShName(String lang) {
		return ModelReadingUtils.readLiteralInLang(propertyShape, SH.name, lang);
	}
	
	public Optional<Literal> getShDescription() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.description);
	}

	/**
	 * @return The sh:name list in the provided language, or an empty list if none is present
	 */
	public List<Literal> getShDescription(String lang) {
		return ModelReadingUtils.readLiteralInLang(propertyShape, SH.description, lang);
	}
	
	public Optional<RDFNode> getShHasValue() {
		return ModelReadingUtils.getOptionalRdfNode(this.propertyShape, SH.hasValue);
	}
	
	public Optional<Literal> getShOrder() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.order);
	}
	
	public Optional<Literal> getShUniqueLang() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.uniqueLang);
	}
	
	public Optional<Literal> getShMinCount() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.minCount);
	}
	
	public Optional<Literal> getShMaxCount() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.maxCount);
	}
	
	public Optional<Literal> getShQualifiedMinCount() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.qualifiedMinCount);
	}
	
	public Optional<Literal> getShQualifiedMaxCount() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.qualifiedMaxCount);
	}
	
	public Optional<Literal> getShMinLength() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.minLength);
	}
	
	public Optional<Literal> getShMaxLength() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.maxLength);
	}
	
	public Optional<Literal> getShMinInclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.minInclusive);
	}
	
	public Optional<Literal> getShMaxInclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.maxInclusive);
	}
	
	public Optional<Literal> getShMinExclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.minExclusive);
	}
	
	public Optional<Literal> getShMaxExclusive() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape, SH.maxExclusive);
	}
	
	public List<Literal> getShLanguageIn() {
		Optional<Resource> list = ModelReadingUtils.getOptionalResource(this.propertyShape, SH.languageIn);
		if(list.isPresent()) {
			List<RDFNode> languages = ModelReadingUtils.asJavaList(list.get());
			return languages.stream().filter(n -> n.isLiteral()).map(n -> n.asLiteral()).collect(Collectors.toList());
		} else {
			return new ArrayList<Literal>();
		}
	}
	
	public boolean hasShOrShClassOrShNode() {
		return 
				(this.getShOrShClass() != null && this.getShOrShClass().size() > 0)
				|| 
				(this.getShOrShNode() != null && this.getShOrShNode().size() > 0)
		;
	}
	
	
	public String getShNodeLabel() {
		return this.getShNode().map(r -> ModelRenderingUtils.render(r, true)).orElse(null);
	}
	
	public String getShQualifiedValueShapeLabel() {
		return this.getShQualifiedValueShape().map(r -> ModelRenderingUtils.render(r, true)).orElse(null);
	}
	
	public String getColorString() {
		return this.getColor().map(node -> node.asLiteral().toString()).orElse(null);
	}

	public List<Resource> getShOrShDatatype() {
		if (this.propertyShape.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShDatatypeInShOr(this.propertyShape.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}		
		return null;
	}
	
	public Optional<Literal> getShDeactivated() {
		return ModelReadingUtils.getOptionalLiteral(this.propertyShape,SH.deactivated);
	}
	
	public List<Resource> getShOrShNodeKind() {
		if (this.propertyShape.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShNodeKindInShOr(this.propertyShape.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}		
		return null;
	}
	
	public List<Resource> getShOrShClass() {
		if (this.propertyShape.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShClassInShOr(this.propertyShape.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}
		
		return null;
	}
	
	public List<Resource> getShOrShNode() {
		if (this.propertyShape.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShNodeInShOr(this.propertyShape.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values;
			}
		}
		
		return null;
	}
	
	public String getPathAsSparql() {
		// render the property path using prefixes
		// TODO : this default behavior should be elsewhere probably
		return this.getShPath().map(path -> ModelRenderingUtils.renderSparqlPropertyPath(path, true)).orElse(this.propertyShape.getURI());
	}	

	public String getShLanguageInString() {
		return this.getShLanguageIn().stream().map(l -> l.toString()).collect(Collectors.joining(", "));
	}
	
	public boolean isUniqueLang() {
		return this.getShUniqueLang().isPresent() && this.getShUniqueLang().get().getBoolean();
	}

	public boolean isDeactivated() {
		return this.getShDeactivated().isPresent() && this.getShDeactivated().get().getBoolean();
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
		
		return "[" + this.getShMinCount().map(l-> ModelRenderingUtils.render(l)).orElse("0") + ".." + this.getShMaxCount().map(l-> ModelRenderingUtils.render(l)).orElse("*") + "]" ;
	}

	public String getPlantUmlQualifiedCardinalityString() {
		if(this.getShQualifiedMinCount().isEmpty() && this.getShQualifiedMaxCount().isEmpty()) {
			return null;
		}
		
		return "[" + this.getShQualifiedMinCount().map(l-> ModelRenderingUtils.render(l)).orElse("0") + ".." + this.getShQualifiedMaxCount().map(l-> ModelRenderingUtils.render(l)).orElse("*") + "]" ;
	}
	
	public String getPlantUmlLengthString() {
		if(this.getShMinLength().isEmpty() && this.getShMaxLength().isEmpty()) {
			return null;
		}
		
		return "{field} (Length [" + this.getShMinLength().map(l-> ModelRenderingUtils.render(l)).orElse("0") + ".." + this.getShMaxCount().map(l-> ModelRenderingUtils.render(l)).orElse("*") + "])" ;
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
