package fr.sparna.rdf.shacl.diagram;

import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class PlantUmlProperty {

	protected Resource propertyShape;
	
	protected String value_path;
	protected String value_datatype;
	protected String value_nodeKind;
	protected String value_cardinality;	
	protected String value_range;	
	protected String value_length;
	protected String value_pattern;
	protected String value_language;
	protected String value_uniquelang;
	protected PlantUmlBox value_node;
	// contains a shortForm of the sh:class
	protected String value_class_property;
	protected Double value_order_shacl;
	protected String value_hasValue;
	protected PlantUmlBox value_qualifiedvalueshape;
	protected String value_qualifiedMaxMinCount;
	protected List<String> value_inverseOf;
	protected List<String> value_shor;
	protected List<String> value_shor_datatype;
	protected String value_colorProperty;
	protected String value_colorBackGround;
	
	
	/**
	 * Returns the identifier of the arrow reference (a shortForm), either through an sh:node to an existing NodeShape,
	 * an sh:class to an existing NodeShape, or an sh:class to a class that is not a NodeShape or targeted by a NodeShape
	 * 
	 * @param allBoxes
	 * @return
	 */
	public String getShNodeOrShClassReference() {
		if(this.value_node != null) {
			return this.value_node.getLabel();
		} else if(this.value_class_property != null) {			
			// sh:class may not be targeted to a NodeShape
			// PlantUML will make up a box with the class shortForm automatically
			// we need to return it to indicate the property will generate an arrow in the diagram
			// we don't jave to search in the PlantUmlBoxes
			return this.value_class_property;
			
			// TODO : we may be interested to get the references made through a sh:or ?
		}
		return null;
	}
	
	
	
	
	public String getValue_colorBackGround() {
		return value_colorBackGround;
	}
	
	public void setValue_colorBackGround(String value_colorBackGround) {
		this.value_colorBackGround = value_colorBackGround;
	}
	
	public List<String> getValue_shor_datatype() {
		return value_shor_datatype;
	}
	
	public void setValue_shor_datatype(List<String> value_shor_datatype) {
		this.value_shor_datatype = value_shor_datatype;
	}

	public String getValue_colorProperty() {
		return value_colorProperty;
	}

	public void setValue_colorProperty(String value_colorProperty) {
		this.value_colorProperty = value_colorProperty;
	}

	public List<String> getValue_inverseOf() {
		return value_inverseOf;
	}

	public void setValue_inverseOf(List<String> value_inverseOf) {
		this.value_inverseOf = value_inverseOf;
	}

	public PlantUmlProperty(Resource propertyShape) {
		super();
		this.propertyShape = propertyShape;
	}

	public List<String> getValue_shor() {
		return value_shor;
	}
	
	public void setValue_shor(List<String> value_shor) {
		this.value_shor = value_shor;
	}

	
	public String getValue_path() {
		return value_path;
	}

	public void setValue_path(String value_path) {
		this.value_path = value_path;
	}

	public String getValue_datatype() {
		return value_datatype;
	}

	public void setValue_datatype(String value_datatype) {
		this.value_datatype = value_datatype;
	}

	public String getValue_nodeKind() {
		return value_nodeKind;
	}

	public void setValue_nodeKind(String value_nodeKind) {
		this.value_nodeKind = value_nodeKind;
	}

	public String getValue_cardinality() {
		return value_cardinality;
	}

	public void setValue_cardinality(String value_cardinality) {
		this.value_cardinality = value_cardinality;
	}

	public String getValue_range() {
		return value_range;
	}

	public void setValue_range(String value_range) {
		this.value_range = value_range;
	}

	public String getValue_length() {
		return value_length;
	}

	public void setValue_length(String value_length) {
		this.value_length = value_length;
	}

	public String getValue_pattern() {
		return value_pattern;
	}

	public void setValue_pattern(String value_pattern) {
		this.value_pattern = value_pattern;
	}

	public String getValue_language() {
		return value_language;
	}

	public void setValue_language(String value_language) {
		this.value_language = value_language;
	}

	public String getValue_uniquelang() {
		return value_uniquelang;
	}

	public void setValue_uniquelang(String value_uniquelang) {
		this.value_uniquelang = value_uniquelang;
	}

	public PlantUmlBox getValue_node() {
		return value_node;
	}

	public void setValue_node(PlantUmlBox value_node) {
		this.value_node = value_node;
	}

	public String getValue_class_property() {
		return value_class_property;
	}

	public void setValue_class_property(String value_class_property) {
		this.value_class_property = value_class_property;
	}

	public Double getValue_order_shacl() {
		return value_order_shacl;
	}

	public void setValue_order_shacl(Double value_order_shacl) {
		this.value_order_shacl = value_order_shacl;
	}

	public String getValue_hasValue() {
		return value_hasValue;
	}

	public void setValue_hasValue(String value_hasValue) {
		this.value_hasValue = value_hasValue;
	}

	public PlantUmlBox getValue_qualifiedvalueshape() {
		return value_qualifiedvalueshape;
	}

	public void setValue_qualifiedvalueshape(PlantUmlBox value_qualifiedvalueshape) {
		this.value_qualifiedvalueshape = value_qualifiedvalueshape;
	}

	public String getValue_qualifiedMaxMinCount() {
		return value_qualifiedMaxMinCount;
	}

	public void setValue_qualifiedMaxMinCount(String value_qualifiedMaxMinCount) {
		this.value_qualifiedMaxMinCount = value_qualifiedMaxMinCount;
	}

	public Resource getPropertyShape() {
		return propertyShape;
	}
	
	
}
