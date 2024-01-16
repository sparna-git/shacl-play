package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.SHACL_PLAY;


public class PlantUmlPropertyReader {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected List<PlantUmlBox> allBoxes;

	public PlantUmlPropertyReader(List<PlantUmlBox> allBoxes) {
		super();
		this.allBoxes = allBoxes;
	}
	
	// Principal
	public PlantUmlProperty readPlantUmlProperty(Resource constraint, Model owlGraph) {
		
		PlantUmlProperty p = new PlantUmlProperty(constraint);
		
		p.setValue_path(this.readShPath(constraint));
		p.setValue_datatype(this.readShDatatype(constraint));
		p.setValue_nodeKind(this.readShNodeKind(constraint));
		p.setValue_cardinality(this.readShMinCountMaxCount(constraint));		
		p.setValue_range(this.readShMinInclusiveMaxInclusive(constraint));		
		p.setValue_length(this.readShMinLengthMaxLength(constraint));		
		p.setValue_pattern(this.readShPattern(constraint));		
		p.setValue_language(this.readShLanguageIn(constraint));		
		p.setValue_uniquelang(this.readShUniqueLang(constraint));		
		p.setValue_node(this.readShNode(constraint));
		p.setValue_class_property(this.readShClass(constraint));
		p.setValue_order_shacl(this.readShOrder(constraint));
		p.setValue_hasValue(this.readShHasValue(constraint));
		p.setValue_qualifiedvalueshape(this.readShQualifiedValueShape(constraint));
		p.setValue_qualifiedMaxMinCount(this.readShQualifiedMinCountQualifiedMaxCount(constraint));
		p.setValue_inverseOf(this.readOwlInverseOf(owlGraph, p.getValue_path()));
		p.setValue_shor(this.readShOrConstraint(constraint));
		p.setValue_colorProperty(this.readColor(constraint));
		
		return p;
	}
	
	
	
	public String readColor(Resource nodeShape) {	
		String value = null;
		try {
			if(nodeShape.hasProperty(nodeShape.getModel().createProperty(SHACL_PLAY.COLOR))) {
				value= nodeShape.getProperty(nodeShape.getModel().createProperty(SHACL_PLAY.COLOR)).getLiteral().getString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}	
	
	public List<String> readOwlInverseOf(Model owlGraph, String path) {
		List<String> inverBox = new ArrayList<>();
		if(path != null) {
			// read everything typed as NodeShape
			List<Resource> pathOWL = owlGraph.listResourcesWithProperty(RDF.type, OWL.ObjectProperty).toList();
			for(Resource inverseOfResource : pathOWL) {
				if(inverseOfResource.getLocalName().equals(path)){
					if(inverseOfResource.hasProperty(OWL.inverseOf)) {
						inverBox.add(inverseOfResource.getProperty(OWL.inverseOf).getResource().asResource().getLocalName().toString());
					}
				}
			}				
		}		
		return inverBox;
	}
	
	public List<PlantUmlBox> readShOrConstraint (Resource constraint) {
		List<PlantUmlBox> orBoxes = new ArrayList<>();
		
		// 1. Lire la valeur de sh:or
		if (constraint.hasProperty(SH.or)) {
			Resource theOr = constraint.getProperty(SH.or).getResource();
			// now read all sh:node or sh:class inside
			List<RDFNode> rdfList = theOr.as( RDFList.class ).asJavaList();
			for (RDFNode node : rdfList) {
				if(node.canAs(Resource.class)) {
					Resource value = null;
					
					if (node.asResource().hasProperty(SH.node)) {
						value = node.asResource().getProperty(SH.node).getResource();
					} else if (node.asResource().hasProperty(SH.class_)) {
						value = node.asResource().getProperty(SH.class_).getResource();
					}
					
					if(value != null) {
						boolean flagNodeShape = false;
						String shortForm = value.getModel().shortForm(value.getURI());
						
						// 2. Trouver le PlantUmlBox qui a ce nom		
						for (PlantUmlBox plantUmlBox : allBoxes) {
							if(plantUmlBox.getLabel().equals(shortForm)) {
								orBoxes.add(plantUmlBox);
								break;
							}
						}
						
						// in case of sh:class, look for NodeShape with this class as targetClass
						String resolvedClassReference = this.resolveShClassReference(constraint.getModel(), value);
						// Statement, if shape exists in list of orBoxes, is not necessary save again
						flagNodeShape = orBoxes.stream().filter( v -> v.getLabel().equals(resolvedClassReference)).findFirst().isPresent(); 
						if (!flagNodeShape) {
							if(resolvedClassReference != null) {
								for (PlantUmlBox plantUmlBox : allBoxes) {
									if(plantUmlBox.getLabel().equals(constraint.getModel().shortForm(resolvedClassReference))) {
										orBoxes.add(plantUmlBox);
										break;
									}
								}
							}
						}	
					}
				}				
			}
		}
		
		return orBoxes;
	}
	
	public String readShPath(Resource constraint) {
		List<RDFNode> paths = ModelReadingUtils.readObjectAsResource(constraint, SH.path);
		Resource firstResource = paths.stream().filter(p -> p.isResource()).map(p -> p.asResource()).findFirst().orElse(null);
		// render the property path using prefixes
		return ModelRenderingUtils.renderSparqlPropertyPath(firstResource, true);
	}
		
	public String readShDatatype(Resource constraint) {
		return ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.datatype), true);
	}

