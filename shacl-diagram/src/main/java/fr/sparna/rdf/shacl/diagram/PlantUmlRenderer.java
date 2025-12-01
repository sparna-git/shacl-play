package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.ModelRenderingUtils;

public class PlantUmlRenderer {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected boolean generateAnchorHyperlink = false;
	protected boolean displayPatterns = false;
	protected boolean avoidArrowsToEmptyBoxes = true;
	protected boolean includeSubclassLinks = true;
	protected boolean hideProperties = false;
	// true to indicate that the diagram is a section diagram
	protected boolean renderSectionDiagram = false;

	// the current diagram being rendered	
	protected transient PlantUmlDiagram diagram;

	// map to "merge" all properties that point to the same class into a single arrow
	protected transient Map<NodeShapeArrowKey, String> nodeShapeArrows = new HashMap<>();

	// current temporary arrow direction index to determine the u,d,r,l directions of the arrows
	protected transient int currentArrowDirectionIndex = 0;

	/**
	 * A key to be used in the map that gathers arrows inside one node shape
	 */
	class NodeShapeArrowKey {
		String box;
		String color;
		String reference;

		public NodeShapeArrowKey(String box, String color, String reference) {
			this.box = box;
			this.color = color;
			this.reference = reference;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NodeShapeArrowKey) {
				NodeShapeArrowKey other = (NodeShapeArrowKey) obj;
				return this.box.equals(other.box) && this.color.equals(other.color) && this.reference.equals(other.reference);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.box.hashCode() + this.color.hashCode() + this.reference.hashCode();
		}
		
	}
	
	public PlantUmlRenderer() {
		super();
	}

	public PlantUmlRenderer(boolean generateAnchorHyperlink, boolean displayPatterns, boolean avoidArrowsToEmptyBoxes, boolean includeSubclassLinks, boolean hideProperties) {
		super();
		this.generateAnchorHyperlink = generateAnchorHyperlink;
		this.displayPatterns = displayPatterns;
		this.avoidArrowsToEmptyBoxes = avoidArrowsToEmptyBoxes;
		this.includeSubclassLinks = includeSubclassLinks;
		this.hideProperties = hideProperties;
	}

	private String renderProperty(
			PlantUmlProperty property,
			PlantUmlBoxIfc box,
			boolean renderAsDatatypeProperty
			
	) {
		
		// get the color for the arrow drawn
		String colorArrowProperty = "";
		if(property.getColorString() != null) {
			colorArrowProperty = "[bold,#"+property.getColorString()+"]";
		}
				
		if (property.getShNode().isPresent()) {
			return renderAsNodeReference(property, box, renderAsDatatypeProperty, colorArrowProperty);
		} else if (property.getShClass().isPresent()) {
			return renderAsClassReference(property, box, renderAsDatatypeProperty); 
		} else if (property.getShQualifiedValueShape().isPresent()) {
			return renderAsQualifiedShapeReference(property, box,colorArrowProperty); 
		} else if (property.hasShOrShClassOrShNode()) {
			return renderAsOr(property, box,colorArrowProperty);
		} else {
			return renderDefault(property, box);
		}
	}

