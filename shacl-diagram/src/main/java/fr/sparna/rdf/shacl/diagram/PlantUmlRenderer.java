package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;

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

	private String render(PlantUmlProperty property, String boxName, boolean renderAsDatatypeProperty, String NodeShapeId, Map<String, String> collectRelationProperties) {
		
		//get the color for the arrow drawn
		String colorArrowProperty = "";
		if(property.getValue_colorProperty() != null) {
			colorArrowProperty = "[bold,#"+property.getValue_colorProperty()+"]";
		}
				
		if (property.getValue_node() != null) {
			String getCodeUML = renderAsNodeReference(property, boxName, renderAsDatatypeProperty, colorArrowProperty, collectRelationProperties);
			return (getCodeUML.contains("->")) ? "" : getCodeUML;
		} else if (property.getValue_class_property() != null) {
			String getCodeUML = renderAsClassReference(property, boxName, renderAsDatatypeProperty, collectRelationProperties); 
			return (getCodeUML.contains("->")) ? "" : getCodeUML;
		} else if (property.getValue_qualifiedvalueshape() != null) {
			String getCodeUML = renderAsQualifiedShapeReference(property, boxName,colorArrowProperty,collectRelationProperties); 
			return (getCodeUML.contains("->")) ? "" : getCodeUML;
		} else if (property.getValue_shor() != null) {
			return renderAsOr(property, boxName,colorArrowProperty, NodeShapeId);
		} else {
			return renderDefault(property, boxName);
		}
	}

	// uml_shape+ " --> " +"\""+uml_node+"\""+" : "+uml_path+uml_datatype+"
	// "+uml_literal+" "+uml_pattern+" "+uml_nodekind(uml_nodekind)+"\n";
	private String renderAsNodeReference(PlantUmlProperty property, String boxName, Boolean renderAsDatatypeProperty, String colorArrow, Map<String, String> collectRelationProperties) {

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
				
				
				// Merge all arrow 
				collectData(
						// key
						boxName + " <-[bold]-> \"" + property.getValue_node().getLabel()+ "\" : ",
						// Values
						(ncount > 1 ) ? "\" : " + inverse_label : "\" : " + property.getValue_path(),
						// Record
						collectRelationProperties
						);				
				
			} else {
				
				//output = boxName + " -[bold]-> \"" + property.getValue_node().getLabel() + "\" : " + property.getValue_path();
				output = boxName + " -"+colorArrow+"-> \"" + property.getValue_node().getLabel() + "\" : " + property.getValue_path();
				
				if (property.getValue_cardinality() != null) {
					output += " " + property.getValue_cardinality() + " ";
				}
				if (property.getValue_pattern() != null && this.displayPatterns) {
					output += "(" + property.getValue_pattern() + ")" + " ";
				}
				
				// merge the arrow
				String option = "";
				if (property.getValue_cardinality() != null) {
					option += "<U+00A0>" + property.getValue_cardinality() + " ";
				}				
				if (property.getValue_pattern() != null && this.displayPatterns) {
					option += "(" + property.getValue_pattern() + ")" + " ";
				}
				
				// Merge all arrow 
				collectData(
						// key
						boxName + " -"+colorArrow+"-> \"" + property.getValue_node().getLabel()+ "\" : ",
						// Values
						property.getValue_path() + option,
						// Record
						collectRelationProperties
						);
				
				
			}
				
			
		} else {
			
			//output = boxName + " -[bold]-> \"" + property.getValue_node().getLabel() + "\" : " + property.getValue_path();
			output = boxName + " -"+colorArrow+"-> \"" + property.getValue_node().getLabel() + "\" : " + property.getValue_path();
			
			String option = "";
			if (property.getValue_cardinality() != null) {
				output += " " + property.getValue_cardinality() + " ";
				option += "<U+00A0>" + property.getValue_cardinality() + " ";
			}
			if (property.getValue_pattern() != null && this.displayPatterns) {
				output += "(" + property.getValue_pattern() + ")" + " ";
				option += "(" + property.getValue_pattern() + ")" + " ";
			}
			
			// function for Merge all arrow what point to same class
			collectData(
					// key
					boxName + " -"+colorArrow+"-> \"" + property.getValue_node().getLabel()+ "\" : ",
					// Values
					property.getValue_path()+option,
					// Record
					collectRelationProperties
					);
			
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
		String localName = property.getPropertyShape().getLocalName();
		if (localName == null) {
		    localName = property.getPropertyShape().getId().getLabelString();
		}
		String sNameDiamond = "diamond_" + nodeshapeId.replace("-", "_") + "_" + localName.replace("-", "_");
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
		output += "\n";

		// now link diamond to each value in the sh:or
		//for (PlantUmlBox sDataOr : property.getValue_shor()) {
		//	output += sNameDiamond + " .. " + " \""+sDataOr.getNodeShape().getModel().shortForm(sDataOr.getNodeShape().getURI())+ "\"" + "\n";
		//}
		
		for (String sDataOr : property.getValue_shor()) {
			output += sNameDiamond + " .. " + " \""+sDataOr+ "\"" + "\n";
		}
		
		return output;
	}

	// value = uml_shape+ " --> " +"\""+uml_qualifiedvalueshape+"\""+" :
	// "+uml_path+uml_datatype+" "+uml_qualifiedMinMaxCount+"\n";
	private String renderAsQualifiedShapeReference(PlantUmlProperty property, String boxName, String colorArrow, Map<String, String> collectRelationProperties) {
		String output = boxName + " -"+colorArrow+"-> \"" + property.getValue_qualifiedvalueshape().getLabel() + "\" : "
				+ property.getValue_path();

		String option="";
		if (property.getValue_qualifiedMaxMinCount() != null) {
			output += " " + property.getValue_qualifiedMaxMinCount() + " ";
			option = " " + property.getValue_qualifiedMaxMinCount() + " ";
		}
		
		collectData(
				//codeKey
				boxName + " -"+colorArrow+"-> \"" + property.getValue_qualifiedvalueshape().getLabel() + "\" : ",
				//data value
				property.getValue_path()+option, 
				collectRelationProperties);

		output += "\n";

		return output;
	}

	// value = uml_shape+" --> "+"\""+uml_class_property+"\""+" :
	// "+uml_path+uml_literal+" "+uml_pattern+" "+uml_nodekind+"\n";
	private String renderAsClassReference(PlantUmlProperty property, String boxName, boolean renderAsDatatypeProperty, Map<String,String> collectRelationProperties) {

		String output = "";
		
		if (renderAsDatatypeProperty) {
			
			output = boxName + " : +" + property.getValue_path() + " : " + property.getValue_class_property();	
			
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
					
			String option = "";
			if (property.getValue_cardinality() != null) {
				output += " " + property.getValue_cardinality() + " ";
				option += "<U+00A0>" + property.getValue_cardinality() + " ";
			}
			if (property.getValue_pattern() != null && this.displayPatterns) {
				output += "(" + property.getValue_pattern() + ")" + " ";
				option += "(" + property.getValue_pattern() + ")" + " ";
			}

			collectData(
						// Key
						boxName + " --> \""+""+labelColor+ property.getValue_class_property() + "\" : ",
						// data Value
						property.getValue_path() + option,
						collectRelationProperties);
			
			
			
			output += labelColorClose;
		}
		
		output += " \n";
		
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
		
		// if  sh:Or value is of kind of datatype , for each property concat with or word .. eg. xsd:string or rdf:langString
		String shOr_Datatype = "";
		if (property.getValue_shor_datatype() != null) {
			shOr_Datatype = property.getValue_shor_datatype().stream().map(s -> s.toString()).collect(Collectors.joining(" or ")); 
		}
		
		if (property.getValue_datatype() != null) {
			output += " : " + property.getValue_datatype() + " ";
		}else if ((property.getValue_datatype() == null) && (shOr_Datatype != "")) {
			output += " : " +shOr_Datatype+ " ";
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
	
	public String renderDiagram(PlantUmlDiagram diagram) {
		StringBuffer sourceuml = new StringBuffer();	
		sourceuml.append("@startuml\n");
		// this allows to have dots in unescaped classes names, to avoid that are interpreted as namespaces
		// see https://github.com/sparna-git/shacl-play/issues/122
		// see https://forum.plantuml.net/221/dots-in-class-names
		sourceuml.append("set namespaceSeparator none\n");
		sourceuml.append("skinparam classFontSize 14"+"\n");
		sourceuml.append("!define LIGHTORANGE\n");
		sourceuml.append("skinparam componentStyle uml2\n");
		sourceuml.append("skinparam wrapMessageWidth 100\n");
		sourceuml.append("skinparam ArrowColor #Maroon\n");
		sourceuml.append("set namespaceSeparator none \n"); // Command for not create an package uml
		
		PlantUmlRenderer renderer = new PlantUmlRenderer();
		renderer.setGenerateAnchorHyperlink(this.generateAnchorHyperlink);
			
		// retrieve all package declaration
		Set<String> packages = diagram.getBoxes().stream().map(b -> b.getPackageName()).collect(Collectors.toSet());
		for(String aPackage : packages ) {
			if(!aPackage.equals("")) {
				sourceuml.append("namespace "+aPackage+" "+"{\n");
			}
			
			for (PlantUmlBox plantUmlBox : diagram.getBoxes().stream().filter(b -> b.getPackageName().equals(aPackage)).collect(Collectors.toList())) {
					sourceuml.append(renderer.renderNodeShape(plantUmlBox,diagram,this.avoidArrowsToEmptyBoxes));
			}
			
			if(!aPackage.equals("")) {
				sourceuml.append("}\n");
			}			
		}
		
		sourceuml.append("hide circle\n");
		sourceuml.append("hide methods\n");
		sourceuml.append("hide empty members\n");
		sourceuml.append("@enduml\n");
		
		// return output
		return sourceuml.toString();
	}

	public String renderNodeShape(PlantUmlBox box, PlantUmlDiagram diagram, boolean avoidArrowsToEmptyBoxes) {
		// String declaration = "Class"+"
		// "+"\""+box.getNameshape()+"\""+((box.getNametargetclass() != null)?"
		// "+"<"+box.getNametargetclass()+">":"");
		String declaration = "";
		String color = "";
		if(box.getColorClass() != null) {
			color = "#back:"+box.getColorClass()+";";
		}else {
			color = "";
		}
		
		if (box.getProperties().size() > 0 || box.getSuperClasses().size() > 0) {
			if (box.getNodeShape().isAnon()) {
				// give it an empty label
				declaration = "Class" + " " + box.getLabel() +" as " +"\""+" \"";
			} else {
				declaration = "Class" + " " + "\"" + box.getLabel() + "\"";
			}

			declaration += (this.generateAnchorHyperlink) ? " [[#" + box.getLabel() + "]]" : "";
			declaration += " " + color + "\n";
			
			if (box.getSuperClasses() != null) {
				for (PlantUmlBox aSuperClass : box.getSuperClasses()) {
					// generate an "up" arrow - bolder and gray to distinguish it from other arrows
					declaration += "\""+box.getLabel()+"\"" + " -up[#gray,bold]-|> " + "\""+aSuperClass.getLabel()+"\"" + "\n";
				}
			}
			
			String declarationPropertes = "";
			Map<String,String> collectRelationProperties = new HashMap<>();
			for (PlantUmlProperty plantUmlproperty : box.getProperties()) {
				boolean displayAsDatatypeProperty = false;
				
				// if we want to avoid arrows to empty boxes...
				// note that this behavior is triggered only if the diagram has a certain size
				if (avoidArrowsToEmptyBoxes && diagram.getBoxes().size() > 8) {
					// then see if there is a reference to a box
					String arrowReference = plantUmlproperty.getShNodeOrShClassReference();
					PlantUmlBox boxReference = diagram.findBoxById(arrowReference);
					
					// if the box is empty...
					if (
							arrowReference != null
							&&
							(
									// no box : the reference is an sh:class pointing to a URI
									// that does not correspond to a NodeShape
									boxReference == null
									||
									(
											// points to an existing NodeShape, but with no property
											boxReference.getProperties().size() == 0
											&&
											// and does not have a super class
											boxReference.getSuperClasses().size() == 0
									)
							)							
					) {
						// count number of times it is used
						int nCount = 0;
						for (PlantUmlBox plantumlbox : diagram.getBoxes()) {
							nCount += plantumlbox.countShNodeOrShClassReferencesTo(arrowReference);
						}
						
						// if used more than once, then display the box, otherwise don't display it
						if (nCount <= 1) {
							displayAsDatatypeProperty = true;
						}
					}
				}
				
				
				String codePropertyPlantUml = this.render(plantUmlproperty, "\"" + box.getLabel() + "\"", displayAsDatatypeProperty,box.getLabel(), collectRelationProperties);
				if (codePropertyPlantUml!="") {
					declarationPropertes += codePropertyPlantUml;
				} 
				//declaration += this.render(plantUmlproperty, "\"" + box.getLabel() + "\"", displayAsDatatypeProperty,box.getLabel());
			}
			
			if (collectRelationProperties.size() > 0) {
				for (Map.Entry<String, String> entry : collectRelationProperties.entrySet()) {
					String outputData = entry.getKey() + entry.getValue()+" \n";;
					declarationPropertes +=  outputData;
					
				}
			}
			declaration += declarationPropertes;
		}
		
		return declaration;
	}
	
	/*
	 * function for Merge arrows what point to same class.
	 * codeKey - property key
	 * dataValue - data values or additional values
	 * collectRelationProperties - save all properties and if the property is included in the map, generate a list of values.
	 */
	public void collectData(String codeKey,String dataValue ,Map<String,String> collectRelationProperties) {
		
		if (!linkPropertyExist(codeKey, collectRelationProperties)) {
			// 
			collectRelationProperties.put(
					// key
					codeKey,
					// Values
					dataValue);
			
		} else {
			//update record
			updatePropertyRelacionValue(
					// key
					codeKey,
					//data values
					dataValue ,
					//Map
					collectRelationProperties
					);
		}
		
	}	
	
	public boolean linkPropertyExist(String codeKey, Map<String,String> collectRelationProperties) {
		return collectRelationProperties.entrySet().stream().filter(f -> f.getKey().equals(codeKey)).findAny().isPresent();
	}
	
	/* Merge for each input data values in the same property */
	public void updatePropertyRelacionValue(String codeKey,String newValue ,Map<String,String> collectRelationProperties) {
		
		String recordInMap = collectRelationProperties.entrySet().stream().filter(f -> f.getKey().equals(codeKey)).findFirst().get().getValue();
		collectRelationProperties.computeIfPresent(codeKey, (k,v) -> v = recordInMap+" \\l"+newValue);
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