	public String readShNodeKind(Resource constraint) {
		return ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.nodeKind), true);
	}
	
	//Cardinality Constraint Components
	

	public String readShMinCountMaxCount(Resource constraint) {
		String value_minCount = "0";
		String value_maxCount ="*";
		String uml_code =null;
		if (constraint.hasProperty(SH.minCount)){
			value_minCount = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.minCount), true);
		}
		if (constraint.hasProperty(SH.maxCount)) {
			value_maxCount = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.maxCount), true);
		}
		
		if ((constraint.hasProperty(SH.minCount)) || (constraint.hasProperty(SH.maxCount))){
			uml_code = "["+ value_minCount +".."+ value_maxCount +"]";
		} else {
			uml_code = null;
		}
		
		return uml_code;
	}
	
	//Value Range Constraint Components
	

	public String readShMinInclusiveMaxInclusive(Resource constraint) {
		
		boolean a1 = false;
		boolean a2 = false;
		boolean a3 = false;
		boolean a4 = false;
		String value_minIn ="";
		String value_maxIn = "";
		String value_minEx = "";
		String value_maxEx = "";
		String uml_range =null;
		
		if (constraint.hasProperty(SH.minInclusive)) {
			a1 = true;
			value_minIn = "{field} (range : ["+ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.minInclusive), true)+"-";			
		} 
		if (constraint.hasProperty(SH.maxInclusive)){
			a2 = true;
			value_maxIn = "-"+ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.maxInclusive), true)+"])";			
		} 
		if (constraint.hasProperty(SH.minExclusive)){
			a3 = true;
			value_minEx = "{field} (range : ]"+ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.minExclusive), true)+"-";			
		} 
		if(constraint.hasProperty(SH.maxExclusive)) {
			a4 = true;
			value_maxEx = "-"+ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.maxExclusive), true)+"[)";	
		}
		
		if ((a1) & (!a2)) {
			uml_range = value_minIn+"*[";					
		}
		else if ((a2) & (!a1)) {
			uml_range = "{field} (range : ]*"+value_maxIn;
		}
		else if ((a3) & (!a4)) {
			uml_range = value_minEx+"*[)";
		}
		else if ((a4) & (!a3)) {
			uml_range = "{field} (range : ]*"+value_maxEx;
		} else {uml_range = null;}
		
		return uml_range ;
	}
	
	
	//String-based Constraint Components
	
	public String readShMinLengthMaxLength(Resource constraint) {
		String value_maxLength = "";
		String value_minLength = "";
		String uml_code = null;
		
		if(constraint.hasProperty(SH.maxLength)){
			value_maxLength = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.maxLength), true);			
		}				
		if (constraint.hasProperty(SH.minLength)){
			value_minLength = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.minLength), true); 			
		}
		if ((constraint.hasProperty(SH.maxLength)) || (constraint.hasProperty(SH.minLength))){
			if(value_minLength=="") { value_minLength = "0"; }
			if(value_minLength=="") { value_minLength = "*"; }
			uml_code = "{field} (Length ["+value_minLength +".."+value_maxLength+"])";			
		}
		return uml_code;
	}
	

	public String readShPattern(Resource constraint) {
		return ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.pattern), true);
	}
	

	public String readShLanguageIn(Resource constraint) {
		String value = null;
		if (constraint.hasProperty(SH.languageIn)) {
			Resource list = constraint.getProperty(SH.languageIn).getList().asResource();		
		    RDFList rdfList = list.as(RDFList.class);
		    ExtendedIterator<RDFNode> items = rdfList.iterator();
		    value = "";
		    while ( items.hasNext() ) {
		    	RDFNode item = items.next();
		        value += item+" ,";
		    }
		    value.substring(0, (value.length()-1));
		}
		return value;
	}
	

	public String readShUniqueLang(Resource constraint) {
		String value = null;
		if (
				constraint.hasProperty(SH.uniqueLang)
				&&
				!ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.uniqueLang), true).equals("")
		) {			
			value = "uniqueLang";
		}
	    return value;
	}
	
	// Shape-based Constraint Components

	
	public PlantUmlBox readShNode(Resource constraint) {
			// 1. Lire la valeur de sh:node
			//Resource nodeValue = constraint.asResource().getProperty(SH.node).getResource();
			String nodeValue = null;
			nodeValue = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.node), true);		
					
		
			// 2. Trouver le PlantUmlBox qui a ce nom
			PlantUmlBox theBox = null;
			if (nodeValue != null) {
				for (PlantUmlBox plantUmlBox : allBoxes) {
					if(plantUmlBox.getLabel().toString().equals(nodeValue.toString())) {
						theBox = plantUmlBox;
						break;
					}					
				}
			}
			
			return theBox;
	}
	

	public String readShClass(Resource constraint) {
		if (constraint.hasProperty(SH.class_)) {
			Resource idclass = constraint.getProperty(SH.class_).getResource();			
			return resolveShClassReference(constraint.getModel(), idclass);
		}
		
		return null;
	}
	
	public String resolveShClassReference(Model model, Resource classUri) {
		// 1. search for NodeShapes with an sh:targetClass
		List<Resource> nodeShapes = model.listResourcesWithProperty(SH.targetClass,classUri).toList();
		for(Resource aNodeShape : nodeShapes) {
			for (PlantUmlBox plantUmlBox : allBoxes) {
				if(plantUmlBox.getLabel().equals(model.shortForm(aNodeShape.getURI()))) {
					return plantUmlBox.getQualifiedName();
				}
			}
		}
		
		// 2. Not found, could be that the nodeShape is type rdfs:class
		if(classUri.hasProperty(RDF.type, RDFS.Class) && classUri.hasProperty(RDF.type, SH.NodeShape)) {
			return model.shortForm(classUri.toString());	
		} 
		
		// 3. default
		if (classUri.isURIResource()) { 
			return model.shortForm(classUri.getURI());
		} else {
			log.warn("Found a blank sh:class reference on a shape with sh:path "+classUri+", cannot handle it");
			return null;
		}
	}
	
	
	public Double readShOrder(Resource constraint) {
		String v = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.order), true);
		
		return (v != null)?Double.parseDouble(v):null;		
	}
		
	public String readShHasValue(Resource constraint) {
		return ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.hasValue), true);
	}

	public PlantUmlBox readShQualifiedValueShape(Resource constraint) {
		
		// 1. Lire la valeur de sh:node
		String nodeValue = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.qualifiedValueShape), true);
		
		// 2. Trouver le PlantUmlBox qui a ce nom
		PlantUmlBox theBox = null;
		if (nodeValue != null) {
			for (PlantUmlBox plantUmlBox : allBoxes) {
				if(plantUmlBox.getLabel().equals(nodeValue)) {
					theBox = plantUmlBox;
					break;
				}
			}
		}
		
		return theBox;
	} 

	public String readShQualifiedMinCountQualifiedMaxCount(Resource constraint) {
		String value_minCount = "0";
		String value_maxCount ="*";
		String uml_code =null;
		
		if (constraint.hasProperty(SH.qualifiedMinCount)){
			value_minCount = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.qualifiedMinCount), true);
		}
		if (constraint.hasProperty(SH.qualifiedMaxCount)) {
			value_maxCount = ModelRenderingUtils.render(ModelReadingUtils.readObjectAsResourceOrLiteral(constraint, SH.qualifiedMaxCount), true);
		}
		
		if ((constraint.hasProperty(SH.qualifiedMinCount)) || (constraint.hasProperty(SH.qualifiedMaxCount))){
			uml_code = "["+ value_minCount +".."+ value_maxCount +"]";
		} else {
			uml_code = null;
		}
		
		return uml_code;	
	}
	
}