	// uml_shape+ " --> " +"\""+uml_node+"\""+" : "+uml_path+uml_datatype+"
	// "+uml_literal+" "+uml_pattern+" "+uml_nodekind(uml_nodekind)+"\n";
	private String renderAsNodeReference(PlantUmlProperty property, PlantUmlBoxIfc box, Boolean renderAsDatatypeProperty, String colorArrow) {
		String nodeReference = this.resolveShNodeReference(property.getShNode().get());		
		
		if (renderAsDatatypeProperty) {	
			String output = null;			
			output = box.getPlantUmlQuotedBoxName() + " : +" + property.getPathAsSparql() + " : " + nodeReference;	

			if (property.getPlantUmlCardinalityString() != null) {
				output += " " + property.getPlantUmlCardinalityString() + " ";
			}
			if (property.getShPattern().isPresent() && this.displayPatterns) {
				output += "(" + ModelRenderingUtils.render(property.getShPattern().get()) + ")" + " ";
			}
			if (property.getShNodeKind().isPresent() && !property.getShNodeKind().get().equals(SHACLM.IRI)) {
				output += ModelRenderingUtils.render(property.getShNodeKind().get()) + " " ;
			}
			output += "\n";
			return output;			
		} else if(
			property.getShNode().isPresent()
			&&
			diagram.findBoxByResource(property.getShNode().get()) != null
			&&
			diagram.findBoxByResource(property.getShNode().get()).getProperties().size() > 0
		) {			
			// merge the arrow
			String option = "";
			if (property.getPlantUmlCardinalityString() != null) {
				option += "<U+00A0>" + property.getPlantUmlCardinalityString() + " ";
			}				
			if (property.getShPattern().isPresent() && this.displayPatterns) {
				option += "(" + ModelRenderingUtils.render(property.getShPattern().get()) + ")" + " ";
			}
			
			// Merge all arrow
			if (!property.getShGroup().isPresent()) {
				collectData(
						// key
						// box.getPlantUmlQuotedBoxName() + " -"+colorArrow+"-> \"" + nodeReference+ "\" : ",
						box.getPlantUmlQuotedBoxName(), colorArrow, nodeReference,
						// Values
						property.getPathAsSparql() + option
				);
			}

			return null;
		} else {
			String option = "";
			if (property.getPlantUmlCardinalityString() != null) {
				option += "<U+00A0>" + property.getPlantUmlCardinalityString() + " ";
			}
			if (property.getShPattern().isPresent() && this.displayPatterns) {
				option += "(" + ModelRenderingUtils.render(property.getShPattern().get()) + ")" + " ";
			}
			
			// function for Merge all arrow what point to same class
			if (!property.getShGroup().isPresent()) {
				collectData(
					// key
					// box.getPlantUmlQuotedBoxName() + " -"+colorArrow+"-> \"" + nodeReference + "\" : ",
					box.getPlantUmlQuotedBoxName(), colorArrow, nodeReference,
					// Values
					property.getPathAsSparql()+option
				);
			}
			
			return null;
		}
	}

	// value = uml_shape+" --> "+"\""+uml_or;
	private String renderAsOr(PlantUmlProperty property, PlantUmlBoxIfc box, String colorArrow) {
		// use property local name to garantee unicity of diamond
		String nodeShapeLocalName = box.getNodeShape().getLocalName();
		String nodeshapeId = nodeShapeLocalName.contains(":")?nodeShapeLocalName.split(":")[1]:nodeShapeLocalName;
		String localName = property.getPropertyShape().getLocalName();
		if (localName == null) {
		    localName = property.getPropertyShape().getId().getLabelString();
		}
		
		
		String sNameDiamond = "diamond_" + nodeshapeId.replace("-", "_") + "_" + localName.replace("-", "_");
		
		// diamond declaration
		String output = "<> " + sNameDiamond + "\n";

		// link between box and diamond
		// Thomas : empirical : OR arrows look much better when they don't have direction (they will be pointing downwards)
		output += box.getPlantUmlQuotedBoxName() + " -"+colorArrow+"-> \"" + sNameDiamond + "\" : " + property.getPathAsSparql();

		// added information on link
		if (property.getPlantUmlCardinalityString() != null) {
			output += " " + property.getPlantUmlCardinalityString() + " ";
		}
		if (property.getShPattern().isPresent() && this.displayPatterns) {
			output += "(" + ModelRenderingUtils.render(property.getShPattern().get()) + ")" + " ";
		}
		output += "\n";

		// now link diamond to each value in the sh:or
		if(property.getShOrShClass() != null) {
			for (Resource shOrShClass : property.getShOrShClass() ) {
				output += sNameDiamond + " .. " + " \""+this.resolveShClassReference(shOrShClass)+ "\"" + "\n";
			}			
		}
		if(property.getShOrShNode() != null) {
			for (Resource shOrShNode : property.getShOrShNode() ) {
				output += sNameDiamond + " .. " + " \""+this.resolveShNodeReference(shOrShNode)+ "\"" + "\n";
			}
			
		}
		
		return output;
	}

