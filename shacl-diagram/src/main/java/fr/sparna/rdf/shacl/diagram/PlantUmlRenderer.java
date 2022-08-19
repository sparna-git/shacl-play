package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlantUmlRenderer {

	protected boolean generateAnchorHyperlink = false;
	protected boolean displayPatterns = false;
	protected boolean avoidArrowsToEmptyBoxes = true;
	
	protected List<String> inverseList = new ArrayList<String>();
	
	public PlantUmlRenderer() {
		super();
	}

	public PlantUmlRenderer(boolean generateAnchorHyperlink, boolean displayPatterns, boolean avoidArrowsToEmptyBoxes) {
		super();
		this.generateAnchorHyperlink = generateAnchorHyperlink;
		this.displayPatterns = displayPatterns;
		this.avoidArrowsToEmptyBoxes = avoidArrowsToEmptyBoxes;
	}

	private String render(PlantUmlProperty property, String boxName, boolean renderAsDatatypeProperty, String NodeShapeId) {
		
		//get the color for the arrow drawn
		String colorArrowProperty = "";
		if(property.getValue_colorProperty() != null) {
			colorArrowProperty = "[bold,#"+property.getValue_colorProperty()+"]";
		}
		
		
		if (property.getValue_node() != null) {
			return renderAsNodeReference(property, boxName, renderAsDatatypeProperty, colorArrowProperty);
		} else if (property.getValue_class_property() != null) {
			return renderAsClassReference(property, boxName, renderAsDatatypeProperty);
		} else if (property.getValue_qualifiedvalueshape() != null) {
			return renderAsQualifiedShapeReference(property, boxName,colorArrowProperty);
		} else if (property.getValue_shor().size() > 0) {
			return renderAsOr(property, boxName,colorArrowProperty, NodeShapeId);
		} else {
			return renderDefault(property, boxName);
		}
	}

	// uml_shape+ " --> " +"\""+uml_node+"\""+" : "+uml_path+uml_datatype+"
	// "+uml_literal+" "+uml_pattern+" "+uml_nodekind(uml_nodekind)+"\n";
	private String renderAsNodeReference(PlantUmlProperty property, String boxName, Boolean renderAsDatatypeProperty, String colorArrow) {

		// find in property if has attribut
		String output = null;
		String ctrlnodeOrigen = null;
		String ctrlnodeDest = null;

		if (renderAsDatatypeProperty) {
				
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
	private String renderAsOr(PlantUmlProperty property, String boxName, String colorArrow, String NodeShapeId) {
		// use property local name to garantee unicity of diamond
		String nodeshapeId = NodeShapeId.contains(":")?NodeShapeId.split(":")[1]:NodeShapeId;
		String sNameDiamond = "diamond_" + nodeshapeId+"_"+property.getPropertyShape().getLocalName().replace("-", "_");
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
	private String renderAsQualifiedShapeReference(PlantUmlProperty property, String boxName, String colorArrow) {
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
	private String renderAsClassReference(PlantUmlProperty property, String boxName, boolean renderAsDatatypeProperty) {

		String output = "";
		
		// TODO : this can never work as the renderAsDatatypeProperty is true only when sh:node is used
		if (renderAsDatatypeProperty) {
			
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
			
		} else {
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
		}
		

		return output;
	}

	private String renderDefault(PlantUmlProperty property, String boxName) {
		
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
	
	public PlantUmlDiagramOutput renderDiagram(PlantUmlDiagram diagram) {
		StringBuffer sourceuml = new StringBuffer();	
		sourceuml.append("@startuml\n");
		sourceuml.append("skinparam classFontSize 14"+"\n");
		sourceuml.append("!define LIGHTORANGE\n");
		sourceuml.append("skinparam componentStyle uml2\n");
		sourceuml.append("skinparam wrapMessageWidth 100\n");
		sourceuml.append("skinparam ArrowColor #Maroon\n");
		
		PlantUmlRenderer renderer = new PlantUmlRenderer();
		renderer.setGenerateAnchorHyperlink(this.generateAnchorHyperlink);
			
		// retrieve all package declaration
		Set<String> packages = diagram.getBoxes().stream().map(b -> b.getPackageName()).collect(Collectors.toSet());
		for(String aPackage : packages ) {
			if(!aPackage.equals("")) {
				sourceuml.append("namespace "+aPackage+" "+"{\n");
			}
			
			for (PlantUmlBox plantUmlBox : diagram.getBoxes().stream().filter(b -> b.getPackageName().equals(aPackage)).collect(Collectors.toList())) {
					sourceuml.append(renderer.renderNodeShape(plantUmlBox,diagram.getBoxes(),this.avoidArrowsToEmptyBoxes));
			}
			
			if(!aPackage.equals("")) {
				sourceuml.append("}\n");
			}			
		}
		
		sourceuml.append("hide circle\n");
		sourceuml.append("hide methods\n");
		sourceuml.append("hide empty members\n");
		sourceuml.append("@enduml\n");
		
		// create output
		PlantUmlDiagramOutput output = new PlantUmlDiagramOutput(sourceuml.toString());
		if(diagram.getResource() != null) {
			output.setDiagramUri(diagram.getResource().getURI());
		}
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
		

		
		if (box.getProperties().size() > 0 || box.getSuperClasses().size() > 0) {
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
					declaration += "\""+box.getLabel()+"\"" + " -up[#gray,bold]-|> " + "\""+aSuperClass.getLabel()+"\"" + "\n";
				}
			}

			for (PlantUmlProperty plantUmlproperty : box.getProperties()) {
				boolean displayAsDatatypeProperty = false;
				
				if (avoidArrowsToEmptyBoxes) {
					if (plantUmlproperty.value_node != null) {
						if (plantUmlproperty.value_node.getProperties().size() == 0) {
							// count number of times it is used
							int nCount = 0;
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
							// if used more than once, then dispay the box, otherwise don't display it
							if (nCount <= 1) {
								displayAsDatatypeProperty = true;
							}
						}
					}
				}
				
				declaration += this.render(plantUmlproperty, "\"" + box.getLabel() + "\"", displayAsDatatypeProperty,box.getLabel());
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

	public boolean isAvoidArrowsToEmptyBoxes() {
		return avoidArrowsToEmptyBoxes;
	}

	public void setAvoidArrowsToEmptyBoxes(boolean avoidArrowsToEmptyBoxes) {
		this.avoidArrowsToEmptyBoxes = avoidArrowsToEmptyBoxes;
	}
	

}
