package fr.sparna.rdf.shacl.diagram.render;

import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.diagram.model.PlantUmlBoxIfc;
import fr.sparna.rdf.shacl.diagram.model.PlantUmlDiagram;
import fr.sparna.rdf.shacl.diagram.model.PlantUmlProperty;

public class PropertyRenderer {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    protected boolean displayPatterns = false;
    protected BoxRenderer boxRenderer;

	// the current diagram being rendered	
	protected transient PlantUmlDiagram diagram;

    public PropertyRenderer(
        boolean displayPatterns,
        BoxRenderer boxRenderer,
        PlantUmlDiagram diagram
    ) {
        this.displayPatterns = displayPatterns;
        this.boxRenderer = boxRenderer;
        this.diagram = diagram;
    }

    public String renderProperty(
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
				this.boxRenderer.notifyArrow(
					// key : same box, same color, same target
					box.getPlantUmlQuotedBoxName(), colorArrow, nodeReference,
					// Values
					property.getPathAsSparql() + option
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
			this.boxRenderer.notifyArrow(
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
				this.boxRenderer.notifyArrow(
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
    
}
