package fr.sparna.rdf.shacl.excel.model;

import java.util.Optional;

import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.excel.ModelReadingUtils;

public class PropertyShape {

	protected Resource propertyShape;	
	
	public PropertyShape(Resource propertyShape) {
		super();
		this.propertyShape = propertyShape;
	}
	
		
	public Resource getPropertyShape() {
		return propertyShape;
	}
	
	public Double getSh_order() {
		return Optional.ofNullable(this.propertyShape.getProperty(SH.order)).map(s -> s.getDouble()).orElse(null);
	}

	public Resource getDatatype() {
		return Optional.ofNullable(this.propertyShape.getProperty(SH.datatype)).map(s -> s.getResource()).orElse(null);
	}
	
	public Resource getSh_path() {
		return Optional.ofNullable(this.propertyShape.getProperty(SH.path)).map(s -> s.getResource()).orElse(null);
	}

	public String getSh_description(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(this.propertyShape, SH.description, lang);
	}

	public String getSh_name(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(this.propertyShape, SH.name, lang);
	}

}
