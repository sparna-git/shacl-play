package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.jena.shacl.NodeShape;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.PlantUmlSourceGenerator;
import fr.sparna.rdf.jena.shacl.ShapesGraph;
import fr.sparna.rdf.jena.shacl.ShapesGraph;
import fr.sparna.rdf.shacl.doc.model.NamespaceSection;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationDiagram;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;

public class ShapesDocumentationModelReader implements ShapesDocumentationReaderIfc {

	protected boolean readDiagram = true;
	protected boolean hideProperties = false;
	protected boolean readSectionDiagrams = false;
	protected String imgLogo = null;
	protected boolean filterUnusedNodeShapes = true;
	

	public static ShapesDocumentationModelReader buildShapesDocumentationModelReader(
		boolean readDiagram,
		String imgLogo,
		boolean hideProperties,
		boolean readSectionDiagrams,
		boolean filterUnusedNodeShapes
	) {
		ShapesDocumentationModelReader reader = new ShapesDocumentationModelReader();
		reader.setReadDiagram(readDiagram);
		reader.setImgLogo(imgLogo);
		reader.setHideProperties(hideProperties);
		reader.setReadSectionDiagrams(readSectionDiagrams);
		reader.setFilterUnusedNodeShapes(filterUnusedNodeShapes);

		return reader;
	}

	/**
	 * @return a ShapesDocumentationModelReader with default options
	 */
	public static ShapesDocumentationModelReader buildDefaultShapesDocumentationModelReader() {
		return buildShapesDocumentationModelReader(
			false,
			null,
			false,
			true,
			true
		);
	}

	public ShapesDocumentationModelReader() {

	}

	/**
	 * @deprecated use the static factory method buildShapesDocumentationModelReader instead
	 */
	@Deprecated
	public ShapesDocumentationModelReader(
		boolean readDiagram,
		String imgLogo,
		boolean hideProperties,
		boolean readSectionDiagrams
	) {
		super();
		this.readDiagram = readDiagram;
		this.imgLogo = imgLogo;
		this.hideProperties = hideProperties;
		this.readSectionDiagrams = readSectionDiagrams;
	}

	@Override
	public ShapesDocumentation readShapesDocumentation(
			Model shaclGraph,
			Model owlGraph,
			String lang
	) {
		
		// parse SHACL & OWL
		ShapesGraph shapesModel = new ShapesGraph(shaclGraph, owlGraph);
		
		ShapesDocumentation shapesDocumentation = new ShapesDocumentation(shapesModel.getOntology(), lang);	
		shapesDocumentation.setImgLogo(this.imgLogo);	
		
		// Option pour créer le diagramme		
		if (this.readDiagram) {			
			PlantUmlSourceGenerator sourceGenerator = new PlantUmlSourceGenerator(shaclGraph, owlGraph, this.hideProperties, lang);
			List<PlantUmlDiagramOutput> plantUmlDiagrams = sourceGenerator.generatePlantUmlDiagram();
			
			// turn diagrams into output data structure
			plantUmlDiagrams.stream().forEach(d -> shapesDocumentation.getDiagrams().add(new ShapesDocumentationDiagram(d)));			
		}
		
		// Prefixes
		// Sort node shapes for documentation
		List<NodeShape> sortedNodeShapes = shapesModel.getAllNodeShapes().stream()
			.sorted(new ShapesGraph.NodeShapeDisplayLabelComparator(owlGraph, lang))
			.collect(Collectors.toList());
		List<NamespaceSection> nsSections = this.readNamespaceSections(shaclGraph, sortedNodeShapes, lang);
		shapesDocumentation.setPrefixe(nsSections);
		
		
		ShapesDocumentationSectionBuilder sectionBuidler = new ShapesDocumentationSectionBuilder(
			new PlantUmlSourceGenerator(shaclGraph, owlGraph, this.hideProperties, lang)
		);
		// For each NodeShape ...
		List<ShapesDocumentationSection> sections = new ArrayList<>();
		for (NodeShape nodeShape : sortedNodeShapes) {
			
			if (this.isFilterUnusedNodeShapes() ) {
				if (nodeShape.isUsedInShapesGraph()) {
					ShapesDocumentationSection section = sectionBuidler.build(
					nodeShape, 
					shapesModel, 
					// Model
					shaclGraph, 
					// Model
					owlGraph, 
					lang,
					this.readSectionDiagrams
					);
					sections.add(section);
				}				
			} else {
				ShapesDocumentationSection section = sectionBuidler.build(
					nodeShape, 
					shapesModel, 
					// Model
					shaclGraph, 
					// Model
					owlGraph, 
					lang,
					this.readSectionDiagrams
					);
					sections.add(section);
			} 
		}
		shapesDocumentation.setSections(sections);
		
		
		// Apply post-processing to the generated documentation, to populate number of instances and charts
		ShaclVisit visit = new ShaclVisit(shaclGraph);
		// this assumes that the statistics are part of the SHACL graph
		visit.visit(new EnrichDocumentationWithStatisticsVisitor(shaclGraph, shapesDocumentation));
		visit.visit(new EnrichDocumentationWithChartsVisitor(shaclGraph, shapesDocumentation, lang));
		
		// Generate SparqlQuery for each property
		visit.visit(new EnrichDocumentationWithQuerySparqlVisitor(shaclGraph, shapesDocumentation));
		
		return shapesDocumentation;
	}
	
	public List<NamespaceSection> readNamespaceSections(Model shaclGraph, List<NodeShape> AllNodeShapes, String lang) {
		
		// 3. Lire les prefixes
		ShapesGraph shapesGraph = new ShapesGraph(shaclGraph);
		Map<String, String> necessaryPrefixes = shapesGraph.getNamespaces();
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

	public boolean isReadDiagram() {
		return readDiagram;
	}

	public void setReadDiagram(boolean readDiagram) {
		this.readDiagram = readDiagram;
	}

	public boolean isHideProperties() {
		return hideProperties;
	}

	public void setHideProperties(boolean hideProperties) {
		this.hideProperties = hideProperties;
	}

	public boolean isReadSectionDiagrams() {
		return readSectionDiagrams;
	}

	public void setReadSectionDiagrams(boolean readSectionDiagrams) {
		this.readSectionDiagrams = readSectionDiagrams;
	}

	public String getImgLogo() {
		return imgLogo;
	}

	public void setImgLogo(String imgLogo) {
		this.imgLogo = imgLogo;
	}

	public boolean isFilterUnusedNodeShapes() {
		return filterUnusedNodeShapes;
	}

	public void setFilterUnusedNodeShapes(boolean filterUnusedNodeShapes) {
		this.filterUnusedNodeShapes = filterUnusedNodeShapes;
	}

	
}
