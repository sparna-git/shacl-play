package fr.sparna.rdf.shacl.doc;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class resizeImage {
	
	public BufferedImage resizeimg(final URL url, final Dimension size) throws IOException {
		final BufferedImage image = ImageIO.read(url);
	    final BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
	    final Graphics2D g = resized.createGraphics();
	    g.drawImage(image, 0, 0, size.width, size.height, null);
	    g.dispose();
	    return resized;
	}
}
