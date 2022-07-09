package fr.sparna.rdf.shacl.doc.read;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
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
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentationBuilder;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import net.sourceforge.plantuml.core.Diagram;

public class ShapesDocumentationModelReader implements ShapesDocumentationReaderIfc {

	protected boolean readDiagram = true;
	protected String imgLogo = null;

	public ShapesDocumentationModelReader(boolean readDiagram,String imgLogo) {
		super();
		this.readDiagram = readDiagram;
		this.imgLogo = imgLogo;
	}

	@Override
	public ShapesDocumentation readShapesDocumentation(
			Model shaclGraph,
			Model owlGraph,
			String lang,
			String fileName,
			boolean avoidArrowsToEmptyBoxes
	) {

		List<Resource> nodeShapes = shaclGraph.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		// 1. Lire toutes les classes
		ArrayList<ShaclBox> allNodeShapes = new ArrayList<>();
		ShaclBoxReader reader = new ShaclBoxReader(lang);
		for (Resource nodeShape : nodeShapes) {
			ShaclBox dbShacl = reader.read(nodeShape);
			allNodeShapes.add(dbShacl);
		}

		// sort node shapes
		allNodeShapes.sort((ShaclBox ns1, ShaclBox ns2) -> {
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
					if (ns1.getRdfsLabel() != null) {
						if (ns2.getRdfsLabel() != null) {
							return ns1.getRdfsLabel().compareTo(ns2.getRdfsLabel());
						} else {
							return -1;
						}
					} else {
						if (ns2.getRdfsLabel() != null) {
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
		for (ShaclBox aBox : allNodeShapes) {
			aBox.setProperties(reader.readProperties(aBox.getNodeShape(), allNodeShapes));
		}
		
		// Option pour créer le diagramme
		String sImgDiagramme = null;
		String plantUmlSourceDiagram = null;
		String plantUmlSourceCode = null;
		String fileNameGenerationpng = null;
		String UrlDiagram = null;
		
		if (this.readDiagram) {
			SVGGenerator gImgSvg = new SVGGenerator();
			PlantUmlSourceGenerator sourceGenerator = new PlantUmlSourceGenerator();
			try {
				sImgDiagramme = gImgSvg.generateSvgDiagram(shaclGraph, owlGraph,avoidArrowsToEmptyBoxes);
				plantUmlSourceDiagram = sourceGenerator.generatePlantUmlDiagram(shaclGraph, owlGraph,false,true,avoidArrowsToEmptyBoxes);
				// Read source Uml
				plantUmlSourceCode = sourceGenerator.generatePlantUmlDiagram(shaclGraph, owlGraph,false,false,avoidArrowsToEmptyBoxes);
				// if source uml is true generate png file
				if(!plantUmlSourceCode.isEmpty()) {	
					// Write the first image to "png"
					Transcoder t = TranscoderUtil.getDefaultTranscoder();
					String url = t.encode(plantUmlSourceCode);
					UrlDiagram = "http://www.plantuml.com/plantuml/png/"+url;
				}
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
		String sOWLDescDocument = null;
		
		String datecreated = null;
		String dateissued = null;
		String yearCopyRighted = null;
		String license = null;
		String creator = null;
		String publisher = null;
		String rightsHolder = null;		
		
		for (Resource rOntology : sOWL) {
			sOWLlabel = ReadValue.readValueconstraint(rOntology, RDFS.label, lang);
			sOWLComment = ReadValue.readValueconstraint(rOntology, RDFS.comment, lang);
			sOWLVersionInfo = ReadValue.readValueconstraint(rOntology,OWL.versionInfo, null);		
			sOWLDateModified = ReadValue.readValueconstraint(rOntology,DCTerms.modified, null);
			// Read Description for the document title
			sOWLDescDocument = ReadValue.readValueconstraint(rOntology,DCTerms.description, lang);
			
			datecreated = ReadValue.readValueconstraint(rOntology,DCTerms.created, lang);
			dateissued = ReadValue.readValueconstraint(rOntology,DCTerms.issued, lang);
			yearCopyRighted = ReadValue.readValueconstraint(rOntology,DCTerms.dateCopyrighted, lang);;
			license = ReadValue.readValueconstraint(rOntology,DCTerms.license, null);
			creator = ReadValue.readValueconstraint(rOntology,DCTerms.creator, null);
			publisher = ReadValue.readValueconstraint(rOntology,DCTerms.publisher, null);
			rightsHolder = ReadValue.readValueconstraint(rOntology,DCTerms.rightsHolder, null);
		}
		
		List<Resource> rRDFLabels = shaclGraph.listResourcesWithProperty(RDFS.label).toList();
		for(Resource sinfoLabel : rRDFLabels) {
			String label = ReadValue.readValueconstraint(sinfoLabel,RDFS.label, lang);
			if(license != null && license.equals(sinfoLabel.getURI().toString())) {
				license = label;				
			}
			if(creator != null && creator.equals(sinfoLabel.getURI().toString())) {
				creator = label;
			}
			if(publisher != null && publisher.equals(sinfoLabel.getURI().toString())) {
				publisher = label;
			}
			if(rightsHolder != null && rightsHolder.equals(sinfoLabel.getURI().toString())) {
				rightsHolder = label;
			}
		}
		
		// Code XML
		ShapesDocumentation shapesDocumentation = new ShapesDocumentation();
		shapesDocumentation.setTitle(sOWLlabel);
		shapesDocumentation.setImgLogo(this.imgLogo);
		shapesDocumentation.setComment(sOWLComment);
		shapesDocumentation.setDatecreated(datecreated);
		shapesDocumentation.setDateissued(dateissued);
		shapesDocumentation.setYearCopyRighted(yearCopyRighted);
		shapesDocumentation.setModifiedDate(sOWLDateModified);
		shapesDocumentation.setVersionInfo(sOWLVersionInfo);
		shapesDocumentation.setLicense(license);
		shapesDocumentation.setCreator(creator);
		shapesDocumentation.setPublisher(publisher);
		shapesDocumentation.setRightsHolder(rightsHolder);
		shapesDocumentation.setSvgDiagram(sImgDiagramme);
		shapesDocumentation.setPlantumlSource(plantUmlSourceDiagram);
		shapesDocumentation.setPngDiagram(UrlDiagram);
		shapesDocumentation.setDescriptionDocument(sOWLDescDocument);

		// 3. Lire les prefixes
		HashSet<String> gatheredPrefixes = new HashSet<>();
		for (ShaclBox aBox : allNodeShapes) {
			List<String> prefixes = reader.readPrefixes(aBox.getNodeShape());
			gatheredPrefixes.addAll(prefixes);
		}
		Map<String, String> necessaryPrefixes = ShaclPrefixReader.gatherNecessaryPrefixes(shaclGraph.getNsPrefixMap(), gatheredPrefixes);
		List<NamespaceSection> namespaceSections = NamespaceSection.fromMap(necessaryPrefixes);
		List<NamespaceSection> sortNameSpacesectionPrefix = namespaceSections.stream().sorted((s1, s2) -> {
			if(s1.getprefix() != null ) {
				if(s2.getprefix() != null) {
					return s1.getprefix().toString().compareTo(s2.getprefix().toString());
				}else {
					return -1;
				}
			}else {
				if(s2.getprefix() != null) {
					return 1;
				} else {
					return s1.getprefix().compareTo(s2.getprefix());
				}
			}	
		}).collect(Collectors.toList());
		shapesDocumentation.setPrefixe(sortNameSpacesectionPrefix);
		
		
		List<ShapesDocumentationSection> sections = new ArrayList<>();
		// For each NodeShape ...
		for (ShaclBox nodeShape : allNodeShapes) {
			// if (datanodeshape.getNametargetclass() != null) {
			    
			ShapesDocumentationSection currentSection = new ShapesDocumentationSection();
			
			if(nodeShape.getRdfsLabel() == null) {
				currentSection.setTitle(nodeShape.getShortForm());
			}else {
				currentSection.setTitle(nodeShape.getRdfsLabel());
			}
			currentSection.setUri(nodeShape.getShortForm());
			currentSection.setDescription(nodeShape.getRdfsComment());
			if(nodeShape.getShTargetClass() != null) {
				currentSection.setTargetClassLabel(shaclGraph.shortForm(nodeShape.getShTargetClass().getURI()));
				currentSection.setTargetClassUri(nodeShape.getShTargetClass().getURI());
			}
			
			currentSection.setPattern(nodeShape.getShPattern());
			currentSection.setNodeKind(nodeShape.getShNodeKind());
			if(nodeShape.getShClosed()) {
				currentSection.setClosed(nodeShape.getShClosed());
			}
			//Get example data
			currentSection.setSkosExample(nodeShape.getSkosExample());
			
			
			// Read the property shape 

			List<PropertyShapeDocumentation> ListPropriete = new ArrayList<>();
			for (ShaclProperty propriete : nodeShape.getProperties()) {
				PropertyShapeDocumentation psd = PropertyShapeDocumentationBuilder.build(propriete, allNodeShapes, shaclGraph, owlGraph, lang);				
				ListPropriete.add(psd);
			}
			
			currentSection.setPropertySections(ListPropriete);
			sections.add(currentSection);
		}

		// }
		shapesDocumentation.setSections(sections);
		return shapesDocumentation;
	}
	


}