	// value = uml_shape+ " --> " +"\""+uml_qualifiedvalueshape+"\""+" :
	// "+uml_path+uml_datatype+" "+uml_qualifiedMinMaxCount+"\n";
	private String renderAsQualifiedShapeReference(PlantUmlProperty property, PlantUmlBoxIfc box, String colorArrow) {

		String option="";
		if (property.getPlantUmlQualifiedCardinalityString() != null) {
			option = " " + property.getPlantUmlQualifiedCardinalityString() + " ";
		}
		
		if (!property.getShGroup().isPresent()) {
			collectData(
					//codeKey
					// box.getPlantUmlQuotedBoxName() + " -"+colorArrow+"-> \"" + property.getShQualifiedValueShapeLabel() + "\" : ",
					box.getPlantUmlQuotedBoxName(), colorArrow, property.getShQualifiedValueShapeLabel(),
					//data value
					property.getPathAsSparql()+option
			);
		}
		return null;
	}

	// value = uml_shape+" --> "+"\""+uml_class_property+"\""+" :
	// "+uml_path+uml_literal+" "+uml_pattern+" "+uml_nodekind+"\n";
	private String renderAsClassReference(PlantUmlProperty property, PlantUmlBoxIfc box, boolean renderAsDatatypeProperty) {
		String classReference = this.resolveShClassReference(property.getShClass().get());
		
		if (renderAsDatatypeProperty) {
			String output = "";

			// output = box.getPlantUmlQuotedBoxName() + " : +" + property.getPathAsSparql() + " : " + classReference.replaceAll("\\(","").replaceAll("\\)","");	
			output = box.getPlantUmlQuotedBoxName() + " : +" + property.getPathAsSparql() + " : " + property.getShClass().get().getModel().shortForm(property.getShClass().get().getURI());	
			
			if (property.getPlantUmlCardinalityString() != null) {
				output += " " + property.getPlantUmlCardinalityString() ;
			}
			if (property.getShPattern().isPresent() && this.displayPatterns) {
				output += " " + "(" + ModelRenderingUtils.render(property.getShPattern().get()) + ")" + " ";
			}
			if (property.getShNodeKind().isPresent() && !property.getShNodeKind().get().equals(SHACLM.IRI)) {
				output += " " + ModelRenderingUtils.render(property.getShNodeKind().get()) + " " ;
			}

			output += " \n";		
			return output;			
		} else {
			// TODO : why is this computed here diferently than in renderAsNode ???
			String labelColor = "";
			String labelColorClose = "";
			if(property.getColorString() != null) {
				labelColor = "<color:"+property.getColorString()+">"+" ";
				labelColorClose = "</color>";
			}
	
			String option = "";
			if (property.getPlantUmlCardinalityString() != null) {
				option += "<U+00A0>" + property.getPlantUmlCardinalityString() + " ";
			}
			if (property.getShPattern().isPresent() && this.displayPatterns) {
				option += "(" + ModelRenderingUtils.render(property.getShPattern().get()) + ")" + " ";
			}

			if (!property.getShGroup().isPresent()) {
				collectData(
					// Key
					// box.getPlantUmlQuotedBoxName() + " -"+"-> \""+""+labelColor+ classReference + "\" : ",
					box.getPlantUmlQuotedBoxName(), labelColor, classReference,
					// data Value
					property.getPathAsSparql() + option
				);
			}
			
			return null;
		}
	}

