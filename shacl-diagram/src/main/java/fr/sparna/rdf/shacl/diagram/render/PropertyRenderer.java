package fr.sparna.rdf.shacl.diagram.render;

import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.shacl.diagram.model.PlantUmlBoxIfc;
import fr.sparna.rdf.shacl.diagram.model.PlantUmlDiagram;
import fr.sparna.rdf.shacl.diagram.model.PlantUmlProperty;

public class PropertyRenderer {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    protected boolean displayPatterns = false;
    protected BoxRenderer boxRenderer;
	protected boolean useLabels = true;

	// the current diagram being rendered	
	protected transient PlantUmlDiagram diagram;

    public PropertyRenderer(
        boolean displayPatterns,
		boolean useLabels,
        BoxRenderer boxRenderer,
        PlantUmlDiagram diagram
    ) {
        this.displayPatterns = displayPatterns;
		this.useLabels = useLabels;
        this.boxRenderer = boxRenderer;
        this.diagram = diagram;
    }

    public String renderProperty(
        PlantUmlProperty property,
        PlantUmlBoxIfc box,
        boolean renderAsDatatypeProperty
	) {
		
		// get the color for the arrow drawn
		String color = "";
		if(property.getDisplayColor() != null) {
			color = "[bold,#"+property.getDisplayColor()+"]";
		}
				
		if (property.getShNode().isPresent()) {
			return renderAsNodeReference(property, box, renderAsDatatypeProperty, color);
		} else if (property.getShClass().isPresent()) {
			return renderAsClassReference(property, box, renderAsDatatypeProperty); 
		} else if (property.getQualifiedValueShape().isPresent() && ( property.getQualifiedValueShape().get().getShNode().isPresent() || property.getQualifiedValueShape().get().getShClass().isPresent() ) ) {
			return renderAsQualifiedShapeReference(property, box, color); 
		} else if (property.getQualifiedValueShape().isPresent() && property.getQualifiedValueShape().get().getShDatatype().isPresent()) {
			return renderAsQualifiedShapeDatatypeProperty(property, box, color); 
		} else if (property.hasShOrShClassOrShNode()) {
			return renderAsOr(property, box,color);
		} else {
			return renderDefault(property, box);
		}
	}

