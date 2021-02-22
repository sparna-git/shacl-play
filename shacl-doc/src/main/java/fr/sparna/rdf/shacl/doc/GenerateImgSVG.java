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


public class GenerateImgSVG {

	protected String ImgSvg;

	public String getImgSvg() {
		return ImgSvg;
	}

	@SuppressWarnings("deprecation")
	public void setImgSvg(Model shapesModel) throws IOException {

		// draw
		ShaclPlantUmlWriter writer = new ShaclPlantUmlWriter();
		String plantUmlString = writer.writeInPlantUml(shapesModel);

		SourceStringReader reader = new SourceStringReader(plantUmlString);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		reader.generateImage(out, new FileFormatOption(FileFormat.SVG));
		//reader.generateImage(out, new FileFormatOption(FileFormat.SVG));
		out.close();

		// The XML is stored into svg
		String svg = new String(out.toByteArray(), Charset.forName("UTF-8"));

		this.ImgSvg = svg;
	}

}