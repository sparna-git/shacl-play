package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.NodeShapeReader;
import fr.sparna.rdf.shacl.doc.OwlOntology;
import fr.sparna.rdf.shacl.doc.PlantUmlSourceGenerator;
import fr.sparna.rdf.shacl.doc.ShaclPrefixReader;
import fr.sparna.rdf.shacl.doc.model.NamespaceSection;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationDiagram;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;

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
		ArrayList<NodeShape> allNodeShapes = new ArrayList<>();
		NodeShapeReader reader = new NodeShapeReader(lang);
		for (Resource nodeShape : nodeShapes) {
			allNodeShapes.add(new NodeShape(nodeShape));
		}

		// sort node shapes
		allNodeShapes.sort((NodeShape ns1, NodeShape ns2) -> {
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
					// both sh:order are null, try with their display label
					return ns1.getDisplayLabel(owlGraph, lang).compareTo(ns2.getDisplayLabel(owlGraph, lang));
				}
			}
		});

		// 2. Lire les propriétés
		for (NodeShape aBox : allNodeShapes) {
			aBox.setProperties(reader.readProperties(aBox.getNodeShape(), allNodeShapes, owlGraph));
		}		

		// Lecture de OWL
		// this is tricky, because we can have multiple ones if SHACL is merged with OWL or imports OWL
		List<Resource> sOWL = shaclGraph.listResourcesWithProperty(RDF.type, OWL.Ontology).toList();
		
		// let's decide first to exclude the ones that are owl:import-ed from others
		List<Resource> filteredOWL = sOWL.stream().filter(onto1 -> {
			return !sOWL.stream().anyMatch(onto2 -> onto2.hasProperty(OWL.imports, onto1));
		}).collect(Collectors.toList());
		
		OwlOntology ontologyObject = null;
		if(filteredOWL.size() > 0) {
			ontologyObject = new OwlOntology(filteredOWL.get(0), lang);
		}
		
		// Code XML
		ShapesDocumentation shapesDocumentation = new ShapesDocumentation(ontologyObject, lang);
		shapesDocumentation.setImgLogo(this.imgLogo);
		
		
		
		// Option pour créer le diagramme		
		if (this.readDiagram) {
			PlantUmlSourceGenerator sourceGenerator = new PlantUmlSourceGenerator();
			List<PlantUmlDiagramOutput> plantUmlDiagrams = sourceGenerator.generatePlantUmlDiagram(
					shaclGraph,
					owlGraph,
					lang
			);
			
			// turn diagrams into output data structure
			plantUmlDiagrams.stream().forEach(d -> shapesDocumentation.getDiagrams().add(new ShapesDocumentationDiagram(d)));			
		}
		
		
		


		// 3. Lire les prefixes
		HashSet<String> gatheredPrefixes = new HashSet<>();
		for (NodeShape aBox : allNodeShapes) {
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
		for (NodeShape nodeShape : allNodeShapes) {
			ShapesDocumentationSection section = ShapesDocumentationSectionBuilder.build(nodeShape, allNodeShapes, shaclGraph, owlGraph, lang);
			sections.add(section);
		}
		shapesDocumentation.setSections(sections);
		return shapesDocumentation;
	}
	


}
