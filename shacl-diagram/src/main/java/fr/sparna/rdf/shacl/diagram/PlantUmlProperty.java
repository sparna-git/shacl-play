package fr.sparna.rdf.shacl.diagram;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.ShOrReadingUtils;
import fr.sparna.rdf.shacl.SHACL_PLAY;

public class PlantUmlProperty {

	protected Resource propertyShape;
	
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
	
	public PlantUmlProperty(Resource propertyShape) {
		super();
		this.propertyShape = propertyShape;
	}
	
	
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
		return ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(this.propertyShape, propertyShape.getModel().createProperty(SHACL_PLAY.BACKGROUNDCOLOR)), true);
	}
	
	public List<String> getValue_shor_datatype() {
		if (this.propertyShape.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShDatatypeAndShNodeKindInShOr(this.propertyShape.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values.stream().map(r -> { return (r.isURIResource())?r.getModel().shortForm(r.getURI()):r.toString();}).collect(Collectors.toList());
			}			
		}
		
		return null;
	}

	public String getValue_colorProperty() {
		return ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(this.propertyShape, propertyShape.getModel().createProperty(SHACL_PLAY.COLOR)), true);
	}


	public List<String> getValue_inverseOf() {
		return value_inverseOf;
	}

	public void setValue_inverseOf(List<String> value_inverseOf) {
		this.value_inverseOf = value_inverseOf;
	}

	public List<String> getValue_shor() {
		if (this.propertyShape.hasProperty(SH.or)) {			
			List<Resource> values = ShOrReadingUtils.readShClassAndShNodeInShOr(this.propertyShape.getProperty(SH.or).getList());
			if(values.size() > 0) {
				return values.stream().map(r -> { return (r.isURIResource())?r.getModel().shortForm(r.getURI()):r.toString();}).collect(Collectors.toList());
			}			
		}
		
		return null;
	}

	
	public String getValue_path() {
		List<RDFNode> paths = ModelReadingUtils.readObjectAsResource(this.propertyShape, SH.path);
		if(paths != null) {
			Resource firstResource = paths.stream().filter(p -> p.isResource()).map(p -> p.asResource()).findFirst().orElse(null);
			// render the property path using prefixes
			return ModelRenderingUtils.renderSparqlPropertyPath(firstResource, true);
		} else {
			// TODO : this default behavior should be elsewhere probably
			return this.propertyShape.getURI();
		}
	}

	public String getValue_datatype() {
		return ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(this.propertyShape, SH.datatype), true);
	}

	public String getValue_nodeKind() {
		return ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(this.propertyShape, SH.nodeKind), true);
	}

	public String getValue_cardinality() {
		String value_minCount = "0";
		String value_maxCount ="*";
		String uml_code =null;
		if (this.propertyShape.hasProperty(SH.minCount)){
			value_minCount = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(this.propertyShape, SH.minCount), true);
		}
		if (this.propertyShape.hasProperty(SH.maxCount)) {
			value_maxCount = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(this.propertyShape, SH.maxCount), true);
		}
		
		if ((this.propertyShape.hasProperty(SH.minCount)) || (this.propertyShape.hasProperty(SH.maxCount))){
			uml_code = "["+ value_minCount +".."+ value_maxCount +"]";
		} else {
			uml_code = null;
		}
		
		return uml_code;
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
