package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.NodeShapeReader;
import fr.sparna.rdf.shacl.doc.PlantUmlSourceGenerator;
import fr.sparna.rdf.shacl.doc.ShaclPrefixReader;
import fr.sparna.rdf.shacl.doc.ShapesGraph;
import fr.sparna.rdf.shacl.doc.model.NamespaceSection;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationDiagram;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;

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
			String lang
	) {
		
		// parse SHACL & OWL
		ShapesGraph shapesModel = new ShapesGraph(shaclGraph, owlGraph, lang);
		
		ShapesDocumentation shapesDocumentation = new ShapesDocumentation(shapesModel.getOntologyObject(), lang);	
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
		
		// Prefixes
		List<NamespaceSection> nsSections = this.readNamespaceSections(shaclGraph, shapesModel.getAllNodeShapes(), lang);
		shapesDocumentation.setPrefixe(nsSections);
		
		// For each NodeShape ...
		List<ShapesDocumentationSection> sections = new ArrayList<>();
		for (NodeShape nodeShape : shapesModel.getAllNodeShapes()) {
			ShapesDocumentationSection section = ShapesDocumentationSectionBuilder.build(nodeShape, 
					shapesModel.getAllNodeShapes(), 
					 // Model
					shaclGraph, 
					// Model
					owlGraph, 
					lang);
			sections.add(section);
		}
		shapesDocumentation.setSections(sections);
		
		
		// Apply post-processing to the generated documentation, to populate number of instances and charts
		ShaclVisit visit = new ShaclVisit(shaclGraph);
		// this assumes that the statistics are part of the SHACL graph
		visit.visit(new EnrichDocumentationWithStatisticsVisitor(shaclGraph, shapesDocumentation));
		visit.visit(new EnrichDocumentationWithChartsVisitor(shaclGraph, shapesDocumentation, lang));
		
		
		return shapesDocumentation;
	}
	
	public List<NamespaceSection> readNamespaceSections(Model shaclGraph, List<NodeShape> AllNodeShapes, String lang) {
		
		// 3. Lire les prefixes
		HashSet<String> gatheredPrefixes = new HashSet<>();
		NodeShapeReader reader = new NodeShapeReader(lang);
		for (NodeShape aBox : AllNodeShapes) { // allNodeShapes) {
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
		
		return sortNameSpacesectionPrefix; 
	}
}
