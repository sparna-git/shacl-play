package fr.sparna.rdf.shacl.diagram;

public class PlantUmlRenderer {

	protected String nameshape;
	
	public PlantUmlRenderer(String nameshape) {
		super();
		this.nameshape = nameshape;
	}

	public String render(PlantUmlProperty property) {
		if(property.getValue_node() != null) {
			return renderAsNodeReference(property);
		} else if(property.getValue_class_property() != null) {
			return renderAsClassReference(property);
		} else if(property.getValue_qualifiedvalueshape() != null) {
			return renderAsClassReference(property);
		} else {
			return renderDefault(property);
		}
	}
	
	// uml_shape+ " --> " +"\""+uml_node+"\""+" : "+uml_path+uml_datatype+" "+uml_literal+" "+uml_pattern+" "+uml_nodekind(uml_nodekind)+"\n";  
	public String renderAsNodeReference(PlantUmlProperty property) {
		String output = nameshape+" --> \""+property.getValue_class_property()+"\" : "+property.getValue_path();
		
		if(property.getValue_datatype() != null) {		
			output += " : "+property.getValue_datatype()+" ";
		}
		if(property.getValue_cardinality() != null) {
			output += property.getValue_cardinality()+" ";
		}
		if(property.getValue_pattern() != null) {
			output += "{field}"+" "+"("+property.getValue_pattern()+")"+" ";
		}
		if(property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("IRI")) {
			output += property.getValue_nodeKind()+" ";
		}
		output += "\n";
		
		return output;
	}
	
	// value = uml_shape+ " --> " +"\""+uml_qualifiedvalueshape+"\""+" : "+uml_path+uml_datatype+" "+uml_qualifiedMinMaxCount+"\n";
	public String renderAsClassReference(PlantUmlProperty property) {
		String output = nameshape+" --> \""+property.getValue_qualifiedvalueshape()+"\" : "+property.getValue_path();
		
		if(property.getValue_datatype() != null) {		
			output += " : "+property.getValue_datatype()+" ";
		}
		if(property.getValue_qualifiedMaxMinCount() != null) {
			output += property.getValue_cardinality()+" ";
		}

		output += "\n";
		
		return output;
	}
	
	// value =  uml_shape+" --> "+"\""+uml_class_property+"\""+" : "+uml_path+uml_literal+" "+uml_pattern+" "+uml_nodekind+"\n";
	public String renderAsQualifiedShapeReference(PlantUmlProperty property) {
		String output = nameshape+" --> \""+property.getValue_node()+"\" : "+property.getValue_path();
		
		if(property.getValue_datatype() != null) {		
			output += " : "+property.getValue_datatype()+" ";
		}
		if(property.getValue_cardinality() != null) {
			output += property.getValue_cardinality()+" ";
		}
		if(property.getValue_pattern() != null) {
			output += "{field}"+" "+"("+property.getValue_pattern()+")"+" ";
		}
		if(property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("IRI")) {
			output += property.getValue_nodeKind()+" ";
		}
		output += "\n";
		
		return output;
	}
	
	
	public String renderDefault(PlantUmlProperty property) {
		String output = nameshape+" : \""+property.getValue_path()+"\" ";
		
		if(property.getValue_datatype() != null) {		
			output += " : "+property.getValue_datatype()+" ";
		}
		if(property.getValue_pattern() != null) {
			output += "{field}"+" "+"("+property.getValue_pattern()+")"+" ";
		}
		if(property.getValue_uniquelang() != null) {
			output += property.getValue_uniquelang()+" ";
		}
		if(property.getValue_hasValue() != null) {
			output += "["+property.getValue_hasValue()+"]"+" ";
		}
		output += "\n";
		
		return output;
	}
	
	 
}
