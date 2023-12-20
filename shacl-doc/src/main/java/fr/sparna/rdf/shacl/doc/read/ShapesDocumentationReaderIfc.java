package fr.sparna.rdf.shacl.doc.read;

import java.util.List;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.doc.model.ParserModel;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;

public interface ShapesDocumentationReaderIfc {

	//public ShapesDocumentation readShapesDocumentation(Model shaclGraph, Model owlGraph, String lang, String fileName, boolean outExpandDiagram);
	public ShapesDocumentation readShapesDocumentation(ParserModel metadata,
													   List<PlantUmlDiagramOutput> diagrams, 
													    String lang, 
													    String fileName, 
													    boolean outExpandDiagram);
	
}