	// uml_shape+ " --> " +"\""+uml_node+"\""+" : "+uml_path+uml_datatype+"
	// "+uml_literal+" "+uml_pattern+" "+uml_nodekind(uml_nodekind)+"\n";
	private String renderAsNodeReference(PlantUmlProperty property, PlantUmlBoxIfc box, Boolean renderAsDatatypeProperty, String colorArrow) {
		String nodeReference = this.diagram.resolveShNodeReference(property.getShNode().get(), this.boxRenderer.getLang());	
		String cardinality = (property.getPlantUmlCardinalityString() == null)? "[0..*]" : property.getPlantUmlCardinalityString();

		if (renderAsDatatypeProperty) {	
			String output = null;			
			output = this.boxRenderer.renderQuotedBoxLabel(box) + " : +" + this.renderPropertyLabel(property) + " : " + nodeReference;	

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
		} else {
			String option = "";
			if (cardinality != null) {
				option += "<U+00A0>" + cardinality + " ";
			}
			if (property.getShPattern().isPresent() && this.displayPatterns) {
				option += "(" + ModelRenderingUtils.render(property.getShPattern().get()) + ")" + " ";
			}
			
			// function to merge all arrows that point to the same class
			if (!property.getShGroup().isPresent()) {
				this.boxRenderer.notifyArrow(
					// key : same box, same color, same target
					this.boxRenderer.renderQuotedBoxLabel(box), colorArrow, nodeReference,
					// Values
					this.renderPropertyLabel(property) + option
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
		String localName = property.getResource().getLocalName();
		if (localName == null) {
		    localName = property.getResource().getId().getLabelString();
		}
		
		
		String sNameDiamond = "diamond_" + nodeshapeId.replace("-", "_") + "_" + localName.replace("-", "_");
		
		// diamond declaration
		String output = "<> " + sNameDiamond + "\n";

		// link between box and diamond
		// Thomas : empirical : OR arrows look much better when they don't have direction (they will be pointing downwards)
		output += this.boxRenderer.renderQuotedBoxLabel(box) + " -"+colorArrow+"-> \"" + sNameDiamond + "\" : " + this.renderPropertyLabel(property);

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
				output += sNameDiamond + " .. " + " \""+diagram.resolveShClassReference(shOrShClass, this.boxRenderer.getLang())+ "\"" + "\n";
			}			
		}
		if(property.getShOrShNode() != null) {
			for (Resource shOrShNode : property.getShOrShNode() ) {
				output += sNameDiamond + " .. " + " \""+this.diagram.resolveShNodeReference(shOrShNode, this.boxRenderer.getLang())+ "\"" + "\n";
			}
			
		}
		
		return output;
	}

	// value = uml_shape+ " --> " +"\""+uml_qualifiedvalueshape+"\""+" :
	// "+uml_path+uml_datatype+" "+uml_qualifiedMinMaxCount+"\n";
	private String renderAsQualifiedShapeReference(PlantUmlProperty property, PlantUmlBoxIfc box, String colorArrow) {
		String qualifiedValueShapeReference = this.diagram.resolveShNodeReference(property.getShQualifiedValueShape().get(), this.boxRenderer.getLang());	
		
		String option = (property.getPlantUmlQualifiedCardinalityString() != null)?" " + property.getPlantUmlQualifiedCardinalityString() + " ":"";

		if (!property.getShGroup().isPresent()) {
			this.boxRenderer.notifyArrow(
					//codeKey
					this.boxRenderer.renderQuotedBoxLabel(box), colorArrow, qualifiedValueShapeReference,
					//data value
					this.renderPropertyLabel(property) + option
			);
		}
		return null;
	}

	private String renderAsQualifiedShapeDatatypeProperty(PlantUmlProperty property, PlantUmlBoxIfc box, String colorArrow) {

		NodeShape qualifiedValueShape = property.getQualifiedValueShape().get();		
		String output = this.boxRenderer.renderQuotedBoxLabel(box) + " : " + this.renderPropertyLabel(property) + " ";
		
		if (qualifiedValueShape.getShDatatype().isPresent()) {
			output += " : " + ModelRenderingUtils.render(qualifiedValueShape.getShDatatype().get()) + " ";
		}		
		
		
		if (property.getPlantUmlQualifiedCardinalityString() != null) {
			output += " " + property.getPlantUmlCardinalityString() + " ";
		}
		if (qualifiedValueShape.getShPattern().isPresent() && this.displayPatterns) {
			output += "{field}" + " " + "(" + ModelRenderingUtils.render(property.getShPattern().get()) + ")" + " ";
		}
		
		return output+" \n";
	}

	// value = uml_shape+" --> "+"\""+uml_class_property+"\""+" :
	// "+uml_path+uml_literal+" "+uml_pattern+" "+uml_nodekind+"\n";
	private String renderAsClassReference(PlantUmlProperty property, PlantUmlBoxIfc box, boolean renderAsDatatypeProperty) {
		String classReference = diagram.resolveShClassReference(property.getShClass().get(), this.boxRenderer.getLang());
		if(classReference == null) {
			log.warn("Resolved to a null sh:class reference ("+property.getShClass().get()+") on property "+this.renderPropertyLabel(property));
		}
		
		if (renderAsDatatypeProperty) {
			String output = "";

			// output = box.getPlantUmlQuotedBoxName() + " : +" + property.getPathAsSparql() + " : " + classReference.replaceAll("\\(","").replaceAll("\\)","");	
			output = this.boxRenderer.renderQuotedBoxLabel(box) + " : +" + this.renderPropertyLabel(property) + " : " + property.getShClass().get().getModel().shortForm(property.getShClass().get().getURI());	
			
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
			String cardinality = (property.getPlantUmlCardinalityString() == null)? "[0..*]" : property.getPlantUmlCardinalityString();

			if(property.getDisplayColor() != null) {
				labelColor = "<color:"+property.getDisplayColor()+">"+" ";
				labelColorClose = "</color>";
			}
	
			String option = "";
			if (cardinality != null) {
				option += "<U+00A0>" + cardinality + " ";
			}
			if (property.getShPattern().isPresent() && this.displayPatterns) {
				option += "(" + ModelRenderingUtils.render(property.getShPattern().get()) + ")" + " ";
			}

			if (!property.getShGroup().isPresent()) {
				this.boxRenderer.notifyArrow(
					// Key
					// box.getPlantUmlQuotedBoxName() + " -"+"-> \""+""+labelColor+ classReference + "\" : ",
					this.boxRenderer.renderQuotedBoxLabel(box), labelColor, classReference,
					// data Value
					this.renderPropertyLabel(property) + option
				);
			}
			
			return null;
		}
	}

	private String renderDefault(PlantUmlProperty property, PlantUmlBoxIfc box) {
		
		// TODO : why is this computed here diferently than in renderAsNode ???
		String labelColor = "";
		String labelColorClose = "";
		if(property.getDisplayColor() != null) {
			labelColor = "<color:"+property.getDisplayColor()+">"+" ";
			labelColorClose = "</color>";
		}
		
		
		String output = this.boxRenderer.renderQuotedBoxLabel(box) + " : " + labelColor + this.renderPropertyLabel(property) + " ";
		
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

	private String renderPropertyLabel(PlantUmlProperty property) {
		String label = null;
		// first try with labels if we want to
		if(this.useLabels) {
			label = property.getPlantUmlLabel(this.boxRenderer.getLang());
		}
		return label != null && !label.trim().isEmpty() ? label : property.getPathAsSparql();
	}
}
