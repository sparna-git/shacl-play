package fr.sparna.rdf.shacl.doc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import fr.sparna.rdf.shacl.diagram.ShaclPlantUmlWriter;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class SVGGenerator {

	public String generateSvgDiagram(Model shapesModel, Model owlModel) throws IOException {

		// draw - without subclasses links
		// set first parameter to true to draw subclassOf links
		ShaclPlantUmlWriter writer = new ShaclPlantUmlWriter(false, true);
		Model finalModel = ModelFactory.createDefaultModel();
		finalModel.add(shapesModel);
		if(owlModel != null) {
			finalModel.add(owlModel);
		}
		String plantUmlString = writer.writeInPlantUml(finalModel);
		
		// System.out.println(plantUmlString);

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