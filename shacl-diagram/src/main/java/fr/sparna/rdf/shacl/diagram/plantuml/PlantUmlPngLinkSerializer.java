package fr.sparna.rdf.shacl.diagram.plantuml;

import java.io.IOException;

import net.sourceforge.plantuml.code.TranscoderUtil;

public class PlantUmlPngLinkSerializer {

	public String serialize(String plantUmlString) throws IOException {
		String pngLink = "http://www.plantuml.com/plantuml/png/"+TranscoderUtil.getDefaultTranscoder().encode(plantUmlString);

		return pngLink;
	}

}