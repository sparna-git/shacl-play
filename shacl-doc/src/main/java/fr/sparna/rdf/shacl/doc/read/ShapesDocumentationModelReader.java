package fr.sparna.rdf.shacl.doc.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.diagram.PlantUmlBox;
import fr.sparna.rdf.shacl.doc.ConstraintValueReader;
import fr.sparna.rdf.shacl.doc.PlantUmlSourceGenerator;
import fr.sparna.rdf.shacl.doc.SVGGenerator;
import fr.sparna.rdf.shacl.doc.ShaclBox;
import fr.sparna.rdf.shacl.doc.ShaclBoxReader;
import fr.sparna.rdf.shacl.doc.ShaclPrefixReader;
import fr.sparna.rdf.shacl.doc.ShaclProperty;
import fr.sparna.rdf.shacl.doc.model.NamespaceSection;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;

public class ShapesDocumentationModelReader implements ShapesDocumentationReaderIfc {

	protected boolean readDiagram = true;

	public ShapesDocumentationModelReader(boolean readDiagram) {
		super();
		this.readDiagram = readDiagram;
	}

	@Override
	public ShapesDocumentation readShapesDocumentation(
			Model shaclGraph,
			Model owlGraph,
			String lang,
			String fileName
	) {

		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		// 1. Lire toutes les classes
		ArrayList<ShaclBox> Shaclvalue = new ArrayList<>();
		ShaclBoxReader reader = new ShaclBoxReader(lang);
		for (Resource nodeShape : nodeShapes) {
			ShaclBox dbShacl = reader.read(nodeShape);
			Shaclvalue.add(dbShacl);
		}

		// sort node shapes
		Shaclvalue.sort((ShaclBox ns1, ShaclBox ns2) -> {
			if (ns1.getShOrder() != null) {
				if (ns2.getShOrder() != null) {
					return ns1.getShOrder() - ns2.getShOrder();
				} else {
					return -1;
				}
			} else {
				if (ns2.getShOrder() != null) {
					return 1;
				} else {
					// both sh:order are null, try with label
					if (ns1.getRdfslabel() != null) {
						if (ns2.getRdfslabel() != null) {
							return ns1.getRdfslabel().compareTo(ns2.getRdfslabel());
						} else {
							return -1;
						}
					} else {
						if (ns2.getRdfslabel() != null) {
							return 1;
						} else {
							// both sh:name are null, try with URI
							return ns1.getNodeShape().toString().compareTo(ns2.getNodeShape().toString());
						}
					}
				}
			}
		});

		// 2. Lire les propriétés
		for (ShaclBox aBox : Shaclvalue) {
			aBox.setShacl_value(reader.readProperties(aBox.getNodeShape(), Shaclvalue));
		}
		
		// Option pour créer le diagramme
		String sImgDiagramme = null;
		String plantUmlSourceDiagram = null;
		if (this.readDiagram) {
			SVGGenerator gImgSvg = new SVGGenerator();
			PlantUmlSourceGenerator sourceGenerator = new PlantUmlSourceGenerator();
			try {
				sImgDiagramme = gImgSvg.generateSvgDiagram(shaclGraph, owlGraph);
				plantUmlSourceDiagram = sourceGenerator.generatePlantUmlDiagram(shaclGraph, owlGraph);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		

		// Lecture de OWL
		ConstraintValueReader ReadValue = new ConstraintValueReader();
		List<Resource> sOWL = shaclGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		String sOWLlabel = null;
		String sOWLComment = null;
		String sOWLDateModified = null;
		String sOWLVersionInfo = null;
		for (Resource rOntology : sOWL) {
			sOWLlabel = ReadValue.readValueconstraint(rOntology, RDFS.label, lang);
			sOWLComment = ReadValue.readValueconstraint(rOntology, RDFS.comment, lang);
			sOWLVersionInfo = ReadValue.readValueconstraint(rOntology,OWL.versionInfo, null);		
			sOWLDateModified = ReadValue.readValueconstraint(rOntology,DCTerms.modified, null);
		}
		
		

		// Code XML
		ShapesDocumentation shapesDocumentation = new ShapesDocumentation();
		shapesDocumentation.setTitle(sOWLlabel);
		shapesDocumentation.setComment(sOWLComment);
		shapesDocumentation.setModifiedDate(sOWLDateModified);
		shapesDocumentation.setVersionInfo(sOWLVersionInfo);
		shapesDocumentation.setSvgDiagram(sImgDiagramme);
		shapesDocumentation.setPlantumlSource(plantUmlSourceDiagram);
		String pattern_node_nodeshape = null;

		// 3. Lire les prefixes
		HashSet<String> gatheredPrefixes = new HashSet<>();
		for (ShaclBox aBox : Shaclvalue) {
			List<String> prefixes = reader.readPrefixes(aBox.getNodeShape());
			gatheredPrefixes.addAll(prefixes);
		}
		Map<String, String> necessaryPrefixes = ShaclPrefixReader.gatherNecessaryPrefixes(shaclGraph.getNsPrefixMap(), gatheredPrefixes);
		List<NamespaceSection> namespaceSections = NamespaceSection.fromMap(necessaryPrefixes);		
		shapesDocumentation.setPrefixe(namespaceSections);
		
		
		List<ShapesDocumentationSection> sections = new ArrayList<>();
		// For each NodeShape ...
		for (ShaclBox datanodeshape : Shaclvalue) {
			// if (datanodeshape.getNametargetclass() != null) {
			    
			ShapesDocumentationSection currentSection = new ShapesDocumentationSection();
			
			if(datanodeshape.getRdfslabel() == null) {
				currentSection.setTitle(datanodeshape.getShortForm());
			}else {
				currentSection.setTitle(datanodeshape.getRdfslabel());
			}
			currentSection.setUri(datanodeshape.getLocalName());
			currentSection.setDescription(datanodeshape.getRdfsComment());
			currentSection.setTargetClassLabel(datanodeshape.getNametargetclass());
			if(datanodeshape.getNametargetclass() != null) {
				for(NamespaceSection sPrefix : namespaceSections) {
					if(sPrefix.getprefix().equals(datanodeshape.getNametargetclass().split(":")[0])) {
						currentSection.setTargetClassUri(sPrefix.getnamespace()+datanodeshape.getNametargetclass().split(":")[1]);
						break;
					}
				}
			}
			currentSection.setPattern(datanodeshape.getShpatternNodeShape());
			currentSection.setNodeKind(datanodeshape.getShnodeKind());
			if(datanodeshape.getShClose()) {
				currentSection.setClosed(datanodeshape.getShClose());
			}
			
			
			// Information de l'Ontology

			List<PropertyShapeDocumentation> ListPropriete = new ArrayList<>();
			for (ShaclProperty propriete : datanodeshape.getShacl_value()) {
				// Récuperation du pattern si le node est une NodeShape
				if (propriete.getNode() != null) {
					for (ShaclBox pattern_other_nodeshape : Shaclvalue) {
						if (propriete.getNode().contains(pattern_other_nodeshape.getLocalName())) {
							pattern_node_nodeshape = pattern_other_nodeshape.getShpatternNodeShape();
							break;
						}
					}
				}
				//
				PropertyShapeDocumentation proprieteDoc = new PropertyShapeDocumentation();
				//
				proprieteDoc.setLabel(propriete.getName(), null);
				
				if(propriete.getNode() != null) {
					for(ShaclBox aName : Shaclvalue) {
						if(aName.getShortForm().contains(propriete.getNode())) {
							proprieteDoc.setLinkNodeShapeUri(aName.getLocalName());
							if(aName.getRdfslabel() == null) {
								proprieteDoc.setLinkNodeShape(aName.getShortForm());
							}else {
								proprieteDoc.setLinkNodeShape(aName.getRdfslabel());
							}							
						}
					}
				}
				
				proprieteDoc.setShortForm(propriete.getPath());
				// Identifier le lien avec une node vers la Propriete
				if(proprieteDoc.getShortForm()!=null) {
					if(proprieteDoc.getShortForm().contains(":")) {
						for(NamespaceSection sPrefix : namespaceSections) {
							if(sPrefix.getprefix().equals(proprieteDoc.getShortForm().split(":")[0])) {
								proprieteDoc.setShortFormUri(sPrefix.getnamespace()+proprieteDoc.getShortForm().split(":")[1]);
							    break;
							}
						}
					}
				}
				
				
				proprieteDoc.setExpectedValueLabel(propriete.getClass_node(), propriete.getNode(),
						propriete.getClass_property(), propriete.getDatatype(), propriete.getNodeKind(),
						propriete.getPath());
				
				if(propriete.getClass_node() != null) {
					for(ShaclBox getNodeShape : Shaclvalue) {
						if(getNodeShape.getNametargetclass() != null) {
							if(getNodeShape.getNametargetclass().equals(propriete.getClass_node())) {
								proprieteDoc.setLinknameNodeShapeuri(getNodeShape.getLocalName());
								proprieteDoc.setLinknameNodeShape(getNodeShape.getRdfslabel());
								break;
							}
						}						
					}					
				}

				proprieteDoc.setExpectedValueAdditionnalInfoPattern(propriete.getPattern(), datanodeshape.getShpatternNodeShape(),
						pattern_node_nodeshape, propriete.getClass_node(), propriete.getNode(),
						propriete.getClass_property(), propriete.getDatatype(), propriete.getNodeKind(),
						propriete.getPath());
				proprieteDoc.setExpectedValueAdditionnalInfoIn(propriete.getShin());
				proprieteDoc.setExpectedValueAdditionnalInfoValue(propriete.getShValue());
				proprieteDoc.setCardinalite(propriete.getCardinality());
				proprieteDoc.setDescription(propriete.getDescription());				
				proprieteDoc.setOr(propriete.getShOr());
				ListPropriete.add(proprieteDoc);
			}
			
			currentSection.setPropertySections(ListPropriete);
			sections.add(currentSection);
		}

		// }
		shapesDocumentation.setSections(sections);
		return shapesDocumentation;
	}

}
