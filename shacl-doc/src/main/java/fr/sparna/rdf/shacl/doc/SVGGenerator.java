package fr.sparna.rdf.shacl.doc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class SVGGenerator {

	public List<String> generateSvgDiagram(List<PlantUmlDiagramOutput> diagrams) throws IOException {
		List<String> svg = new ArrayList<String>();
		
		for (PlantUmlDiagramOutput d : diagrams) {
			String puml = d.getPlantUmlString();
			SourceStringReader reader = new SourceStringReader(puml);
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			reader.generateImage(out, new FileFormatOption(FileFormat.SVG));
			out.close();
				
			// The XML is stored into svg
			String svgStr = new String(out.toByteArray(), Charset.forName("UTF-8"));
			svg.add(svgStr);
		}
		
		return svg;
	}

}