	private String renderDefault(PlantUmlProperty property, PlantUmlBoxIfc box) {
		
		// TODO : why is this computed here diferently than in renderAsNode ???
		String labelColor = "";
		String labelColorClose = "";
		if(property.getColorString() != null) {
			labelColor = "<color:"+property.getColorString()+">"+" ";
			labelColorClose = "</color>";
		}
		
		
		String output = box.getPlantUmlQuotedBoxName() + " : "+""+labelColor+ property.getPathAsSparql() + " ";
		
		// if  sh:or value is of kind of datatype , for each property concat with or word .. eg. xsd:string or rdf:langString
		String shOr_Datatype = "";
		if(property.getShOrShDatatype() != null) {
			shOr_Datatype += property.getShOrShDatatype().stream().map(r -> ModelRenderingUtils.render(r)).collect(Collectors.joining(" or "));
		}
		if(property.getShOrShNodeKind() != null) {
			shOr_Datatype += property.getShOrShNodeKind().stream().map(r -> ModelRenderingUtils.render(r)).collect(Collectors.joining(" or "));
		}
		
		if (property.getShDatatype().isPresent()) {
			output += " : " + ModelRenderingUtils.render(property.getShDatatype().get()) + " ";
		} else if (property.getShDatatype().isEmpty() && !shOr_Datatype.equals("")) {
			output += " : " +shOr_Datatype+ " ";
		}
		
		
		
		if (property.getPlantUmlCardinalityString() != null) {
			output += " " + property.getPlantUmlCardinalityString() + " ";
		}
		if (property.getShPattern().isPresent() && this.displayPatterns) {
			output += "{field}" + " " + "(" + ModelRenderingUtils.render(property.getShPattern().get()) + ")" + " ";
		}
		if (property.isUniqueLang()) {
			output += "uniqueLang" + " ";
		}
		if (property.getShHasValue().isPresent()) {
			output += " = " + ModelRenderingUtils.render(property.getShHasValue().get()) + " ";
		}
		
		
		if (
			 (!property.getShHasValue().isPresent())
			 &&
			 (!property.getShClass().isPresent())
			 &&
			 (!property.getShDatatype().isPresent())
			 &&
			 (!property.getShNode().isPresent())
			 &&					
			 (property.getPlantUmlCardinalityString() == null)
			 &&
			 (property.getShNodeKind().isPresent()) 
			) {
			String nameNodeKind = ModelRenderingUtils.render(property.getShNodeKind().get()); 
			output += " :  "+nameNodeKind.split(":")[1];
		}
				
		output += labelColorClose+" \n";
		
		return output;
	}
	
	public String renderDiagram(PlantUmlDiagram diagram) {
		this.diagram = diagram;
		
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
		
		
		if (this.renderSectionDiagram) {
			// sourceuml.append("left to right direction\n");
		}
		
		// retrieve all package declaration
		for (PlantUmlBoxIfc plantUmlBox : diagram.getBoxes()) {
			sourceuml.append(this.renderNodeShape(plantUmlBox,this.avoidArrowsToEmptyBoxes));
		}
		
		sourceuml.append("hide circle\n");
		sourceuml.append("hide methods\n");
		sourceuml.append("hide empty members\n");
		
		if (this.hideProperties) {
			sourceuml.append("hide fields\n");
		}
		
		// we don't set remove @unlinked if the diagram contains a single box otherwise
		// PlantUML crashes
		if (diagram.usesShGroup(diagram.boxes) && diagram.boxes.size() > 1) {
			sourceuml.append("remove @unlinked\n");
		}
		
		sourceuml.append("@enduml\n");
		
		// return output
		return sourceuml.toString();
	}

