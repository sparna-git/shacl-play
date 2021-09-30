package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

public class PlantUmlRenderer {

	protected boolean generateAnchorHyperlink = false;
	protected boolean displayPatterns = false;
	List<String> inverseList = new ArrayList<String>();

	public String render(PlantUmlProperty property, String boxName, Boolean ctrlExpDiagramm) {
		
		//get the color for the arrow drawn
		String colorArrowProperty = "";
		if(property.getValue_colorProperty() != null) {
			colorArrowProperty = "[bold,#"+property.getValue_colorProperty()+"]";
		}
		
		
		if (property.getValue_node() != null) {
			return renderAsNodeReference(property, boxName, ctrlExpDiagramm, colorArrowProperty);
		} else if (property.getValue_class_property() != null) {
			return renderAsClassReference(property, boxName);
		} else if (property.getValue_qualifiedvalueshape() != null) {
			return renderAsQualifiedShapeReference(property, boxName,colorArrowProperty);
		} else if (property.getValue_shor().size() > 0) {
			return renderAsOr(property, boxName,colorArrowProperty);
		} else {
			return renderDefault(property, boxName);
		}
	}

	// uml_shape+ " --> " +"\""+uml_node+"\""+" : "+uml_path+uml_datatype+"
	// "+uml_literal+" "+uml_pattern+" "+uml_nodekind(uml_nodekind)+"\n";
	public String renderAsNodeReference(PlantUmlProperty property, String boxName, Boolean ctrlExpDiagramm, String colorArrow) {

		// find in property if has attribut
		String output = null;
		String ctrlnodeOrigen = null;
		String ctrlnodeDest = null;

		if (ctrlExpDiagramm) {
				
				output = boxName + " : +" + property.getValue_path() + " : " + property.getValue_node().getLabel();	
				
				if (property.getValue_cardinality() != null) {
					output += " " + property.getValue_cardinality() + " ";
				}
				if (property.getValue_pattern() != null && this.displayPatterns) {
					output += "(" + property.getValue_pattern() + ")" + " ";
				}
				if (property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("sh:IRI")) {
					output += property.getValue_nodeKind() + " ";
				}
				
		} else if(property.getValue_node().getProperties().size() > 0) {
			boolean bInverseOf = false;
			// find the relation when it's the inverse Of property
			int inverseOf = property.getValue_inverseOf().size();
			String inverse_label = "";
			
				ctrlnodeOrigen = property.getValue_node().getLabel();
				for (PlantUmlProperty inverseOfProperty : property.getValue_node().getProperties()) {
					if (inverseOfProperty.getValue_node() != null) {
						//if (inverseOfProperty.getValue_node().getNodeShape().getLocalName().equals(boxName)) {
						if (inverseOfProperty.getValue_node().getLabel().equals(property.getValue_node().toString())) {	
							bInverseOf = true;
							ctrlnodeDest = inverseOfProperty.getValue_node().getNodeShape().getLocalName();
							inverse_label += inverseOfProperty.getValue_path();
							
							
							//Read 
							if (property.getValue_cardinality() != null) {
								inverse_label += " " + property.getValue_cardinality() + " ";
							}
							if (property.getValue_pattern() != null && this.displayPatterns) {
								inverse_label += "(" + property.getValue_pattern() + ")" + " ";
							}
							if (property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("sh:IRI")) {
								inverse_label += property.getValue_nodeKind() + " ";
							}
							
							inverse_label += " / ";
						}
					}
				}
				
				if(inverse_label.length() > 0) {
					inverse_label = inverse_label.substring(0, inverse_label.length() - 3);
				}
			
			
			if(inverse_label.length() > 0) {
				String[] nfois = inverse_label.split(" / ");
				Integer ncount = 0;
				for(String nRep : nfois) {
					ncount +=1;
				}
				
				if(ncount > 1 ) {
					
					output = boxName + " <-[bold]-> \"" + property.getValue_node().getLabel() + "\" : " + inverse_label;
				} else {
					output = boxName + " <-[bold]-> \"" + property.getValue_node().getLabel() + "\" : " + property.getValue_path()
					+ " / " + inverse_label;
				}
				
			} else {
				
				//output = boxName + " -[bold]-> \"" + property.getValue_node().getLabel() + "\" : " + property.getValue_path();
				output = boxName + " -"+colorArrow+"-> \"" + property.getValue_node().getLabel() + "\" : " + property.getValue_path();
				
				if (property.getValue_cardinality() != null) {
					output += " " + property.getValue_cardinality() + " ";
				}
				if (property.getValue_pattern() != null && this.displayPatterns) {
					output += "(" + property.getValue_pattern() + ")" + " ";
				}
				if (property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("sh:IRI")) {
					output += property.getValue_nodeKind() + " ";
				}
				
			}
				
			
		} else {
			
			//output = boxName + " -[bold]-> \"" + property.getValue_node().getLabel() + "\" : " + property.getValue_path();
			output = boxName + " -"+colorArrow+"-> \"" + property.getValue_node().getLabel() + "\" : " + property.getValue_path();
			
			if (property.getValue_cardinality() != null) {
				output += " " + property.getValue_cardinality() + " ";
			}
			if (property.getValue_pattern() != null && this.displayPatterns) {
				output += "(" + property.getValue_pattern() + ")" + " ";
			}
			if (property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("sh:IRI")) {
				output += property.getValue_nodeKind() + " ";
			}
			
		}

		if (ctrlnodeOrigen != null & ctrlnodeDest != null) {
			if (inverseList.contains("node" + '-' + ctrlnodeOrigen + '-' + ctrlnodeDest)
					|| inverseList.contains("node" + '-' + ctrlnodeDest + '-' + ctrlnodeOrigen)) {
				output = "";
			}
			inverseList.add("node" + '-' + ctrlnodeOrigen + '-' + ctrlnodeDest);
		}

		output += "\n";
		return output;
	}

