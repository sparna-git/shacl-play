package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.ShOrReadingUtils;
import fr.sparna.rdf.shacl.SHACL_PLAY;

public class PlantUmlProperty {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Resource propertyShape;
	
	protected List<String> value_inverseOf;
	
	public PlantUmlProperty(Resource propertyShape) {
		super();
		this.propertyShape = propertyShape;
	}
	
	public Resource getPropertyShape() {
		return propertyShape;
	}

	
	public Optional<Literal> getColor() {
		return ModelReadingUtils.getOptionalLiteral(propertyShape, propertyShape.getModel().createProperty(SHACL_PLAY.COLOR));
	}
	
	
	public Optional<Resource> getShNode() {
		return ModelReadingUtils.getOptionalResource(propertyShape, SH.node);
	}
	
	public Optional<Resource> getShClass() {
		return ModelReadingUtils.getOptionalResource(propertyShape, SH.class_);
	}
	
	public Optional<Resource> getShGroup() {
		return ModelReadingUtils.getOptionalResource(propertyShape, SH.group);
	}
	
	/*
	public Optional<Resource> getShPropertyGroup() {
		return ModelReadingUtils.getOptionalResource(propertyShape, SH.PropertyGroup);
	}
	*/
	
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