	public String renderNodeShape(PlantUmlBoxIfc box, boolean avoidArrowsToEmptyBoxes) {
		// String declaration = "Class"+"
		// "+"\""+box.getNameshape()+"\""+((box.getNametargetclass() != null)?"
		// "+"<"+box.getNametargetclass()+">":"");
		String declaration = "";

		String colorBackGround = "";
		if (box.getBackgroundColorString() != null ) {			
			colorBackGround = "#back:"+box.getBackgroundColorString();						
		}else {
			colorBackGround = "";
		}
		
		String labelColorClass = "";
		if(box.getColorString() != null) {
			if(!colorBackGround.equals("")) {
				labelColorClass += ";";
			} else {
				labelColorClass += "#";
			}
			labelColorClass += "text:"+box.getColorString();
		}
		
		// resolve subclasses only if we were asked for it
		List<PlantUmlBoxIfc> superClassesBoxes = new ArrayList<>();
		if(this.includeSubclassLinks) {
			superClassesBoxes = box.getRdfsSubClassOf().stream().map(sc -> this.diagram.findBoxByResource(sc)).filter(b -> b != null).collect(Collectors.toList());
			superClassesBoxes.addAll(
				box.getShNode().stream().map(sc -> this.diagram.findBoxByResource(sc)).filter(b -> b != null).collect(Collectors.toList())
			);
		}
		
		Map<String,String> collectGroupProperties = new HashMap<>();
		// declare the class if it has properties or super classes or a color
		if (
				(box.getProperties().size() > 0 || superClassesBoxes.size() > 0)
				||
				(box.getProperties().size() == 0 && box.getRdfsSubClassOf().size() == 0 && box.getDepiction().size() == 0 )
				||
				box.getBackgroundColorString() != null
				||
				box.getColorString() != null 			
		) {
			if (box.getNodeShape().isAnon()) {
				// give it an empty label
				declaration = "Class" + " " + box.getLabel() +" as " +"\""+" \"";
			} else {
				declaration = "Class" + " " + "\"" + box.getLabel() + "\"";
			}

			declaration += (this.generateAnchorHyperlink) ? " [["+box.getLink()+"]]" : "";
			declaration += " " + colorBackGround+labelColorClass + "\n";
			
			if (superClassesBoxes != null) {
				for (PlantUmlBoxIfc aSuperClass : superClassesBoxes) {
					// generate an "up" arrow - bolder and gray to distinguish it from other arrows
					declaration += "\""+box.getLabel()+"\"" + " -up[#gray,bold]-|> " + "\""+aSuperClass.getLabel()+"\"" + "\n";
				}
			}
			
			String propertiesDeclaration = "";
			for (int i=0;i<box.getProperties().size();i++) {
				PlantUmlProperty plantUmlproperty = box.getProperties().get(i);

				boolean displayAsDatatypeProperty = false;
				
				// if we want to avoid arrows to empty boxes...
				// note that this behavior is triggered only if the diagram has a certain size
				if (avoidArrowsToEmptyBoxes && diagram.getBoxes().size() > 8) {
					// then see if there is a reference to a box
					String arrowReference = diagram.resolvePropertyShapeShNodeOrShClass(plantUmlproperty);
					PlantUmlBoxIfc boxReference = diagram.findBoxById(arrowReference);
					
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
											superClassesBoxes.size() == 0
									)
							)							
					) {
						// count number of times it is used
						int nCount = 0;
						for (PlantUmlBoxIfc plantumlbox : diagram.getBoxes()) {
							nCount += plantumlbox.countShNodeOrShClassReferencesTo(arrowReference, diagram);
						}
						
						// if used more than once, then display the box, otherwise don't display it
						if (nCount <= 1) {
							displayAsDatatypeProperty = true;
						}
					}
				}
				
