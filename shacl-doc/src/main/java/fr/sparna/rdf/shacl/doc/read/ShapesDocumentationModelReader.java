package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PlantUmlSourceGenerator;
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
			Model shaclGraph,
			Model owlGraph,
			String lang,
			boolean avoidArrowsToEmptyBoxes
	) {
		
		// parse SHACL & OWL
		ParserModelReader r = new ParserModelReader();
		ParserModel shapesModel = r.readMetadata(shaclGraph, owlGraph, lang);
		
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
		shapesDocumentation.setPrefixe(shapesModel.getNamespaceSections()); // sortNameSpacesectionPrefix);
		
		// For each NodeShape ...
		List<ShapesDocumentationSection> sections = new ArrayList<>();
		for (NodeShape nodeShape : shapesModel.getAllNodeShapes() ) {
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
		return shapesDocumentation;
	}
}