	// value = uml_shape+" --> "+"\""+uml_or;
	public String renderAsOr(PlantUmlProperty property, String boxName, String colorArrow) {
		// use property local name to garantee unicity of diamond
		String sNameDiamond = "diamond_" + property.getPropertyShape().getLocalName();
		// diamond declaration
		String output = "<> " + sNameDiamond + "\n";

		// link between box and diamond
		//output += boxName + " -[bold]-> \"" + sNameDiamond + "\" : " + property.getValue_path();
		output += boxName + " -"+colorArrow+"-> \"" + sNameDiamond + "\" : " + property.getValue_path();

		// added information on link
		if (property.getValue_cardinality() != null) {
			output += " " + property.getValue_cardinality() + " ";
		}
		if (property.getValue_pattern() != null && this.displayPatterns) {
			output += "(" + property.getValue_pattern() + ")" + " ";
		}
		if (property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("sh:IRI")) {
			output += property.getValue_nodeKind() + " ";
		}
		output += "\n";

		// now link diamond to each value in the sh:or
		for (PlantUmlBox sDataOr : property.getValue_shor()) {
			output += sNameDiamond + " .. " + " \""+sDataOr.getNodeShape().getModel().shortForm(sDataOr.getNodeShape().getURI())+ "\"" + "\n";
		}

		return output;
	}

	// value = uml_shape+ " --> " +"\""+uml_qualifiedvalueshape+"\""+" :
	// "+uml_path+uml_datatype+" "+uml_qualifiedMinMaxCount+"\n";
	public String renderAsQualifiedShapeReference(PlantUmlProperty property, String boxName, String colorArrow) {
		String output = boxName + " -"+colorArrow+"-> \"" + property.getValue_qualifiedvalueshape().getLabel() + "\" : "
				+ property.getValue_path();

		if (property.getValue_qualifiedMaxMinCount() != null) {
			output += " " + property.getValue_qualifiedMaxMinCount() + " ";
		}

		output += "\n";

		return output;
	}

	// value = uml_shape+" --> "+"\""+uml_class_property+"\""+" :
	// "+uml_path+uml_literal+" "+uml_pattern+" "+uml_nodekind+"\n";
	public String renderAsClassReference(PlantUmlProperty property, String boxName) {

		String output = "";
		
		String labelColor = "";
		String labelColorClose = "";
		if(property.getValue_colorProperty() != null) {
			labelColor = "<color:"+property.getValue_colorProperty()+">"+" ";
			labelColorClose = "</color>";
		}
		
		// attempt with dotted lines
		// output = boxName + " -[dotted]-> \"" + property.getValue_class_property() + "\" : " + property.getValue_path();
		output = boxName + " --> \""+""+labelColor+ property.getValue_class_property() + "\" : " + property.getValue_path()+" ";
		

		if (property.getValue_cardinality() != null) {
			output += " " + property.getValue_cardinality() + " ";
		}
		if (property.getValue_pattern() != null && this.displayPatterns) {
			output += "(" + property.getValue_pattern() + ")" + " ";
		}
		if (property.getValue_nodeKind() != null && !property.getValue_nodeKind().equals("sh:IRI")) {
			output += property.getValue_nodeKind() + " ";
		}

		output += labelColorClose+" \n";
		return output;
	}

