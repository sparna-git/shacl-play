package fr.sparna.rdf.shacl.diagram.plantuml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class PlantUmlPngSerializer {

	public void serialize(String plantUmlString, OutputStream output) throws IOException {
		String pngLink = new PlantUmlPngLinkSerializer().serialize(plantUmlString);

		URL pngURL = new URL(pngLink);
		InputStream is = pngURL.openStream();
			        	
		byte[] b = new byte[2048];
		int length;
		while ((length = is.read(b)) != -1) {
			output.write(b, 0, length);
		}
		is.close();	
	}

}