package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.List;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.model.ParserModel;
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
			ParserModel metadata,
			List<PlantUmlDiagramOutput> plantUmlDiagrams,
			//Model shaclGraph,
			//Model owlGraph,
			String lang,
			String fileName,
			boolean avoidArrowsToEmptyBoxes
	) {
		
		
		// Code XML
		
		//ontologyObject
		
		ShapesDocumentation shapesDocumentation = new ShapesDocumentation(metadata.getOntologyObject(), lang);
		
		
		shapesDocumentation.setImgLogo(this.imgLogo);	
		
		// Option pour créer le diagramme		
		if (this.readDiagram) {
			// turn diagrams into output data structure
			plantUmlDiagrams.stream().forEach(d -> shapesDocumentation.getDiagrams().add(new ShapesDocumentationDiagram(d)));			
		}
		
		// Prefixes
		shapesDocumentation.setPrefixe(metadata.getNamespaceSections()); // sortNameSpacesectionPrefix);
		
		// For each NodeShape ...
		List<ShapesDocumentationSection> sections = new ArrayList<>();
		for (NodeShape nodeShape : metadata.getAllNodeShapes() ) {
			ShapesDocumentationSection section = ShapesDocumentationSectionBuilder.build(nodeShape, 
																						 metadata.getAllNodeShapes(), 
																						 // Model
																						 metadata.getShaclGraph(),//shaclGraph, 
																						 // Model
																						 metadata.getOwlGraph(), //owlGraph, 
																						 lang);
			sections.add(section);
		}
		shapesDocumentation.setSections(sections);
		return shapesDocumentation;
	}
}