	public String renderDefault(PlantUmlProperty property, String boxName) {
		
		String labelColor = "";
		String labelColorClose = "";
		if(property.getValue_colorProperty() != null) {
			labelColor = "<color:"+property.getValue_colorProperty()+">"+" ";
			labelColorClose = "</color>";
		}
		
		
		String output = boxName + " : "+""+labelColor+ property.getValue_path() + " ";

		if (property.getValue_datatype() != null) {
			output += " : " + property.getValue_datatype() + " ";
		}
		if (property.getValue_cardinality() != null) {
			output += " " + property.getValue_cardinality() + " ";
		}
		if (property.getValue_pattern() != null && this.displayPatterns) {
			output += "{field}" + " " + "(" + property.getValue_pattern() + ")" + " ";
		}
		if (property.getValue_uniquelang() != null) {
			output += property.getValue_uniquelang() + " ";
		}
		if (property.getValue_hasValue() != null) {
			//output += "[" + property.getValue_hasValue() + "]" + " ";
			output += " = " + property.getValue_hasValue() + " ";
		}
		output += labelColorClose+" \n";

		return output;
	}

	public String renderNodeShape(PlantUmlBox box, List<PlantUmlBox> GlobalBox, boolean avoidArrowsToEmptyBoxes) {
		// String declaration = "Class"+"
		// "+"\""+box.getNameshape()+"\""+((box.getNametargetclass() != null)?"
		// "+"<"+box.getNametargetclass()+">":"");
		String declaration = "";
		
		
		String versionColorClass = "";
		if(box.getColorClass() != null) {
			versionColorClass = "#line:"+box.getColorClass()+";";
		}else {
			versionColorClass = "";
		}

		//String defaultColorRow = "[bold,#green]";
		String colorDrawnClass = "";
		// Array for control inverse
		

		if (avoidArrowsToEmptyBoxes) {
			if (box.getProperties().size() > 0) {
				if (box.getNodeShape().isAnon()) {
					declaration = "Class" + " " + box.getLabel() +" as " +"\""+" \"";
				} else {
					if(this.generateAnchorHyperlink) {
						declaration = "Class" + " " + "\"" + box.getLabel() + "\"";
					}else {
						declaration = "Class" + " " + "\"" + box.getLabel() + "\""+" "+ versionColorClass;
					}
				}
				declaration += (this.generateAnchorHyperlink) ? " [[#" + box.getLabel() + "]]"+" "+ versionColorClass + "\n" : "\n";
				if (box.getSuperClasses() != null) {
					for (PlantUmlBox aSuperClass : box.getSuperClasses()) {
						// generate an "up" arrow
						declaration += "\""+box.getLabel()+"\"" + "-up-|>" + "\""+aSuperClass.getLabel()+"\"" + "\n";
					}
				}

				// 
				for (PlantUmlProperty plantUmlproperty : box.getProperties()) {
					Boolean valueIsNodeShape = false;
					if (plantUmlproperty.value_node != null) {
						if (plantUmlproperty.value_node.getProperties().size() < 1) {
							Integer nCount = 0;
							for (PlantUmlBox plantumlbox : GlobalBox) {
								for (PlantUmlProperty UmlProperty : plantumlbox.getProperties()) {
									if (UmlProperty.value_node != null) {
										if (UmlProperty.value_node.getNodeShape()
												.equals(plantUmlproperty.value_node.getNodeShape())) {
											nCount += 1;
										}
									}
								}
							}
							if (nCount > 1) {
								valueIsNodeShape = false;
							} else {
								valueIsNodeShape = true;
							}
						}
					}
					
					declaration += this.render(plantUmlproperty, "\"" + box.getLabel() + "\"", valueIsNodeShape);
				}
			}
		} else {
			
			if (box.getNodeShape().isAnon()) {
				declaration = "Class" + " " + box.getLabel() +" as " +"\""+" \"";
			} else {
				if(this.generateAnchorHyperlink) {
					declaration = "Class" + " " + "\"" + box.getLabel() + "\"";
				}else {
					declaration = "Class" + " " + "\"" + box.getLabel() + "\""+" "+ versionColorClass;
				}				
			}
						
			declaration += (this.generateAnchorHyperlink) ? " [[#" + box.getLabel() + "]]"+" "+ versionColorClass + "\n" : "\n";
			if (box.getSuperClasses() != null) {
				for (PlantUmlBox aSuperClass : box.getSuperClasses()) {
					// generate an "up" arrow
					declaration += "\""+box.getLabel()+"\"" + "-up-|>" + "\""+aSuperClass.getLabel()+"\"" + "\n";
				}
			}

			for (PlantUmlProperty plantUmlproperty : box.getProperties()) {
				declaration += this.render(plantUmlproperty, "\"" + box.getLabel() + "\"", false);
			}
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