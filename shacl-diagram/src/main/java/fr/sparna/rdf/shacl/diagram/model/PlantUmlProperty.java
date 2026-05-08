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
		try {
			return ModelRenderingUtils.renderSparqlPropertyPath(this.getShPath(), true);
		} catch(Exception e) {
			// just in case we have a property shape without an sh:path
			return this.propertyShape.getURI();
		}
	}	

	public String getPlantUmlCardinalityString() {
		if(super.getShMinCount().isEmpty() && this.getShMaxCount().isEmpty()) {
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
		
		return "{field} (Length [" + this.getShMinLength().map(l-> Integer.toString(l)).orElse("0") + ".." + this.getShMaxLength().map(l-> Integer.toString(l)).orElse("*") + "])" ;
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