				String codePropertyPlantUml = this.renderProperty(
						plantUmlproperty, 
						box,
						displayAsDatatypeProperty
				);
				
				
				// if the property a une sh:Group, remove the contain in the property and generate a new property
				if (plantUmlproperty.getShGroup().isPresent()) {
					
					// read property group
					String notationName = "";
					
					// label of the group, to be used as the label of the separator line
					for (Statement r : plantUmlproperty.getShGroup().get().listProperties().toList()) {
						Resource pr = r.getPredicate();						
						if (pr.equals(RDFS.label)) {
							String ob = r.getObject().asLiteral().getLexicalForm();
							notationName = ob;
						}
					}
					
					String GroupId = box.getPlantUmlQuotedBoxName() + " : "+ "__"+notationName+"__\n";
					
					String codePropertyPlantUmlGroup = this.renderProperty(
							plantUmlproperty, 
							box,
							// force rendering as a datatype property
							true
					);
					
					// store the property inside the property group
					if (collectGroupProperties.get(GroupId) == null) {
						collectGroupProperties.put(GroupId,codePropertyPlantUmlGroup);
					} else {
						collectGroupProperties.computeIfPresent(GroupId, (k,v) -> v.concat(codePropertyPlantUmlGroup));
					}					
					
				} else if (codePropertyPlantUml != null) {
					propertiesDeclaration += codePropertyPlantUml; 					
				}
			}
			
			// now print all the arrows that were gathered during the processing
			// with a different direction each time
			if (nodeShapeArrows.size() > 0) {
				for (Map.Entry<NodeShapeArrowKey, String> entry : nodeShapeArrows.entrySet()) {
					String output = entry.getKey().box + " -"+getDirectionStarDiagram()+"-> \""+entry.getKey().color+ entry.getKey().reference + "\" : "+entry.getValue()+" \n";
					propertiesDeclaration +=  output;					
				}
			}
			
			declaration += propertiesDeclaration;			
		}
		
		// print all property groups that were gathered
		if (collectGroupProperties.size() > 0) {
			for (Map.Entry<String, String> entry : collectGroupProperties.entrySet()) {				
				declaration += entry.getKey() + entry.getValue()+"\n";				
			}
		}

		// node shape done, reset the arrow direction counter
		this.currentArrowDirectionIndex = 0;
		// also reset grouped arrows
		this.nodeShapeArrows = new HashMap<>();
		
		return declaration;
	}
	
	public String resolveShClassReference(Resource shClassReference) {
		PlantUmlBoxIfc b = this.diagram.findBoxByTargetClass(shClassReference);
		if(b != null) {
			return b.getLabel();
		} else {
			if (shClassReference.isURIResource()) { 
				return shClassReference.getModel().shortForm(shClassReference.getURI());
			} else {
				log.warn("Found a blank sh:class reference on a shape with sh:path "+shClassReference+", cannot handle it");
				return shClassReference.toString();
			}
		}
	}
	
	public String resolveShNodeReference(Resource shNodeReference) {
		PlantUmlBoxIfc b = this.diagram.findBoxByResource(shNodeReference);
		if(b != null) {
			return b.getLabel();
		} else {
			return ModelRenderingUtils.render(shNodeReference, true);
		}
	}
	
	/*
	 * function for Merge arrows what point to same class.
	 * codeKey - property key
	 * dataValue - data values or additional values
	 * collectRelationProperties - save all properties and if the property is included in the map, generate a list of values.
	 */
	public void collectData(String box, String color, String reference,String dataValue) {
		
		NodeShapeArrowKey key = new NodeShapeArrowKey(box, color, reference);
		if (!this.nodeShapeArrows.containsKey(key)) {
			this.nodeShapeArrows.put(
					// key
					key,
					// Values
					dataValue
			);
		} else {
			/* Merge for each input data values in the same property */
			this.nodeShapeArrows.computeIfPresent(key, (k,v) -> v+" \\l"+dataValue);
		}
		
	}	

	public String getDirectionStarDiagram() {
		// not a section diagram, no direction at all
		if(!this.renderSectionDiagram) {
			return "";
		}

		String direction = "";
		switch (this.currentArrowDirectionIndex%3) {
		// case 0:
		// 	direction = "u";
		// 	break;
		case 0:
			direction = "u";
			break;
		case 1:
			direction = "d";
			break;
		case 2:
			direction = "r";
			break;
		default:
			break;
		}

		// increment direction counter so that next time we get a different direction
		this.currentArrowDirectionIndex++;
		
		return direction;
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

	public boolean isIncludeSubclassLinks() {
		return includeSubclassLinks;
	}

	public void setIncludeSubclassLinks(boolean includeSubclassLinks) {
		this.includeSubclassLinks = includeSubclassLinks;
	}

	public boolean isHideProperties() {
		return hideProperties;
	}

	public void setHideProperties(boolean hideProperties) {
		this.hideProperties = hideProperties;
	}

	public boolean isRenderSectionDiagram() {
		return renderSectionDiagram;
	}

	public void setRenderSectionDiagram(boolean renderSectionDiagram) {
		this.renderSectionDiagram = renderSectionDiagram;
	}

}
