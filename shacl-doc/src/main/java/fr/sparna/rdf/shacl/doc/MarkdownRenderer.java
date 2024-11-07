package fr.sparna.rdf.shacl.doc;

import java.util.List;

import org.commonmark.Extension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;

public class MarkdownRenderer {
	
	private static MarkdownRenderer instance;

	private HtmlRenderer renderer;
	private Parser parser;
	
	private MarkdownRenderer() {
		// init markdown parser & renderer
		List<Extension> extensions = List.of(ImageAttributesExtension.create(), AutolinkExtension.create());
		
		this.parser = Parser.builder().extensions(extensions).build();
		this.renderer = HtmlRenderer.builder().extensions(extensions).build();
	}

	public static MarkdownRenderer getInstance() {
		if(MarkdownRenderer.instance == null) {
			instance = new MarkdownRenderer();
		}
		return MarkdownRenderer.instance;
	}

	public String renderMarkdown(String mdString) {
		if(mdString == null) return null;
		Node document = parser.parse(mdString);					
		return renderer.render(document);	
	}
}
