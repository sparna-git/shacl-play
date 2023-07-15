package fr.sparna.rdf.shacl.shacl2xsd;

import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class ShaclXsdProperty {

	protected Resource propertyShape;
	
	protected String value_path;
	protected String value_datatype;
	protected String value_nodeKind;
	protected String value_minCount;
	protected String value_maxCount;
	protected String value_range;	
	protected String value_length;
	protected String value_pattern;
	protected String value_language;
	protected String value_uniquelang;
	protected ShaclXsdBox value_node;
	protected String value_class_property;
	protected Double value_order_shacl;
	protected String value_hasValue;
	protected ShaclXsdBox value_qualifiedvalueshape;
	protected String value_qualifiedMaxMinCount;
	protected List<String> value_inverseOf;
	protected List<ShaclXsdBox> value_shor;
	protected String value_version;
	protected String value_description;
	

	public String getValue_description() {
		return value_description;
	}

	public void setValue_description(String value_description) {
		this.value_description = value_description;
	}

	public String getValue_minCount() {
		return value_minCount;
	}

	public void setValue_minCount(String value_minCount) {
		this.value_minCount = value_minCount;
	}

	public String getValue_maxCount() {
		return value_maxCount;
	}

	public void setValue_maxCount(String value_maxCount) {
		this.value_maxCount = value_maxCount;
	}

	
	public String getValue_version() {
		return value_version;
	}

	public void setValue_version(String value_version) {
		this.value_version = value_version;
	}

	public List<String> getValue_inverseOf() {
		return value_inverseOf;
	}

	public void setValue_inverseOf(List<String> value_inverseOf) {
		this.value_inverseOf = value_inverseOf;
	}

	public ShaclXsdProperty(Resource propertyShape) {
		super();
		this.propertyShape = propertyShape;
	}

	public List<ShaclXsdBox> getValue_shor() {
		return value_shor;
	}
	
	public void setValue_shor(List<ShaclXsdBox> value_shor) {
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

	public ShaclXsdBox getValue_node() {
		return value_node;
	}

	public void setValue_node(ShaclXsdBox value_node) {
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

	public ShaclXsdBox getValue_qualifiedvalueshape() {
		return value_qualifiedvalueshape;
	}

	public void setValue_qualifiedvalueshape(ShaclXsdBox value_qualifiedvalueshape) {
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
