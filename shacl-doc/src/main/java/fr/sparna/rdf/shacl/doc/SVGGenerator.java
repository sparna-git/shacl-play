package fr.sparna.rdf.shacl.doc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import fr.sparna.rdf.shacl.diagram.ShaclPlantUmlWriter;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class SVGGenerator {

	@SuppressWarnings("deprecation")
	public String generateSvgDiagram(Model shapesModel) throws IOException {

		// draw - without subclasses links
		ShaclPlantUmlWriter writer = new ShaclPlantUmlWriter(false, true);
		String plantUmlString = writer.writeInPlantUml(shapesModel);
		
		System.out.println(plantUmlString);

		SourceStringReader reader = new SourceStringReader(plantUmlString);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		reader.generateImage(out, new FileFormatOption(FileFormat.SVG));
		//reader.generateImage(out, new FileFormatOption(FileFormat.SVG));
		out.close();

		// The XML is stored into svg
		String svg = new String(out.toByteArray(), Charset.forName("UTF-8"));

		return svg;
	}

}