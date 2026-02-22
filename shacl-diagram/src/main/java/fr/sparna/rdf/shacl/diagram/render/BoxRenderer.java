package fr.sparna.rdf.shacl.diagram.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import fr.sparna.rdf.shacl.diagram.model.PlantUmlBoxIfc;
import fr.sparna.rdf.shacl.diagram.model.PlantUmlDiagram;
import fr.sparna.rdf.shacl.diagram.model.PlantUmlProperty;

public class BoxRenderer {

	protected boolean includeSubclassLinks = true;
	protected boolean generateAnchorHyperlink = false;
	// true to indicate that the diagram is a section diagram
	protected boolean renderSectionDiagram = false;

	// the current diagram being rendered	
	protected transient PlantUmlDiagram diagram;

	// map to "merge" all properties that point to the same class into a single arrow
	protected transient Map<NodeShapeArrowKey, String> nodeShapeArrows = new HashMap<>();

	// current temporary arrow direction index to determine the u,d,r,l directions of the arrows
	protected transient int currentArrowDirectionIndex = 0;

	protected transient PropertyRenderer propertyRenderer;

	

	public BoxRenderer(
		boolean includeSubclassLinks,
		boolean generateAnchorHyperlink,
		boolean renderSectionDiagram,
		boolean displayPatterns,
		PlantUmlDiagram diagram
	) {
		this.includeSubclassLinks = includeSubclassLinks;
		this.generateAnchorHyperlink = generateAnchorHyperlink;
		this.renderSectionDiagram = renderSectionDiagram;
		this.diagram = diagram;

		this.propertyRenderer = new PropertyRenderer(
			displayPatterns,
			this
		);
	}

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
				
				String codePropertyPlantUml = this.propertyRenderer.renderProperty(
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
					
					String codePropertyPlantUmlGroup = this.propertyRenderer.renderProperty(
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
	

	/*
	 * function for Merge arrows what point to same class.
	 * codeKey - property key
	 * dataValue - data values or additional values
	 * collectRelationProperties - save all properties and if the property is included in the map, generate a list of values.
	 */
	public void notifyArrow(String box, String color, String reference,String pathAsSparqlWithOptions) {
		
		NodeShapeArrowKey key = new NodeShapeArrowKey(box, color, reference);
		if (!this.nodeShapeArrows.containsKey(key)) {
			this.nodeShapeArrows.put(
					// key
					key,
					// Values
					pathAsSparqlWithOptions
			);
		} else {
			/* Merge for each input data values in the same property */
			this.nodeShapeArrows.computeIfPresent(key, (k,v) -> v+" \\l"+pathAsSparqlWithOptions);
		}		
	}	

}
