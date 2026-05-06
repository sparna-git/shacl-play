package fr.sparna.rdf.shacl.diagram.plantuml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;

public class PlantUmlHtmlSerializer {

	public void serialize(List<PlantUmlDiagramOutput> diagrams, OutputStream output) throws IOException {
		for (PlantUmlDiagramOutput oneDiagram : diagrams) {
			output.write("<div>\n".getBytes());	
			if(oneDiagram.getDisplayTitle() != null) {
				output.write(("<h2>"+oneDiagram.getDisplayTitle()+"</h2>\n").getBytes());					
			}
			if(oneDiagram.getDiagramDescription() != null) {
				output.write(("<p>"+oneDiagram.getDiagramDescription()+"</p>\n").getBytes());
			}
			// render in SVG inside HTML
			PlantUmlSvgSerializer svgSerializer = new PlantUmlSvgSerializer();
			svgSerializer.serializeInSVG(oneDiagram.getPlantUmlString(), output);
			output.write("\n".getBytes());
			output.write("<hr /><br />\n".getBytes());
			output.write("</div>\n".getBytes());
		}			
	}

}