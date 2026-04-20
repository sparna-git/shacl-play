package fr.sparna.rdf.shacl.diagram.model;

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.PropertyShape;

public class PlantUmlProperty extends PropertyShape {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Resource propertyShape;
	
	protected List<String> value_inverseOf;
	
	public PlantUmlProperty(Resource propertyShape) {
		super(propertyShape);
		this.propertyShape = propertyShape;
	}
	
	public Optional<Literal> getColor() {
		return super.getShaclPlayColor();
	}
		
	@Deprecated
	public String getPathAsSparql() {
		// render the property path using prefixes
		// TODO : this default behavior should be elsewhere probably
		return this.getShPathAsOptional().map(path -> ModelRenderingUtils.renderSparqlPropertyPath(path, true)).orElse(this.propertyShape.getURI());
	}	

	public String getPlantUmlCardinalityString() {
		if(super.getShMinCountAsLiteral().isEmpty() && this.getShMaxCountAsLiteral().isEmpty()) {
			return null;
		}
		
		return "[" + this.getShMinCountAsLiteral().map(l-> ModelRenderingUtils.render(l)).orElse("0") + ".." + this.getShMaxCountAsLiteral().map(l-> ModelRenderingUtils.render(l)).orElse("*") + "]" ;
	}

	public String getPlantUmlQualifiedCardinalityString() {
		if(this.getShQualifiedMinCountAsLiteral().isEmpty() && this.getShQualifiedMaxCountAsLiteral().isEmpty()) {
			return null;
		}
		
		return "[" + this.getShQualifiedMinCountAsLiteral().map(l-> ModelRenderingUtils.render(l)).orElse("0") + ".." + this.getShQualifiedMaxCountAsLiteral().map(l-> ModelRenderingUtils.render(l)).orElse("*") + "]" ;
	}
	
	public String getPlantUmlLengthString() {
		if(this.getShMinLengthAsLiteral().isEmpty() && this.getShMaxLengthAsLiteral().isEmpty()) {
			return null;
		}
		
		return "{field} (Length [" + this.getShMinLengthAsLiteral().map(l-> ModelRenderingUtils.render(l)).orElse("0") + ".." + this.getShMaxLengthAsLiteral().map(l-> ModelRenderingUtils.render(l)).orElse("*") + "])" ;
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
