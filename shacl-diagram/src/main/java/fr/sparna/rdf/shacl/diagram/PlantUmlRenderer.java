package fr.sparna.rdf.shacl.diagram;

public class PlantUmlRenderer {

	protected boolean generateAnchorHyperlink = false;
	protected boolean displayPatterns = false;


	public String render(PlantUmlProperty property, String boxName) {
		if(property.getValue_node() != null) {
			return renderAsNodeReference(property, boxName);
		} else if(property.getValue_class_property() != null) {
			return renderAsClassReference(property, boxName);
		} else if(property.getValue_qualifiedvalueshape() != null) {
			return renderAsQualifiedShapeReference(property, boxName);
		} else if(property.getValue_shor().size() > 0) {
			return renderAsOr(property, boxName);
		} else {
			return renderDefault(property, boxName);
		}
	}
	
	// uml_shape+ " --> " +"\""+uml_node+"\""+" : "+uml_path+uml_datatype+" "+uml_literal+" "+uml_pattern+" "+uml_nodekind(uml_nodekind)+"\n";  
	public String renderAsNodeReference(PlantUmlProperty property, String boxName) {
		String output = boxName+" --> \""+property.getValue_node().getLabel()+"\" : "+property.getValue_path();
		
		if(property.getValue_cardinality() != null) {
			output += " "+property.getValue_cardinality()+" ";
		}
		if(property.getValue_pattern() != null && this.displayPatterns) {
			output += "("+property.getValue_pattern()+")"+" ";
		}
		if(property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("IRI")) {
			output += property.getValue_nodeKind()+" ";
		}
		output += "\n";
		
		return output;
	}
	
	// value =  uml_shape+" --> "+"\""+uml_or;	
	public String renderAsOr(PlantUmlProperty property, String boxName) {
		// use property local name to garantee unicity of diamond
		String sNameDiamond = "diamond_"+property.getPropertyShape().getLocalName();
		// diamond declaration
		String output = "<> "+ sNameDiamond +"\n";

		// link between box and diamond
		output += boxName+" --> \""+sNameDiamond+"\" : "+property.getValue_path();
		
		// added information on link
		if(property.getValue_cardinality() != null) {
			output += " "+property.getValue_cardinality()+" ";
		}
		if(property.getValue_pattern() != null && this.displayPatterns) {
			output += "("+property.getValue_pattern()+")"+" ";
		}
		if(property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("IRI")) {
			output += property.getValue_nodeKind()+" ";
		}
		output +="\n";
		
		// now link diamond to each value in the sh:or
		for(PlantUmlBox sDataOr : property.getValue_shor()) {
			output += sNameDiamond + " .. "+sDataOr.getNodeShape().getLocalName()+"\n";
		}

		return output;				
	}
	
	// value = uml_shape+ " --> " +"\""+uml_qualifiedvalueshape+"\""+" : "+uml_path+uml_datatype+" "+uml_qualifiedMinMaxCount+"\n";
	public String renderAsQualifiedShapeReference(PlantUmlProperty property, String boxName) {
		String output = boxName+" --> \""+property.getValue_qualifiedvalueshape().getLabel()+"\" : "+property.getValue_path();
		
		if(property.getValue_qualifiedMaxMinCount() != null) {
			output += " "+property.getValue_qualifiedMaxMinCount()+" ";
		}

		output += "\n";
		
		return output;
	}
	
	// value =  uml_shape+" --> "+"\""+uml_class_property+"\""+" : "+uml_path+uml_literal+" "+uml_pattern+" "+uml_nodekind+"\n";
	public String renderAsClassReference(PlantUmlProperty property, String boxName) {
		String output = boxName+" --> \""+property.getValue_class_property()+"\" : "+property.getValue_path();
		
		if(property.getValue_cardinality() != null) {
			output += " "+property.getValue_cardinality()+" ";
		}
		if(property.getValue_pattern() != null && this.displayPatterns) {
			output += "("+property.getValue_pattern()+")"+" ";
		}
		if(property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("IRI")) {
			output += property.getValue_nodeKind()+" ";
		}
		output += "\n";
		
		return output;
	}
	
	public String renderDefault(PlantUmlProperty property, String boxName) {
		String output = boxName+" : "+property.getValue_path()+" ";
		
		if(property.getValue_datatype() != null) {		
			output += " : "+property.getValue_datatype()+" ";
		}
		if(property.getValue_cardinality() != null) {		
			output += " "+property.getValue_cardinality()+" ";
		}
		if(property.getValue_pattern() != null && this.displayPatterns) {
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

	public String renderNodeShape(PlantUmlBox box) {
		// String declaration = "Class"+" "+"\""+box.getNameshape()+"\""+((box.getNametargetclass() != null)?" "+"<"+box.getNametargetclass()+">":"");
		String declaration = "Class"+" "+"\""+box.getLabel()+"\"";
		declaration += (this.generateAnchorHyperlink)?"[[#"+box.getLabel()+"]]"+"\n":"\n";
		if(box.getSuperClasses() != null) {
			for (PlantUmlBox aSuperClass : box.getSuperClasses()) {
				// generate an "up" arrow
				declaration += "\""+box.getLabel()+"\"" + "-up-|>" + "\""+aSuperClass.getLabel()+"\"" + "\n";
			}
		}
		
		for (PlantUmlProperty plantUmlproperty : box.getProperties()) {
			declaration += this.render(plantUmlproperty, box.getLabel());
		}
		
		return declaration;
	}	
	

	public boolean isGenerateAnchorHyperlink() {
		return generateAnchorHyperlink;
	}

	public void setGenerateAnchorHyperlink(boolean generateAnchorHyperlink) {
		this.generateAnchorHyperlink = generateAnchorHyperlink;
	}

	public boolean isDisplayPatterns() {
		return displayPatterns;
	}

	public void setDisplayPatterns(boolean displayPatterns) {
		this.displayPatterns = displayPatterns;
	}
	
}
